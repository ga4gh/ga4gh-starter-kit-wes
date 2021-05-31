package org.ga4gh.starterkit.wes.testutils;

import java.io.FileInputStream;
import java.io.IOException;

public class ResourceLoader {

    public static String load(String resourcePath) throws IOException {
        String filename = ResourceLoader.class.getResource(resourcePath).getFile();
        FileInputStream fs = new FileInputStream(filename);
        byte[] bytes = fs.readAllBytes();
        String json = new String(bytes, "UTF-8");
        fs.close();
        return json;
    }
}
