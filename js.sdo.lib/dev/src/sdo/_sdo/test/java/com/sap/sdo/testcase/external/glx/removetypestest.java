package com.sap.sdo.testcase.external.glx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapTypeHelper;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XSDHelper;

public class RemoveTypesTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public RemoveTypesTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testRemoveMessageMany1() throws IOException {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_many_1K.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        List types = xsdHelper.define(url.openStream(), url.toString());

        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();
        Property rootElement = typeHelper.getOpenContentProperty("http://www.sap.com", "rss");
        assertNotNull(rootElement);
        Set<Type> removedTypes = new HashSet<Type>();
        Set<Property> removedProperties = new HashSet<Property>();
        removedProperties.add(rootElement);
        typeHelper.removeTypesAndProperties(removedTypes, removedProperties);
        assertEquals(0, removedTypes.size());
        assertEquals(1, removedProperties.size());
        rootElement = typeHelper.getOpenContentProperty("http://www.sap.com", "rss");
        assertEquals(null, rootElement);
        rootElement = xsdHelper.getGlobalProperty("http://www.sap.com", "rss", true);
        assertEquals(null, rootElement);
        for (SdoType type: (List<SdoType>)types) {
            Type storedType = typeHelper.getType(type.getURI(), type.getName());
            assertSame(type, storedType);
        }
    }

    @Test
    public void testRemoveMessageMany2() throws IOException {
        URL url = getClass().getClassLoader().getResource(PACKAGE + "message_many_1K.xsd");
        XSDHelper xsdHelper = _helperContext.getXSDHelper();
        List types = xsdHelper.define(url.openStream(), url.toString());

        SapTypeHelper typeHelper = (SapTypeHelper)_helperContext.getTypeHelper();
        Property rootElement = typeHelper.getOpenContentProperty("http://www.sap.com", "rss");
        Type deepType = rootElement.getType().getProperty("channel").getType().getProperty("item").getType().getProperty("guid").getType();
        assertNotNull(rootElement);
        Set<Type> removedTypes = new HashSet<Type>();
        Set<Property> removedProperties = new HashSet<Property>();
        removedTypes.add(deepType);
        typeHelper.removeTypesAndProperties(removedTypes, removedProperties);
        assertEquals(types.size(), removedTypes.size());
        assertTrue(removedTypes.containsAll(types));
        assertEquals(1, removedProperties.size());
        assertTrue(removedProperties.contains(rootElement));
        rootElement = typeHelper.getOpenContentProperty("http://www.sap.com", "rss");
        assertEquals(null, rootElement);
        rootElement = xsdHelper.getGlobalProperty("http://www.sap.com", "rss", true);
        assertEquals(null, rootElement);
        for (SdoType type: (List<SdoType>)types) {
            Type storedType = typeHelper.getType(type.getURI(), type.getName());
            assertEquals(null, storedType);
        }
    }
}
