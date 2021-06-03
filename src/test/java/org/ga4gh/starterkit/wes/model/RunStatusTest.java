package org.ga4gh.starterkit.wes.model;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class RunStatusTest {

    @DataProvider(name = "cases")
    public Object[][] getData() {
        return new Object[][] {
            {
                "bbb3e5db-1335-4ae1-8e8f-c70d3db31bc3",
                State.RUNNING
            },
            {
                "9ff7536c-b4e3-4fbf-9b8d-6fe72023fcaa",
                State.COMPLETE
            }
        };
    }

    private void assertions(RunStatus runStatus, String id, State state) {
        Assert.assertEquals(runStatus.getRunId(), id);
        Assert.assertEquals(runStatus.getState(), state);
    }

    @Test(dataProvider = "cases")
    public void testRunIdNoArgsConstructor(String id, State state) {
        RunStatus runStatus = new RunStatus();
        runStatus.setRunId(id);
        runStatus.setState(state);
        assertions(runStatus, id, state);
    }

    @Test(dataProvider = "cases")
    public void testRunIdAllArgsConstructor(String id, State state) {
        RunStatus runStatus = new RunStatus(id, state);
        assertions(runStatus, id, state);
    }
}
