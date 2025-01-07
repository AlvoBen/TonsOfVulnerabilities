package com.sap.httpclient;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import com.sap.httpclient.http.methods.CUSTOM;
import com.sap.httpclient.http.methods.DELETE;
import com.sap.httpclient.http.methods.OPTIONS;
import com.sap.httpclient.http.methods.TRACE;

public class TestMethods extends TestCase {

  public TestMethods(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestMethods.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestMethods.class);
  }

  public void testCUSTOM() throws Exception {
    CUSTOM custom = new CUSTOM("NWN");
    assertEquals(custom.getName(), "NWN");
  }

  public void testDELETE() throws Exception {
    DELETE delete = new DELETE("NWN");
    assertEquals(delete.getName(), HttpMethod.METHOD_DELETE);
  }

  public void testTRACE() throws Exception {
    TRACE trace = new TRACE();
    assertEquals(trace.getName(), HttpMethod.METHOD_TRACE);
  }

  public void testOPTIONS() throws Exception {
    OPTIONS options = new OPTIONS();
    assertEquals(options.getName(), HttpMethod.METHOD_OPTIONS);
    try {
      options.isAllowed("OOPS");
    } catch (IllegalStateException e) {
      // $JL-EXC$
    }
    try {
      options.isDAVSupported();
    } catch (IllegalStateException e) {
      // $JL-EXC$
    }
    try {
      options.getAllowedMethods();
    } catch (IllegalStateException e) {
      // $JL-EXC$
    }
    try {
      options.getDAVHeaderValue();
    } catch (IllegalStateException e) {
      // $JL-EXC$
    }
  }
}