package org.ga4gh.starterkit.wes.controller;

import org.ga4gh.starterkit.wes.app.WesServer;
import org.ga4gh.starterkit.wes.app.WesServerSpringConfig;
import org.ga4gh.starterkit.wes.testutils.ResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.ga4gh.starterkit.wes.constant.WesApiConstants.WES_API_V1;

@SpringBootTest
@ContextConfiguration(classes = {
    WesServer.class,
    WesServerSpringConfig.class,
    WesServiceInfo.class
})
@WebAppConfiguration
public class ServiceInfoTest extends AbstractTestNGSpringContextTests {

    private static final String API_PREFIX = WES_API_V1;

    @Autowired
    private WebApplicationContext webAppContext;

    private MockMvc mockMvc;

    @BeforeMethod
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    }

    private static final String serviceInfoExpFile = "/responses/service-info/show/00.json";

    @Test
    public void testGetServiceInfo() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/service-info"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        String expResponseBody = ResourceLoader.load(serviceInfoExpFile);
        Assert.assertEquals(responseBody, expResponseBody);
    }
}
