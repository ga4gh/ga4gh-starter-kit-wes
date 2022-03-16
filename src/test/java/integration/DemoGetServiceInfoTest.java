package integration;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;

import java.io.File;
import java.nio.file.Path;  
import java.nio.file.Paths;

import org.json.JSONObject;
import org.json.JSONArray;

import org.ga4gh.starterkit.wes.testutils.ResourceLoader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DemoGetServiceInfoTest
{
    // Define variables and constants
    private static final String DEFAULT_PUBLIC_URL = "http://localhost:4500/ga4gh/wes/v1/";
    private static final String CUSTOM_PUBLIC_URL = "http://localhost:7000/ga4gh/wes/v1/";

    private static final String serviceInfoExpFile = "/responses/service-info/show/";
    
    @Test
    public void testDemoGetServiceInfo() throws Exception 
    {
        HttpClient client = HttpClient.newHttpClient();
        String requestURL = DEFAULT_PUBLIC_URL + "service-info";

        Builder requestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(requestURL)); //Ask for the object

        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

        String responseBody = response.body();
        String expResponseBody = ResourceLoader.load(serviceInfoExpFile + "00.json");
        Assert.assertEquals(responseBody, expResponseBody);

        //////////////////////////////

        String customRequestURL = CUSTOM_PUBLIC_URL + "service-info";

        Builder customRequestBuilder = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create(customRequestURL)); //Ask for the object

        HttpRequest customRequest = customRequestBuilder.build();
        HttpResponse<String> customResponse = client.send(customRequest, BodyHandlers.ofString());

        String customResponseBody = customResponse.body();
        String customExpResponseBody = ResourceLoader.load(serviceInfoExpFile + "01.json");
        Assert.assertEquals(customResponseBody, customExpResponseBody);
    }
}
