package e2e;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.testutils.ExpectedLogValues;
import org.ga4gh.starterkit.wes.testutils.WesE2ERunAndMonitorWorkflow;
import org.testng.annotations.Test;

public class WdlEchoTest extends WesE2ERunAndMonitorWorkflow {

    private static final WorkflowType WORKFLOW_TYPE = WorkflowType.WDL;
    private static final String WORKFLOW_TYPE_VERSION = "1.0";
    private static final String WORKFLOW_URL = "https://raw.githubusercontent.com/ga4gh-tech-team/wdl-echo/v0.1.1/Dockstore.wdl";
    private static final String WORKFLOW_PARAMS = "{\"echo.message1\":\"FOO\",\"echo.message2\":\"BAR\",\"echo.message3\":\"HELLOWORLD\"}";
    
    private static final ExpectedLogValues EXP_RUN_LOG = new ExpectedLogValues() {{
        setExpName("echo");
        setExpCmd(new ArrayList<String>(Arrays.asList(
            "echo \"STDOUT: writing message 1 to message_1.txt\"",
            "echo \"STDERR: writing message 1 to message_1.txt\" >&2",
            "echo \"Current message: FOO\" > message_1.txt",
            "echo \"STDOUT: writing message 2 to message_2.txt\"",
            "echo \"STDERR: writing message 2 to message_2.txt\" >&2",
            "echo \"Current message: BAR\" > message_2.txt",
            "echo \"STDOUT: writing message 3 to message_3.txt\"",
            "echo \"STDERR: writing message 3 to message_3.txt\" >&2",
            "echo \"Current message: HELLOWORLD\" > message_3.txt"
        )));
        setExpStdoutMd5(null);
        setExpStderrMd5(null);
        setExpExitCode(0);
    }};

    private static final List<ExpectedLogValues> EXP_TASK_LOGS = new ArrayList<>() {{
        add(new ExpectedLogValues() {{
            setExpName("echo.echoMessage1.step-0");
            setExpCmd(new ArrayList<String>(Arrays.asList(
                "echo \"STDOUT: writing message 1 to message_1.txt\"",
                "echo \"STDERR: writing message 1 to message_1.txt\" >&2",
                "echo \"Current message: FOO\" > message_1.txt"
            )));
            setExpStdoutMd5("4dd961d06916fd0ca9b9db4f41553693");
            setExpStderrMd5("2785ec9fa04371e914be4a64b307910c");
            setExpExitCode(0);
        }});
        add(new ExpectedLogValues() {{
            setExpName("echo.echoMessage2.step-0");
            setExpCmd(new ArrayList<String>(Arrays.asList(
                "echo \"STDOUT: writing message 2 to message_2.txt\"",
                "echo \"STDERR: writing message 2 to message_2.txt\" >&2",
                "echo \"Current message: BAR\" > message_2.txt"
            )));
            setExpStdoutMd5("1027020dbc11c3777c62f129449ca779");
            setExpStderrMd5("260c7e55c4a5e4c0f3ef4014acdad12d");
            setExpExitCode(0);
        }});
        add(new ExpectedLogValues() {{
            setExpName("echo.echoMessage3.step-0");
            setExpCmd(new ArrayList<String>(Arrays.asList(
                "echo \"STDOUT: writing message 3 to message_3.txt\"",
                "echo \"STDERR: writing message 3 to message_3.txt\" >&2",
                "echo \"Current message: HELLOWORLD\" > message_3.txt"
            )));
            setExpStdoutMd5("01fa750dedd581c295f9e74d28f93a8b");
            setExpStderrMd5("94e537de7cf7343e7a834bc8de52d10b");
            setExpExitCode(0);
        }});
    }};

    private static final HashMap<String, String> EXP_OUTPUT_MD5 = new HashMap<>() {{
        put("echo.echoMessage1.done", "b326b5062b2f0e69046810717534cb09");
        put("echo.echoMessage2.done", "b326b5062b2f0e69046810717534cb09");
        put("echo.echoMessage3.done", "b326b5062b2f0e69046810717534cb09");
        put("echo.echoMessage1.message1_output", "9b9384b69c2f2c2cf653f25b6725d59a");
        put("echo.echoMessage2.message2_output", "4fb1af51dbd9aa628a1b34d9e08e6d1b");
        put("echo.echoMessage3.message3_output", "9ebb038f0cf3b8c6e0d98b0e7a723aff");
    }};

    @Test
    public void test() throws Exception {
        runEndToEndTest(WORKFLOW_TYPE, WORKFLOW_TYPE_VERSION, WORKFLOW_URL, WORKFLOW_PARAMS, EXP_RUN_LOG, EXP_TASK_LOGS, EXP_OUTPUT_MD5);
    }
}
