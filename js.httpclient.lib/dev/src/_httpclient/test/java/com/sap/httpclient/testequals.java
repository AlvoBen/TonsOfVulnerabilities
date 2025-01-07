package com.sap.httpclient;

import com.sap.httpclient.net.DefaultSocketFactory;
import com.sap.httpclient.net.Protocol;
import com.sap.httpclient.net.ProtocolSocketFactory;
import com.sap.httpclient.net.SSLSocketFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 */
public class TestEquals extends TestCase {

  public static Test suite() {
    return new TestSuite(TestEquals.class);
  }

  public TestEquals() {
    super();
  }

  public TestEquals(String arg0) {
    super(arg0);
  }

  public void testProtocol() {
    Protocol p1 = new Protocol("test", new DefaultSocketFactory(), 123);
    Protocol p2 = new Protocol("test", new DefaultSocketFactory(), 123);
    assertTrue(p1.equals(p2));
    assertTrue(p2.equals(p1));
  }

  public void testProtocolSocketFactory() {
    ProtocolSocketFactory p1 = new DefaultSocketFactory();
    ProtocolSocketFactory p2 = new DefaultSocketFactory();
    assertTrue(p1.equals(p2));
    assertTrue(p2.equals(p1));
    p1 = new SSLSocketFactory();
    p2 = new SSLSocketFactory();
    assertTrue(p1.equals(p2));
    assertTrue(p2.equals(p1));

  }

  public void testHostConfiguration() {
    HostConfiguration hc1 = new HostConfiguration();
    hc1.setHost("http", 80, "http");
    HostConfiguration hc2 = new HostConfiguration();
    hc2.setHost("http", 80, "http");
    assertTrue(hc1.equals(hc2));
    assertTrue(hc2.equals(hc1));
  }

}