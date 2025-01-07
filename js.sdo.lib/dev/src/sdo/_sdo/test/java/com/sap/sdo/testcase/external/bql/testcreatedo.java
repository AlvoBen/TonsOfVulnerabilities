/*
 * Copyright (c) 2007 SAP AG. All rights reserved.
 * SAP AG PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * Company Info: http://www.sap.com
 */

package com.sap.sdo.testcase.external.bql;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;

import com.sap.sdo.testcase.SdoTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.helper.XMLDocument;

public class TestCreateDO extends SdoTestCase {

    /**
     * @param pHelperContext
     */
    public TestCreateDO(String pContextId, Feature pFeature) {
        super(pContextId, pFeature);
    }

    @Test
    public void testCreateUntypedDOfromXMLWithoutNS() throws IOException {
        URL url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/external/bql/BQLQueryResultWithoutNS.xml");

        XMLDocument xmlDoc = _helperContext.getXMLHelper().load(url.openStream());
        DataObject root = xmlDoc.getRootObject();

        assertNotNull(root);
    }

    @Test
    public void testCreateUntypedDOfromXMLWithNS() throws IOException {
        URL url = getClass().getClassLoader().getResource("com/sap/sdo/testcase/external/bql/BQLQueryResultWithNS.xml");

        XMLDocument xmlDoc = _helperContext.getXMLHelper().load(url.openStream());
        DataObject root = xmlDoc.getRootObject();

        assertNotNull(root);
    }

    @Test
    public void testCreateTypedDOfromXMLWithoutNS() throws IOException {
      // load XML Schema for BQL result set
      URL schemaUrl = getClass().getClassLoader().getResource(
          "com/sap/sdo/testcase/external/bql/BQLResultSet.xsd");
      _helperContext.getXSDHelper().define(schemaUrl.openStream(), schemaUrl
          .getFile());

      URL xmlUrl = getClass().getClassLoader().getResource("com/sap/sdo/testcase/external/bql/BQLQueryResultWithoutNS.xml");
      XMLDocument xmlDoc = _helperContext.getXMLHelper().load(xmlUrl.openStream());
      DataObject root = xmlDoc.getRootObject();

      assertNotNull(root);
    }

    @Test
    public void testCreateTypedDOfromXMLWithNS() throws IOException {
      // load XML Schema for BQL result set
      URL schemaUrl = getClass().getClassLoader().getResource(
          "com/sap/sdo/testcase/external/bql/BQLResultSet.xsd");
      _helperContext.getXSDHelper().define(schemaUrl.openStream(), schemaUrl
          .getFile());

      URL xmlUrl = getClass().getClassLoader().getResource("com/sap/sdo/testcase/external/bql/BQLQueryResultWithNSneu.xml");
      XMLDocument xmlDoc = _helperContext.getXMLHelper().load(xmlUrl.openStream());
      DataObject root = xmlDoc.getRootObject();

      assertNotNull(root);
    }

}

