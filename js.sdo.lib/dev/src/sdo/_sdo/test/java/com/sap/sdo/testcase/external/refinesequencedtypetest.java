package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.sap.sdo.api.helper.SapXmlHelper;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.SequencedInheritedIntf;
import com.sap.sdo.testcase.typefac.SequencedParentIntf;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.ChangeSummary.Setting;
import commonj.sdo.helper.XMLDocument;

public class RefineSequencedTypeTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public RefineSequencedTypeTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testRefineInheritedType() throws IOException {
        SequencedParentIntf do1 = (SequencedParentIntf)_helperContext.getDataFactory().create(SequencedParentIntf.class);
        SequencedInheritedIntf do2 = (SequencedInheritedIntf)_helperContext.getDataFactory().create(SequencedInheritedIntf.class);
        do1.setName("Parent");
        do1.getReferenced().add(do2);
        do1.getContained().add(do2);
        do2.setName("Child");
        do2.setMoreInfo("extends SequencedParentIntf");
        String xml = _helperContext.getXMLHelper().save((DataObject)do1, "com.sap.sdo.testcase.typefac", "SequencedParentIntf");

        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(xml);
        StringWriter stringWriter = new StringWriter();
        _helperContext.getXMLHelper().save(xmlDocument, stringWriter, null);

        assertEquals(xml, stringWriter.toString());
    }

    @Test
    public void testRefineInheritedTypeCS() throws IOException {
        DataGraph dataGraph = (DataGraph)_helperContext.getDataFactory().create("commonj.sdo", "DataGraphType");
        Type sequencedParentIntfType = _helperContext.getTypeHelper().getType(SequencedParentIntf.class);
        SequencedParentIntf do1 = (SequencedParentIntf)dataGraph.createRootObject(sequencedParentIntfType);
        SequencedInheritedIntf do2 = (SequencedInheritedIntf)_helperContext.getDataFactory().create(SequencedInheritedIntf.class);
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

        URL storedXml = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/DataGraphSequencedWithFewTypeInfos.xml");

        XMLDocument xmlDocument2 = _helperContext.getXMLHelper().load(storedXml.openStream(), storedXml.toString(), null);
        StringWriter stringWriter2 = new StringWriter();
        _helperContext.getXMLHelper().save(xmlDocument2, stringWriter2, null);

        assertEquals(xml, stringWriter2.toString());
    }

    @Test
    public void testWithoutSimplifyContent() throws Exception {
        Map<String,String> options = new HashMap<String,String>();
        options.put(SapXmlHelper.OPTION_KEY_SIMPLIFY_OPEN_CONTENT, SapXmlHelper.OPTION_VALUE_FALSE);
        URL xmlURL = getClass().getClassLoader().getResource(PACKAGE + "DataGraphSequencedForNonSimplifyOC.xml");
        XMLDocument doc = _helperContext.getXMLHelper().load(xmlURL.openStream(), xmlURL.toString(), options);
        assertNotNull(doc);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        _helperContext.getXMLHelper().save(doc, out, null);

        doc = _helperContext.getXMLHelper().load(new ByteArrayInputStream(out.toByteArray()));
        assertNotNull(doc);
        DataObject root = doc.getRootObject().getDataObject(3);
        assertEquals("SequencedParentIntf", root.getType().getName());
        assertTrue(root instanceof SequencedParentIntf);
        SequencedParentIntf parent = (SequencedParentIntf)root;
        assertEquals("Parent", parent.getName());
        List<SequencedParentIntf> containedList = parent.getContained();
        assertEquals(1, containedList.size());
        SequencedParentIntf contained = containedList.get(0);
        assertSame(contained, parent.getReferenced().get(0));
        assertEquals("Attribute2", contained.getAttribute());
        assertEquals("Child2", contained.getName());
        assertTrue(contained instanceof SequencedInheritedIntf);
        assertEquals("extends SequencedParentIntf", ((SequencedInheritedIntf)contained).getMoreInfo());
        assertEquals("Extra2", ((SequencedInheritedIntf)contained).getExtraAttribute());

        ChangeSummary cs = root.getChangeSummary();
        assertNotNull(cs);
        assertEquals(true, cs.isLogging());

        List<DataObject> changedList = cs.getChangedDataObjects();
        assertEquals(1, changedList.size());
        DataObject changed = changedList.get(0);
        assertEquals(true, cs.isModified(changed));
        assertSame(contained, changed);

        List<Setting> oldContained = cs.getOldValues(changed);
        for (Setting setting : oldContained) {
            String name = setting.getProperty().getName();
            if ("name".equals(name)) {
                assertEquals("Child", setting.getValue());
            } else if ("attribute".equals(name)) {
                assertEquals("Attribute", setting.getValue());
            } else if ("extraAttribute".equals(name)) {
                assertEquals("Extra", setting.getValue());
            } else {
                fail("found unexpected setting: " + name + " = " + setting.getValue());
            }
        }

        Sequence oldSeq = cs.getOldSequence(changed);
        assertNotNull(oldSeq);
        assertEquals(2, oldSeq.size());
        assertEquals("name", oldSeq.getProperty(0).getName());
        assertEquals("Child", oldSeq.getValue(0));
        assertEquals("moreInfo", oldSeq.getProperty(1).getName());
        assertEquals("extends SequencedParentIntf", oldSeq.getValue(1));
    }
}
