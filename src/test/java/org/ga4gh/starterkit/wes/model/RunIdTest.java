package org.ga4gh.starterkit.wes.model;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class RunIdTest {

    @DataProvider(name = "cases")
    public Object[][] getData() {
        return new Object[][] {
            {
                "bbb3e5db-1335-4ae1-8e8f-c70d3db31bc3"
            },
            {
                "9ff7536c-b4e3-4fbf-9b8d-6fe72023fcaa"
            }
        };
    }

    @Test(dataProvider = "cases")
    public void testRunIdNoArgsConstructor(String id) {
        RunId runId = new RunId();
        runId.setRunId(id);
        Assert.assertEquals(runId.getRunId(), id);
    }

    @Test(dataProvider = "cases")
    public void testRunIdAllArgsConstructor(String id) {
        RunId runId = new RunId(id);
        Assert.assertEquals(runId.getRunId(), id);
    }
}
