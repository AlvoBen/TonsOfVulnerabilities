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
package com.sap.httpclient;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Closes the underlying stream, when reached end of the stream, and the listener
 *
 * @author Nikolai Neichev
 */
class CloseNotifyInputStream extends FilterInputStream {

  /**
   * is the stream.close() method invoked
   */
  private boolean closeInvoked = false;

  /**
   * The listener is notified when the contents of the stream have been exhausted
   */
  private ResponseConsumedListener listener = null;

  /**
   * Create a new notify closing stream with specified underlying stream and listener
   *
   * @param in      the incoming stream to read from
   * @param listener To be notified when the contents of the stream have been consumed.
   */
  public CloseNotifyInputStream(final InputStream in, final ResponseConsumedListener listener) {
    super(in);
    this.listener = listener;
  }

  /**
   * Reads the next byte of data from the incoming stream.
   *
   * @return the character read, or -1 for EOF
   * @throws IOException when there is an error reading
   */
  public int read() throws IOException {
    int r = super.read();
    if (r == -1) { // end of stream
      notifyListener();
    }
    return r;
  }

  /**
   * Reads up to <code>len</code> bytes of data from the stream.
   *
   * @param b   a <code>byte</code> array to read data into
   * @param off an offset within the array to store data
   * @param len the maximum number of bytes to read
   * @return the number of bytes read or -1 for EOF
   * @throws IOException if there are errors reading
   */
  public int read(byte[] b, int off, int len) throws IOException {
    int r = super.read(b, off, len);
    if (r == -1) {
      notifyListener();
    }
    return r;
  }

  /**
   * Reads some number of bytes from the incoming stream and stores them into the buffer array b.
   *
   * @param b a <code>byte</code> array to read data into
   * @return the number of bytes read or -1 for EOF
   * @throws IOException if there are errors reading
   */
  public int read(byte[] b) throws IOException {
    int r = super.read(b);
    if (r == -1) {
      notifyListener();
    }
    return r;
  }

  /**
   * Close the stream, and also close the underlying stream if it is not already closed.
   *
   * @throws IOException If an IO problem occurs.
   */
  public void close() throws IOException {
    if (!closeInvoked) {
      closeInvoked = true;
      notifyListener();
    }
  }

  /**
   * Notify the listener that the contents have been consumed.
   *
   * @throws IOException If an IO problem occurs.
   */
  private void notifyListener() throws IOException {
    try{
      super.close();
    } finally {
      if (listener != null) {
        listener.responseConsumed();
      }
    }
  }
}