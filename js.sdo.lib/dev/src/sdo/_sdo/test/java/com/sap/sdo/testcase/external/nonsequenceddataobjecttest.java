package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.impl.SapHelperProvider;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.NestedMVInner;
import com.sap.sdo.testcase.typefac.OpenInterface;
import com.sap.sdo.testcase.typefac.SimpleIntf1;
import com.sap.sdo.testcase.typefac.SimpleTypesIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public class NonSequencedDataObjectTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public NonSequencedDataObjectTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }


    /**
     * See spec page 20 "Sequenced DataObjects".
     */
    @Test
    public void testIsSequenced() {
        DataObject d = _helperContext.getDataFactory().create(SimpleTypesIntf.class);
        //check if type is not sequenced
        assertFalse("Type is not suitable for this test", d.getType().isSequenced());
        assertNull("Data Object has sequence", d.getSequence());
    }


    @Test
    public void testSetGet() {
        List<String> list = Arrays.asList(new String[] { "one", "two", "three", "four" });

        DataObject d = _helperContext.getDataFactory().create(NestedMVInner.class);
        //check if type is not sequenced
        assertFalse("Type is not suitable for this test", d.getType().isSequenced());
        NestedMVInner dO = (NestedMVInner)d;
        assertFalse(d.isSet("mv"));
        assertFalse(d.isSet("id"));
        assertEquals(null, dO.getId());
        assertEquals(0, dO.getMv().size());
        dO.setId("ID");
        assertEquals("ID", dO.getId());
        dO.setMv(Collections.EMPTY_LIST);
        assertFalse(d.isSet("mv"));
        assertEquals(0, dO.getMv().size());
        dO.setMv(list);
        assertTrue(d.isSet("mv"));
        assertEquals(4, dO.getMv().size());
        List<String> mvList = dO.getMv();
        assertEquals("three", mvList.remove(2));
        assertEquals(3, dO.getMv().size());
        mvList.clear();
        assertFalse(d.isSet("mv"));
        assertEquals(0, dO.getMv().size());
        d.unset("id");
        assertFalse(d.isSet("id"));
    }

    @Test
    public void testSetManyProperty() {
        DataObject data1 = _helperContext.getDataFactory().create(SimpleIntf1.class);
        DataObject data2 = _helperContext.getDataFactory().create(SimpleIntf1.class);
        DataObject openIntf = _helperContext.getDataFactory().create(OpenInterface.class);
        DataObject pData = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        pData.set("name", "b");
        pData.set("type", data1.getType());
        pData.setBoolean("containment", true);
        pData.setBoolean("many", true);
        Property prData = _helperContext.getTypeHelper().defineOpenContentProperty(null, pData);
        assertTrue("generated property is not containment", prData.isContainment());
        assertTrue("generated property is not many", prData.isMany());
        List<DataObject> l = openIntf.getList(prData);
        assertNotNull("getter returned null instead of emtpty list", l);
        assertTrue("initial list of property values is not empty", l.isEmpty());
        l.add(data1);
        try {
            l.add(data1);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            // expected
        }
        l.add(data2);
        assertEquals("parent container association incorrect ", openIntf, data1.getContainer());
        assertEquals("parent container association incorrect ", openIntf, data2.getContainer());
        List datas = openIntf.getList(prData);
        assertEquals("value list is wrong", 2, datas.size());
        for (Object d : datas) {
            assertTrue("list doesn't contain expected objects", d.equals(data1) || d.equals(data2));
        }
        List<SdoProperty> instProperties = openIntf.getInstanceProperties();
        assertNotNull("list of instance properties is null", instProperties);
        assertFalse("list of instance properties is empty", instProperties.isEmpty());
        // just for testing
        // openIntf.setList(prData, openIntf.getList(prData));
        assertTrue(
            "list of instance properties doesn't contain Property 'b': " + instProperties,
            instProperties.contains(prData));
    }

    @Test
    public void testDefaultValues() {
        SimpleIntf1 data = (SimpleIntf1)_helperContext.getDataFactory().create(SimpleIntf1.class);
        assertEquals("default value wasn't returned", "an id", data.getId());
        assertEquals("default value wasn't returned", "data", data.getData());
        assertTrue("default value wasn't returned", data.isGreen());
        assertTrue("default value wasn't returned", data.isBlue());
        List<SdoProperty> properties = ((DataObject)data).getInstanceProperties();
        assertNotNull("instance properties should be in a list", properties);
        assertTrue(
            "list of instance properties should contain at least 5 properties",
            properties.size()>=5);
        // Note: the order of the properties is not defined!
        List<String> propNames = new ArrayList<String>(5);
        propNames.add("name");
        propNames.add("id");
        propNames.add("data");
        propNames.add("green");
        propNames.add("blue");
        assertTrue(propNames.remove(properties.get(0).getName()));
        assertTrue(propNames.remove(properties.get(1).getName()));
        assertTrue(propNames.remove(properties.get(2).getName()));
        assertTrue(propNames.remove(properties.get(3).getName()));
        assertTrue(propNames.remove(properties.get(4).getName()));
    }

    @Test
    public void testNonSequencedSchema() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/nonSequenced.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());
        assertEquals(3, types.size());
        for (Type type: types) {
            assertEquals(type.getName(), false, type.isSequenced());
        }
        String genXsd = _helperContext.getXSDHelper().generate(types);
        HelperContext helperContext = SapHelperProvider.getNewContext();
        List<Type> types2 = helperContext.getXSDHelper().define(genXsd);
        assertEquals(3, types2.size());
        for (Type type: types2) {
            assertEquals(type.getName(), false, type.isSequenced());
        }
        System.out.println(genXsd);
    }
}
