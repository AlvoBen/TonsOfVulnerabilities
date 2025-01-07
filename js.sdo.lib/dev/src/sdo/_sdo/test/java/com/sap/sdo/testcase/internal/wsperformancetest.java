package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXmlDocument;
import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class WsPerformanceTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public WsPerformanceTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testLoadSave() throws IOException {
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_TRUE);

        URL wsdlUrl = getClass().getClassLoader().getResource(PACKAGE + "KpiConfigPortType.xml");
        _helperContext.getXMLHelper().load(wsdlUrl.openStream(), wsdlUrl.toString(), options);
        Type longClassType = _helperContext.getTypeHelper().getType("urn:kpi_test/long", "LongClass");
        assertNotNull(longClassType);

        URL xmlUrl = getClass().getClassLoader().getResource(PACKAGE + "output.xml");
        XMLDocument document = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject root = document.getRootObject();
        Object value = root.get("elements/WrapperClass.9/list/ArrayList.3/value");
        assertEquals("simple", value);

        String string = root.getString("elements/WrapperClass.9/list/ArrayList.3");
        assertEquals("simple", string);

        StringWriter output = new StringWriter();
        _helperContext.getXMLHelper().save(document, output, null);
        System.out.println(output.toString());
    }

    @Test
    public void testGenerateAnnotadedSchemas() throws IOException {
        Map options = new HashMap();
        options.put(SapXmlHelper.OPTION_KEY_DEFINE_SCHEMAS, SapXmlHelper.OPTION_VALUE_FALSE);

        URL wsdlUrl = getClass().getClassLoader().getResource(PACKAGE + "KpiConfigPortType.xml");
        SapXmlDocument xmlDocument = (SapXmlDocument)_helperContext.getXMLHelper().load(wsdlUrl.openStream(), wsdlUrl.toString(), options);

        List<DataObject> types = xmlDocument.getDefinedTypes();
        assertFalse(types.isEmpty());
        Set<DataObject> schemas = new HashSet<DataObject>();
        for (DataObject typeObj: types) {
            DataObject xsdRef = typeObj.getDataObject("schemaReference");
            schemas.add(xsdRef.getRootObject());
        }

         for (DataObject schema: schemas) {
            String xsd = _helperContext.getXMLHelper().save(schema, schema.getType().getURI(), "schema");
            System.out.println(xsd);
            System.out.println();
        }
    }
}
