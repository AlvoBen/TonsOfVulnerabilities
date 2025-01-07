/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.io;

public class ByteArrayOutputStream {
  public static final int BUFFER_SIZE = 4096;

  private byte[] buffer = new byte[BUFFER_SIZE];
  private int ptr = 0;
  private byte[] all = new byte[0];


  public ByteArrayOutputStream() {
  }

  public void write(byte b) {
    buffer[ptr++] = b;
    if (ptr == BUFFER_SIZE) {
      flush();
    }
  }

  public void write(int b) {
    write((byte)b);
  }

  public void write(byte[] b) {
    if (b == null) {
      return;
    }
    write(b, 0, b.length);
  }

  public void write(byte[] b, int off, int len) {
    if (b == null || off < 0 || len < 0 || off + len > b.length) {
      return;
    }
    if (len > BUFFER_SIZE - ptr) {
      System.arraycopy(b, off, buffer, ptr, BUFFER_SIZE - ptr);
      off += BUFFER_SIZE - ptr;
      len -= BUFFER_SIZE - ptr;
      ptr += len;
      flush();
      write(b, off, len);
    } else {
      System.arraycopy(b, off, buffer, ptr, len);
      ptr += len;
      if (ptr == BUFFER_SIZE) {
        flush();
      }
    }
  }

  public void flush() {
    if (ptr > 0) {
      byte[] tmp = new byte[all.length + ptr];
      System.arraycopy(all, 0, tmp, 0, all.length);
      System.arraycopy(buffer, 0, tmp, all.length, ptr);
      all = tmp;
    }
    ptr = 0;
  }

  public void reset() {
    ptr = 0;
    all = new byte[0];
  }

  public int size() {
    return all.length + ptr;
  }

  public byte[] toByteArray() {
    if (ptr > 0) {
      byte[] tmp = new byte[all.length + ptr];
      System.arraycopy(all, 0, tmp, 0, all.length);
      System.arraycopy(buffer, 0, tmp, all.length, ptr);
      all = tmp;
    }
    return all;
  }
}
