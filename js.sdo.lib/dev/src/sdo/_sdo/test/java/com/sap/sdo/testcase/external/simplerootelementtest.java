package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.simple.JavaSimpleType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class SimpleRootElementTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public SimpleRootElementTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    public static final String XSD =
        "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:sdo=\"commonj.sdo/xml\" targetNamespace=\"http://www.sap.com\" xmlns=\"http://www.sap.com\">\n" +
        "    <xsd:element name=\"test\" type=\"xsd:string\"/>\n" +
        "</xsd:schema>";

    public static final String XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<p:test xmlns:p=\"http://www.sap.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
        "    Galaxy\n" +
        "</p:test>";

    @Test
    public void testSimpleRootElement() throws Exception {
        _helperContext.getXSDHelper().define(XSD);
        Property property = _helperContext.getTypeHelper().getOpenContentProperty("http://www.sap.com", "test");
        assertNotNull(property);
        Type expectedType = _helperContext.getTypeHelper().getType("http://www.w3.org/2001/XMLSchema", "string");
        assertEquals(expectedType, property.getType());

        XMLDocument xmlDoc = _helperContext.getXMLHelper().load(XML);

        DataObject data = xmlDoc.getRootObject();
        assertNotNull(data);
        List<Property> props = data.getInstanceProperties();
        assertNotNull(props);
        assertEquals(1, props.size());
        Property value = props.get(0);
        assertEquals("value", value.getName());
        assertEquals(JavaSimpleType.STRING, value.getType());
        assertEquals("Galaxy", data.getString(value));

        DataObject data2 = _helperContext.getXMLHelper().load(
            _helperContext.getXMLHelper().save(
                data, xmlDoc.getRootElementURI(), xmlDoc.getRootElementName())).getRootObject();

        assertNotNull(data2);
        List<Property> props2 = data2.getInstanceProperties();
        assertNotNull(props2);
        assertEquals(1, props2.size());
        Property value2 = props2.get(0);
        assertEquals("value", value2.getName());
        assertEquals(JavaSimpleType.STRING, value2.getType());
        assertEquals("Galaxy", data2.getString(value2));

    }

}
