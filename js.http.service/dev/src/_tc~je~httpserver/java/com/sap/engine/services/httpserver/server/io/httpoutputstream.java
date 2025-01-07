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

import com.sap.engine.services.httpserver.server.Client;
import com.sap.engine.services.httpserver.server.ServiceContext;
import com.sap.engine.services.httpserver.CommunicationConstants;

import java.io.OutputStream;

public class HttpOutputStream extends OutputStream {
  private byte[] buffer = null;
  private int ptr = 0;
  private Client client = null;
  private boolean keepAll = false;
  private byte[] all = new byte[0];


  public HttpOutputStream(Client client) {
    this.client = client;
    this.buffer = new byte[ServiceContext.getServiceContext().getHttpProperties().getFileBufferSize()];
  }

  public void write(byte b) {
    buffer[ptr++] = b;
    if (ptr == buffer.length) {
      flush(CommunicationConstants.RESPONSE_FLAG_NOOP);
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
    if (len > buffer.length - ptr) {
      System.arraycopy(b, off, buffer, ptr, buffer.length - ptr);
      off += buffer.length - ptr;
      len -= buffer.length - ptr;
      ptr += len;
      flush(CommunicationConstants.RESPONSE_FLAG_NOOP);
      write(b, off, len);
    } else {
      System.arraycopy(b, off, buffer, ptr, len);
      ptr += len;
      if (ptr == buffer.length) {
        flush(CommunicationConstants.RESPONSE_FLAG_NOOP);
      }
    }
  }

  public void flush(byte flag) {
    if (ptr <= 0) {
      return;
    }
    if (keepAll) {
      byte[] tmp = new byte[all.length + ptr];
      System.arraycopy(all, 0, tmp, 0, all.length);
      System.arraycopy(buffer, 0, tmp, all.length, ptr);
      all = tmp;
    }
    client.send(buffer, 0, ptr, flag);
    buffer = new byte[ServiceContext.getServiceContext().getHttpProperties().getFileBufferSize()];
    ptr = 0;
  }

  public void reset() {
    ptr = 0;
    if (keepAll) {
      all = new byte[0];
    }
    keepAll = false;
  }

  public void keepAll() {
    keepAll = true;
    all = new byte[0];
  }

  public byte[] toByteArray() {
    return all;
  }
}
