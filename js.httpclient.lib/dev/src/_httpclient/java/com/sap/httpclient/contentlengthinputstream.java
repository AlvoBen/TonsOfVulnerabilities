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

import java.io.IOException;
import java.io.InputStream;

/**
 * Represents a stream with specified content length
 *
 * @author Nikolai Neichev
 */
public class ContentLengthInputStream extends InputStream {

  /**
   * The maximum number of bytes that can be read from the stream.
   * After exausted any read operations will return -1.
   */
  private long contentLength;

  /**
   * The current position
   */
  private long pos = 0;

  /**
   * True if the stream is closed.
   */
  private boolean closed = false;

  /**
   * Wrapped incoming stream that all calls are delegated to.
   */
  private InputStream wrappedStream = null;

  /**
   * Creates a new length limited stream
   *
   * @param in the stream to wrap
   * @param contentLength The maximum number of bytes that can be read from the stream.
   */
  public ContentLengthInputStream(InputStream in, long contentLength) {
    super();
    this.wrappedStream = in;
    this.contentLength = contentLength;
  }

  /**
   * Closes the stream
   *
   * @throws IOException If an I/O problem occurs.
   */
  public void close() throws IOException {
    if (!closed) {
      try {
        ChunkedInputStream.exhaustInputStream(this);
      } finally {
        closed = true; // mark as closed
      }
    }
  }

  /**
   * Read the next byte from the stream
   *
   * @return The next byte or -1 if the end of stream has been reached.
   * @throws IOException If an I/O problem occurs
   */
  public int read() throws IOException {
    if (closed) {
      throw new IOException("Stream is closed.");
    }
    if (pos >= contentLength) {
      return -1;
    }
    pos++;
    return this.wrappedStream.read();
  }

  /**
   * Reads bytes and fills them in the specified byte array
   *
   * @param b   The byte array to fill.
   * @param off start filling at this position.
   * @param len the number of bytes to attempt to read.
   * @return The number of bytes read, or -1 if the end of content has been reached.
   * @throws java.io.IOException if an I/O error occurs
   */
  public int read(byte[] b, int off, int len) throws java.io.IOException {
    if (closed) {
      throw new IOException("Stream is closed.");
    }
    if (pos >= contentLength) {
      return -1;
    }
    if (pos + len > contentLength) {
      len = (int) (contentLength - pos);
    }
    int count = this.wrappedStream.read(b, off, len);
    pos += count;
    return count;
  }


  /**
   * Reads bytes and fills them in the specified byte array
   *
   * @param b The byte array to put the new data in.
   * @return the number of bytes read into the buffer.
   * @throws IOException If an I/O problem occurs
   */
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /**
   * Skips a number of bytes from the incoming stream.
   *
   * @param n the number of bytes to skip.
   * @return the actual number of bytes skipped. <= 0 if no bytes are skipped.
   * @throws IOException If an I/O error occurs while skipping bytes.
   */
  public long skip(long n) throws IOException {
    long length = Math.min(n, contentLength - pos); // we don't wan't to skip more bytes than available
    length = this.wrappedStream.skip(length); // actual skipped
    if (length > 0) { // if anything is skipped, set the position
      pos += length;
    }
    return length; // skipped count
  }

  public int available() throws IOException {
    return (int) (contentLength - pos);
  }

}