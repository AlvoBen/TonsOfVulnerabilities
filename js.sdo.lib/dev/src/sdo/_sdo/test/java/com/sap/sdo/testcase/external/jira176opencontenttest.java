package com.sap.sdo.testcase.external;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.sap.sdo.api.helper.SapHelperContext;
import com.sap.sdo.testcase.SdoTestCase;
import com.sap.sdo.testcase.typefac.OpenInterface;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class Jira176OpenContentTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public Jira176OpenContentTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    @Test
    public void testGetOpenContentList() {
        DataObject openDO = _helperContext.getDataFactory().create(OpenInterface.class);
        List list = openDO.getList( "newProperty" );
        System.out.println(list);
        //TODO what is the decision now???
    }

    @Test
    public void testSetOpenContentList() {
        DataObject openDO = _helperContext.getDataFactory().create(OpenInterface.class);
        openDO.setList("newProperty", Collections.singletonList("test"));
        List list = openDO.getList( "newProperty" );
        assertEquals(1, list.size());
        assertEquals("test", list.get(0));
        Property newProperty = openDO.getInstanceProperty("newProperty");
        assertEquals(_helperContext.getTypeHelper().getType(String.class), newProperty.getType());
    }
}
