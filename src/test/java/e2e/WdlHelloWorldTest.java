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
    private static final String WORKFLOW_URL = "https://raw.githubusercontent.com/ga4gh-tech-team/wdl-hello-world/v0.1.0/Dockstore.wdl";
    private static final String WORKFLOW_PARAMS = "{}";
    
    private static final ExpectedLogValues EXP_RUN_LOG = new ExpectedLogValues() {{
        setExpName("helloWorld");
        setExpCmd(new ArrayList<String>(Arrays.asList(
            "echo \"hello world\"",
            "echo \"world\"",
            "echo \"hello\""
        )));
        setExpStdoutMd5(null);
        setExpStderrMd5(null);
        setExpExitCode(0);
    }};

    private static final List<ExpectedLogValues> EXP_TASK_LOGS = new ArrayList<>() {{
        add(new ExpectedLogValues() {{
            // setExpName(expName);
        }});
        add(new ExpectedLogValues() {{
            
        }});
        add(new ExpectedLogValues() {{
            
        }});
    }};

    private static final HashMap<String, String> EXP_OUTPUT_MD5 = new HashMap<>() {{
        put(null, null);
    }};


    @Test
    public void test() throws Exception {
        // runEndToEndTest(WORKFLOW_TYPE, WORKFLOW_TYPE_VERSION, WORKFLOW_URL, WORKFLOW_PARAMS, EXP_RUN_LOG, EXP_TASK_LOGS, EXP_OUTPUT_MD5);
    }
}
