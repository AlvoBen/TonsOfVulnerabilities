package com.sap.sdo.testcase.external.glx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.api.types.PropertyConstants;
import com.sap.sdo.api.types.TypeConstants;
import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;

public class FreakyGlxNamesTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public FreakyGlxNamesTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testGlxName() {
        DataObject dataTypeDO = _helperContext.getDataFactory().create(URINamePair.TYPE.getURI(), URINamePair.TYPE.getName());
        dataTypeDO.set(TypeConstants.OPEN,false);
        dataTypeDO.set(TypeConstants.SEQUENCED,false);
        dataTypeDO.set(TypeConstants.URI,"com.sap.test.sdo");
        dataTypeDO.set(TypeConstants.NAME,"RenamedPropType");
        dataTypeDO.set(TypeConstants.DATA_TYPE,false);

        DataObject propertyDO = _helperContext.getDataFactory().create(URINamePair.PROPERTY.getURI(), URINamePair.PROPERTY.getName());
        final String badGlxPropName = "$http://sap.com/SATests/AdaptedCustomer/:GetCustomersInfo";
        propertyDO.setString(PropertyConstants.NAME, badGlxPropName);
        propertyDO.set(PropertyConstants.TYPE, _helperContext.getTypeHelper().getType(String.class));

        dataTypeDO.getList(TypeConstants.PROPERTY).add(propertyDO);

        Type renamedPropType = _helperContext.getTypeHelper().define(dataTypeDO);

        DataObject renamedPropTypeDO = _helperContext.getDataFactory().create(renamedPropType);

        Property prop = renamedPropTypeDO.getInstanceProperty(badGlxPropName);
        assertNotNull(prop);

        renamedPropTypeDO.setString(prop, "value1");
        assertEquals("value1", renamedPropTypeDO.getString(prop));

//        assertEquals("value1", renamedPropTypeDO.getString(badGlxPropName));

        renamedPropTypeDO.unset(badGlxPropName);


    }

}
