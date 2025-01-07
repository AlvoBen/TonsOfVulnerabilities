package com.sap.sdo.testcase.internal.propindex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.Property;
import commonj.sdo.Type;

public class PropIndexAnnoTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public PropIndexAnnoTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testFullIndexAnno() {
        Type fullIndexAnnoType = _helperContext.getTypeHelper().getType(FullIndexAnno.class);
        List<Property> fullIndexAnnoProps = fullIndexAnnoType.getProperties();
        assertEquals("c", fullIndexAnnoProps.get(0).getName());
        assertEquals("a", fullIndexAnnoProps.get(1).getName());
        assertEquals("b", fullIndexAnnoProps.get(2).getName());
    }

    @Test
    public void testHalfIndexAnno() {
        Type halfIndexAnnoType = _helperContext.getTypeHelper().getType(HalfIndexAnno.class);
        List<Property> halfIndexAnnoProps = halfIndexAnnoType.getProperties();
        assertEquals("c", halfIndexAnnoProps.get(0).getName());
        assertEquals("a", halfIndexAnnoProps.get(1).getName());
        assertEquals("b", halfIndexAnnoProps.get(2).getName());
        assertEquals("d", halfIndexAnnoProps.get(3).getName());
    }

    @Test
    public void testNoIndexAnno() {
        Type noIndexAnnoType = _helperContext.getTypeHelper().getType(NoIndexAnno.class);
        List<Property> noIndexAnnoProps = noIndexAnnoType.getProperties();
        assertEquals("a", noIndexAnnoProps.get(0).getName());
        assertEquals("b", noIndexAnnoProps.get(1).getName());
        assertEquals("c", noIndexAnnoProps.get(2).getName());
    }

    @Test
    public void testFullIndexAnnoInherit() {
        Type fullIndexAnnoType = _helperContext.getTypeHelper().getType(FullIndexAnnoInherit.class);
        List<Property> fullIndexAnnoProps = fullIndexAnnoType.getProperties();
        assertEquals("c", fullIndexAnnoProps.get(0).getName());
        assertEquals("a", fullIndexAnnoProps.get(1).getName());
        assertEquals("b", fullIndexAnnoProps.get(2).getName());
        assertEquals("e", fullIndexAnnoProps.get(3).getName());
        assertEquals("d", fullIndexAnnoProps.get(4).getName());
    }

    @Test
    public void testNoIndexAnnoInheritFull() {
        Type noIndexAnnoInheritFullType = _helperContext.getTypeHelper().getType(NoIndexAnnoInheritFull.class);
        List<Property> noIndexAnnoInheritFullProps = noIndexAnnoInheritFullType.getProperties();
        assertEquals("c", noIndexAnnoInheritFullProps.get(0).getName());
        assertEquals("a", noIndexAnnoInheritFullProps.get(1).getName());
        assertEquals("b", noIndexAnnoInheritFullProps.get(2).getName());
        assertEquals("d", noIndexAnnoInheritFullProps.get(3).getName());
        assertEquals("e", noIndexAnnoInheritFullProps.get(4).getName());
    }

    @Test
    public void testHalfIndexAnnoInheritMixed() {
        //this is just a test of the expectet result of the current implementation
        //if a better behavior is specified, feel free to change the test
        Type halfIndexAnnoInheritMixedType = _helperContext.getTypeHelper().getType(HalfIndexAnnoInheritMixed.class);
        List<Property> halfIndexAnnoInheritMixedProps = halfIndexAnnoInheritMixedType.getProperties();
        assertEquals("c", halfIndexAnnoInheritMixedProps.get(0).getName());
        assertEquals("f", halfIndexAnnoInheritMixedProps.get(1).getName());
        assertEquals("a", halfIndexAnnoInheritMixedProps.get(2).getName());
        assertEquals("b", halfIndexAnnoInheritMixedProps.get(3).getName());
        assertEquals("d", halfIndexAnnoInheritMixedProps.get(4).getName());
        assertEquals("e", halfIndexAnnoInheritMixedProps.get(5).getName());
    }

    @Test
    public void testFullIndexAnnoMultibleInherit() {
        //this is just a test of the expectet result of the current implementation
        //if a better behavior is specified, feel free to change the test
        Type fullIndexAnnoMultibleInheritType = _helperContext.getTypeHelper().getType(FullIndexAnnoMultibleInherit.class);
        List<Property> fullIndexAnnoMultibleInheritProps = fullIndexAnnoMultibleInheritType.getProperties();
        assertEquals("c", fullIndexAnnoMultibleInheritProps.get(0).getName());
        assertEquals("a", fullIndexAnnoMultibleInheritProps.get(1).getName());
        assertEquals("b", fullIndexAnnoMultibleInheritProps.get(2).getName());
        assertEquals("e", fullIndexAnnoMultibleInheritProps.get(3).getName());
        assertEquals("d", fullIndexAnnoMultibleInheritProps.get(4).getName());
        assertEquals("g", fullIndexAnnoMultibleInheritProps.get(5).getName());
        assertEquals("f", fullIndexAnnoMultibleInheritProps.get(6).getName());
    }

    @Test
    public void testTooHighIndexAnno() {
        try {
            _helperContext.getTypeHelper().getType(TooHighIndexAnno.class);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            // expected
        }
    }

    @Test
    public void testTooHighIndexAnnoInherit() {
        try {
            _helperContext.getTypeHelper().getType(TooHighIndexAnnoInherit.class);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            // expected
        }
    }

    @Test
    public void testDoubleIndexAnno() {
        try {
            _helperContext.getTypeHelper().getType(DoubleIndexAnno.class);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException e) { //$JL-EXC$
            // expected
        }
    }

}
