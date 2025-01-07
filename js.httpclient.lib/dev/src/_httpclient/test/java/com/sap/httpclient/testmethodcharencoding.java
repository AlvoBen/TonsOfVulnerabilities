package com.sap.httpclient;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.sap.httpclient.http.methods.GET;
import com.sap.httpclient.http.methods.POST;
import com.sap.httpclient.http.methods.RequestData;
import com.sap.httpclient.http.methods.StringRequestData;
import com.sap.httpclient.http.HttpStatus;
import com.sap.httpclient.uri.URIUtil;

public class TestMethodCharEncoding extends HttpClientTestBase {

  static final String CHARSET_DEFAULT = "ISO-8859-1";
  static final String CHARSET_ASCII = "US-ASCII";
  static final String CHARSET_UTF8 = "UTF-8";
  static final String CHARSET_KOI8_R = "KOI8_R";
  static final String CHARSET_WIN1251 = "Cp1251";

  static final int SWISS_GERMAN_STUFF_UNICODE [] = {
    0x47, 0x72, 0xFC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xE4, 0x6D, 0xE4
  };

  static final int SWISS_GERMAN_STUFF_ISO8859_1 [] = {
    0x47, 0x72, 0xFC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xE4, 0x6D, 0xE4
  };

  static final int SWISS_GERMAN_STUFF_UTF8 [] = {
    0x47, 0x72, 0xC3, 0xBC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xC3, 0xA4,
    0x6D, 0xC3, 0xA4
  };

  static final int RUSSIAN_STUFF_UNICODE [] = {
    0x412, 0x441, 0x435, 0x43C, 0x5F, 0x43F, 0x440, 0x438,
    0x432, 0x435, 0x442
  };

  static final int RUSSIAN_STUFF_UTF8 [] = {
    0xD0, 0x92, 0xD1, 0x81, 0xD0, 0xB5, 0xD0, 0xBC, 0x5F,
    0xD0, 0xBF, 0xD1, 0x80, 0xD0, 0xB8, 0xD0, 0xB2, 0xD0,
    0xB5, 0xD1, 0x82
  };

  static final int RUSSIAN_STUFF_KOI8R [] = {
    0xF7, 0xD3, 0xC5, 0xCD, 0x5F, 0xD0, 0xD2, 0xC9, 0xD7,
    0xC5, 0xD4
  };

  static final int RUSSIAN_STUFF_WIN1251 [] = {
    0xC2, 0xF1, 0xE5, 0xEC, 0x5F, 0xEF, 0xF0, 0xE8, 0xE2,
    0xE5, 0xF2
  };

  public TestMethodCharEncoding(final String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(TestMethodCharEncoding.class);
  }

  public void testRequestCharEncoding() throws IOException {
    GET httpget = new GET("/");
    assertEquals(CHARSET_DEFAULT, httpget.getRequestCharSet());
    httpget.setRequestHeader("Content-Type", "text/plain; charset=" + CHARSET_ASCII);
    assertEquals(CHARSET_ASCII, httpget.getRequestCharSet());
    httpget.setRequestHeader("Content-Type", "text/plain; charset=" + CHARSET_UTF8);
    assertEquals(CHARSET_UTF8, httpget.getRequestCharSet());

  }

