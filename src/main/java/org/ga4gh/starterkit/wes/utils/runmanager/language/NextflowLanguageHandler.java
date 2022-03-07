package org.ga4gh.starterkit.wes.utils.runmanager.language;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.util.Strings;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
import org.ga4gh.starterkit.wes.config.language.NextflowLanguageConfig;
import org.ga4gh.starterkit.wes.constant.WesApiConstants;
import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesLog;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Faciliates access to data/information for nextflow workflow runs
 */
public class NextflowLanguageHandler extends AbstractLanguageHandler {

    @Autowired
    private ServerProps serverProps;

    private NextflowLanguageConfig languageConfig;
    private String workflowSignature;
    private String workflowRevision;
    

    private final static DateTimeFormatter NEXTFLOW_LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private List<String> taskLogTableList;

    /**
     * Instantiate a new NextflowTypeDetailsHandler instance
     */
    public NextflowLanguageHandler() {
        workflowSignature = null;
        workflowRevision = null;
    }

    public void setup() {
        try {
            determineWorkflowSignatureAndRevision();
        } catch (Exception ex) {

        }
    }

    @Override
    public void setLanguageConfig(LanguageConfig languageConfig) {
        this.languageConfig = (NextflowLanguageConfig) languageConfig;
    }

    @Override
    public NextflowLanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    // for submission of workflows

    public String[] constructWorkflowRunCommand() throws Exception {
        if (!validWorkflowFound()) {
            throw new Exception("A valid workflow could not be determined from the workflow URL");
        }
        return new String[] {
            "nextflow",
            "run",
            "-r",
            workflowRevision,
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
        // complete
        if (runLogStatus.equals("OK")) {
            runStatus.setState(State.COMPLETE);
            return runStatus;
        }
        // executor error
        if (runLogStatus.equals("ERR")) {
            runStatus.setState(State.EXECUTOR_ERROR);
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

    private String getLogURLPrefix() {
        StringBuffer sb = new StringBuffer();
        sb.append(serverProps.getScheme());
        sb.append("://");
        sb.append(serverProps.getHostname());
        if (!serverProps.getPublicApiPort().equals("80")) {
            sb.append(":" + serverProps.getPublicApiPort());
        }
        sb.append(WesApiConstants.WES_API_V1);
        sb.append("/logs/nextflow");
        return sb.toString();
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
            String logURLPrefix = getLogURLPrefix();
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
        workflowLog.setName(workflowSignature);

        // set log URLs for stdout and stderr
        
        List<String> workdirs = new ArrayList<>();
        List<String> cmds = new ArrayList<>();

        for (String taskLogRow: taskLogTableList) {
            // unpack the task level nextflow log row, get contents of workdir
            String[] taskLogRowArr = taskLogRow.split("\t");
            String workdir = taskLogRowArr[4];
            String cmd = requestFileContentsFromEngine(Paths.get(workdir, ".command.sh").toString());
            for (String c : cmd.split("\n")) {
                cmds.add(c);
            }

            List<String> workdirSplit = Arrays.asList(workdir.split("/"));
            List<String> subdirsSplit = workdirSplit.subList(workdirSplit.size() - 2, workdirSplit.size());
            String subdirs = Strings.join(subdirsSplit, '/');
            workdirs.add(subdirs);
        }

        workflowLog.setCmd(cmds);

        String firstStartTime = taskLogTableList.get(0).split("\t")[1];
        String finalEndTime = taskLogTableList.get(taskLogTableList.size() - 1).split("\t")[2];
        workflowLog.setStartTime(LocalDateTime.parse(firstStartTime, NEXTFLOW_LOG_DATE_FORMAT));
        workflowLog.setEndTime(LocalDateTime.parse(finalEndTime, NEXTFLOW_LOG_DATE_FORMAT));

        String finalExitCode = taskLogTableList.get(taskLogTableList.size() - 1).split("\t")[3];
        workflowLog.setExitCode(Integer.parseInt(finalExitCode));
        
        String logURLPrefix = getLogURLPrefix();
        String logURLSuffix = "/" + getWesRun().getId();
        String logURLQuery = "?workdirs=" + URLEncoder.encode(Strings.join(workdirs, ','), "UTF-8");
        workflowLog.setStdout(logURLPrefix + "/stdout" + logURLSuffix + logURLQuery);
        workflowLog.setStderr(logURLPrefix + "/stderr" + logURLSuffix + logURLQuery);
        return workflowLog;
    }

    private Map<String, String> determineOutputs() throws Exception {
        HashMap<String, String> outputs = new HashMap<>();

        HashSet<String> nextflowFiles = new HashSet<>() {{
            add(".command.begin");
            add(".command.err");
            add(".command.log");
            add(".command.out");
            add(".command.run");
            add(".command.sh");
            add(".exitcode");
        }};

        // get the location of each working directory
        for (String testTaskLogRow: taskLogTableList) {
            String[] testTaskLogRowArr = testTaskLogRow.split("\t");
            String testWorkdir = testTaskLogRowArr[4];

            // collect all files from the working directory
            Collection<File> files = FileUtils.listFiles(
                Paths.get(testWorkdir).toFile(),
                TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE
            );
            
            // place files into the outputs hashmap if they are not standard
            // nextflow files
            for (File file : files) {
                String key = file.getPath().replaceAll(testWorkdir+"/", "");
                String value = "file://" + file.getPath();
                
                if (!nextflowFiles.contains(key)) {
                    if (!outputs.containsKey(key)) {
                        outputs.put(key, value);
                    }
                }
            }
        }

        return outputs;
    }

    // private convenience methods

    /**
     * Get the nextflow workflow signature from the workflowURL, to be used
     * in 'nextflow run' to indicate what workflow to run
     * @return 'nextflow run' signature indicating workflow to run
     * @throws Exception a server-side error occurred
     */
    private void determineWorkflowSignatureAndRevision() throws Exception {
        URL url = new URL(getWesRun().getWorkflowUrl());
        String urlHost = url.getHost();
        switch (urlHost) {
            case "github.com":
                githubWorkflowSignatureAndRevision(url);
                break;
        }
    }

    /**
     * Obtain the workflow signature from a "github.com" URL referencing a
     * nextflow workflow repo 
     * @param url github.com URL
     * @return 'nextflow run' signature indicating workflow to run
     */
    private void githubWorkflowSignatureAndRevision(URL url) {
        String urlPath = url.getPath();
        List<String> urlPathSplit = Arrays.asList(urlPath.split("/"));

        // nextflow workflow signature is {org}/{repo}
        workflowSignature = String.join("/", urlPathSplit.subList(1, 3));

        // nextflow revision is 'main' if no branch specified
        workflowRevision = "main"; 
        
        // nextflow revision is branch name if one is provided is workflow URL
        if (urlPathSplit.size() > 3 && urlPathSplit.get(3).equals("tree")) {
            workflowRevision = String.join("/", urlPathSplit.subList(4, urlPathSplit.size()));
        }
    }

    /**
     * Sets the nextflow run job name according to the run's id
     * @return short job name
     */
    private String constructNextflowRunName() {
        return "job" + getWesRun().getId().substring(0, 5);
    }

    private boolean validWorkflowFound() {
        if (workflowSignature == null || workflowRevision == null) {
            return false;
        }
        return true;
    }
}
