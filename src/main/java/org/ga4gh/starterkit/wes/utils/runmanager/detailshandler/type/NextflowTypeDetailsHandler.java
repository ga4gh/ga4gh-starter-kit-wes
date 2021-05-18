package org.ga4gh.starterkit.wes.utils.runmanager.detailshandler.type;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.util.Strings;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.wes.constant.WesApiConstants;
import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesLog;
import org.springframework.beans.factory.annotation.Autowired;

public class NextflowTypeDetailsHandler extends AbstractRunTypeDetailsHandler {

    @Autowired
    private ServerProps serverProps;

    private final static DateTimeFormatter NEXTFLOW_LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private List<String> taskLogTableList;

    public NextflowTypeDetailsHandler() {

    }

    // for submission of workflows

    public String[] constructWorkflowRunCommand() throws Exception {
        String workflowSignature = getWorkflowSignature();
        return new String[] {
            "nextflow",
            "run",
            "-r",
            "main",
            "-params-file",
            "params.json",
            "-name",
            constructNextflowRunName(),
            workflowSignature
        };
    }

    // for reading workflow run state

    public RunStatus determineRunStatus() throws Exception {
        RunStatus runStatus = new RunStatus(getWesRun().getId(), State.UNKNOWN);
        String runLogStdout = requestCommandStdoutFromEngine(new String[] {"nextflow", "log"});
        String[] runLog = runLogStdout.split("\n")[1].split("\t");
        String runLogStatus = runLog[3].strip();
        if (runLogStatus.equals("OK")) {
            runStatus.setState(State.COMPLETE);
            return runStatus;
        }
        return runStatus;
    }

    // for reading workflow log

    public void completeRunLog(RunLog runLog) throws Exception {
        List<WesLog> taskLogs = determineTaskLogs();
        WesLog workflowLog = determineWorkflowLog();
        Map<String, String> outputs = determineOutputs();
        runLog.setTaskLogs(taskLogs);
        runLog.setRunLog(workflowLog);
        runLog.setOutputs(outputs);
    }

    private List<WesLog> determineTaskLogs() throws Exception {
        List<WesLog> taskLogs = new ArrayList<>();
        String taskLogTable = requestCommandStdoutFromEngine(new String[] {"nextflow", "log", constructNextflowRunName(), "-f", "process,start,complete,exit,workdir"});
        String[] taskLogTableArray = taskLogTable.split("\n");
        List<String> taskLogTableList = Arrays.asList(taskLogTableArray).subList(0, taskLogTableArray.length);
        this.taskLogTableList = taskLogTableList;

        for (String taskLogRow: taskLogTableList) {
            // unpack the task level nextflow log row, get contents of workdir
            String[] taskLogRowArr = taskLogRow.split("\t");
            String process = taskLogRowArr[0];
            String start = taskLogRowArr[1];
            String complete = taskLogRowArr[2];
            String exit = taskLogRowArr[3];
            String workdir = taskLogRowArr[4];
            String cmd = requestFileContentsFromEngine(Paths.get(workdir, ".command.sh").toString());

            List<String> workdirSplit = Arrays.asList(workdir.split("/"));
            List<String> subdirsSplit = workdirSplit.subList(workdirSplit.size() - 2, workdirSplit.size());
            String subdirs = Strings.join(subdirsSplit, '/');
            String logURLPrefix = serverProps.getScheme() + "://" + serverProps.getHostname() + WesApiConstants.WES_API_V1 + "/logs/nextflow";
            String logURLSuffix = "/" + getWesRun().getId() + "/" + subdirs;

            WesLog taskLog = new WesLog();            
            taskLog.setName(process);
            taskLog.setStartTime(LocalDateTime.parse(start, NEXTFLOW_LOG_DATE_FORMAT));
            taskLog.setEndTime(LocalDateTime.parse(complete, NEXTFLOW_LOG_DATE_FORMAT));
            taskLog.setStdout(logURLPrefix + "/stdout" + logURLSuffix);
            taskLog.setStderr(logURLPrefix + "/stderr" + logURLSuffix);
            taskLog.setExitCode(Integer.parseInt(exit));
            taskLog.setCmd(Arrays.asList(cmd.split("\n")));
            taskLogs.add(taskLog);
        }

        return taskLogs;
    }

    private WesLog determineWorkflowLog() throws Exception {
        WesLog workflowLog = new WesLog();
        workflowLog.setName(getWorkflowSignature());

        // set log URLs for stdout and stderr
        String logURLPrefix = serverProps.getScheme() + "://" + serverProps.getHostname() + WesApiConstants.WES_API_V1 + "/logs/nextflow";
        String logURLSuffix = "/" + getWesRun().getId();
        List<String> workdirs = new ArrayList<>();

        for (String taskLogRow: taskLogTableList) {
            // unpack the task level nextflow log row, get contents of workdir
            String[] taskLogRowArr = taskLogRow.split("\t");
            String workdir = taskLogRowArr[4];
            List<String> workdirSplit = Arrays.asList(workdir.split("/"));
            List<String> subdirsSplit = workdirSplit.subList(workdirSplit.size() - 2, workdirSplit.size());
            String subdirs = Strings.join(subdirsSplit, '/');
            workdirs.add(subdirs);
        }
        
        String logURLQuery = "?workdirs=" + URLEncoder.encode(Strings.join(workdirs, ','), "UTF-8");
        workflowLog.setStdout(logURLPrefix + "/stdout" + logURLSuffix + logURLQuery);
        workflowLog.setStderr(logURLPrefix + "/stderr" + logURLSuffix + logURLQuery);
        return workflowLog;
    }

    private Map<String, String> determineOutputs() throws Exception {
        HashMap<String, String> outputs = new HashMap<>();

        for (String taskLogRow: taskLogTableList) {
            // unpack the task level nextflow log row, get contents of workdir
            String[] taskLogRowArr = taskLogRow.split("\t");
            String workdir = taskLogRowArr[4];
            List<String> workDirFiles = getRunEngineDetailsHandler().provideDirectoryContents(workdir);
            for (String workDirFile : workDirFiles) {
                if (!workDirFile.startsWith(".")) {
                    if (!outputs.containsKey(workDirFile)) {
                        String outputLocation = "file://" + Paths.get(workdir, workDirFile).toString();
                        outputs.put(workDirFile, outputLocation);
                    }
                }
            }
        }

        return outputs;
    }

    // private convenience methods

    private String getWorkflowSignature() throws Exception {
        URL url = new URL(getWesRun().getWorkflowUrl());
        String urlHost = url.getHost();
        String workflowSignature = null;
        switch (urlHost) {
            case "github.com":
                workflowSignature = githubWorkflowSignature(url);
                break;
        }
        return workflowSignature;
    }

    private String githubWorkflowSignature(URL url) {
        String urlPath = url.getPath();
        List<String> urlPathSplit = Arrays.asList(urlPath.split("/"));
        return String.join("/", urlPathSplit.subList(1, 3));
    }

    private String constructNextflowRunName() {
        return "job" + getWesRun().getId().substring(0, 5);
    }
}
