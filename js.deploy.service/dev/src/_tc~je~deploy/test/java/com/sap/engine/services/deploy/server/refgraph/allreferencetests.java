/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.refgraph;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sap.engine.services.deploy.server.cache.dpl_info.CompRefGraphRemoveNodeTest;
import com.sap.engine.services.deploy.server.cache.dpl_info.CompRefGraphTest;


/**
 *@author Luchesar Cekov
 */
public class AllReferenceTests {

  public static void main(String[] args) {
    junit.textui.TestRunner.run(AllReferenceTests.suite());
  }

  public static Test suite() {
    TestSuite suite = new TestSuite("Test for com.sap.engine.services.deploy.server.refgraph");
    //$JUnit-BEGIN$    
    suite.addTestSuite(GraphTest.class);
    suite.addTestSuite(GraphTraverceTest.class);
//    suite.addTestSuite(ApplicationsTest.class);
    suite.addTestSuite(CompRefGraphTest.class);    
    suite.addTestSuite(CompRefGraphRemoveNodeTest.class);
    //$JUnit-END$
    return suite;
  }

}
