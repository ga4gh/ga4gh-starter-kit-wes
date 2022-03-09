package org.ga4gh.starterkit.wes.utils.runmanager.language;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.util.Strings;
import org.ga4gh.starterkit.common.config.ServerProps;
import org.ga4gh.starterkit.wes.config.language.LanguageConfig;
import org.ga4gh.starterkit.wes.config.language.NextflowLanguageConfig;
import org.ga4gh.starterkit.wes.constant.WesApiConstants;
import org.ga4gh.starterkit.wes.exception.NextflowLogException;
import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesLog;
import org.ga4gh.starterkit.wes.utils.runmanager.engine.FileMetadata;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

/**
 * Faciliates access to data/information for nextflow workflow runs
 */
@Setter
@Getter
public class NextflowLanguageHandler extends AbstractLanguageHandler {

    @Autowired
    private ServerProps serverProps;

    private NextflowLanguageConfig languageConfig;
    private String workflowSignature;
    private String workflowRevision;
    private final static DateTimeFormatter NEXTFLOW_LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private List<NextflowTask> sortedNextflowTasks;

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
        CommandOutput nextflowLogCommandOutput = requestCommandOutputFromEngine(new String[] {"nextflow", "log"});
        String runLogStdout = nextflowLogCommandOutput.getStdout();
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
        // attempt to load run log via primary means, i.e. "nextflow log"
        try {
            constructNextflowTasksPrimary();
        // run log load by "nextflow log" failed, using backup method
        } catch (NextflowLogException ex) {
            constructNextflowTasksBackup();
        }
        runLog.setTaskLogs(determineTaskLogs());
        runLog.setRunLog(determineWorkflowLog());
        runLog.setOutputs(determineOutputs());
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

    private void constructNextflowTasksPrimary() throws Exception {
        CommandOutput nextflowLogCommandOutput = requestCommandOutputFromEngine(new String[] {"nextflow", "log", constructNextflowRunName(), "-f", "process,start,complete,exit,workdir"});
        if (nextflowLogCommandOutput.getExitCode() != 0) {
            throw new NextflowLogException("nextflow log returned non-zero exit code");
        }

        List<NextflowTask> sortedNextflowTasks = new ArrayList<>();
        String taskLogTable = nextflowLogCommandOutput.getStdout();
        String[] taskLogTableArray = taskLogTable.split("\n");
        for (String taskLogRow : taskLogTableArray) {
            String[] taskLogRowArr = taskLogRow.split("\t");
            NextflowTask nextflowTask = new NextflowTask();
            nextflowTask.setProcess(taskLogRowArr[0]);
            nextflowTask.setStartTime(taskLogRowArr[1]);
            nextflowTask.setCompleteTime(taskLogRowArr[2]);
            nextflowTask.setExitCode(taskLogRowArr[3]);
            nextflowTask.setWorkdir(taskLogRowArr[4]);
            sortedNextflowTasks.add(nextflowTask);
        }
        setSortedNextflowTasks(sortedNextflowTasks);
    }

    private void constructNextflowTasksBackup() {
        List<FileMetadata> workdirs = new ArrayList<>();
        for (String subdirA : getEngineHandler().provideDirectoryContents("work")) {
            for (String subdirB : getEngineHandler().provideDirectoryContents("work/"+subdirA)) {
                String workdir = "work/" + subdirA + "/" + subdirB;
                FileMetadata fileMetadata = getEngineHandler().provideFileAttributes(workdir);
                fileMetadata.setFilename(workdir);
                workdirs.add(fileMetadata);
            }
        }

        FileMetadata[] workdirsArray = new FileMetadata[]{};
        workdirsArray = workdirs.toArray(workdirsArray);
        Arrays.sort(workdirsArray, new Comparator<FileMetadata>() {
            public int compare(FileMetadata f1, FileMetadata f2) {
                return Long.compare(
                    f1.getFileAttributes().creationTime().toMillis(),
                    f2.getFileAttributes().creationTime().toMillis()
                );
            }
        });

        List<NextflowTask> sortedNextflowTasks = Arrays
            .asList(workdirsArray)
            .stream()
            .map(workdir -> {
                NextflowTask nextflowTask = new NextflowTask();
                nextflowTask.setProcess("foo");
                nextflowTask.setExitCode("1");
                nextflowTask.setStartTime("2022-02-14 09:15:00.000");
                nextflowTask.setCompleteTime("2022-02-14 09:15:00.000");
                nextflowTask.setWorkdir(workdir.getFilename());
                return nextflowTask;
            })
            .collect(Collectors.toList());
        
        setSortedNextflowTasks(sortedNextflowTasks);
    }

