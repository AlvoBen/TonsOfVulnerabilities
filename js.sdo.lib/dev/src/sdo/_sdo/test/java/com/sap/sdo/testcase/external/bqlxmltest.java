package com.sap.sdo.testcase.external;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.helper.XMLDocument;

public class BqlXmlTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public BqlXmlTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
    }

    @Test
    public void testXml() throws IOException {
        final String fileName = "com/sap/sdo/testcase/schemas/result.xml";
        URL url  = getClass().getClassLoader().getResource(fileName);
        XMLDocument document = _helperContext.getXMLHelper().load(url.openStream());
    }

}
