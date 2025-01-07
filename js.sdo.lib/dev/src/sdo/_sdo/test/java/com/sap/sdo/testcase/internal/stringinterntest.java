package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.xml.XMLHelperImpl;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.Property;
import commonj.sdo.Type;

public class StringInternTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public StringInternTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testStringInternXml() throws XMLStreamException {
        String xml = "<ns:mySpecialElement ns2:mySpecialAttribute=\"mySpecialValue2\" xmlns:ns=\"mySpecialElementNameSpace\" xmlns:ns2=\"mySpecialAttributeNameSpace\">mySpecialValue1</ns:mySpecialElement>";
        XMLStreamReader xmlStreamReader = XMLHelperImpl.XML_INPUT_FACTORY.createXMLStreamReader(new StringReader(xml));
        while (!xmlStreamReader.isStartElement()) {
            xmlStreamReader.next();
        }
        String elementName = xmlStreamReader.getLocalName();
        String elementUri = xmlStreamReader.getNamespaceURI();
        String attributeName = xmlStreamReader.getAttributeLocalName(0);
        String attributeUri = xmlStreamReader.getAttributeNamespace(0);
        String attributeValue = xmlStreamReader.getAttributeValue(0);
        String elementValue = xmlStreamReader.getElementText();

        checkString("mySpecialElement", elementName);
        checkString("mySpecialElementNameSpace", elementUri);
        checkString("mySpecialValue1", elementValue);
        checkString("mySpecialAttribute", attributeName);
        checkString("mySpecialAttributeNameSpace", attributeUri);
        checkString("mySpecialValue2", attributeValue);

    }

    @Test
    public void testInternJavaClass() {
        checkString("com.sap.sdo.testcase.internal.StringInternTest", getClass().getName());
        checkString("StringInternTest", getClass().getSimpleName());
        checkString("com.sap.sdo.testcase.internal", getClass().getPackage().getName());
    }

    private void checkString(String intern, String xml) {
        assertEquals(intern, xml);
        System.out.println("intern: " + (intern==xml) + ' ' + xml);
    }

    @Test
    public void testXsdHelper() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "DefaultValues.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());
        assertEquals(1, types.size());
        Type type = types.get(0);
        checkString("rootType", type.getName());
        checkString("com.sap.test", type.getURI());
        Property element = (Property)type.getProperties().get(0);
        checkString("elementDefault", element.getName());
        Property attribute = (Property)type.getProperties().get(2);
        checkString("attrDefault", attribute.getName());
    }

}
