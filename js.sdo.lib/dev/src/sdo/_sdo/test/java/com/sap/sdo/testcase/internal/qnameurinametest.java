package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.QNameURINamePairIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class QNameURINameTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public QNameURINameTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testQName() {
        DataObject dataObject = _helperContext.getDataFactory().create(QNameURINamePairIntf.class);
        QNameURINamePairIntf intf = (QNameURINamePairIntf)dataObject;

        QName qName = new QName("com.sap.test", "aLocalName");

        intf.setQName(qName);
        assertEquals(qName, intf.getQName());

        Property qNameProp = dataObject.getInstanceProperty("qName");

        assertEquals("URI", qNameProp.getType().getName());

        assertEquals(qName.getNamespaceURI() + '#' + qName.getLocalPart(), dataObject.getString(qNameProp));
    }

    @Test
    public void testURINamePair() {
        DataObject dataObject = _helperContext.getDataFactory().create(QNameURINamePairIntf.class);
        QNameURINamePairIntf intf = (QNameURINamePairIntf)dataObject;

        URINamePair uriNamePair = new URINamePair("com.sap.test", "aLocalName");

        intf.setURINamePair(uriNamePair);
        assertEquals(uriNamePair, intf.getURINamePair());

        Property qNameProp = dataObject.getInstanceProperty("uRINamePair");

        assertEquals("URI", qNameProp.getType().getName());

        assertEquals(uriNamePair.toStandardSdoFormat(), dataObject.getString(qNameProp));
    }

    @Test
    public void testQNames() {
        DataObject dataObject = _helperContext.getDataFactory().create(QNameURINamePairIntf.class);
        QNameURINamePairIntf intf = (QNameURINamePairIntf)dataObject;

        QName qName1 = new QName("com.sap.test", "aLocalName1");
        QName qName2 = new QName("com.sap.test", "aLocalName2");
        QName qName3 = new QName("com.sap.test", "aLocalName3");

        List<QName> qNames = new ArrayList(3);
        qNames.add(qName1);
        qNames.add(qName2);
        qNames.add(qName3);

        intf.setQNames(qNames);

        Property qNameProp = dataObject.getInstanceProperty("qNames");

        assertEquals("URI", qNameProp.getType().getName());

        final List<QName> storedQNames = intf.getQNames();
        assertEquals(qNames.size(), storedQNames.size());

        assertEquals(qName1, storedQNames.get(0));
        assertEquals(qName2, storedQNames.get(1));
        assertEquals(qName3, storedQNames.get(2));

        assertEquals(qNames, storedQNames);

        List<String> internalList = dataObject.getList(qNameProp);
        assertEquals(qNames.size(), internalList.size());

        assertEquals(qName1.getNamespaceURI()+'#'+qName1.getLocalPart(), internalList.get(0));
        assertEquals(qName2.getNamespaceURI()+'#'+qName2.getLocalPart(), internalList.get(1));
        assertEquals(qName3.getNamespaceURI()+'#'+qName3.getLocalPart(), internalList.get(2));

        QName qName15 = new QName("com.sap.test", "aLocalName15");

        storedQNames.add(1, qName15);
        assertEquals(storedQNames.size(), internalList.size());
        assertEquals(qName15.getNamespaceURI()+'#'+qName15.getLocalPart(), internalList.get(1));

        QName qName4 = new QName("com.sap.test", "aLocalName4");
        QName replaced = storedQNames.set(3, qName4);
        assertEquals(qName3, replaced);

        assertEquals(storedQNames.size(), internalList.size());
        assertEquals(qName4.getNamespaceURI()+'#'+qName4.getLocalPart(), internalList.get(3));

        QName removed = storedQNames.remove(0);
        assertEquals(qName1, removed);

        assertEquals(storedQNames.size(), internalList.size());
    }

    @Test
    public void testURINamePairs() {
        DataObject dataObject = _helperContext.getDataFactory().create(QNameURINamePairIntf.class);
        QNameURINamePairIntf intf = (QNameURINamePairIntf)dataObject;

        URINamePair uriNamePair1 = new URINamePair("com.sap.test", "aLocalName1");
        URINamePair uriNamePair2 = new URINamePair("com.sap.test", "aLocalName2");
        URINamePair uriNamePair3 = new URINamePair("com.sap.test", "aLocalName3");

        List<URINamePair> uriNamePairs = new ArrayList(3);
        uriNamePairs.add(uriNamePair1);
        uriNamePairs.add(uriNamePair2);
        uriNamePairs.add(uriNamePair3);

        intf.setURINamePairs(uriNamePairs);

        Property uriNamePairProp = dataObject.getInstanceProperty("uRINamePairs");

        assertEquals("URI", uriNamePairProp.getType().getName());

        final List<URINamePair> storedURINamePairs = intf.getURINamePairs();
        assertEquals(uriNamePairs.size(), storedURINamePairs.size());

        assertEquals(uriNamePair1, storedURINamePairs.get(0));
        assertEquals(uriNamePair2, storedURINamePairs.get(1));
        assertEquals(uriNamePair3, storedURINamePairs.get(2));

        assertEquals(uriNamePairs, storedURINamePairs);

        List<String> internalList = dataObject.getList(uriNamePairProp);
        assertEquals(uriNamePairs.size(), internalList.size());

        assertEquals(uriNamePair1.toStandardSdoFormat(), internalList.get(0));
        assertEquals(uriNamePair2.toStandardSdoFormat(), internalList.get(1));
        assertEquals(uriNamePair3.toStandardSdoFormat(), internalList.get(2));

        URINamePair uriNamePair15 = new URINamePair("com.sap.test", "aLocalName15");

        storedURINamePairs.add(1, uriNamePair15);
        assertEquals(storedURINamePairs.size(), internalList.size());
        assertEquals(uriNamePair15.toStandardSdoFormat(), internalList.get(1));

        URINamePair uriNamePair4 = new URINamePair("com.sap.test", "aLocalName4");
        URINamePair replaced = storedURINamePairs.set(3, uriNamePair4);
        assertEquals(uriNamePair3, replaced);

        assertEquals(storedURINamePairs.size(), internalList.size());
        assertEquals(uriNamePair4.toStandardSdoFormat(), internalList.get(3));

        URINamePair removed = storedURINamePairs.remove(0);
        assertEquals(uriNamePair1, removed);

        assertEquals(storedURINamePairs.size(), internalList.size());
    }

}
