/**
 * Copyright (c) 2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.rmi_p4.finalizer;


import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This is the entry point of the whole test package.
 * Here we can 
 * 
 * @author Trendafilov, Tsvetko
 */
public class TestAll extends TestCase {

  /**
   * Constructor with name. It creates a new TestCase instance with the specified name.
   *  
   * @param testName The specified name.  
   */
  public TestAll(String testName) {
    super(testName);
  }

  /**
   * Generate a <code>junit.framework.TestSuite</code> object cast to <code>junit.framework.Test</code> 
   * with all suites collected from the whole test package. 
   * 
   * @return 
   *    A test with all suites collected from the whole package.
   */
  public static Test suite() {
    TestSuite suite = new TestSuite();
    
    // Testing finalizer 
    //suite.addTest(ConnectionWrapper.suite());
    //suite.addTest(StubEmulator.suite());
    suite.addTest(StubsFinalizerTest.suite());
    
    return suite;
  }

  /**
   * This method just provides ability to run the JUnit test cases as
   * stand alone application. It can be removed at anytime.
   * 
   * @param args We do not use this argument here.
   */
  public static void main(String args[]) {
    String[] testCaseName = {TestAll.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

}
