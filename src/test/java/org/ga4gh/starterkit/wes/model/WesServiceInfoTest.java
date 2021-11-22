package org.ga4gh.starterkit.wes.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.ga4gh.starterkit.wes.config.WesServiceProps;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class WesServiceInfoTest {

    @DataProvider(name = "workflowTypeVersions")
    public Object[][] getWorkflowTypeVersionsCases() {
        return new Object[][] {
            {
                new HashMap<WorkflowType, Set<String>>() {{
                    put(
                        WorkflowType.CWL,
                        new HashSet<String>() {{
                            add("1.0.0");
                            add("2.0.0");
                        }}
                    );
                    put(
                        WorkflowType.WDL,
                        new HashSet<String>() {{
                            add("1.2.3");
                        }}
                    );
                }}
            }
        };
    }

    @DataProvider(name = "workflowEngineVersions")
    public Object[][] getWorkflowEngineVersionsCases() {
        return new Object[][] {
            {
                new HashMap<WorkflowEngine, String>() {{
                    put(WorkflowEngine.NATIVE, "");
                    put(WorkflowEngine.SLURM, "1.2.3");
                }}
            }
        };
    }

    @Test
    public void testDefaultWesServiceInfo() {
        WesServiceInfo si = new WesServiceInfo();
        WesServiceProps wesServiceProps = new WesServiceProps();
        si.updateServiceInfoFromWesServiceProps(wesServiceProps);

        Assert.assertEquals(si.isWorkflowTypeSupported(WorkflowType.NEXTFLOW), true);
        Assert.assertEquals(si.isWorkflowTypeSupported(WorkflowType.CWL), false);
        Assert.assertEquals(si.isWorkflowTypeVersionSupported(WorkflowType.NEXTFLOW, "21.04.0"), true);
        Assert.assertEquals(si.isWorkflowTypeVersionSupported(WorkflowType.NEXTFLOW, "1.0.0"), false);
        Assert.assertEquals(si.isWorkflowTypeVersionSupported(WorkflowType.CWL, "1.0.0"), false);

        Assert.assertEquals(si.isWorkflowEngineSupported(WorkflowEngine.NATIVE), true);
        Assert.assertEquals(si.isWorkflowEngineSupported(WorkflowEngine.SLURM), false);
        Assert.assertEquals(si.isWorkflowEngineVersionSupported(WorkflowEngine.NATIVE, "1.0.0"), true);
        Assert.assertEquals(si.isWorkflowEngineVersionSupported(WorkflowEngine.NATIVE, "2.0.0"), false);
        Assert.assertEquals(si.isWorkflowEngineVersionSupported(WorkflowEngine.SLURM, "1.0.0"), false);
    }

    @Test(dataProvider = "workflowTypeVersions")
    public void testSetWorkflowTypeVersions(HashMap<WorkflowType, Set<String>> workflowTypeVersions) {
        WesServiceInfo si = new WesServiceInfo();
        si.setWorkflowTypeVersions(workflowTypeVersions);
        Assert.assertEquals(si.getWorkflowTypeVersions(), workflowTypeVersions);
    }

    @Test(dataProvider = "workflowEngineVersions")
    public void testSetWorkflowEngineVersions(HashMap<WorkflowEngine, String> workflowEngineVersions) {
        WesServiceInfo si = new WesServiceInfo();
        si.setWorkflowEngineVersions(workflowEngineVersions);
        Assert.assertEquals(si.getWorkflowEngineVersions(), workflowEngineVersions);
    }
}