    private List<WesLog> determineTaskLogs() throws Exception {
        List<WesLog> taskLogs = new ArrayList<>();

        for (NextflowTask nextflowTask : getSortedNextflowTasks()) {
            
            String cmd = requestFileContentsFromEngine(Paths.get(nextflowTask.getWorkdir(), ".command.sh").toString());
            List<String> workdirSplit = Arrays.asList(nextflowTask.getWorkdir().split("/"));
            List<String> subdirsSplit = workdirSplit.subList(workdirSplit.size() - 2, workdirSplit.size());
            String subdirs = Strings.join(subdirsSplit, '/');
            String logURLPrefix = getLogURLPrefix();
            String logURLSuffix = "/" + getWesRun().getId() + "/" + subdirs;

            WesLog taskLog = new WesLog();
            taskLog.setName(nextflowTask.getProcess());
            taskLog.setStartTime(LocalDateTime.parse(nextflowTask.getStartTime(), NEXTFLOW_LOG_DATE_FORMAT));
            taskLog.setEndTime(LocalDateTime.parse(nextflowTask.getCompleteTime(), NEXTFLOW_LOG_DATE_FORMAT));
            taskLog.setStdout(logURLPrefix + "/stdout" + logURLSuffix);
            taskLog.setStderr(logURLPrefix + "/stderr" + logURLSuffix);
            taskLog.setExitCode(Integer.parseInt(nextflowTask.getExitCode()));
            if (cmd != null) {
                taskLog.setCmd(Arrays.asList(cmd.split("\n")));
            } else {
                taskLog.setCmd(null);
            }
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

        for (NextflowTask nextflowTask : getSortedNextflowTasks()) {
            // unpack the task level nextflow log row, get contents of workdir
            String workdir = nextflowTask.getWorkdir();
            String cmd = requestFileContentsFromEngine(Paths.get(workdir, ".command.sh").toString());
            if (cmd != null) {
                for (String c : cmd.split("\n")) {
                    cmds.add(c);
                }
            }

            List<String> workdirSplit = Arrays.asList(workdir.split("/"));
            List<String> subdirsSplit = workdirSplit.subList(workdirSplit.size() - 2, workdirSplit.size());
            String subdirs = Strings.join(subdirsSplit, '/');
            workdirs.add(subdirs);
        }

        workflowLog.setCmd(cmds);

        NextflowTask firstNextflowTask = getSortedNextflowTasks().get(0);
        NextflowTask finalNextflowTask = getSortedNextflowTasks().get(getSortedNextflowTasks().size()-1);

        String firstStartTime = firstNextflowTask.getStartTime();
        String finalEndTime = finalNextflowTask.getCompleteTime();
        workflowLog.setStartTime(LocalDateTime.parse(firstStartTime, NEXTFLOW_LOG_DATE_FORMAT));
        workflowLog.setEndTime(LocalDateTime.parse(finalEndTime, NEXTFLOW_LOG_DATE_FORMAT));

        String finalExitCode = finalNextflowTask.getExitCode();
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
            add(".command.in");
            add(".command.run");
            add(".command.sh");
            add(".exitcode");
        }};

        // get the location of each working directory
        for (NextflowTask nextflowTask: getSortedNextflowTasks()) {
            String testWorkdir = nextflowTask.getWorkdir();

            String fullDir = getEngineHandler().provideFileAttributes(testWorkdir).getFilename();

            // collect all files from the working directory
            Collection<File> files = FileUtils.listFiles(
                Paths.get(fullDir).toFile(),
                TrueFileFilter.INSTANCE,
                TrueFileFilter.INSTANCE
            );
            
            // place files into the outputs hashmap if they are not standard
            // nextflow files
            for (File file : files) {
                String key = file.getPath().replaceAll(fullDir+"/", "");
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
