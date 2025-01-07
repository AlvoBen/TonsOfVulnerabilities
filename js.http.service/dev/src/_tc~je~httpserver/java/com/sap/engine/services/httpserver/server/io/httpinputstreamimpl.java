/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server.io;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_TRACE_REQUEST;
import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_REQUEST;

import java.io.IOException;
import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.engine.services.httpserver.exceptions.HttpIOException;
import com.sap.engine.services.httpserver.exceptions.HttpIllegalArgumentException;
import com.sap.engine.services.httpserver.interfaces.io.HttpInputStream;
import com.sap.engine.services.httpserver.lib.util.HexPrinter;
import com.sap.engine.services.httpserver.server.Client;

/**
 *
 * @author Maria Jurova
 * @version 4.0
 */
public class HttpInputStreamImpl extends HttpInputStream {
  private static final int MAX_MARKED_COUNT = 4096;
  private Client client = null;
  private long totalLen = -1;
  private byte[][] content = new byte[100][];
  private int[] contentLens = new int[100];
  private int[] contentOffss = new int[100];
  private int contentPtr = 1;
  private long currentLen = -1;
  private int ptrToArr = 0;
  private int ptr = 0;
  private long read = 0;
  private boolean connectionClosed = false;
  //mark - reset support
  private boolean marked = false;
  private boolean hasMarkedUnread = false;
  private int markedBufferReadOffset = 0;
  private byte[] markedBuffer = null;
  private int markedBufferOffset = 0;
  private FCAConnection connection = null;
  
  public HttpInputStreamImpl(long totalLen, byte[] data, int dataLen, Client client) {
    this.client = client;
    this.totalLen = totalLen;
    content[0] = data;
    contentLens[0] = dataLen;
    contentOffss[0] = 0;
    currentLen = (long) (dataLen);
    this.connection = client.getConnection();
    if (LOCATION_HTTP_REQUEST.bePath()) {
    	LOCATION_HTTP_REQUEST.pathT("HttpInputStreamImpl(HttpProperties, long, byte[], int, Client): ",
        "CLIENT: "+client.getClientId()+" totalLen("+totalLen+") \r\n" + HexPrinter.toString(data, 0, dataLen));
    }
  }

  /*
   * Used for chunked input stream.
   */
  public HttpInputStreamImpl(byte[] data, int dataLen, Client client) {
    this.client = client;
    this.totalLen = -1;
    content[0] = data;
    contentLens[0] = dataLen;
    contentOffss[0] = 0;
    currentLen = (long) (dataLen);
    this.connection = client.getConnection();
    if (LOCATION_HTTP_REQUEST.beDebug()) {
    	LOCATION_HTTP_REQUEST.debugT("HttpInputStreamImpl(HttpProperties, byte[], int, Client): ",
        "CLIENT: "+client.getClientId()+" totalLen("+totalLen+") \r\n" + HexPrinter.toString(data, 0, dataLen));
    }
  }

  public int read() throws IOException {
    synchronized (this) {
      if (connectionClosed) {
        throw new HttpIOException(HttpIOException.CONNECTION_IS_CLOSED);
      }
      if (!marked && hasMarkedUnread) {
        try {
          return markedBuffer[markedBufferReadOffset++] & 0x000000ff;
        } finally {
          if (markedBufferReadOffset == markedBufferOffset) {
            hasMarkedUnread = false;
            markedBuffer = null;
          }
        }
      }
      if (read >= totalLen && totalLen != -1) {
        return -1;
      }
      if (read >= currentLen) {
        if (connectionClosed) {
          throw new HttpIOException(HttpIOException.CONNECTION_IS_CLOSED);
        }
        long delta = System.currentTimeMillis();
        byte[] tmpByteArr = new byte[8192];
        int cnt = connection.getInputStream().read(tmpByteArr);
        if (cnt != -1) {
          fill(tmpByteArr, 0, cnt);
        }
        delta = System.currentTimeMillis() - delta;
        if (LOCATION_HTTP_REQUEST.bePath()) {
        	LOCATION_HTTP_REQUEST.pathT("HttpInputStreamImpl.read(): ", "read=" + read + "; currentLen=" 
            + currentLen + "; connection=" + connection + "; delta=" + delta);
        }
      }
      if (read >= currentLen) {
        if (totalLen == -1) {
          throw new HttpIOException(
            HttpIOException.CLIENT_LOST_OR_SYNCHRONIZATION_ERROR,
            new Object[] {"" + read, "End-Of-Chunk" });
        } else {
          throw new HttpIOException(
            HttpIOException.CLIENT_LOST_OR_SYNCHRONIZATION_ERROR,
            new Object[]{"" + read, "" + totalLen});
        }
      }
      read++;
      checkPointers();
      if (marked) {
        byte result = content[ptrToArr][ptr++];
        if (markedBufferOffset == markedBuffer.length) {
          reset();
        } else {
          markedBuffer[markedBufferOffset++] = result;
        }
        return result & 0x000000ff;
      } else {
        return content[ptrToArr][ptr++] & 0x000000ff;
      }
    }
  }

