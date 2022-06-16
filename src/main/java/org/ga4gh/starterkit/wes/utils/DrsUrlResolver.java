package org.ga4gh.starterkit.wes.utils;

import java.io.IOException;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ga4gh.starterkit.drs.model.AccessMethod;
import org.ga4gh.starterkit.drs.model.AccessURL;
import org.ga4gh.starterkit.drs.model.DrsObject;

/**
 * Resolves a supplied Data Repository Service (DRS) URL. The WES service is 
 * DRS-aware, in that it understands and attempts to resolve DRS URLs to real
 * https URLs or file paths (ie an AccessMethod).
 */
public class DrsUrlResolver {

    /**
     * Resolves a DRS URL to an access method (URL or file path)
     * @param object workflow run input parameter
     * @return the resolved access URL, or null if one could not be determined
     */
    public static String resolveAccessPathOrUrl(Object object, boolean drsDockerContainer) {
        String resolved = null;
        URI drsUri = toURI(object);
        if (isValidDrsURI(drsUri)) {
            String httpURL = constructHttpUrl(drsUri, drsDockerContainer);
            DrsObject drsObject = loadDrsObject(httpURL);
            if (drsObject != null) {
                return renderAccessPathOrUrl(drsObject);
            }
        }
        return resolved;
    }

    /**
     * Converts an untyped input parameter to a URI 
     * @param object untyped workflow run input parameter
     * @return URI representation of input parameter
     */
    private static URI toURI(Object object) {
        String objString = object.toString();
        return URI.create(objString);
    }

    /**
     * Determine if the constructed URI is a valid DRS URI
     * @param drsUri the URI to be evaluated
     * @return true if a valid DRS URI, false if not
     */
    private static boolean isValidDrsURI(URI drsUri) {
        // URI scheme must be 'drs'
        if (drsUri.getScheme() != null) {
            if (drsUri.getScheme().equals("drs")) {
                // DRS URI paths must not contain additional slashes, ie only the ID
                int drsUriPathLength = drsUri.getPath().split("/").length - 1;
                if (drsUriPathLength == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Constructs the http(s) request to the DRS service
     * @param drsUri the DRS URI
     * @return http(s) URL to access DRSObject endpoint for the given ID
     */
    private static String constructHttpUrl(URI drsUri, boolean drsDockerContainer) {
        StringBuffer httpURLBuffer = new StringBuffer();
        httpURLBuffer.append("http://"); //TODO make https

        if (drsDockerContainer){
            // If wes and drs are both docker containers then localhost in drs object id would refer to wes container.
            // drsDockerContainer flag is used to set localhost to "host.docker.internal" which refers to the actual host machine.
            httpURLBuffer.append("host.docker.internal");
        } else {
            httpURLBuffer.append(drsUri.getHost());
        }
        // check if docker_host_name is provided, if yes, then use docker_host_name
        if (drsUri.getPort() != -1) {
            httpURLBuffer.append(":" + String.valueOf(drsUri.getPort()));
        }
        httpURLBuffer.append("/ga4gh/drs/v1/objects"); // add the canonical DRS Object route
        httpURLBuffer.append(drsUri.getPath());
        return httpURLBuffer.toString();
    }

    /**
     * Make an API call to the DRS service and load a DRSObject from the response
     * @param url http(s) URL to access DRSObject under the given ID
     * @return loaded DrsObject returned by DRS service
     */
    private static DrsObject loadDrsObject(String url) {
        try {
            HttpUriRequest request = new HttpGet(url);
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            String responseBody = new String(entity.getContent().readAllBytes());
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(responseBody, DrsObject.class);
        } catch (IOException ex) {
            return null;
        }
    }

    /**
     * Lookup DrsObject access methods and get a definite URL or file path to the file bytes
     * @param drsObject DrsObject under the requested ID
     * @return file path or URL to file byte location
     */
    private static String renderAccessPathOrUrl(DrsObject drsObject) {
        // TODO for now, client only returns the first valid AccessURL/Path it
        // finds, better to do something more sophisticated here
        for (AccessMethod accessMethod: drsObject.getAccessMethods()) {
            AccessURL accessURL = accessMethod.getAccessUrl();
            URI url = accessURL.getUrl();
            String scheme = url.getScheme();

            switch(scheme) {
                case "file":
                    return url.getRawPath();
                case "s3":
                    return renderS3URL(accessMethod);
            }
        }
        return null;
    }

    private static String renderS3URL(AccessMethod accessMethod) {
        StringBuffer httpURL = new StringBuffer();
        httpURL.append("https://s3");
        
        if (accessMethod.getRegion() != null) {
            httpURL.append("." + accessMethod.getRegion());
        }
        httpURL.append(".amazonaws.com");
        
        URI s3URL = accessMethod.getAccessUrl().getUrl();
        httpURL.append("/" + s3URL.getHost()); // s3 bucket
        httpURL.append(s3URL.getPath()); // s3 key
        
        return httpURL.toString();
    }
}
