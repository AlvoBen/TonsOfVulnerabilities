package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class ExtendedInstanceClassTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public ExtendedInstanceClassTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testReadXsd() throws IOException {

        URL xsdUrl = getClass().getClassLoader().getResource("com/sap/sdo/testcase/schemas/ExtendedInstanceClass.xsd");
        List<Type> types = _helperContext.getXSDHelper().define(xsdUrl.openStream(), xsdUrl.toString());

        Type myContainerType = _helperContext.getTypeHelper().getType("com.sap.test1", "MyContainer");
        Property dateProp = myContainerType.getProperty("date");
        Property myDateProp = myContainerType.getProperty("myDate");
        Property sqlDateProp = myContainerType.getProperty("sqlDate");
        assertEquals(String.class, dateProp.getType().getInstanceClass());
        assertEquals(java.util.Date.class, myDateProp.getType().getInstanceClass());
        assertEquals(java.sql.Date.class, sqlDateProp.getType().getInstanceClass());

        DataObject myContainer = _helperContext.getDataFactory().create(myContainerType);
        java.util.Date myDate = new Date();
        String date = _helperContext.getDataHelper().toDateTime(myDate);
        java.sql.Date sqlDate = new java.sql.Date(myDate.getTime());

        myContainer.set(dateProp, date);
        myContainer.set(myDateProp, myDate);
        myContainer.set(sqlDateProp, sqlDate);

        assertEquals(date, myContainer.get(dateProp));
        assertEquals(myDate, myContainer.get(myDateProp));
        assertEquals(sqlDate, myContainer.get(sqlDateProp));

        //JavaGeneratorVisitor javaGenerator = new JavaGeneratorVisitor(null,"c:\\test", xsdUrl.toString());
        //javaGenerator.generate(types);

        String xsd = _helperContext.getXSDHelper().generate(types);
        //transform into another namespace
        xsd = xsd.replace("test1", "test2");
        System.out.println(xsd);
        types = _helperContext.getXSDHelper().define(xsd);
        myContainerType = _helperContext.getTypeHelper().getType("com.sap.test1", "MyContainer");
        dateProp = myContainerType.getProperty("date");
        myDateProp = myContainerType.getProperty("myDate");
        sqlDateProp = myContainerType.getProperty("sqlDate");
        assertEquals(String.class, dateProp.getType().getInstanceClass());
        assertEquals(java.util.Date.class, myDateProp.getType().getInstanceClass());
        assertEquals(java.sql.Date.class, sqlDateProp.getType().getInstanceClass());

        myContainer = _helperContext.getDataFactory().create(myContainerType);
        myDate = new Date();
        date = _helperContext.getDataHelper().toDateTime(myDate);
        sqlDate = new java.sql.Date(myDate.getTime());

        myContainer.set(dateProp, date);
        myContainer.set(myDateProp, myDate);
        myContainer.set(sqlDateProp, sqlDate);

        assertEquals(date, myContainer.get(dateProp));
        assertEquals(myDate, myContainer.get(myDateProp));
        assertEquals(sqlDate, myContainer.get(sqlDateProp));

        //javaGenerator = new JavaGeneratorVisitor(null,"c:\\test", null);
        //javaGenerator.generate(types);
    }

}
