package com.sap.sdo.testcase.internal.pojo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.objects.strategy.pojo.PojoDataObjectFactory;
import com.sap.sdo.testcase.AbstractDataGraphTest;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class PojoChangeSummaryTest extends AbstractDataGraphTest {

    /**
     * @param pHelperContext
     */
    public PojoChangeSummaryTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testComplexPojo() {

        Type complexType = _helperContext.getTypeHelper().getType(PojoComplexIntf.class);
        Type simpleType = _helperContext.getTypeHelper().getType(PojoSimpleIntf.class);

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
        DataGraph dataGraph = getDataGraph("root", (DataObject)complexDO);

        DataObject complexDOcopy = _helperContext.getCopyHelper().copy((DataObject)complexDO);

        dataGraph.getChangeSummary().beginLogging();

        DataObject simpleDO1 = (DataObject)complexDO.getComplexValue();
        assertTrue(simpleDO1.getChangeSummary().isLogging());
        ((PojoSimpleIntf)simpleDO1).setSimpleInt(5);

        assertEquals(1, dataGraph.getChangeSummary().getOldValue(simpleDO1, simpleDO1.getProperty("simpleInt")).getValue());

        DataObject simpleDO2 = (DataObject)complexDO.getComplexValues().get(0);
        simpleDO2.delete();

        String xml = _helperContext.getXMLHelper().save((DataObject)dataGraph, null, "datagraph");
        System.out.println(xml);

        dataGraph.getChangeSummary().undoChanges();

        assertTrue(_helperContext.getEqualityHelper().equal(complexDOcopy, (DataObject)complexDO));
    }

}