  public int read(byte buf[], int off, int len) throws IOException {
    synchronized (this) {
      if (buf == null || buf.length < off + len) {
        throw new HttpIllegalArgumentException(HttpIllegalArgumentException.BUFFER_IS_NULL_OR_OFFSET_AND_LENGTH_ARE_NOT_CORRECT);
      }
      if (connectionClosed) {
        throw new HttpIOException(HttpIOException.CONNECTION_IS_CLOSED);
      }
      if (marked || hasMarkedUnread) {
        return super.read(buf, off, len);
      }
      if (read >= totalLen && totalLen != -1) {
        return -1;
      }
      if (read >= currentLen) {
        if (connectionClosed) {
          throw new HttpIOException(HttpIOException.CONNECTION_IS_CLOSED);
        }
        long delta = System.currentTimeMillis();
        byte[] tmpByteArr = new byte[8192];
        int cnt = connection.getInputStream().read(tmpByteArr);
        if (cnt != -1) {
          fill(tmpByteArr, 0, cnt);
        }
        delta = System.currentTimeMillis() - delta;
        if (LOCATION_HTTP_REQUEST.bePath()) {
        	LOCATION_HTTP_REQUEST.pathT("HttpInputStreamImpl.read(byte[], int, int): ", "read=" + read + "; currentLen=" 
            + currentLen + "; connection=" + connection + "delta=" + delta);
        }
      }
      if (read >= currentLen) {
        if (totalLen == -1) {
          throw new HttpIOException(
            HttpIOException.CLIENT_LOST_OR_SYNCHRONIZATION_ERROR,
            new Object[] {"" + read, "End-Of-Chunk" });
        } else {
          throw new HttpIOException(
            HttpIOException.CLIENT_LOST_OR_SYNCHRONIZATION_ERROR,
            new Object[] { "" + read, "" + totalLen });
        }
      }
      checkPointers();
      int avLen = contentLens[ptrToArr] + contentOffss[ptrToArr] - ptr;
      if (avLen > len) {
        System.arraycopy(content[ptrToArr], ptr, buf, off, len);
        read += len;
        ptr += len;
        return len;
      } else {
        System.arraycopy(content[ptrToArr], ptr, buf, off, avLen);
        read += avLen;
        ptr += avLen;
        return avLen;
      }
    }
  }

  public int available() {
    synchronized (this) {
      if (totalLen == -1) {
        return (int)currentLen;
      }
      if (hasMarkedUnread) {
        return (int) (totalLen - read + markedBufferOffset);
      } else {
        return (int) (totalLen - read);
      }
    }
  }

  public void mark(int count) {
    if (count > MAX_MARKED_COUNT) {
      throw new HttpIllegalArgumentException(HttpIllegalArgumentException.ILLEGAL_MARK_PARAMETER,
          new Object[]{new Integer(count), new Integer(MAX_MARKED_COUNT)});
    }
    marked = true;
    hasMarkedUnread = false;
    markedBuffer = new byte[count];
    markedBufferOffset = 0;
  }

  public void reset() {
    if (!marked) {
      return;
    }
    marked = false;
    if (markedBufferOffset > 0) {
      hasMarkedUnread = true;
      markedBufferReadOffset = 0;
    } else {
      hasMarkedUnread = false;
      markedBuffer = null;
    }
  }

  public boolean isEmpty() {
    return totalLen != -1 && available() == 0;
  }

  private void fill(byte[] nextData, int offset, int dataLen) {
    synchronized (this) {
      trace(nextData, offset, dataLen);
      
      if (contentPtr == content.length) {
        resize();
      }
      content[contentPtr] = nextData;
      contentLens[contentPtr] = dataLen;
      contentOffss[contentPtr] = offset;
      contentPtr++;
      currentLen = currentLen + dataLen;
      notifyAll();
    }
  }

  private void resize() {
    byte[][] temp = new byte[content.length + 100][];
    int[] tempLen = new int[contentLens.length + 100];
    int[] tempOffs = new int[contentOffss.length + 100];
    System.arraycopy(content, 0, temp, 0, content.length);
    System.arraycopy(contentLens, 0, tempLen, 0, contentLens.length);
    System.arraycopy(contentOffss, 0, tempOffs, 0, contentOffss.length);
    content = temp;
    contentLens = tempLen;
    contentOffss = tempOffs;
  }

  public void releaseBuffers() {
    for (; ptrToArr < contentPtr; ptrToArr++) {
      if (ptrToArr != 0) {
        content[ptrToArr] = null;
      }
    }
  }

  public void connectionClosed() {
    synchronized (this) {
      connectionClosed = true;
      notifyAll();
    }
  }

  private void checkPointers() {
    if (ptr == contentOffss[ptrToArr] + contentLens[ptrToArr]) {
      if (ptrToArr != 0) {
        content[ptrToArr] = null;
      }
      ptrToArr++;
      ptr = contentOffss[ptrToArr];
    }
  }

  /**
   * Dumps the request in the traces. 
   * For severity "debug" the dump is in HEX format.
   * For severity "path" the dump is in String format.
   * @param msg
   * @param off
   * @param len
   */
  private void trace(byte[] msg, int off, int len){    
    if (LOCATION_HTTP_TRACE_REQUEST.beDebug()) {
    	LOCATION_HTTP_TRACE_REQUEST.debugT("CLIENT: "+client.getClientId()+", REQUEST BODY read(" + len + "):\r\n" + HexPrinter.toString(msg, off, len));
    }else if (LOCATION_HTTP_TRACE_REQUEST.bePath()) {
    	LOCATION_HTTP_TRACE_REQUEST.pathT("CLIENT: "+client.getClientId()+", REQUEST BODY read(" + len + "):\r\n" + new String(msg, off, len));
    }
  }

}

