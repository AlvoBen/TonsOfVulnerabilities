package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.helper.XMLDocument;

public class Jira224ListAutoWrappingTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public Jira224ListAutoWrappingTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testGetters() throws IOException {
        URL xmlUrl = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/ElementQualAttributeUnqual.xml");
        XMLDocument document = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        DataObject dataGraph = _helperContext.getDataFactory().create(URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());
        Property rootProp = _helperContext.getTypeHelper().getOpenContentProperty("com.sap.test.eqau", "aElement");
        assertEquals(true, rootProp.isMany());
        final DataObject rootObject = document.getRootObject();

        assertEquals(Collections.emptyList(), dataGraph.get(rootProp));
        assertEquals(null, dataGraph.getDataObject(rootProp));

        dataGraph.set(rootProp, Collections.singletonList(rootObject));
        assertEquals(Collections.singletonList(rootObject), dataGraph.get(rootProp));
        assertSame(rootObject, dataGraph.getDataObject(rootProp));

    }

    @Test
    public void testSetters() throws IOException {
        URL xmlUrl = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/ElementQualAttributeUnqual.xml");
        XMLDocument document = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);
        DataObject dataGraph = _helperContext.getDataFactory().create(URINamePair.DATAGRAPH_TYPE.getURI(), URINamePair.DATAGRAPH_TYPE.getName());
        Property rootProp = _helperContext.getTypeHelper().getOpenContentProperty("com.sap.test.eqau", "aElement");
        assertEquals(true, rootProp.isMany());
        final DataObject rootObject = document.getRootObject();

        dataGraph.setDataObject(rootProp, rootObject);
        assertEquals(Collections.singletonList(rootObject), dataGraph.get(rootProp));
        assertSame(rootObject, dataGraph.getDataObject(rootProp));

    }
}
