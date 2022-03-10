package org.ga4gh.starterkit.wes.utils.runmanager.language;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public NextflowLanguageHandler() {
        workflowSignature = null;
        workflowRevision = null;
    }

    public void setup() {
        try {
            determineWorkflowSignatureAndRevision();
        } catch (Exception ex) {}
    }

    @Override
    public void setLanguageConfig(LanguageConfig languageConfig) {
        this.languageConfig = (NextflowLanguageConfig) languageConfig;
    }

    @Override
    public NextflowLanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    /* ##################################################
       # SUBMIT WORKFLOW RUN
       ################################################## */

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

    /* ##################################################
       # GET RUN STATUS
       ################################################## */

    public RunStatus determineRunStatus() throws Exception {
        // run status is determined based on output of "nextflow log" command
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
        else if (runLogStatus.equals("ERR")) {
            runStatus.setState(State.EXECUTOR_ERROR);
            return runStatus;
        } else if (runLogStatus.equals("-")) {
            List<String> workdirContents = getEngineHandler().provideDirectoryContents("work");
            if (workdirContents != null) {
                runStatus.setState(State.RUNNING);
            }
        }
        return runStatus;
    }

    /* ##################################################
       # GET RUN LOG
       ################################################## */

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
        // get a table of all tasks (one row per task),
        // columns include process name, exit code, start time, end time,
        // working subdirectory
        CommandOutput nextflowLogCommandOutput = requestCommandOutputFromEngine(new String[] {"nextflow", "log", constructNextflowRunName(), "-f", "process,start,complete,exit,workdir"});
        if (nextflowLogCommandOutput.getExitCode() != 0) {
            throw new NextflowLogException("nextflow log returned non-zero exit code");
        }

        // for each task row, parse the table and create a NextflowTask instance
        // from the data
        List<NextflowTask> sortedNextflowTasks = new ArrayList<>();
        String taskLogTable = nextflowLogCommandOutput.getStdout();
        String[] taskLogTableArray = taskLogTable.split("\n");
        for (String taskLogRow : taskLogTableArray) {
            String[] taskLogRowArr = taskLogRow.split("\t");
            // set NextflowTask attributes based on table
            NextflowTask nextflowTask = new NextflowTask();
            nextflowTask.setProcess(taskLogRowArr[0]);
            nextflowTask.setStartTime(taskLogRowArr[1]);
            nextflowTask.setCompleteTime(taskLogRowArr[2]);
            nextflowTask.setExitCode(taskLogRowArr[3]);
            nextflowTask.setAbsoluteWorkdir(taskLogRowArr[4]);

            // relativeWorkingDir can be inferred by absoluteWorkingDir
            String relativeWorkdir = null;
            String[] dirsArray = taskLogRowArr[4].split("/");
            String[] relativeWorkdirArray = Arrays.copyOfRange(dirsArray, dirsArray.length - 3, dirsArray.length);
            relativeWorkdir = String.join("/", relativeWorkdirArray);
            nextflowTask.setRelativeWorkdir(relativeWorkdir);
            
            sortedNextflowTasks.add(nextflowTask);
        }
        setSortedNextflowTasks(sortedNextflowTasks);
    }

    private void constructNextflowTasksBackup() {
        // obtain all task subdirectories under "work/"
        List<FileMetadata> workdirsMetadata = new ArrayList<>();

        for (String subdirA : getEngineHandler().provideDirectoryContents("work")) {
            for (String subdirB : getEngineHandler().provideDirectoryContents("work/"+subdirA)) {
                String workdir = "work/" + subdirA + "/" + subdirB;
                FileMetadata fileMetadata = getEngineHandler().provideFileAttributes(workdir);
                workdirsMetadata.add(fileMetadata);
            }
        }

        // sort task subdirectories according to subdirectory creation time,
        // as a proxy for task order
        FileMetadata[] workdirsMetadataArray = new FileMetadata[]{};
        workdirsMetadataArray = workdirsMetadata.toArray(workdirsMetadataArray);
        Arrays.sort(workdirsMetadataArray, new Comparator<FileMetadata>() {
            public int compare(FileMetadata f1, FileMetadata f2) {
                return Long.compare(
                    f1.getFileAttributes().creationTime().toMillis(),
                    f2.getFileAttributes().creationTime().toMillis()
                );
            }
        });

        // for each work directory, create a NextflowTask that will be used
        // to render the RunLog
        List<NextflowTask> sortedNextflowTasks = Arrays
            .asList(workdirsMetadataArray)
            .stream()
            .map(workdirMetadata -> {
                // get task process name from .command.begin
                String process = null;
                try {
                    String fileContent = requestFileContentsFromEngine(workdirMetadata.getRelativePath() + "/.command.run");
                    String taskLine = fileContent.split("\n")[1];
                    process = taskLine.replace("# NEXTFLOW TASK: ", "");
                } catch (Exception ex) {}

                // get task exit code from .exitcode
                String exitCode = null;
                try {
                    exitCode = requestFileContentsFromEngine(workdirMetadata.getRelativePath() + "/.exitcode");
                } catch (Exception ex) {}

                // get task start time from work dir creation
                LocalDateTime startTimeDate = LocalDateTime.ofInstant(
                    workdirMetadata.getFileAttributes().creationTime().toInstant(),
                    ZoneId.systemDefault()
                );
                String startTime = startTimeDate.format(NEXTFLOW_LOG_DATE_FORMAT);
                
                // get task end time from .exitcode creation
                String completeTime = null;
                try {
                    FileMetadata exitcodeAttributes = getEngineHandler().provideFileAttributes(workdirMetadata.getRelativePath() + "/.exitcode");
                    LocalDateTime completeTimeDate = LocalDateTime.ofInstant(
                        exitcodeAttributes.getFileAttributes().creationTime().toInstant(),
                        ZoneId.systemDefault()
                    );
                    completeTime = completeTimeDate.format(NEXTFLOW_LOG_DATE_FORMAT);
                } catch (Exception ex) {}

                // set all properties of the NextflowTask instance
                NextflowTask nextflowTask = new NextflowTask();
                nextflowTask.setProcess(process);
                nextflowTask.setExitCode(exitCode);
                nextflowTask.setStartTime(startTime);
                nextflowTask.setCompleteTime(completeTime);
                nextflowTask.setAbsoluteWorkdir(workdirMetadata.getAbsolutePath());
                nextflowTask.setRelativeWorkdir(workdirMetadata.getRelativePath());
                return nextflowTask;
                
            })
            .collect(Collectors.toList());
        
        setSortedNextflowTasks(sortedNextflowTasks);
    }

    private List<WesLog> determineTaskLogs() throws Exception {
        List<WesLog> taskLogs = new ArrayList<>();
        for (NextflowTask nextflowTask : getSortedNextflowTasks()) {
            
            String cmd = requestFileContentsFromEngine(Paths.get(nextflowTask.getRelativeWorkdir(), ".command.sh").toString());
            List<String> workdirSplit = Arrays.asList(nextflowTask.getRelativeWorkdir().split("/"));
            List<String> subdirsSplit = workdirSplit.subList(workdirSplit.size() - 2, workdirSplit.size());
            String subdirs = Strings.join(subdirsSplit, '/');
            String logURLPrefix = getLogURLPrefix();
            String logURLSuffix = "/" + getWesRun().getId() + "/" + subdirs;

            WesLog taskLog = new WesLog();
            taskLog.setName(nextflowTask.getProcess());
            if (nextflowTask.getStartTime() != null) {
                taskLog.setStartTime(LocalDateTime.parse(nextflowTask.getStartTime(), NEXTFLOW_LOG_DATE_FORMAT));
            }
            if (nextflowTask.getCompleteTime() != null) {
                taskLog.setEndTime(LocalDateTime.parse(nextflowTask.getCompleteTime(), NEXTFLOW_LOG_DATE_FORMAT));
            }
            
            taskLog.setStdout(logURLPrefix + "/stdout" + logURLSuffix);
            taskLog.setStderr(logURLPrefix + "/stderr" + logURLSuffix);
            if (nextflowTask.getExitCode() != null) {
                taskLog.setExitCode(Integer.parseInt(nextflowTask.getExitCode()));
            }
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
            String workdir = nextflowTask.getRelativeWorkdir();
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
        String firstStartTime = firstNextflowTask.getStartTime();
        if (firstStartTime != null) {
            workflowLog.setStartTime(LocalDateTime.parse(firstStartTime, NEXTFLOW_LOG_DATE_FORMAT));
        }

        NextflowTask finalNextflowTask = getSortedNextflowTasks().get(getSortedNextflowTasks().size()-1);
        String finalEndTime = finalNextflowTask.getCompleteTime();
        if (finalEndTime != null) {
            workflowLog.setEndTime(LocalDateTime.parse(finalEndTime, NEXTFLOW_LOG_DATE_FORMAT));
        }

        String finalExitCode = finalNextflowTask.getExitCode();
        if (finalExitCode != null) {
            workflowLog.setExitCode(Integer.parseInt(finalExitCode));
        }
        
        String logURLPrefix = getLogURLPrefix();
        String logURLSuffix = "/" + getWesRun().getId();
        String logURLQuery = "?workdirs=" + URLEncoder.encode(Strings.join(workdirs, ','), "UTF-8");
        workflowLog.setStdout(logURLPrefix + "/stdout" + logURLSuffix + logURLQuery);
        workflowLog.setStderr(logURLPrefix + "/stderr" + logURLSuffix + logURLQuery);
        return workflowLog;
    }

    private Map<String, String> determineOutputs() throws Exception {
        HashMap<String, String> outputs = new HashMap<>();

        // outputs do not include standard Nextflow files
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
            String testWorkdir = nextflowTask.getAbsoluteWorkdir();

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

    /* ##################################################
       # CONVENIENCE METHODS
       ################################################## */

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
