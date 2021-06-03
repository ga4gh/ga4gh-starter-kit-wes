package org.ga4gh.starterkit.wes.model;

import java.util.HashMap;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class WesRunTest {

    @DataProvider(name = "cases")
    public Object[][] getData() {
        return new Object[][] {
            {
                "a0698d4b-62ab-408c-9ba1-dbe007d4813e",
                WorkflowType.NEXTFLOW,
                "21.04.0",
                "https://github.com/jb-adams/echo-nf",
                "{\"message_1\":\"HELLOWORLD\",\"message_2\":\"FOO\",\"message_3\":\"BAR\"}",
                WorkflowEngine.NATIVE,
                "",
                new HashMap<Object, Object>() {{
                    put("message_1", "HELLOWORLD");
                    put("message_2", "FOO");
                    put("message_3", "BAR");
                }}
            },
            {
                "2b62f337-4657-49bb-8a3f-4562cd213620",
                WorkflowType.CWL,
                "1.0.0",
                "https://github.com/jb-adams/echo-nf",
                "not valid JSON workflow params",
                WorkflowEngine.NATIVE,
                "",
                null
            }
        };
    }

    @Test(dataProvider = "cases")
    public void testWesRunGetters(String id, WorkflowType workflowType, String workflowTypeVersion, String workflowUrl, String workflowParams, WorkflowEngine workflowEngine, String workflowEngineVersion, Map expWesRequestWorkflowParams) {
        WesRun wesRun = new WesRun();
        wesRun.loadRelations();

        wesRun.setId(id);
        wesRun.setWorkflowType(workflowType);
        wesRun.setWorkflowTypeVersion(workflowTypeVersion);
        wesRun.setWorkflowUrl(workflowUrl);
        wesRun.setWorkflowParams(workflowParams);
        wesRun.setWorkflowEngine(workflowEngine);
        wesRun.setWorkflowEngineVersion(workflowEngineVersion);

        Assert.assertEquals(wesRun.getId(), id);
        Assert.assertEquals(wesRun.getWorkflowType(), workflowType);
        Assert.assertEquals(wesRun.getWorkflowTypeVersion(), workflowTypeVersion);
        Assert.assertEquals(wesRun.getWorkflowUrl(), workflowUrl);
        Assert.assertEquals(wesRun.getWorkflowParams(), workflowParams);
        Assert.assertEquals(wesRun.getWorkflowEngine(), workflowEngine);
        Assert.assertEquals(wesRun.getWorkflowEngineVersion(), workflowEngineVersion);

        RunId runId = wesRun.toRunId();
        Assert.assertEquals(runId.getRunId(), id);

        WesRequest wesRequest = wesRun.toWesRequest();
        Assert.assertEquals(wesRequest.getWorkflowType(), workflowType);
        Assert.assertEquals(wesRequest.getWorkflowTypeVersion(), workflowTypeVersion);
        Assert.assertEquals(wesRequest.getWorkflowUrl(), workflowUrl);
        Assert.assertEquals(wesRequest.getWorkflowParams(), expWesRequestWorkflowParams);
    }
}
