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

public class DrsUrlResolver {

    public static String resolveAccessPathOrUrl(Object object) {
        String resolved = null;
        URI drsUri = toURI(object);
        if (isValidDrsURI(drsUri)) {
            String httpURL = constructHttpUrl(drsUri);
            DrsObject drsObject = loadDrsObject(httpURL);
            if (drsObject != null) {
                return renderAccessPathOrUrl(drsObject);
            }
        }
        return resolved;
    }

    private static URI toURI(Object object) {
        String objString = object.toString();
        return URI.create(objString);
    }

    private static boolean isValidDrsURI(URI drsUri) {
        if (drsUri.getScheme() != null) {
            if (drsUri.getScheme().equals("drs")) {
                int drsUriPathLength = drsUri.getPath().split("/").length - 1;
                if (drsUriPathLength == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String constructHttpUrl(URI drsUri) {
        StringBuffer httpURLBuffer = new StringBuffer();
        httpURLBuffer.append("http://");
        httpURLBuffer.append(drsUri.getHost());
        if (drsUri.getPort() != -1) {
            httpURLBuffer.append(":" + String.valueOf(drsUri.getPort()));
        }
        httpURLBuffer.append("/ga4gh/drs/v1/objects");
        httpURLBuffer.append(drsUri.getPath());
        return httpURLBuffer.toString();
    }

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

    private static String renderAccessPathOrUrl(DrsObject drsObject) {
        // TODO for now, client only returns the first valid AccessURL/Path it
        // finds, better to do something more sophisticated here
        for (AccessMethod accessMethod: drsObject.getAccessMethods()) {
            AccessURL accessURL = accessMethod.getAccessUrl();
            URI url = accessURL.getUrl();
            if (url.getScheme().equals("file")) {
                return url.getRawPath();
            };
            return url.toString();
        }
        return null;
    }
}
