package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class ElementAttributeFormDefaultTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ElementAttributeFormDefaultTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testElementQualAttributeUnqual() throws IOException {
        validateXml("com/sap/sdo/testcase/schemas/ElementQualAttributeUnqual.xml",
            "com/sap/sdo/testcase/schemas/ElementQualAttributeUnqual.xsd");
    }

    @Test
    public void testElementQualAttributeQual() throws IOException {
        validateXml("com/sap/sdo/testcase/schemas/ElementQualAttributeQual.xml",
            "com/sap/sdo/testcase/schemas/ElementQualAttributeQual.xsd");
    }

    @Test
    public void testElementUnqualAttributeQual() throws IOException {
        validateXml("com/sap/sdo/testcase/schemas/ElementUnqualAttributeQual.xml",
            "com/sap/sdo/testcase/schemas/ElementUnqualAttributeQual.xsd");
    }

    @Test
    public void testElementUnqualAttributeUnqual() throws IOException {
        validateXml("com/sap/sdo/testcase/schemas/ElementUnqualAttributeUnqual.xml",
            "com/sap/sdo/testcase/schemas/ElementUnqualAttributeUnqual.xsd");
    }

    @Test
    public void testElementQualAttributeQualExtended() throws IOException {
        validateXml("com/sap/sdo/testcase/schemas/ElementQualAttributeQualExtended.xml",
            "com/sap/sdo/testcase/schemas/ElementQualAttributeQualExtended.xsd");
    }

    @Test
    public void testElementQualAttributeQualRestricted() throws IOException {
        validateXml("com/sap/sdo/testcase/schemas/ElementQualAttributeQualRestricted.xml",
            "com/sap/sdo/testcase/schemas/ElementQualAttributeQualRestricted.xsd");
    }

    public void validateXml(String pXmlPath, String pXsdPath) throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(pXsdPath);
        List<Type> types = _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());
        Type aType = null;
        for (Type type: types) {
            if (type.getName().equals("aType")) {
                aType = type;
                break;
            }
        }
        Property refAttribute = aType.getProperty("refAttribute");
        assertNotNull(refAttribute);
        Property refElement = aType.getProperty("refElement");
        assertNotNull(refElement);

        URL xmlUrl = getClass().getClassLoader().getResource(pXmlPath);

        BufferedReader reader = new BufferedReader(new InputStreamReader(xmlUrl.openStream(), "UTF8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
            sb.append('\n');
        }
        reader.close();
        String originalXml = sb.toString();
        XMLDocument document = _helperContext.getXMLHelper().load(originalXml);

        final String expectedSchemaLoc = document.getRootElementURI() + ' ' + pXsdPath.substring(pXsdPath.lastIndexOf('/')+1);
        assertEquals(expectedSchemaLoc, document.getSchemaLocation());

        DataObject rootObject = document.getRootObject();
        assertEquals("Ref", rootObject.getString(refAttribute));
        assertEquals("refref", rootObject.getString(refElement));

        StringWriter stringWriter = new StringWriter();
        _helperContext.getXMLHelper().save(document, stringWriter, null);
        assertLineEquality(originalXml, stringWriter.toString());
    }
}
