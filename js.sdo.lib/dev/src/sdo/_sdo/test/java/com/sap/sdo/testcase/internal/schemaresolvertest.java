/*
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.api.helper.SchemaResolver;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.impl.types.SdoType;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.xml.DefaultSchemaResolver;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;

/**
 * @author D042774
 *
 */
public class SchemaResolverTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public SchemaResolverTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    private final static String SCHEMA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
        + "<v1:schema xmlns:v1=\"http://www.w3.org/2001/XMLSchema\" xmlns:sdo=\"commonj.sdo\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
        + "<v1:complexType name=\"Principal\" v2:name=\"Principal_4664348229E03E0012B311DC95A2003005C5EA9E\" xmlns:v2=\"commonj.sdo/xml\">"
        + "<v1:sequence/>"
        + "</v1:complexType>"
        + "</v1:schema>";

    /* (non-Javadoc)
     * @see com.sap.sdo.testcase.SdoTestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
    }

    class TestSchemaResover extends DefaultSchemaResolver {
        private boolean _alias = false;

        public TestSchemaResover(HelperContext pHelperContext) {
            super(pHelperContext);
        }

        public TestSchemaResover(HelperContext pHelperContext, boolean pAliasVersion) {
            this(pHelperContext);
            _alias = pAliasVersion;
        }

        /* (non-Javadoc)
         * @see com.sap.sdo.impl.xml.DefaultSchemaResolver#resolveImport(java.lang.String, java.lang.String)
         */
        @Override
        public InputStream resolveImport(String pTargetNamespace, String pSchemaLocation) throws IOException, URISyntaxException {
            if ("sap.com/glx/".equals(pSchemaLocation)) {
                return new ByteArrayInputStream(SCHEMA.getBytes("UTF-8"));
            } else if ("http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest".equals(pSchemaLocation)) {
                String resource;
                if (_alias) {
                    resource = PACKAGE+"ProcessLeaveRequestAlias.xsd";
                } else {
                    resource = PACKAGE+"ProcessLeaveRequest.xsd";
                }
                URL uri = getClass().getClassLoader().getResource(resource);
                return uri.openStream();
            }
            return super.resolveImport(pTargetNamespace, pSchemaLocation);
        }
    }

    @Test
    public void testSchemaResolver() throws Exception{
        SchemaResolver resolver = new TestSchemaResover(_helperContext);
        //assertEquals(SCHEMA, resolver.resolveImport(null, "sap.com/glx/").toString());

        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<v1:schema xmlns:v1=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"sap.com/glx/\" xmlns:sdo=\"commonj.sdo\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
            + "<v1:import namespace=\"sap.com/glx/\" schemaLocation=\"sap.com/glx/\" />"
            + "<v1:element name=\"balko8\" type=\"qname01:Principal\" v2:name=\"balko8_466434832A3EFFD012B311DC82E7003005C5EA9E\" xmlns:qname01=\"sap.com/glx/\" xmlns:v2=\"commonj.sdo/xml\"/>"
            + "</v1:schema>";

        List<? extends Type> types = ((SapXsdHelper)_helperContext.getXSDHelper()).define(
            new StringReader(xsd),
            null,
            Collections.singletonMap(SchemaResolver.class.getName(), resolver));
        for (Type type : types) {
            String typeName = type.getName();
            if (typeName.startsWith("Principal")) {
                assertEquals("Principal", ((SdoType)type).getXmlName());
                assertEquals("Principal_4664348229E03E0012B311DC95A2003005C5EA9E", typeName);
            }
        }

        assertEquals(null, _helperContext.getTypeHelper().getOpenContentProperty("sap.com/glx/", "balko8"));
        SdoProperty balkoProp = (SdoProperty)_helperContext.getTypeHelper().getOpenContentProperty("sap.com/glx/", "balko8_466434832A3EFFD012B311DC82E7003005C5EA9E");
        assertNotNull(balkoProp);
        assertNotNull(((TypeHelperImpl)_helperContext.getTypeHelper()).getOpenContentPropertyByXmlName("sap.com/glx/", "balko8", true));
        assertEquals("balko8", balkoProp.getXmlName());

        DataObject balko = _helperContext.getDataFactory().create(types.get(0));

        String xml = _helperContext.getXMLHelper().save(balko, "sap.com/glx/", "balko8");

        assertTrue(xml + " contains " + balkoProp.getName(), xml.indexOf(balkoProp.getName()) < 0);

        XMLDocument doc = _helperContext.getXMLHelper().load(xml);

        assertEquals(balkoProp.getType().getURI(), doc.getRootElementURI());
        assertEquals(balkoProp.getXmlName(), doc.getRootElementName());

        StringWriter xml2 = new StringWriter();
        _helperContext.getXMLHelper().save(doc, xml2, null);

        assertEquals(xml, xml2.toString());

    }

    @Test
    public void testParsing() throws Exception {
        SchemaResolver resolver = new TestSchemaResover(_helperContext);
        //assertEquals(SCHEMA, resolver.resolveImport(null, "sap.com/glx/").toString());

        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<v1:schema xmlns:v1=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"sap.com/glx/\" xmlns:sdo=\"commonj.sdo\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
            + "<v1:import namespace=\"http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest\" schemaLocation=\"http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest\"/>\n"
            + "<v1:element name=\"Context\" type=\"qname01:Context\" v2:name=\"Context_466FB456D231E281198D11DCC12B003005C5EA9E\" xmlns:qname01=\"http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest\" xmlns:v2=\"commonj.sdo\"/>\n"
            + "</v1:schema>";

        List<? extends Type> types = ((SapXsdHelper)_helperContext.getXSDHelper()).define(
            new StringReader(xsd),
            null,
            Collections.singletonMap(SchemaResolver.class.getName(), resolver));
        for (Type type : types) {
            if ("_466FB410A85DE080198D11DC8C79003005C5EA9E".equals(type.getName())) {
                Property prop = type.getProperty("note_466FB410A8764A84198D11DCB339003005C5EA9E");
                assertNotNull(prop);
                assertEquals(
                    "com.sap.dictionary.string_466FB40FA81A95F0198D11DCCB75003005C5EA9E",
                    prop.getType().getName());
            }
        }

    }

    @Test
    public void testParsingAlias() throws Exception {
        SchemaResolver resolver = new TestSchemaResover(_helperContext, true);
        //assertEquals(SCHEMA, resolver.resolveImport(null, "sap.com/glx/").toString());

        String xsd = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<v1:schema xmlns:v1=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"sap.com/glx/\" xmlns:sdo=\"commonj.sdo\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
            + "<v1:import namespace=\"http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest\" schemaLocation=\"http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest\"/>\n"
            + "<v1:element name=\"Context\" type=\"qname01:Context\" v2:aliasName=\"Context_466FB456D231E281198D11DCC12B003005C5EA9E\" xmlns:qname01=\"http://sap.com/tc/bpem/wdui/leavereq/ProcessLeaveRequest\" xmlns:v2=\"commonj.sdo/xml\"/>\n"
            + "</v1:schema>";

        List<? extends Type> types = ((SapXsdHelper)_helperContext.getXSDHelper()).define(
            new StringReader(xsd),
            null,
            Collections.singletonMap(SchemaResolver.class.getName(), resolver));
        boolean found = false;
        for (Type type : types) {
            List<String> aliases = type.getAliasNames();
            if (aliases != null && aliases.contains("_466FB410A85DE080198D11DC8C79003005C5EA9E")) {
                found = true;
                System.out.println(type.getName());
                Property prop = type.getProperty("note_466FB410A8764A84198D11DCB339003005C5EA9E");
                assertNotNull(prop);
                assertEquals("com.sap.dictionary.string", prop.getType().getName());
                assertEquals(
                    "com.sap.dictionary.string_466FB40FA81A95F0198D11DCCB75003005C5EA9E",
                    prop.getType().getAliasNames().get(0));
            }
        }
        assertEquals(true, found);
    }

    @Test
    public void testDefaultSchemaResolver() throws Exception {
        DefaultSchemaResolver resolver = new DefaultSchemaResolver(_helperContext);
        assertNull(resolver.resolveInclude(null));
        assertEquals("absolute/schema/location", resolver.getAbsoluteSchemaLocation("/absolute/schema/location", "/current/absolute/schema/location"));
        assertEquals(null, resolver.resolveInclude(PACKAGE + "nonExisting.xsd"));
    }
    
    @Test
    public void testGetAbsoluteSchemaLocation() throws Exception {
        DefaultSchemaResolver resolver = new DefaultSchemaResolver(_helperContext);

        assertEquals("current/absolute/schema/location/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("imported schema.xsd", "current/absolute/schema/location/wsdl file.wsdl"));
        assertEquals("current/absolute/schema/location/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("./imported schema.xsd", "current/absolute/schema/location/wsdl file.wsdl"));
        assertEquals("current/absolute/schema/location/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("./imported%20schema.xsd", "current/absolute/schema/location/wsdl%20file.wsdl"));
        assertEquals("current/absolute/schema/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("/current/absolute/schema/imported schema.xsd", "current/absolute/schema/location/wsdl%20file.wsdl"));
        assertEquals("current/absolute/schema/location/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("current/absolute/schema/location/imported schema.xsd", "wsdl file.wsdl"));
        assertEquals("current/absolute/schema/location/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("/current/absolute/schema/location/imported schema.xsd", "wsdl file.wsdl"));
        assertEquals("file:/C:/current/absolute/schema/location/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("./imported schema.xsd", "file:/C:/current/absolute/schema/location/wsdl%20file.wsdl"));
        assertEquals("jar:file:/C:/folder/jar-file.jar!/current/absolute/schema/location/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("./imported schema.xsd", "jar:file:/C:/folder/jar-file.jar!/current/absolute/schema/location/wsdl%20file.wsdl"));
        assertEquals("file:/C:/current/absolute/schema/location/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("C:\\current\\absolute\\schema\\location\\imported schema.xsd", null));
        assertEquals("file:/C:/current/absolute/schema/location/imported%20schema.xsd", resolver.getAbsoluteSchemaLocation("./imported schema.xsd", "C:\\current\\absolute\\schema\\location\\wsdl file.wsdl"));

        try {
            resolver.getAbsoluteSchemaLocation("&{???", null);
            fail();
        } catch (URISyntaxException e) {
            // expected
        }

        try {
            resolver.getAbsoluteSchemaLocation("./imported schema.xsd", "&{???");
            fail();
        } catch (URISyntaxException e) {
            // expected
        }

    }

}
