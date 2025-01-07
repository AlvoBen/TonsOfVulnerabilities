package com.sap.httpclient.http.cookie;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import junit.framework.Test;
import junit.framework.TestSuite;
import com.sap.httpclient.http.Header;

/**
 * Test cases for Cookie
 */
public class TestCookie extends TestCookieBase {

  public TestCookie(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(TestCookie.class);
  }

  /**
   * Tests default constructor.
   */
  public void testDefaultConstuctor() {
    Cookie dummy = new Cookie();
    assertEquals("noname=", dummy.toText());
  }

  public void testComparator() throws Exception {
    Header setCookie;
    Cookie[] parsed;
    Vector<Cookie> cookies = new Vector<Cookie>();
    // Cookie 0
    setCookie = new Header("Set-Cookie",
            "cookie-name=cookie-value;Path=/sap;Domain=.sap.com;Expires=Wed, 07-Feb-1979 00:00:20 GMT");
    CookieSpec cookiespec = new CookieSpecBase();
    parsed = cookieParse(cookiespec, ".sap.com", 80, "/sap/httpclient", true, setCookie);
    cookies.add(parsed[0]);
    // Cookie 1
    setCookie = new Header("Set-Cookie",
            "cookie-name=cookie-value;Path=/com/sap;Domain=.sap.com;Expires=Wed, 07-Feb-1979 00:00:20 GMT");
    parsed = cookieParse(cookiespec, ".sap.com", 80, "/com/sap/httpclient", true, setCookie);
    cookies.add(parsed[0]);
    // Cookie 2
    setCookie = new Header("Set-Cookie",
            "cookie-name=cookie-value;Path=/sap;Domain=.sap.org;Expires=Wed, 07-Feb-1979 00:00:20 GMT");
    parsed = cookieParse(cookiespec, ".sap.org", 80, "/sap/httpclient", true, setCookie);
    cookies.add(parsed[0]);
    // Cookie 3
    setCookie = new Header("Set-Cookie",
            "cookie-name=cookie-value;Path=/com/sap;Domain=.sap.org;Expires=Wed, 07-Feb-1979 00:00:20 GMT");
    parsed = cookieParse(cookiespec, ".sap.org", 80, "/com/sap/httpclient", true, setCookie);
    cookies.add(parsed[0]);
    // Cookie 4
    setCookie = new Header("Set-Cookie",
            "cookie-name=cookie-value;Path=/sap;Domain=.sap.com;Expires=Wed, 07-Feb-1979 00:00:20 GMT");
    parsed = cookieParse(cookiespec, ".sap.com", 80, "/sap/httpclient", true, setCookie);
    cookies.add(parsed[0]);
    // The order should be:
    // 1, 0, 3, 2, 4
    parsed = (Cookie[]) cookies.toArray(new Cookie[0]);
    SortedSet set = new TreeSet(parsed[0]);
		int pass = 0;
		for (Object aSet : set) {
			Cookie cookie = (Cookie) aSet;
			switch (pass) {
				case 0:
					assertTrue("0th cookie should be cookie[1]", cookie == parsed[1]);
					break;
				case 1:
					assertTrue("1st cookie should be cookie[0]", cookie == parsed[0]);
					break;
				case 2:
					assertTrue("2nd cookie should be cookie[3]", cookie == parsed[3]);
					break;
				case 3:
					assertTrue("3rd cookie should be cookie[2]", cookie == parsed[2]);
					break;
				case 4:
					assertTrue("4th cookie should be cookie[4]", cookie == parsed[4]);
					break;
				default:
					fail("This should never happen.");
			}
			pass++;
		}
		try {
      parsed[0].compare("foo", "bar");
      fail("Should have thrown an exception trying to compare non-cookies");
    } catch (ClassCastException is_OK) {
      // $JL-EXC$
    }
  }
}