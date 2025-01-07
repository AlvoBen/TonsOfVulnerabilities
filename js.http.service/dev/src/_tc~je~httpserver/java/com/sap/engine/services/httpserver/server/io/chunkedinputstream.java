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

import com.sap.engine.services.httpserver.lib.util.Ascii;
import com.sap.engine.services.httpserver.exceptions.HttpIOException;
import com.sap.engine.services.httpserver.exceptions.HttpIllegalArgumentException;

import javax.servlet.ServletInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class ChunkedInputStream extends ServletInputStream {
  private static final int BUFFER_SIZE = 4096;
  private InputStream input = null;
  private byte[] buffer = new byte[BUFFER_SIZE];
  private int bufferLength = -1;
  private int ptr = -1;
  private boolean empty = false;
  private int chunkLength = 0;
  private int readFromCurrentChunk = 0;

  public ChunkedInputStream(InputStream input) {
    this.input = input;
  }

  public int read() throws IOException {
    if (empty) {
      return -1;
    }
    if (ptr == -1 || ptr == bufferLength) {
      unchunkNext();
      if (empty) {
        return -1;
      }
    }
    return buffer[ptr++] & 0x000000ff;
  }

  public int read(byte buf[], int off, int len) throws IOException {
    if (off < 0 || len <= 0 || buf == null || buf.length < off + len) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.BUFFER_IS_NULL_OR_OFFSET_AND_LENGTH_ARE_NOT_CORRECT);
    }
    if (empty) {
      return -1;
    }
    if (ptr == -1 || ptr == bufferLength) {
      unchunkNext();
      if (empty) {
        return -1;
      }
    }
    int returnLen = len;
    if (returnLen > bufferLength - ptr) {
      returnLen = bufferLength - ptr;
    }
    System.arraycopy(buffer, ptr, buf, off, returnLen);
    ptr += returnLen;
    return returnLen;
  }

  public boolean isEmpty() {
    return empty;
  }

  private void unchunkNext() throws IOException {
    if (ptr != -1) {
      readFromCurrentChunk += ptr;
    }
    if (readFromCurrentChunk == chunkLength) {
      chunkLength = readLength();
      readFromCurrentChunk = 0;
      if (chunkLength == 0) {
        empty = true;
        return;
      }
    }
    ptr = 0;
    if (chunkLength - readFromCurrentChunk > BUFFER_SIZE) {
      bufferLength = BUFFER_SIZE;
    } else {
      bufferLength = chunkLength - readFromCurrentChunk;
    }
    int ptr = 0;
    int read = -1;
    while (ptr < bufferLength && (read = input.read(buffer, ptr, bufferLength - ptr)) != -1) {
      ptr += read;
    }
    if (bufferLength == chunkLength - readFromCurrentChunk) {
      input.read();
      input.read();
    }
  }

  private int readLength() throws IOException {
    ByteArrayOutputStream line = new ByteArrayOutputStream();
    int c = -1;
    boolean RFound = false;
    while ((c = input.read()) != -1) {
      line.write(c & 0x000000ff);
      if (RFound && c == '\n') {
        byte[] res = line.toByteArray();
        return Ascii.asciiArrHexToInt(res, 0, res.length - 2);
      } else {
        RFound = false;
      }
      if (c == '\r') {
        RFound = true;
      }
    }
    throw new HttpIOException(HttpIOException.INCORRECT_CHUNK_HEADER);
  }
}
