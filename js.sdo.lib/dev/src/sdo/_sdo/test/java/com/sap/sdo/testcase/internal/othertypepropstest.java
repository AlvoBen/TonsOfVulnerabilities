package com.sap.sdo.testcase.internal;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.impl.types.SdoProperty;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OtherTypePropsIntf;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class OtherTypePropsTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public OtherTypePropsTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testType() {
        Type type = _helperContext.getTypeHelper().getType(OtherTypePropsIntf.class);
        SdoProperty numberProp = (SdoProperty)type.getProperty("number");
        assertEquals(String.class, numberProp.getJavaClass());

        SdoProperty idProp = (SdoProperty)type.getProperty("id");
        assertEquals(Integer.class, idProp.getJavaClass());

        SdoProperty dateProp = (SdoProperty)type.getProperty("date");
        assertEquals(Date.class, dateProp.getJavaClass());

        SdoProperty intProp = (SdoProperty)type.getProperty("int");
        assertEquals(int.class, intProp.getJavaClass());

        SdoProperty integerProp = (SdoProperty)type.getProperty("integer");
        assertEquals(int.class, integerProp.getJavaClass());
    }

    @Test
    public void testProps() {
        DataObject dataObject = _helperContext.getDataFactory().create(OtherTypePropsIntf.class);
        OtherTypePropsIntf intf = (OtherTypePropsIntf)dataObject;

        assertEquals("0", intf.getNumber());
        assertEquals(null , intf.getId());
        assertEquals(null , intf.getDate());
        assertEquals(0 , intf.getInt());
        assertEquals(0 , intf.getInteger());

        intf.setNumber("1234");
        intf.setId(12);
        Calendar calendar = Calendar.getInstance();
        calendar.set(2007, Calendar.FEBRUARY, 27);
        intf.setDate(calendar.getTime());
        intf.setInt(123);
        intf.setInteger(12345);

        assertEquals("1234", intf.getNumber());
        assertEquals(Integer.valueOf(12), intf.getId());
        calendar = Calendar.getInstance();
        calendar.setTime(intf.getDate());
        assertEquals(2007, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.FEBRUARY, calendar.get(Calendar.MONTH));
        assertEquals(27, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(123, intf.getInt());
        assertEquals(12345, intf.getInteger());

        assertEquals(Integer.valueOf(1234), dataObject.get("number"));
        assertEquals("12", dataObject.get("id"));
        assertEquals("2007-02-27", dataObject.get("date"));
        assertEquals("123", dataObject.get("int"));
        assertEquals(Integer.valueOf(12345), dataObject.get("integer"));

        dataObject.set("number", Integer.valueOf(2345));
        dataObject.set("id", "34");
        dataObject.set("date", "2006-01-26");
        dataObject.set("int", "234");
        dataObject.set("integer", Integer.valueOf(23456));

        assertEquals("2345", intf.getNumber());
        assertEquals(Integer.valueOf(34), intf.getId());
        calendar = Calendar.getInstance();
        calendar.setTime(intf.getDate());
        assertEquals(2006, calendar.get(Calendar.YEAR));
        assertEquals(Calendar.JANUARY, calendar.get(Calendar.MONTH));
        assertEquals(26, calendar.get(Calendar.DAY_OF_MONTH));
        assertEquals(234, intf.getInt());
        assertEquals(23456, intf.getInteger());

        assertEquals(Integer.valueOf(2345), dataObject.get("number"));
        assertEquals("34", dataObject.get("id"));
        assertEquals("2006-01-26", dataObject.get("date"));
        assertEquals("234", dataObject.get("int"));
        assertEquals(Integer.valueOf(23456), dataObject.get("integer"));
    }
}
