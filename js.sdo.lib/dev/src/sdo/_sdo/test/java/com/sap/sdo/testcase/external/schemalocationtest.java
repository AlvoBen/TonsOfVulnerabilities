package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.helper.XMLDocument;

public class SchemaLocationTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public SchemaLocationTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private static final String FILE_NAME =
        "com/sap/sdo/testcase/schemas/ElementUnqualAttributeUnqual.xml";

    @Test
    public void testSchemaLocation() throws IOException {

        URL url = getClass().getClassLoader().getResource(FILE_NAME);
        XMLDocument document = _helperContext.getXMLHelper().load(url.openStream(), url.toString(), null);

        StringWriter stringWriter = new StringWriter();
        _helperContext.getXMLHelper().save(document, stringWriter, null);
        String xml = stringWriter.toString();
        assertTrue(xml, xml.contains("xsi:schemaLocation=\"com.sap.test.euau ElementUnqualAttributeUnqual.xsd\""));

    }

}
