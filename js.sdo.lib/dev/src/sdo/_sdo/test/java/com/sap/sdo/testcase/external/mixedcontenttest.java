package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class MixedContentTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public MixedContentTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }
    @Test
    public void testClosedMixedText() {
    	_helperContext.getXSDHelper().getGlobalProperty("commonj.sdo","text",true);
        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        assertTrue("returned value is not a type",typeDO instanceof Type);
        typeDO.set("open",false);
        typeDO.set("mixed",true);
        typeDO.set("sequenced",true);
        typeDO.set("uri","com.sap.sdo");
        typeDO.set("name","ClosedMixedContentExample");
        //DataObject baseType = typeDO.createDataObject("baseType");

        Type doType = _helperContext.getTypeHelper().define(typeDO);
        assertTrue("returned value is not a type",doType instanceof Type);

        DataObject dO = _helperContext.getDataFactory().create(doType);
        /*
        DataObject prop = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        prop.set("name", "co");
        prop.set("type", doType);
        DataObject c = _helperContext.getDataFactory().create(doType);
        Property pr = _helperContext.getTypeHelper().defineGlobalProperty(null, prop);
        dO.setDataObject(pr, c);
        assertEquals(dO.toString(), c, dO.getDataObject(pr));
        */

        dO.getSequence().add(0, "mixedText1");
        dO.getSequence().add(0, "mixedText0");
        dO.getSequence().add("mixedText2");
        assertEquals("mixedText0", dO.getSequence().getValue(0));
        assertEquals("mixedText1", dO.getSequence().getValue(1));
        assertEquals("mixedText2", dO.getSequence().getValue(2));
        dO.getSequence().addText(0, "mixedText00");
        dO.getSequence().addText(2, "mixedText05");
        dO.getSequence().addText("mixedText3");
        assertEquals("mixedText00", dO.getSequence().getValue(0));
        assertEquals("mixedText0", dO.getSequence().getValue(1));
        assertEquals("mixedText05", dO.getSequence().getValue(2));
        assertEquals("mixedText1", dO.getSequence().getValue(3));
        assertEquals("mixedText2", dO.getSequence().getValue(4));
        assertEquals("mixedText3", dO.getSequence().getValue(5));
        dO.getSequence().remove(0);
        dO.getSequence().remove(1);
        dO.getSequence().remove(3);
        assertEquals("mixedText0", dO.getSequence().getValue(0));
        assertEquals("mixedText1", dO.getSequence().getValue(1));
        assertEquals("mixedText2", dO.getSequence().getValue(2));
        dO.getSequence().move(2,0);
        assertEquals("mixedText1", dO.getSequence().getValue(0));
        assertEquals("mixedText2", dO.getSequence().getValue(1));
        assertEquals("mixedText0", dO.getSequence().getValue(2));
    }
    @Test
    public void testAddMixedText() {
    	_helperContext.getXSDHelper().getGlobalProperty("commonj.sdo","text",true);
    	DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        assertTrue("returned value is not a type",typeDO instanceof Type);
        typeDO.set("open",true);
        typeDO.set("mixed",true);
        typeDO.set("sequenced",true);
        typeDO.set("uri","com.sap.sdo");
        typeDO.set("name","MixedContentExample");
        //DataObject baseType = typeDO.createDataObject("baseType");

        Type doType = _helperContext.getTypeHelper().define(typeDO);
        assertTrue("returned value is not a type",doType instanceof Type);

        DataObject dO = _helperContext.getDataFactory().create(doType);
        DataObject prop = _helperContext.getDataFactory().create("commonj.sdo", "Property");
        prop.set("name", "co");
        prop.set("type", doType);
        prop.setBoolean("containment", true);
        DataObject c = _helperContext.getDataFactory().create(doType);
        Property pr = _helperContext.getTypeHelper().defineOpenContentProperty(null, prop);
        dO.setDataObject(pr, c);
        assertEquals(dO.toString(), c, dO.getDataObject(pr));

        dO.getSequence().add(1, "mixedText1");
        dO.getSequence().add(0, "mixedText0");
        dO.getSequence().add("mixedText2");
        assertEquals("mixedText0", dO.getSequence().getValue(0));
        //assertEquals(c, dO.getSequence().getValue(1));
        assertEquals("mixedText1", dO.getSequence().getValue(2));
        assertEquals("mixedText2", dO.getSequence().getValue(3));

        dO.getSequence().move(2,1);
        assertEquals("mixedText0", dO.getSequence().getValue(0));
        assertEquals("mixedText1", dO.getSequence().getValue(1));
        assertEquals(c, dO.getSequence().getValue(2));
        assertEquals("mixedText2", dO.getSequence().getValue(3));

        dO.getSequence().move(1,2);
        assertEquals("mixedText0", dO.getSequence().getValue(0));
        assertEquals(c, dO.getSequence().getValue(1));
        assertEquals("mixedText1", dO.getSequence().getValue(2));
        assertEquals("mixedText2", dO.getSequence().getValue(3));

        dO.getSequence().move(3,0);
        assertEquals(c, dO.getSequence().getValue(0));
        assertEquals("mixedText1", dO.getSequence().getValue(1));
        assertEquals("mixedText2", dO.getSequence().getValue(2));
        assertEquals("mixedText0", dO.getSequence().getValue(3));

        dO.getSequence().move(0,3);
        dO.getSequence().move(2,2);
        assertEquals("mixedText0", dO.getSequence().getValue(0));
        assertEquals(c, dO.getSequence().getValue(1));
        assertEquals("mixedText1", dO.getSequence().getValue(2));
        assertEquals("mixedText2", dO.getSequence().getValue(3));

        DataObject c2 = _helperContext.getDataFactory().create(doType);
        dO.getSequence().setValue(1, c2);
        dO.getSequence().setValue(2, "mixedText1'");
        assertEquals("mixedText0", dO.getSequence().getValue(0));
        assertEquals(c2, dO.getSequence().getValue(1));
        assertEquals("mixedText1'", dO.getSequence().getValue(2));
        assertEquals("mixedText2", dO.getSequence().getValue(3));

        assertEquals(1,dO.getInstanceProperties().size());
        //assertEquals(dO.getProperty("text"), dO.getInstanceProperties().get(0));
        assertEquals(pr, dO.getInstanceProperties().get(0));

        try {
            dO.getSequence().add(2, 0, c);
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException e) { //$JL-EXC$
            //expected
        }
        assertEquals("mixedText0", dO.getSequence().getValue(0));
        assertEquals(c2, dO.getSequence().getValue(1));
        assertEquals("mixedText1'", dO.getSequence().getValue(2));
        assertEquals("mixedText2", dO.getSequence().getValue(3));

        dO.getSequence().add(2, "mixedText1");
        dO.getSequence().add("mixedText3");

        assertEquals("mixedText0", dO.getSequence().getValue(0));
        assertEquals(c2, dO.getSequence().getValue(1));
        assertEquals("mixedText1", dO.getSequence().getValue(2));
        assertEquals("mixedText1'", dO.getSequence().getValue(3));
        assertEquals("mixedText2", dO.getSequence().getValue(4));
        assertEquals("mixedText3", dO.getSequence().getValue(5));

        assertEquals(false, dO.isSet("co/co"));

        dO.unset("co");
        assertEquals(false, dO.isSet("co"));

        try {
            assertEquals(false, dO.isSet("co/co"));
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            //expected
        }

        assertEquals("mixedText0", dO.getSequence().getValue(0));
        assertEquals("mixedText1", dO.getSequence().getValue(1));
        assertEquals("mixedText1'", dO.getSequence().getValue(2));
        assertEquals("mixedText2", dO.getSequence().getValue(3));
        assertEquals("mixedText3", dO.getSequence().getValue(4));
    }
}
