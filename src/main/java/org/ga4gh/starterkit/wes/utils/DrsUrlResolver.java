package org.ga4gh.starterkit.wes.utils;

import java.net.URI;

public class DrsUrlResolver {

    public static String resolveAccessPathOrUrl(Object object) {
        String resolved = null;
        URI drsUri = toURI(object);
        if (isValidDrsURI(drsUri)) {
            String httpURL = constructHttpUrl(drsUri);
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
        httpURLBuffer.append("/ga4gh/drs/v1");
        httpURLBuffer.append(drsUri.getPath());
        return httpURLBuffer.toString();
    }
}
