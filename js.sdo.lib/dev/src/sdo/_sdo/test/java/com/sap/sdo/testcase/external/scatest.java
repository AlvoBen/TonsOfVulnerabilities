package com.sap.sdo.testcase.external;

import static com.sap.sdo.api.types.PropertyConstants.XSD_TYPE;
import static com.sap.sdo.api.util.URINamePair.DATATYPE_XML_URI;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.TypeHelper;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XSDHelper;

public class ScaTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ScaTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    String PATH = "com/sap/sdo/testcase/schemas/sca10/";

    @Test
    public void testSca10() throws IOException {
        URL bindingScaUrl = getClass().getClassLoader().getResource(PATH + "sca-binding-sca.xsd");
        URL interfaceJavaUrl = getClass().getClassLoader().getResource(PATH + "sca-interface-java.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(bindingScaUrl.openStream(), bindingScaUrl.toString());
        xsdHelper.define(interfaceJavaUrl.openStream(), interfaceJavaUrl.toString());

        TypeHelper typeHelper = _helperContext.getTypeHelper();
        Type serviceType = typeHelper.getType("http://www.osoa.org/xmlns/sca/1.0", "Service");
        DataObject x = _helperContext.getDataFactory().create(serviceType);
        Property bindingProp = serviceType.getProperty("binding");
        x.set(bindingProp, null);

        Property bindingScaProp = typeHelper.getOpenContentProperty("http://www.osoa.org/xmlns/sca/1.0", "binding.sca");
        assertNotNull(bindingScaProp);
        x.set(bindingScaProp, null);

        Property interfaceJavaProp = typeHelper.getOpenContentProperty("http://www.osoa.org/xmlns/sca/1.0", "interface.java");

        DataObject interfaceJava = x.createDataObject(interfaceJavaProp);
        assertNotNull(interfaceJava);
        assertEquals(x, interfaceJava.getContainer());
        interfaceJava.setString("interface", "sample.shop.processes.IOrderProcess");

        String xml = _helperContext.getXMLHelper().save(x, "x", "x");

        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(xml);
        String xml2 = _helperContext.getXMLHelper().save(xmlDocument.getRootObject(), xmlDocument.getRootElementURI(), xmlDocument.getRootElementName());

        assertEquals(xml, xml2);

        System.out.println(xml);
    }

    @Test
    public void testSca10ErrorFile() throws IOException {
        URL bindingScaUrl = getClass().getClassLoader().getResource(PATH + "sca-binding-sca.xsd");
        URL interfaceJavaUrl = getClass().getClassLoader().getResource(PATH + "sca-interface-java.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(bindingScaUrl.openStream(), bindingScaUrl.toString());
        xsdHelper.define(interfaceJavaUrl.openStream(), interfaceJavaUrl.toString());

        URL errorXmlUrl = getClass().getClassLoader().getResource(PATH + "sca10Error.xml");
        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(errorXmlUrl.openStream(), errorXmlUrl.toString(), null);
        String xml = _helperContext.getXMLHelper().save(xmlDocument.getRootObject(), xmlDocument.getRootElementURI(), xmlDocument.getRootElementName());
        System.out.println(xml);
    }

    @Test
    public void testListOfQNames() throws Exception {
        URL coreScaUrl = getClass().getClassLoader().getResource(PATH + "sca-core.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        xsdHelper.define(coreScaUrl.openStream(), coreScaUrl.toString());

        String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<container" +
                " sca:requires=\"sapIntent:project\"" +
                " xmlns:sapIntent=\"http://sap.com/sca/intents\"" +
                " xmlns:sca=\"http://www.osoa.org/xmlns/sca/1.0\"/>\n";
        DataObject container = _helperContext.getXMLHelper().load(xml).getRootObject();
        assertNotNull(container);

        Object requires = container.get("requires");
        assertTrue(requires instanceof List);
        List reqList = (List)requires;
        assertEquals(1, reqList.size());
        assertEquals("http://sap.com/sca/intents#project", reqList.get(0));

        Property xsdType =
            _helperContext.getTypeHelper().getOpenContentProperty(DATATYPE_XML_URI, XSD_TYPE);
        assertNotNull(xsdType);

        Property reqProp = container.getInstanceProperty("requires");
        assertNotNull(reqProp);
        assertEquals(URINamePair.SCHEMA_Q_NAME.toStandardSdoFormat(), reqProp.get(xsdType));
    }
}