  public void testNoExplicitCharEncoding() throws Exception {
    this.server.setHttpService(new EchoService());
    GET httpget = new GET("/test/");
    httpget.setRequestHeader("Content-Type", "text/plain");
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      assertEquals(CHARSET_DEFAULT, httpget.getResponseCharSet());
    } finally {
      httpget.releaseConnection();
    }
  }

  public void testExplicitCharEncoding() throws Exception {
    this.server.setHttpService(new EchoService());
    GET httpget = new GET("/test/");
    httpget.setRequestHeader("Content-Type", "text/plain; charset=" + CHARSET_UTF8);
    try {
      this.client.executeMethod(httpget);
      assertEquals(HttpStatus.SC_OK, httpget.getStatusCode());
      assertEquals(CHARSET_UTF8, httpget.getResponseCharSet());
    } finally {
      httpget.releaseConnection();
    }
  }

  private String constructString(int[] unicodeChars) {
    StringBuilder buffer = new StringBuilder();
    if (unicodeChars != null) {
			for (int unicodeChar : unicodeChars) {
				buffer.append((char) unicodeChar);
			}
		}
    return buffer.toString();
  }


  private void verifyEncoding(RequestData data, int[] sample) throws IOException {
    assertNotNull("Request body", data);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    data.writeRequest(bos);
    InputStream instream = new ByteArrayInputStream(bos.toByteArray());
		for (int aSample : sample) {
			int b = instream.read();
			assertTrue("Unexpected end of stream", b != -1);
			if (aSample != b) {
				fail("Invalid request body encoding");
			}
		}
		assertTrue("End of stream expected", instream.read() == -1);
  }

  public void testLatinAccentInRequestBody() throws IOException {
    POST httppost = new POST("/");
    String s = constructString(SWISS_GERMAN_STUFF_UNICODE);
    // Test default encoding ISO-8859-1
    httppost.setRequestData(new StringRequestData(s, "text/plain", CHARSET_DEFAULT));
    verifyEncoding(httppost.getRequesData(), SWISS_GERMAN_STUFF_ISO8859_1);
    // Test UTF-8 encoding
    httppost.setRequestData(new StringRequestData(s, "text/plain", CHARSET_UTF8));
    verifyEncoding(httppost.getRequesData(), SWISS_GERMAN_STUFF_UTF8);

  }

  public void testRussianInRequestBody() throws IOException {
    POST httppost = new POST("/");
    String s = constructString(RUSSIAN_STUFF_UNICODE);
    // Test UTF-8 encoding
    httppost.setRequestData(new StringRequestData(s, "text/plain", CHARSET_UTF8));
    verifyEncoding(httppost.getRequesData(), RUSSIAN_STUFF_UTF8);
    // Test KOI8-R
    httppost.setRequestData(new StringRequestData(s, "text/plain", CHARSET_KOI8_R));
    verifyEncoding(httppost.getRequesData(), RUSSIAN_STUFF_KOI8R);
    // Test WIN1251
    httppost.setRequestData(new StringRequestData(s, "text/plain", CHARSET_WIN1251));
    verifyEncoding(httppost.getRequesData(), RUSSIAN_STUFF_WIN1251);
  }

  public void testQueryParams() throws Exception {
    GET get = new GET("/");
    String ru_msg = constructString(RUSSIAN_STUFF_UNICODE);
    String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE);
    get.setQueryString(new NameValuePair[]{
      new NameValuePair("ru", ru_msg),
      new NameValuePair("ch", ch_msg)
    });
    Map<String, String> params = new HashMap<String, String>();
    StringTokenizer tokenizer = new StringTokenizer(get.getQuery(), "&");
    while (tokenizer.hasMoreTokens()) {
      String s = tokenizer.nextToken();
      int i = s.indexOf('=');
      assertTrue("Invalid url-encoded parameters", i != -1);
      String name = s.substring(0, i).trim();
      String value = s.substring(i + 1, s.length()).trim();
      value = URIUtil.decode(value, CHARSET_UTF8);
      params.put(name, value);
    }
    assertEquals(ru_msg, params.get("ru"));
    assertEquals(ch_msg, params.get("ch"));
  }

  public void testUrlEncodedRequestBody() throws Exception {
    POST httppost = new POST("/");
    String ru_msg = constructString(RUSSIAN_STUFF_UNICODE);
    String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE);
    httppost.setRequestBody(new NameValuePair[]{
      new NameValuePair("ru", ru_msg),
      new NameValuePair("ch", ch_msg)
    });
    httppost.setRequestHeader("Content-Type", POST.FORM_URL_ENCODED_CONTENT_TYPE
            + "; charset=" + CHARSET_UTF8);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    httppost.getRequesData().writeRequest(bos);
    Map<String, String> params = new HashMap<String, String>();
    StringTokenizer tokenizer = new StringTokenizer(new String(bos.toByteArray(), CHARSET_UTF8), "&");
    while (tokenizer.hasMoreTokens()) {
      String s = tokenizer.nextToken();
      int i = s.indexOf('=');
      assertTrue("Invalid url-encoded parameters", i != -1);
      String name = s.substring(0, i).trim();
      String value = s.substring(i + 1, s.length()).trim();
      value = URIUtil.decode(value, CHARSET_UTF8);
      params.put(name, value);
    }
    assertEquals(ru_msg, params.get("ru"));
    assertEquals(ch_msg, params.get("ch"));
  }

  public void testRequestEntityLength() throws IOException {
    String s = constructString(SWISS_GERMAN_STUFF_UNICODE);
    RequestData requestentity = new StringRequestData(s, "text/plain", CHARSET_UTF8);
    assertEquals(s.getBytes(CHARSET_UTF8).length, requestentity.getContentLength());
  }

}