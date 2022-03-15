package org.ga4gh.starterkit.wes.testutils;

import org.ga4gh.starterkit.wes.app.WesServer;
import org.ga4gh.starterkit.wes.app.WesServerSpringConfig;
import org.ga4gh.starterkit.wes.controller.Logs;
import org.ga4gh.starterkit.wes.controller.Runs;
import org.ga4gh.starterkit.wes.model.RunId;
import org.ga4gh.starterkit.wes.model.RunLog;
import org.ga4gh.starterkit.wes.model.RunStatus;
import org.ga4gh.starterkit.wes.model.State;
import org.ga4gh.starterkit.wes.model.WesLog;
import org.ga4gh.starterkit.wes.model.WesRequest;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import static org.ga4gh.starterkit.wes.constant.WesApiConstants.WES_API_V1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 * Abstract class for E2E tests. For a single test case, submits a workflow run
 * request to the WES API, and monitors for workflow completion. When the
 * workflow run has completed, assert logs and outputs match expected.
 * This base class should be extended into one child class, per workflow that
 * is being tested. The child class should contain all test cases for that 
 * workflow.
 */
@SpringBootTest
@ContextConfiguration(classes = {
    WesServer.class,
    WesServerSpringConfig.class,
    Runs.class,
    Logs.class
})
@WebAppConfiguration
public abstract class WesE2ERunAndMonitorWorkflow extends AbstractTestNGSpringContextTests {

    private static final String API_PREFIX = WES_API_V1;

    @Autowired
    private WebApplicationContext webAppContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    public void runEndToEndTest(WorkflowType workflowType, 
                                String workflowTypeVersion,
                                String workflowUrl, 
                                String workflowParams,
                                ExpectedLogValues expRunLog, 
                                List<ExpectedLogValues> expTaskLogs,
                                HashMap<String, 
                                String> expOutputMd5Map) throws Exception 
    {
        // submit the workflow
        RunId runId = executePostRequestAndAssert(workflowType, workflowTypeVersion, workflowUrl, workflowParams);

        // poll for status every 5s for workflow completion to maximum of 
        // 12 retries (1min)
        Thread.sleep(5000);
        boolean runIncomplete = true;
        int attempt = 0; 

        RunStatus runStatus = getRunStatus(runId.getRunId());

        while (runIncomplete && attempt < 12) 
        {
            runStatus = getRunStatus(runId.getRunId());

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
        RunLog runLog = getRunLog(runId.getRunId());
        Assert.assertEquals(runLog.getRunId(), runId.getRunId());
        Assert.assertEquals(runLog.getState(), State.COMPLETE);

        // assert 'request' attribute
        WesRequest request = runLog.getRequest();
        Assert.assertEquals(request.getWorkflowType(), workflowType);
        Assert.assertEquals(request.getWorkflowTypeVersion(), workflowTypeVersion);
        Assert.assertEquals(request.getWorkflowUrl(), workflowUrl);

        // assert 'runLog' attribute
        assertWesLogEquivalence(runLog.getRunLog(), expRunLog);

        // assert 'taskLogs' attribute
        Assert.assertEquals(runLog.getTaskLogs().size(), expTaskLogs.size());
        for (int i = 0; i < runLog.getTaskLogs().size(); i++) {
            WesLog taskLog = runLog.getTaskLogs().get(i);
            ExpectedLogValues expTaskLog = expTaskLogs.get(i);
            assertWesLogEquivalence(taskLog, expTaskLog);
        }
        
        // assert 'outputs' attribute
        Map<String, String> outputs = runLog.getOutputs();
        Assert.assertEquals(outputs.size(), expOutputMd5Map.size());
        for (String key : outputs.keySet()) {
            Assert.assertTrue(expOutputMd5Map.containsKey(key));
            assertOutputEquivalence(outputs.get(key), expOutputMd5Map.get(key));
        }
    }

    private RunId executePostRequestAndAssert(WorkflowType workflowType, 
                                              String workflowTypeVersion, 
                                              String workflowUrl, 
                                              String workflowParams) throws Exception 
    {
        MvcResult result = mockMvc.perform(
            post(API_PREFIX + "/runs")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .content(EntityUtils.toString(
                new UrlEncodedFormEntity(
                    Arrays.asList(
                        new BasicNameValuePair("workflow_type", workflowType.toString()),
                        new BasicNameValuePair("workflow_type_version", workflowTypeVersion),
                        new BasicNameValuePair("workflow_url", workflowUrl),
                        new BasicNameValuePair("workflow_params", workflowParams)
                    )
                )
            ))
        )
        .andExpect(status().isOk()) 
        .andReturn();

        RunId runId = objectMapper.readValue(result.getResponse().getContentAsString(), RunId.class);
        Assert.assertNotNull(runId.getRunId());
        return runId;
    }

    private RunStatus getRunStatus(String runId) throws Exception 
    {
        MvcResult result = mockMvc.perform(
            get(API_PREFIX + "/runs/" + runId + "/status")
        )
        .andExpect(status().isOk())
        .andReturn();

        RunStatus runStatus = objectMapper.readValue(result.getResponse().getContentAsString(), RunStatus.class);
        Assert.assertNotNull(runStatus);
        return runStatus;
    }

    private RunLog getRunLog(String runId) throws Exception {
        MvcResult result = mockMvc.perform(
            get(API_PREFIX + "/runs/" + runId)
        )
        .andExpect(status().isOk())
        .andReturn();

        RunLog runLog = objectMapper.readValue(result.getResponse().getContentAsString(), RunLog.class);
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
            String logURLPath = logURI.getPath();
            String logURLQuery = logURI.getQuery();

            MockHttpServletRequestBuilder requestBuilder = get(logURLPath);
            if (logURLQuery != null) {
                requestBuilder.params(parseQueryString(logURLQuery));
            }

            MvcResult result = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();

            return result.getResponse().getContentAsString();
        }
    }

    private LinkedMultiValueMap<String, String> parseQueryString(String queryString) throws Exception {
        LinkedMultiValueMap<String, String> queryMap = new LinkedMultiValueMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            queryMap.add(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return queryMap;
    }

    private void assertWesLogEquivalence(WesLog wesLog, ExpectedLogValues expLogValues) throws Exception {
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
            Assert.assertEquals(stderrMd5, expLogValues.getExpStderrMd5());
        }
    }

    private void assertOutputEquivalence(String outputURLOrContent, String expMd5) throws Exception {
        String md5;

        try {
            URL outputURL = new URL(outputURLOrContent);
            md5 = DigestUtils.md5DigestAsHex(outputURL.openStream().readAllBytes());
        } catch (MalformedURLException ex) {
            md5 = DigestUtils.md5DigestAsHex(outputURLOrContent.getBytes()) ;
        }

        Assert.assertEquals(md5, expMd5);
    }
}
