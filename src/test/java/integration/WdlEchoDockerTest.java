package integration;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.ga4gh.starterkit.wes.app.WesServer;
import org.ga4gh.starterkit.wes.app.WesServerSpringConfig;
import org.ga4gh.starterkit.wes.controller.Logs;
import org.ga4gh.starterkit.wes.controller.Runs;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.ga4gh.starterkit.wes.testutils.ExpectedLogValues;
import org.ga4gh.starterkit.wes.model.RunId;
import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesLog;
import org.ga4gh.starterkit.wes.model.WesRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.DigestUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
@ContextConfiguration
(
    classes = 
    {
        WesServer.class,
        WesServerSpringConfig.class,
        Runs.class,
        Logs.class
    }
)
@WebAppConfiguration
public class WdlEchoDockerTest extends AbstractTestNGSpringContextTests
{
    // Define variables and constants
    private static final String REQUEST_URL = "http://localhost:4545/ga4gh/wes/v1/runs";
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

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() throws Exception 
    {
        // submit the workflow
        RunId runId = executePostRequestAndAssert(REQUEST_URL, WORKFLOW_TYPE, WORKFLOW_TYPE_VERSION, WORKFLOW_URL, WORKFLOW_PARAMS);

        // poll for status every 5s for workflow completion to maximum of 
        // 12 retries (1min)
        Thread.sleep(5000);
        boolean runIncomplete = true;
        int attempt = 0; 

        RunStatus runStatus = getRunStatus(REQUEST_URL, runId.getRunId());

        while (runIncomplete && attempt < 12) 
        {
            runStatus = getRunStatus(REQUEST_URL, runId.getRunId());

            if (runStatus.getState().equals(State.COMPLETE)) 
            {
                runIncomplete = false;
            } 
            else if (runStatus.getState().equals(State.EXECUTOR_ERROR)) 
            {
                throw new Exception("workflow run errored unexpectedly");
            }

            Thread.sleep(5000);
            attempt++;
        }

        // throw an error if the run hasn't completed in 1 min
        if (runIncomplete) {
            throw new Exception("workflow run has not completed in expected time frame");
        }
   
        Assert.assertEquals(runStatus.getRunId(), runId.getRunId());
        Assert.assertEquals(runStatus.getState(), State.COMPLETE);

        // retrieve run log and assert run log components
        RunLog runLog = getRunLog(REQUEST_URL, runId.getRunId());
        Assert.assertEquals(runLog.getRunId(), runId.getRunId());
        Assert.assertEquals(runLog.getState(), State.COMPLETE);

        // assert 'request' attribute
        WesRequest request = runLog.getRequest();
        Assert.assertEquals(request.getWorkflowType(), WORKFLOW_TYPE);
        Assert.assertEquals(request.getWorkflowTypeVersion(), WORKFLOW_TYPE_VERSION);
        Assert.assertEquals(request.getWorkflowUrl(), WORKFLOW_URL);

        // assert 'runLog' attribute
        assertWesLogEquivalence(runLog.getRunLog(), EXP_RUN_LOG);

        // assert 'taskLogs' attribute
        Assert.assertEquals(runLog.getTaskLogs().size(), EXP_TASK_LOGS.size());
        for (int i = 0; i < runLog.getTaskLogs().size(); i++) 
        {
            WesLog taskLog = runLog.getTaskLogs().get(i);
            ExpectedLogValues expTaskLog = EXP_TASK_LOGS.get(i);
            assertWesLogEquivalence(taskLog, expTaskLog);
        }
        
        // assert 'outputs' attribute
        Map<String, String> outputs = runLog.getOutputs();
        Assert.assertEquals(outputs.size(), EXP_OUTPUT_MD5.size());
        for (String key : outputs.keySet()) {
            Assert.assertTrue(EXP_OUTPUT_MD5.containsKey(key));
            assertOutputEquivalence(outputs.get(key), EXP_OUTPUT_MD5.get(key));
        }
    }

