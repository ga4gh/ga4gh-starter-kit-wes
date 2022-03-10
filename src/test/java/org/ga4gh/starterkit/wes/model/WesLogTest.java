package org.ga4gh.starterkit.wes.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test
public class WesLogTest {

    @DataProvider(name = "cases")
    public Object[][] getData() {
        return new Object[][] {
            {
                "echoMessage",
                new ArrayList<String>() {{
                    add("echo \"HELLOWORLD\"");
                    add("echo \"FOO\"");
                    add("echo \"BAR\"");
                }},
                LocalDateTime.now(),
                LocalDateTime.now(),
                "http://localhost:4500/ga4gh/wes/v1/logs/nextflow/stdout/2baa89d5-3ebc-4b5d-9453-e4cbf6aa4ca4",
                "http://localhost:4500/ga4gh/wes/v1/logs/nextflow/stderr/2baa89d5-3ebc-4b5d-9453-e4cbf6aa4ca4",
                Integer.valueOf(0)
            }
        };
    }

    @Test(dataProvider = "cases")
    public void testWesLog(String name, List<String> cmd, LocalDateTime startTime, LocalDateTime endTime, String stdout, String stderr, Integer exitCode) {
        WesLog wesLog = new WesLog();
        wesLog.setName(name);
        wesLog.setCmd(cmd);
        wesLog.setStartTime(startTime);
        wesLog.setEndTime(endTime);
        wesLog.setStdout(stdout);
        wesLog.setStderr(stderr);
        wesLog.setExitCode(exitCode);

        Assert.assertEquals(wesLog.getName(), name);
        Assert.assertEquals(wesLog.getCmd(), cmd);
        Assert.assertEquals(wesLog.getStartTime(), startTime);
        Assert.assertEquals(wesLog.getEndTime(), endTime);
        Assert.assertEquals(wesLog.getStdout(), stdout);
        Assert.assertEquals(wesLog.getStderr(), stderr);
        Assert.assertEquals(wesLog.getExitCode(), exitCode);
    }
}
