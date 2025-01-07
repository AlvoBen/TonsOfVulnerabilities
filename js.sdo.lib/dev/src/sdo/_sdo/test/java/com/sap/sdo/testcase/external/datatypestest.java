package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Type;

public class DataTypesTest extends SdoTestCase {
    /**
     * @param pHelperContext
     */
    public DataTypesTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testDataTypes() {
        DataObject dataTypeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        assertTrue("returned value is not a type",dataTypeDO instanceof Type);
        dataTypeDO.set("open",false);
        dataTypeDO.set("sequenced",false);
        dataTypeDO.set("uri","com.sap.sdo");
        dataTypeDO.set("name","SimpleDataTypeExampleDataType");
        dataTypeDO.set(_helperContext.getTypeHelper().getOpenContentProperty(URINamePair.DATATYPE_JAVA_URI, TypeConstants.JAVA_CLASS),String.class.getName());
        dataTypeDO.set("dataType",true);

        Type dataType = _helperContext.getTypeHelper().define(dataTypeDO);
        assertTrue("returned value is not a type",dataType instanceof Type);


        DataObject typeDO = _helperContext.getDataFactory().create("commonj.sdo", "Type");
        typeDO.set("open",false);
        typeDO.set("sequenced",false);
        typeDO.set("uri","com.sap.sdo");
        typeDO.set("name","SimpleDataTypeExample");
        DataObject prop = typeDO.createDataObject("property");
        prop.set("name", "dataType");
        prop.set("type", dataType);
        prop.set("many", false);

        Type type = _helperContext.getTypeHelper().define(typeDO);

        DataObject c = _helperContext.getDataFactory().create(type);
        c.set("dataType","aValue");
        assertEquals("aValue",c.get("dataType"));
    }
}
