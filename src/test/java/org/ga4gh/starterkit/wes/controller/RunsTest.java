package org.ga4gh.starterkit.wes.controller;

import org.ga4gh.starterkit.wes.app.WesServer;
import org.ga4gh.starterkit.wes.app.WesServerSpringConfig;
import org.ga4gh.starterkit.wes.model.RunId;
import org.ga4gh.starterkit.wes.model.WorkflowType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.ga4gh.starterkit.wes.constant.WesApiConstants.WES_API_V1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Arrays;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

@SpringBootTest
@ContextConfiguration(classes = {
    WesServer.class,
    WesServerSpringConfig.class,
    Runs.class
})
@WebAppConfiguration
public class RunsTest extends AbstractTestNGSpringContextTests {

    private static final String API_PREFIX = WES_API_V1;

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    @DataProvider(name = "createRunCases")
    public Object[][] getCreateRunCases() {
        return new Object[][] {
            {
                WorkflowType.NEXTFLOW,
                "21.04.0",
                "https://github.com/jb-adams/echo-nf",
                "{\"message_1\":\"HELLOWORLD\",\"message_2\":\"FOO\",\"message_3\":\"BAR\"}"
            }
        };
    }

    @Test(dataProvider = "createRunCases")
    public void testCreateRun(WorkflowType workflowType, String workflowTypeVersion, String workflowUrl, String workflowParams) throws Exception {
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

        ObjectMapper mapper = new ObjectMapper();
        RunId runId = mapper.readValue(result.getResponse().getContentAsString(), RunId.class);
        Assert.assertNotNull(runId.getRunId());
    }
}