    private RunId executePostRequestAndAssert(String requestURL,
                                              WorkflowType workflowType, 
                                              String workflowTypeVersion, 
                                              String workflowUrl, 
                                              String workflowParams) throws Exception 
    {
        HttpClient client = HttpClient.newHttpClient();

        // post but with HTTP
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(requestURL))
            .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString()) // not sure if it will work
            .POST
            (
                BodyPublishers.ofString
                (
                    EntityUtils.toString
                    (
                        new UrlEncodedFormEntity
                        (
                            Arrays.asList(new BasicNameValuePair("workflow_type", workflowType.toString()),
                                          new BasicNameValuePair("workflow_type_version", workflowTypeVersion),
                                          new BasicNameValuePair("workflow_url", workflowUrl),
                                          new BasicNameValuePair("workflow_params", workflowParams))
                        )
                    )
                )
            )
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        RunId runId = objectMapper.readValue(response.body(), RunId.class);
        Assert.assertNotNull(runId.getRunId());
        return runId;
    }

    private RunStatus getRunStatus(String requestURL, String runId) throws Exception 
    {
        HttpClient client = HttpClient.newHttpClient();

        Builder requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(requestURL + "/" + runId + "/status"));

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        RunStatus runStatus = objectMapper.readValue(response.body(), RunStatus.class);
        Assert.assertNotNull(runStatus);
        return runStatus;
    }

    private RunLog getRunLog(String requestURL, String runId) throws Exception 
    {
        HttpClient client = HttpClient.newHttpClient();

        Builder requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(requestURL + "/" + runId));

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        
        RunLog runLog = objectMapper.readValue(response.body(), RunLog.class);
        Assert.assertNotNull(runLog);
        return runLog;
    }

    private String getLogOutput(String logURL) throws Exception {

        URI logURI = URI.create(logURL);

        if (logURI.getScheme().equals("file")) {
            String filePath = logURI.getRawPath();
            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            return new String(fileBytes);
        } else {
            HttpClient client = HttpClient.newHttpClient();

            Builder requestBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(logURL));

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            return response.body();
        }
    }

    private LinkedMultiValueMap<String, String> parseQueryString(String queryString) throws Exception 
    {
        LinkedMultiValueMap<String, String> queryMap = new LinkedMultiValueMap<>();
        String[] pairs = queryString.split("&");

        for (String pair : pairs) 
        {
            int idx = pair.indexOf("=");
            queryMap.add(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }

        return queryMap;
    }

    private void assertWesLogEquivalence(WesLog wesLog, ExpectedLogValues expLogValues) throws Exception 
    {
        Assert.assertEquals(wesLog.getName(), expLogValues.getExpName());
        Assert.assertEquals(wesLog.getCmd(), expLogValues.getExpCmd());
        Assert.assertEquals(wesLog.getExitCode(), expLogValues.getExpExitCode());

        // assert md5 sums match expected for stdout
        if (expLogValues.getExpStdoutMd5() != null) {
            String stdout = getLogOutput(wesLog.getStdout());
            String stdoutMd5 = DigestUtils.md5DigestAsHex(stdout.getBytes());
            Assert.assertEquals(stdoutMd5, expLogValues.getExpStdoutMd5());
        }

        // assert md5 sums match expected for stderr
        if (expLogValues.getExpStderrMd5() != null) {
            String stderr = getLogOutput(wesLog.getStderr());
            String stderrMd5 = DigestUtils.md5DigestAsHex(stderr.getBytes());
            Assert.assertEquals(stderrMd5, expLogValues.getExpStderrMd5()); //problem here
        }
    }

    private void assertOutputEquivalence(String outputURLOrContent, String expMd5) throws Exception {
        String md5;

        try {
            URL outputURL = new URL(outputURLOrContent);
            md5 = DigestUtils.md5DigestAsHex(outputURL.openStream().readAllBytes());
        } catch (MalformedURLException ex) {
            md5 = DigestUtils.md5DigestAsHex(outputURLOrContent.getBytes());
        }
        
        Assert.assertEquals(md5, expMd5);
    }
}
