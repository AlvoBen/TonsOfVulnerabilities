package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.NonSequencedInheritedIntf;
import com.sap.sdo.testcase.typefac.NonSequencedParentIntf;

import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class RefineNonSequencedTypeTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public RefineNonSequencedTypeTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testRefineInheritedType() throws IOException {
        NonSequencedParentIntf do1 = (NonSequencedParentIntf)_helperContext.getDataFactory().create(NonSequencedParentIntf.class);
        NonSequencedInheritedIntf do2 = (NonSequencedInheritedIntf)_helperContext.getDataFactory().create(NonSequencedInheritedIntf.class);
        do1.setName("Parent");
        do1.getReferenced().add(do2);
        do1.getContained().add(do2);
        do2.setName("Child");
        do2.setMoreInfo("extends NonSequencedParentIntf");
        String xml = _helperContext.getXMLHelper().save((DataObject)do1, "com.sap.sdo.testcase.typefac", "SequencedParentIntf");

        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(xml);
        StringWriter stringWriter = new StringWriter();
        _helperContext.getXMLHelper().save(xmlDocument, stringWriter, null);

        assertEquals(xml, stringWriter.toString());
        System.out.println(xml);
    }

    @Test
    public void testRefineInheritedTypeCS() throws IOException {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo", "DataGraphType");
        Type nonSequencedParentIntfType = _helperContext.getTypeHelper().getType(NonSequencedParentIntf.class);
        NonSequencedParentIntf do1 = (NonSequencedParentIntf)dataGraph.createRootObject(nonSequencedParentIntfType);
        NonSequencedInheritedIntf do2 = (NonSequencedInheritedIntf)_helperContext.getDataFactory().create(NonSequencedInheritedIntf.class);
        do1.setName("Parent");
        do1.getReferenced().add(do2);
        do1.getContained().add(do2);
        do2.setName("Child");
        do2.setMoreInfo("extends SequencedParentIntf");
        do2.setAttribute("Attribute");
        do2.setExtraAttribute("Extra");
        dataGraph.getChangeSummary().beginLogging();
        do2.setName("Child2");
        do2.setAttribute("Attribute2");
        do2.setExtraAttribute("Extra2");

        ((DataObject)dataGraph).createDataObject(0);
        String xml = _helperContext.getXMLHelper().save((DataObject)dataGraph, "commonj.sdo", "datagraph");


        System.out.println(xml);
        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(xml);
        StringWriter stringWriter = new StringWriter();
        _helperContext.getXMLHelper().save(xmlDocument, stringWriter, null);

        assertEquals(xml, stringWriter.toString());

        URL storedXml = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/DataGraphNonSequencedWithFewTypeInfos.xml");

        XMLDocument xmlDocument2 = _helperContext.getXMLHelper().load(storedXml.openStream(), storedXml.toString(), null);
        StringWriter stringWriter2 = new StringWriter();
        _helperContext.getXMLHelper().save(xmlDocument2, stringWriter2, null);

        assertEquals(xml, stringWriter2.toString());


    }
}
