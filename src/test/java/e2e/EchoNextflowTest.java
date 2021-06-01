package e2e;

import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.testutils.ExpectedLogValues;
import org.ga4gh.starterkit.wes.testutils.WesE2ERunAndMonitorWorkflow;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Run the 'echo-nf' workflow as an E2E test. Workflow outputs 3 custom messages
 * to stdout and writes to file. This test submits workflow runs and validates
 * outputs and logs
 */
public class EchoNextflowTest extends WesE2ERunAndMonitorWorkflow {

    private static final WorkflowType WORKFLOW_TYPE = WorkflowType.NEXTFLOW;
    private static final String WORKFLOW_TYPE_VERSION = "21.04.0";
    private static final String WORKFLOW_URL = "https://github.com/jb-adams/echo-nf";

    @DataProvider(name = "cases")
    public Object[][] getData() {
        return new Object[][] {
            // workflow run 0
            {
                "{\"message_1\":\"HELLOWORLD\",\"message_2\":\"FOO\",\"message_3\":\"BAR\"}",
                new ExpectedLogValues() {{
                    setExpName("jb-adams/echo-nf");
                    setExpCmd(new ArrayList<String>(Arrays.asList(
                        "#!/bin/bash -ue",
                        "echo \"STDOUT: writing message 1 to message_1.txt\"",
                        "echo \"STDERR: writing message 1 to message_1.txt\" >&2",
                        "echo \"Current message: HELLOWORLD\" > message_1.txt",
                        "#!/bin/bash -ue",
                        "echo \"STDOUT: writing message 2 to message_2.txt\"",
                        "echo \"STDERR: writing message 2 to message_2.txt\" >&2",
                        "echo \"Contents of previous message: `cat message_1.txt`\"",
                        "echo \"Current message: FOO\" > message_2.txt",
                        "#!/bin/bash -ue",
                        "echo \"STDOUT: writing message 3 to message_3.txt\"",
                        "echo \"STDERR: writing message 3 to message_3.txt\" >&2",
                        "echo \"Contents of previous message: `cat message_2.txt`\"",
                        "echo \"Current message: BAR\" > message_3.txt"
                    )));
                    setExpStdoutMd5("a8aaf562ecccb0b877db257d8dff66e4");
                    setExpStderrMd5("5f19d60316bff6cdb924a419e6d2ca39");
                    setExpExitCode(0);
                }},
                new ArrayList<ExpectedLogValues>() {{
                    add(new ExpectedLogValues() {{
                        setExpName("output_message_1");
                        setExpCmd(new ArrayList<String>(Arrays.asList(
                            "#!/bin/bash -ue",
                            "echo \"STDOUT: writing message 1 to message_1.txt\"",
                            "echo \"STDERR: writing message 1 to message_1.txt\" >&2",
                            "echo \"Current message: HELLOWORLD\" > message_1.txt"
                        )));
                        setExpStdoutMd5("4dd961d06916fd0ca9b9db4f41553693");
                        setExpStderrMd5("2785ec9fa04371e914be4a64b307910c");
                        setExpExitCode(0);
                    }});
                    add(new ExpectedLogValues() {{
                        setExpName("output_message_2");
                        setExpCmd(new ArrayList<String>(Arrays.asList(
                            "#!/bin/bash -ue",
                            "echo \"STDOUT: writing message 2 to message_2.txt\"",
                            "echo \"STDERR: writing message 2 to message_2.txt\" >&2",
                            "echo \"Contents of previous message: `cat message_1.txt`\"",
                            "echo \"Current message: FOO\" > message_2.txt"
                        )));
                        setExpStdoutMd5("46bb429850e032880977337362994bd1");
                        setExpStderrMd5("260c7e55c4a5e4c0f3ef4014acdad12d");
                        setExpExitCode(0);
                    }});
                    add(new ExpectedLogValues() {{
                        setExpName("output_message_3");
                        setExpCmd(new ArrayList<String>(Arrays.asList(
                            "#!/bin/bash -ue",
                            "echo \"STDOUT: writing message 3 to message_3.txt\"",
                            "echo \"STDERR: writing message 3 to message_3.txt\" >&2",
                            "echo \"Contents of previous message: `cat message_2.txt`\"",
                            "echo \"Current message: BAR\" > message_3.txt"
                        )));
                        setExpStdoutMd5("8cd93c14c6a2bb5142ce6c3769274311");
                        setExpStderrMd5("94e537de7cf7343e7a834bc8de52d10b");
                        setExpExitCode(0);
                    }});
                }},
                new HashMap<String, String>() {{
                    put("message_1.txt", "9ebb038f0cf3b8c6e0d98b0e7a723aff");
                    put("message_2.txt", "9b9384b69c2f2c2cf653f25b6725d59a");
                    put("message_3.txt", "4fb1af51dbd9aa628a1b34d9e08e6d1b");
                }}
            }
        };
    }

    @Test(dataProvider = "cases")
    public void test(String workflowParams,
        ExpectedLogValues expRunLog, List<ExpectedLogValues> expTaskLogs,
        HashMap<String, String> outputMd5Map
    ) throws Exception {
        runEndToEndTest(WORKFLOW_TYPE, WORKFLOW_TYPE_VERSION, WORKFLOW_URL, workflowParams, expRunLog, expTaskLogs, outputMd5Map);
    }
}
