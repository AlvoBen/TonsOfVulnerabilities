package com.sap.sdo.testcase.internal.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.strategy.pojo.PojoDataObjectFactory;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class PojoDasTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public PojoDasTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testSimplePojo() {
        Type simpleType = _helperContext.getTypeHelper().getType(PojoSimpleIntf.class);
        PojoSimpleIntf simplePojo = new PojoSimple();
        PojoSimpleIntf simpleDO = (PojoSimpleIntf)PojoDataObjectFactory.createDataObject(simpleType, simplePojo);
        simpleDO.setSimpleInt(42);
        simpleDO.getSimpleValues().add("1.");
        simpleDO.getSimpleValues().add(0, "nullter");

        assertEquals(simplePojo.getSimpleInt(), simpleDO.getSimpleInt());
        assertEquals(42, simpleDO.getSimpleInt());

        assertEquals(simplePojo.getSimpleValues(), simpleDO.getSimpleValues());
        assertEquals("nullter", simpleDO.getSimpleValues().get(0));
        assertEquals("1.", simpleDO.getSimpleValues().get(1));
    }

    @Test
    public void testPrefilledSimplePojo() {
        Type simpleType = _helperContext.getTypeHelper().getType(PojoSimpleIntf.class);
        PojoSimpleIntf simplePojo = new PojoSimple();
        simplePojo.setSimpleInt(42);

        simplePojo.setSimpleValues(new ArrayList<String>());
        simplePojo.getSimpleValues().add("1.");
        simplePojo.getSimpleValues().add(0, "nullter");
        PojoSimpleIntf simpleDO = (PojoSimpleIntf)PojoDataObjectFactory.createDataObject(simpleType, simplePojo);

        assertEquals(simplePojo.getSimpleInt(), simpleDO.getSimpleInt());
        assertEquals(42, simpleDO.getSimpleInt());

        assertEquals(simplePojo.getSimpleValues(), simpleDO.getSimpleValues());
        assertEquals("nullter", simpleDO.getSimpleValues().get(0));
        assertEquals("1.", simpleDO.getSimpleValues().get(1));
    }

    @Test
    public void testComplexPojo() {
        Type complexType = _helperContext.getTypeHelper().getType(PojoComplexIntf.class);

        PojoComplexIntf complexPojo = new PojoComplex();
        PojoSimpleIntf simplePojo1 = new PojoSimple();
        PojoSimpleIntf simplePojo2 = new PojoSimple();
        PojoSimpleIntf simplePojo3 = new PojoSimple();

        simplePojo1.setSimpleInt(1);
        simplePojo2.setSimpleInt(2);
        simplePojo3.setSimpleInt(3);

        complexPojo.setComplexValue(simplePojo1);

        complexPojo.setComplexValues(new ArrayList<PojoSimpleIntf>());
        complexPojo.getComplexValues().add(simplePojo2);
        complexPojo.getComplexValues().add(simplePojo3);

        PojoComplexIntf complexDO = (PojoComplexIntf)PojoDataObjectFactory.createDataObject(complexType, complexPojo);
        PojoSimpleIntf simpleDO1 = complexDO.getComplexValue();
        PojoSimpleIntf simpleDO2 = complexDO.getComplexValues().get(0);
        PojoSimpleIntf simpleDO3 = complexDO.getComplexValues().get(1);

        assertSame(complexDO, ((DataObject)simpleDO1).getContainer());
        assertSame(complexDO, ((DataObject)simpleDO2).getContainer());
        assertSame(complexDO, ((DataObject)simpleDO3).getContainer());

        assertEquals(simplePojo1.getSimpleInt(), simpleDO1.getSimpleInt());
        assertEquals(simplePojo2.getSimpleInt(), simpleDO2.getSimpleInt());
        assertEquals(simplePojo3.getSimpleInt(), simpleDO3.getSimpleInt());
    }

}
