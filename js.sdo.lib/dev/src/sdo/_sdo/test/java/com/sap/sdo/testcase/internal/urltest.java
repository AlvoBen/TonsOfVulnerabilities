package com.sap.sdo.testcase.internal;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

public class UrlTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public UrlTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testUrls() throws URISyntaxException, MalformedURLException {
        String path =
            "file:///C:/perforce/sdo/engine/j2ee.apps/dev/src/sdo/_sdo_test/test/java/com/sap/sdo/testcase/schemas/temp13.xsd";
        URI uri = new URI(path);
        System.out.println(uri);
        System.out.println(uri.toURL());
    }

    @Test
    public void testIncludeSchemas() throws IOException {
        URL url = getClass().getClassLoader().getResource("C:/perforce/sdo/engine/j2ee.apps/dev/src/sdo/_sdo_test/test/java/com/sap/sdo/testcase/schemas/temp11.xsd");
        System.out.println(url.toString());
        _helperContext.getXSDHelper().define(url.openStream(), url.toString());
    }

}
