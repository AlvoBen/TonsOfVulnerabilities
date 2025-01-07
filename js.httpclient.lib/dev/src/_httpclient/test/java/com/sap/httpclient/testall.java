package com.sap.httpclient;

import com.sap.httpclient.auth.TestAuthAll;
import com.sap.httpclient.http.cookie.TestCookieAll;
import com.sap.httpclient.params.TestParamsAll;

import junit.framework.*;

public class TestAll extends TestCase {

  public TestAll(String testName) {
    super(testName);
  }

  public static Test suite() {
    TestSuite suite = new TestSuite();
    // Fundamentals
    suite.addTest(TestHttpClient.suite());
    suite.addTest(TestMethods.suite());    
    suite.addTest(TestHttpMethodFundamentals.suite());
    suite.addTest(TestHttpStatus.suite());
    suite.addTest(TestStatusLine.suite());
    suite.addTest(TestRequestLine.suite());
    suite.addTest(TestHeader.suite());
    suite.addTest(TestHeaderElement.suite());
    suite.addTest(TestHeaderOps.suite());
    suite.addTest(TestResponseHeaders.suite());
    suite.addTest(TestRequestHeaders.suite());
    suite.addTest(TestStreams.suite());
    suite.addTest(TestParameterParser.suite());
    suite.addTest(TestParameterFormatter.suite());
    suite.addTest(TestNVP.suite());
    suite.addTest(TestMethodCharEncoding.suite());
    suite.addTest(TestHttpVersion.suite());
    suite.addTest(TestEffectiveHttpVersion.suite());
    suite.addTest(TestHttpParser.suite());
    suite.addTest(TestBadContentLength.suite());
    suite.addTest(TestEquals.suite());
    suite.addTest(TestQueryParameters.suite());
    suite.addTest(TestContentEncoding.suite());
    // Exceptions
    suite.addTest(TestExceptions.suite());
    // HTTP state management
    suite.addTest(TestHttpState.suite());
    suite.addTest(TestCookieAll.suite());
    // Authentication
    suite.addTest(TestCredentials.suite());
    suite.addTest(TestAuthAll.suite());
    // Redirects
    suite.addTest(TestRedirects.suite());
    // Connection management
    suite.addTest(TestHttpConnection.suite());
    suite.addTest(TestHttpConnectionManager.suite());
    suite.addTest(TestConnectionPersistence.suite());
    suite.addTest(TestIdleConnectionTimeout.suite());
    suite.addTest(TestMethodAbort.suite());
    // Preferences
    suite.addTest(TestParamsAll.suite());
    suite.addTest(TestVirtualHost.suite());
    suite.addTest(TestHostConfiguration.suite());
    // URIs
    suite.addTest(TestURI.suite());
    suite.addTest(TestURIUtil.suite());
    suite.addTest(TestURIUtil2.suite());
    // Method specific
    suite.addTest(TestEntityEnclosingMethod.suite());
    suite.addTest(TestPostParameterEncoding.suite());
    suite.addTest(TestPostMethod.suite());
    suite.addTest(TestPartsNoHost.suite());
    suite.addTest(TestMultipartPost.suite());
    // Non compliant behaviour
    suite.addTest(TestNoncompliant.suite());
    // Proxy
    suite.addTest(TestProxy.suite());
    suite.addTest(TestProxyWithRedirect.suite());
    return suite;
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestAll.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

}