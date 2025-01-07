package com.sap.sdo.testcase.tutorial;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

public class TutorialTest extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public TutorialTest(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
        // TODO Auto-generated constructor stub
    }

    public static String TUTORIAL_PACKAGE = "com/sap/sdo/testcase/tutorial/";

    @Test
    public void testDelete() throws IOException {
        URL xsdUrl = getClass().getClassLoader().getResource(
            TUTORIAL_PACKAGE + "hotel_management.xsd");
        _helperContext.getXSDHelper().define(xsdUrl.openStream(),
            xsdUrl.toString());
        URL xmlUrl = getClass().getClassLoader().getResource(
            TUTORIAL_PACKAGE + "hotelsManagement.xml");
        XMLDocument document = _helperContext.getXMLHelper().load(
            xmlUrl.openStream(), xmlUrl.toString(), null);
        DataObject rootDO = document.getRootObject();
        DataObject cust2 = rootDO.getDataObject("customer[id='c001']");
        DataObject roomToCheck = rootDO
            .getDataObject("city[name='Leipzig']/hotel[id='10']/rooms[quantity='54']");

        cust2.delete();

        String xml = _helperContext.getXMLHelper().save(roomToCheck, "com.sap.sdo.tutorial",
            "rooms");
        assertEquals(xml, 1, roomToCheck.getList("reservations").size());
    }

}
