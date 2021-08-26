package org.ga4gh.starterkit.wes.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class RunLogTest {

    @DataProvider(name = "cases")
    public Object[][] getData() {
        return new Object[][] {
            {
                "2baa89d5-3ebc-4b5d-9453-e4cbf6aa4ca4",
                new WesRequest() {{
                    setWorkflowType(WorkflowType.NEXTFLOW);
                    setWorkflowTypeVersion("21.04.0");
                    setWorkflowUrl("https://github.com/jb-adams/echo-nf");
                }},
                State.COMPLETE,
                new WesLog() {{
                    setName("echoMessage");
                    setCmd(new ArrayList<String>() {{
                        add("echo \"HELLOWORLD\"");
                        add("echo \"FOO\"");
                        add("echo \"BAR\"");
                    }});
                    setStartTime(LocalDateTime.now());
                    setEndTime(LocalDateTime.now());
                    setStdout("http://localhost:4500/ga4gh/wes/v1/logs/nextflow/stdout/2baa89d5-3ebc-4b5d-9453-e4cbf6aa4ca4");
                    setStderr("http://localhost:4500/ga4gh/wes/v1/logs/nextflow/stderr/2baa89d5-3ebc-4b5d-9453-e4cbf6aa4ca4");
                    setExitCode(0);
                }},
                new ArrayList<WesLog>() {{
                    add(new WesLog() {{
                        setName("echoMessage1");
                        setCmd(new ArrayList<String>() {{
                            add("echo \"HELLOWORLD\"");
                        }});
                        setStartTime(LocalDateTime.now());
                        setEndTime(LocalDateTime.now());
                        setStdout("http://localhost:4500/ga4gh/wes/v1/logs/nextflow/stdout/2baa89d5-3ebc-4b5d-9453-e4cbf6aa4ca4/ab/cdef");
                        setStderr("http://localhost:4500/ga4gh/wes/v1/logs/nextflow/stderr/2baa89d5-3ebc-4b5d-9453-e4cbf6aa4ca4/gh/ijkl");
                        setExitCode(0);
                    }});
                }},
                new HashMap<String, String>() {{
                    put("file_1", "/path/to/file1.txt");
                    put("file_2", "/path/to/file2.txt");
                    put("file_3", "/path/to/file3.txt");
                }}
            }
        };
    }

    @Test(dataProvider = "cases")
    public void testRunLog(String runId, WesRequest request, State state, WesLog runLog, List<WesLog> taskLogs, Map<String, String> outputs) {
        RunLog log = new RunLog();
        log.setRunId(runId);
        log.setRequest(request);
        log.setState(state);
        log.setRunLog(runLog);
        log.setTaskLogs(taskLogs);
        log.setOutputs(outputs);

        Assert.assertEquals(log.getRunId(), runId);
        Assert.assertEquals(log.getRequest(), request);
        Assert.assertEquals(log.getState(), state);
        Assert.assertEquals(log.getRunLog(), runLog);
        Assert.assertEquals(log.getTaskLogs(), taskLogs);
        Assert.assertEquals(log.getOutputs(), outputs);
    }
}
