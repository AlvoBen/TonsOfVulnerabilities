package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SimpleIntf1;
import com.sap.sdo.testcase.typefac.SimpleNonSeqIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class IsSetSetNullTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public IsSetSetNullTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testSequenced() {
        DataObject d = _helperContext.getDataFactory().create(SimpleIntf1.class);
        SimpleIntf1 s = (SimpleIntf1)d;
        //check if type is sequenced
        assertTrue("Type is not suitable for this test", d.getType().isSequenced());

        Property dataProperty = d.getProperty("data");
        assertEquals("data", dataProperty.getDefault());

        // never set
        assertEquals("data", s.getData());
        assertEquals(false, d.isSet(dataProperty));
        assertEquals(0, d.getSequence().size());


        // set to null but has default
        s.setData(null);
        assertEquals(null, s.getData());
        assertEquals(true, d.isSet(dataProperty));
        assertEquals(1, d.getSequence().size());
        assertEquals(dataProperty, d.getSequence().getProperty(0));
        assertEquals(null, d.getSequence().getValue(0));

        // set to default
        s.setData("data");
        assertEquals("data", s.getData());
        // TODO this is open in spec
        // but I think it is in the sequence and that causes it is set
        assertEquals(true, d.isSet(dataProperty));
        assertEquals(1, d.getSequence().size());
        assertEquals(dataProperty, d.getSequence().getProperty(0));
        assertEquals("data", d.getSequence().getValue(0));

        // unset
        d.unset(dataProperty);
        assertEquals("data", s.getData());
        assertEquals(false, d.isSet(dataProperty));
        assertEquals(0, d.getSequence().size());
    }

    @Test
    public void testSequencedPrimitive() {
        DataObject d = _helperContext.getDataFactory().create(SimpleIntf1.class);
        SimpleIntf1 s = (SimpleIntf1)d;
        //check if type is sequenced
        assertTrue("Type is not suitable for this test", d.getType().isSequenced());

        Property greenProperty = d.getInstanceProperty("green");
        assertEquals(true, greenProperty.getDefault());
        assertEquals(false, d.isSet(greenProperty));
        assertEquals(true, d.getBoolean(greenProperty));

        // spec expects Exception or unset.
        try {
            d.set(greenProperty, null);
            assertEquals(false, d.isSet(greenProperty));
            assertEquals(true, d.getBoolean(greenProperty));
        } catch (RuntimeException e) { //$JL-EXC$
            // expected
        }
        assertEquals(0, d.getSequence().size());
    }

    @Test
    public void testNonSequenced() {
        DataObject d = _helperContext.getDataFactory().create(SimpleNonSeqIntf.class);
        SimpleNonSeqIntf s = (SimpleNonSeqIntf)d;
        //check if type is non-sequenced
        assertFalse("Type is not suitable for this test", d.getType().isSequenced());

        Property dataProperty = d.getProperty("data");
        assertEquals("data", dataProperty.getDefault());

        // never set
        assertEquals("data", s.getData());
        assertEquals(false, d.isSet(dataProperty));

        // set to null but has default
        s.setData(null);
        assertEquals(null, s.getData());
        assertEquals(true, d.isSet(dataProperty));

        // set to default
        s.setData("data");
        assertEquals("data", s.getData());
        // TODO this is open in spec
        // but I think it this should be consitent to the sequenced case
        assertEquals(true, d.isSet(dataProperty));

        // unset
        d.unset(dataProperty);
        assertEquals("data", s.getData());
        assertEquals(false, d.isSet(dataProperty));
    }

    @Test
    public void testNonSequencedPrimitive() {
        DataObject d = _helperContext.getDataFactory().create(SimpleNonSeqIntf.class);
        SimpleNonSeqIntf s = (SimpleNonSeqIntf)d;
        //check if type is non-sequenced
        assertFalse("Type is not suitable for this test", d.getType().isSequenced());

        Property greenProperty = d.getInstanceProperty("green");
        assertEquals(true, greenProperty.getDefault());
        assertEquals(false, d.isSet(greenProperty));
        assertEquals(true, d.getBoolean(greenProperty));

        // spec expects Exception or unset.
        try {
            d.set(greenProperty, null);
            assertEquals(false, d.isSet(greenProperty));
            assertEquals(true, d.getBoolean(greenProperty));
        } catch (RuntimeException e) { //$JL-EXC$
            // expected
        }
    }
}
