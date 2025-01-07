package com.sap.httpclient;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.sap.httpclient.uri.URIUtil;

/**
 * Unit tests for {@link URIUtil}.  These tests care currently quite limited
 * and should be expanded to test more functionality.
 */
public class TestURIUtil extends TestCase {
  URITestCase pathTests[] = {new URITestCase("http://www.server.com/path1/path2", "/path1/path2"),
                             new URITestCase("http://www.server.com/path1/path2/", "/path1/path2/"),
                             new URITestCase("http://www.server.com/path1/path2?query=string", "/path1/path2"),
                             new URITestCase("http://www.server.com/path1/path2/?query=string", "/path1/path2/"),
                             new URITestCase("www.noscheme.com/path1/path2", "/path1/path2"),
                             new URITestCase("www.noscheme.com/path1/path2#anchor?query=string", "/path1/path2"),
                             new URITestCase("/noscheme/nohost/path", "/noscheme/nohost/path"),
                             new URITestCase("http://www.server.com", "/"),
                             new URITestCase("https://www.server.com:443/ssl/path", "/ssl/path"),
                             new URITestCase("http://www.server.com:8080/path/with/port", "/path/with/port"),
                             new URITestCase("http://www.server.com/path1/path2?query1=string?1&query2=string2", "/path1/path2")};

  URITestCase queryTests[] = {new URITestCase("http://www.server.com/path1/path2", null),
                              new URITestCase("http://www.server.com/path1/path2?query=string", "query=string"),
                              new URITestCase("http://www.server.com/path1/path2/?query=string", "query=string"),
                              new URITestCase("www.noscheme.com/path1/path2#anchor?query=string", "query=string"),
                              new URITestCase("/noscheme/nohost/path?query1=string1&query2=string2", "query1=string1&query2=string2"),
                              new URITestCase("https://www.server.com:443/ssl/path?query1=string1&query2=string2", "query1=string1&query2=string2"),
                              new URITestCase("http://www.server.com:8080/path/with/port?query1=string1&query2=string2", "query1=string1&query2=string2"),
                              new URITestCase("http://www.server.com/path1/path2?query1=string?1&query2=string2", "query1=string?1&query2=string2")};


  public TestURIUtil(String testName) {
    super(testName);
  }

  public static void main(String args[]) {
    String[] testCaseName = {TestURIUtil.class.getName()};
    junit.textui.TestRunner.main(testCaseName);
  }

  public static Test suite() {
    return new TestSuite(TestURIUtil.class);
  }

  public void testGetPath() {
    String testValue;
    String expectedResult;
		for (URITestCase pathTest : pathTests) {
			testValue = pathTest.getTestValue();
			expectedResult = pathTest.getExpectedResult();
			assertEquals("Path test", expectedResult, URIUtil.getPath(testValue));
		}
	}

  public void testGetQueryString() {
    String testValue;
    String expectedResult;
		for (URITestCase queryTest : queryTests) {
			testValue = queryTest.getTestValue();
			expectedResult = queryTest.getExpectedResult();
			assertEquals("Path test", expectedResult, URIUtil.getQuery(testValue));
		}
	}

  private class URITestCase {
    private String testValue;
    private String expectedResult;

    public URITestCase(String testValue, String expectedResult) {
      this.testValue = testValue;
      this.expectedResult = expectedResult;
    }

    public String getTestValue() {
      return testValue;
    }

    public String getExpectedResult() {
      return expectedResult;
    }
  }
}