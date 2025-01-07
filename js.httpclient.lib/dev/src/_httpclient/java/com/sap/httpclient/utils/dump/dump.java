/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.utils.dump;

import com.sap.tc.logging.Location;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Logs data to the dump LOG.
 *
 * @author Nikolai Neichev
 */
public class Dump {

  public static Dump HEADER_DUMP = new Dump(Location.getLocation("httpclient.dump.header"));

  public static Dump CONTENT_DUMP = new Dump(Location.getLocation("httpclient.dump.content"));

  private static String SEND = "SEND: ";
  private static String RECV = "RECV: ";
  public static boolean DEBUG = false; // engage the console debug


  /**
   * Log for any dump messages.
   */
  private Location LOG;

  private Dump(Location log) {
    this.LOG = log;
  }

  private void dump(String header, InputStream instream) throws IOException {
    StringBuilder buffer = new StringBuilder();
    int ch;
    while ((ch = instream.read()) != -1) {
      if (ch == 13) {
        buffer.append("[\\r]");
      } else if (ch == 10) {
        buffer.append("[\\n]\"");
        buffer.insert(0, "\"");
        buffer.insert(0, header);
        internalTrace(buffer.toString());
        buffer.setLength(0);
      } else if ((ch < 32) || (ch > 127)) {
        buffer.append("[0x");
        buffer.append(Integer.toHexString(ch));
        buffer.append("]");
      } else {
        buffer.append((char) ch);
      }
    }
    if (buffer.length() > 0) {
      buffer.insert(0, header);
      internalTrace(buffer.toString());
    }
  }

  private void internalTrace(String str) {
    LOG.debugT(str);
//    System.out.println(str); // manual console debug
  }

  public boolean enabled() {
    return LOG.beDebug();
  }

  public void outgoing(byte[] b, int off, int len) throws IOException {
    if (b == null) {
      throw new IllegalArgumentException("Output is null");
    }
    dump(SEND, new ByteArrayInputStream(b, off, len));
  }

  public void incoming(byte[] b, int off, int len) throws IOException {
    if (b == null) {
      throw new IllegalArgumentException("Input is null");
    }
    dump(RECV, new ByteArrayInputStream(b, off, len));
  }

  public void outgoing(byte[] b) throws IOException {
    if (b == null) {
      throw new IllegalArgumentException("Output is null");
    }
    dump(SEND, new ByteArrayInputStream(b));
  }

  public void incoming(byte[] b) throws IOException {
    if (b == null) {
      throw new IllegalArgumentException("Input is null");
    }
    dump(RECV, new ByteArrayInputStream(b));
  }

  public void outgoing(int b) throws IOException {
    outgoing(new byte[]{(byte) b});
  }

  public void incoming(int b) throws IOException {
    incoming(new byte[]{(byte) b});
  }

  public void outgoing(final String s) throws IOException {
    if (s == null) {
      throw new IllegalArgumentException("Output is null");
    }
    outgoing(s.getBytes());
  }

  public void incoming(final String s) throws IOException {
    if (s == null) {
      throw new IllegalArgumentException("Input is null");
    }
    incoming(s.getBytes());
  }
}