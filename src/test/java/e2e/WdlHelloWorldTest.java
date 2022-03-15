package e2e;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.testutils.ExpectedLogValues;
import org.ga4gh.starterkit.wes.testutils.WesE2ERunAndMonitorWorkflow;
import org.testng.annotations.Test;

public class WdlHelloWorldTest extends WesE2ERunAndMonitorWorkflow {

    private static final WorkflowType WORKFLOW_TYPE = WorkflowType.WDL;
    private static final String WORKFLOW_TYPE_VERSION = "1.0";
    private static final String WORKFLOW_URL = "https://raw.githubusercontent.com/ga4gh-tech-team/wdl-hello-world/v0.1.2/Dockstore.wdl";
    private static final String WORKFLOW_PARAMS = "{}";
    
    private static final ExpectedLogValues EXP_RUN_LOG = new ExpectedLogValues() {{
        setExpName("helloWorld");
        setExpCmd(new ArrayList<String>(Arrays.asList(
            "echo \"hello\"",
            "echo \"hello -> world\"",
            "echo \"hello -> world -> hello world\""
        )));
        setExpStdoutMd5(null);
        setExpStderrMd5(null);
        setExpExitCode(0);
    }};

    private static final List<ExpectedLogValues> EXP_TASK_LOGS = new ArrayList<>() {{
        add(new ExpectedLogValues() {{
            setExpName("helloWorld.sayHello.step-0");
            setExpCmd(new ArrayList<String>(Arrays.asList(
                "echo \"hello\""
            )));
            setExpStdoutMd5("b1946ac92492d2347c6235b4d2611184");
            setExpStderrMd5("d41d8cd98f00b204e9800998ecf8427e");
            setExpExitCode(0);
        }});
        add(new ExpectedLogValues() {{
            setExpName("helloWorld.sayWorld.step-0");
            setExpCmd(new ArrayList<String>(Arrays.asList(
                "echo \"hello -> world\""
            )));
            setExpStdoutMd5("e5eb08dd419a5db46e3be5c8890f7ada");
            setExpStderrMd5("d41d8cd98f00b204e9800998ecf8427e");
            setExpExitCode(0);
        }});
        add(new ExpectedLogValues() {{
            setExpName("helloWorld.sayHelloWorld.step-0");
            setExpCmd(new ArrayList<String>(Arrays.asList(
                "echo \"hello -> world -> hello world\""
            )));
            setExpStdoutMd5("1687d244102eff07cb36a4bb3a282a03");
            setExpStderrMd5("d41d8cd98f00b204e9800998ecf8427e");
            setExpExitCode(0);
        }});
    }};

    private static final HashMap<String, String> EXP_OUTPUT_MD5 = new HashMap<>() {{
        put("helloWorld.sayHello.resultHello", "5d41402abc4b2a76b9719d911017c592");
        put("helloWorld.sayWorld.resultWorld", "021742debd63f28c273de97d689f0f8b");
        put("helloWorld.sayHelloWorld.out", "beee3164bf10906315834eaed6839097");
    }};

    @Test
    public void test() throws Exception {
        runEndToEndTest(WORKFLOW_TYPE, WORKFLOW_TYPE_VERSION, WORKFLOW_URL, WORKFLOW_PARAMS, EXP_RUN_LOG, EXP_TASK_LOGS, EXP_OUTPUT_MD5);
    }
}
