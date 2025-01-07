package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.helper.SapXsdHelper;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

public class NillableTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public NillableTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testRoundtripNil() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "NillableComplexType.xsd");
        SapXsdHelper xsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();
        xsdHelper.define(xsdUrl.openStream(), xsdUrl.toString());
        URL xmlUrl =
            getClass().getClassLoader().getResource(PACKAGE + "NillableComplexType.xml");
        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);

        DataObject query = xmlDocument.getRootObject();
        List<DataObject> intRanges = query.getList("intRange");
        DataObject intRange0 = intRanges.get(0);
        DataObject intRange1 = intRanges.get(1);
        DataObject intRange2 = intRanges.get(2);
        DataObject intRange3 = intRanges.get(3);
        DataObject intRange4 = intRanges.get(4);
        DataObject intRange5 = intRanges.get(5);
        List<DataObject> intRangeStrings = query.getList("intRangeString");
        DataObject intRangeString0 = intRangeStrings.get(0);
        DataObject intRangeString1 = intRangeStrings.get(1);
        DataObject intRangeString2 = intRangeStrings.get(2);
        DataObject intRangeString3 = intRangeStrings.get(3);
        DataObject intRangeString4 = intRangeStrings.get(4);
        List<DataObject> intRangeInts = query.getList("intRangeInt");
        DataObject intRangeInt0 = intRangeInts.get(0);
        DataObject intRangeInt1 = intRangeInts.get(1);
        DataObject intRangeInt2 = intRangeInts.get(2);

        assertEquals(false, intRange0.isSet("e1"));
        assertEquals(false, intRange0.isSet("e2"));
        assertEquals(true, xsdHelper.isNil(intRange0));
        assertEquals(false, intRange1.isSet("e1"));
        assertEquals(false, intRange1.isSet("e2"));
        assertEquals(true, xsdHelper.isNil(intRange1));
        assertEquals(true, intRange2.isSet("e1"));
        assertEquals(null, intRange2.getString("e1"));
        assertEquals(false, intRange2.isSet("e2"));
        assertEquals(false, xsdHelper.isNil(intRange2));
        assertEquals(true, intRange3.isSet("e1"));
        assertEquals("", intRange3.getString("e1"));
        assertEquals(true, intRange3.isSet("e2"));
        assertEquals("", intRange3.getString("e2"));
        assertEquals(false, xsdHelper.isNil(intRange3));
        assertEquals(false, intRange4.isSet("e1"));
        assertEquals(true, intRange4.isSet("e2"));
        assertEquals("", intRange4.getString("e2"));
        assertEquals(false, xsdHelper.isNil(intRange4));
        assertEquals(false, intRange5.isSet("e1"));
        assertEquals(false, intRange5.isSet("e2"));
        assertEquals(false, xsdHelper.isNil(intRange5));

        assertEquals(null, intRangeString0.getString("value"));
        assertEquals(true, intRangeString0.isSet("value"));
        assertEquals(true, xsdHelper.isNil(intRangeString0));
        assertEquals(null, intRangeString1.getString("value"));
        assertEquals(true, intRangeString1.isSet("value"));
        assertEquals(true, xsdHelper.isNil(intRangeString1));
        assertEquals("", intRangeString2.getString("value"));
        assertEquals(true, intRangeString2.isSet("value"));
        assertEquals(false, xsdHelper.isNil(intRangeString2));
        assertEquals("", intRangeString3.getString("value"));
        assertEquals(true, intRangeString3.isSet("value"));
        assertEquals(false, xsdHelper.isNil(intRangeString3));
        assertEquals("e", intRangeString4.getString("value"));
        assertEquals(true, intRangeString4.isSet("value"));
        assertEquals(false, xsdHelper.isNil(intRangeString4));

        assertEquals(null, intRangeInt0.get("value"));
        assertEquals(true, intRangeInt0.isSet("value"));
        assertEquals(true, xsdHelper.isNil(intRangeInt0));
        assertEquals(null, intRangeInt1.get("value"));
        assertEquals(true, intRangeInt1.isSet("value"));
        assertEquals(true, xsdHelper.isNil(intRangeInt1));
        assertEquals(5, intRangeInt2.getInt("value"));
        assertEquals(true, intRangeInt2.isSet("value"));
        assertEquals(false, xsdHelper.isNil(intRangeInt2));

        StringWriter stringWriter = new StringWriter();
        _helperContext.getXMLHelper().save(xmlDocument, stringWriter, null);

        assertLineEquality(readFile(xmlUrl), stringWriter.toString());
    }

    @Test
    public void testNillableLists() throws IOException {
        URL xmlUrl =
            getClass().getClassLoader().getResource(PACKAGE + "NillableLists.xml");
        XMLDocument xmlDocument = _helperContext.getXMLHelper().load(xmlUrl.openStream(), xmlUrl.toString(), null);

        StringWriter stringWriter = new StringWriter();
        _helperContext.getXMLHelper().save(xmlDocument, stringWriter, null);

        DataObject lists = xmlDocument.getRootObject();
        DataObject dg2117 = lists.getDataObject("ref.2");
        assertSame(lists.getDataObject("complex[number=DG21-17]"), dg2117);
        assertLineEquality(readFile(xmlUrl), stringWriter.toString());

        List simple = lists.getList("simple");
        assertEquals(null, simple.get(2));

//        List complex = lists.getList("complex");
//        assertEquals(null, complex.get(3));
    }

    @Test
    public void testSetNil() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(PACKAGE + "NillableComplexType.xsd");
        SapXsdHelper xsdHelper = (SapXsdHelper)_helperContext.getXSDHelper();
        xsdHelper.define(xsdUrl.openStream(), xsdUrl.toString());

        DataObject integerRange = _helperContext.getDataFactory().create("", "IntegerRange");
        DataObject integerRangeString = _helperContext.getDataFactory().create("", "IntegerRangeString");
        DataObject integerRangeInt = _helperContext.getDataFactory().create("", "IntegerRangeInt");

        assertEquals(true, xsdHelper.isNil(integerRange));
        integerRange.setInt("min", 0);
        integerRange.setInt("max", 10);
        assertEquals(true, xsdHelper.isNil(integerRange));
        integerRange.setString("e1", null);
        assertEquals(false, xsdHelper.isNil(integerRange));
        integerRange.unset("e1");
        assertEquals(true, xsdHelper.isNil(integerRange));
        xsdHelper.setNil(integerRange, false);
        assertEquals(false, xsdHelper.isNil(integerRange));
        xsdHelper.setNil(integerRange, true);
        assertEquals(true, xsdHelper.isNil(integerRange));
        integerRange.setString("e1", null);
        assertEquals(true, integerRange.isSet("e1"));
        assertEquals(false, xsdHelper.isNil(integerRange));
        xsdHelper.setNil(integerRange, true);
        assertEquals(false, integerRange.isSet("e1"));
        assertEquals(0, integerRange.getInt("min"));
        assertEquals(10, integerRange.getInt("max"));

        assertEquals(true, xsdHelper.isNil(integerRangeString));
        integerRangeString.setInt("min", 0);
        integerRangeString.setInt("max", 10);
        assertEquals(true, xsdHelper.isNil(integerRangeString));
        integerRangeString.setString("value", "hello");
        assertEquals(false, xsdHelper.isNil(integerRangeString));
        integerRangeString.setString("value", null);
        assertEquals(true, xsdHelper.isNil(integerRangeString));
        integerRangeString.setString("value", "");
        assertEquals(false, xsdHelper.isNil(integerRangeString));
        xsdHelper.setNil(integerRangeString, true);
        assertEquals(null, integerRangeString.getString("value"));
        assertEquals(true, integerRangeString.isSet("value"));
        assertEquals(true, xsdHelper.isNil(integerRangeString));
        integerRangeString.setString("value", "hi");
        assertEquals(false, xsdHelper.isNil(integerRangeString));
        integerRangeString.unset("value");
        assertEquals(null, integerRangeString.getString("value"));
        assertEquals(true, xsdHelper.isNil(integerRangeString));

        assertEquals(true, xsdHelper.isNil(integerRangeInt));
        integerRangeInt.setInt("min", 0);
        integerRangeInt.setInt("max", 10);
        assertEquals(true, xsdHelper.isNil(integerRangeInt));
        integerRangeInt.setInt("value", 2);
        assertEquals(false, xsdHelper.isNil(integerRangeInt));
        integerRangeInt.set("value", null);
        assertEquals(true, xsdHelper.isNil(integerRangeInt));
        integerRangeInt.setInt("value", 3);
        assertEquals(false, xsdHelper.isNil(integerRangeInt));
        xsdHelper.setNil(integerRangeInt, true);
        assertEquals(null, integerRangeInt.get("value"));
        assertEquals(true, integerRangeInt.isSet("value"));
        assertEquals(true, xsdHelper.isNil(integerRangeInt));
        integerRangeInt.setInt("value", 4);
        assertEquals(false, xsdHelper.isNil(integerRangeInt));
        integerRangeInt.unset("value");
        assertEquals(null, integerRangeInt.getString("value"));
        assertEquals(true, xsdHelper.isNil(integerRangeInt));
    }

}
