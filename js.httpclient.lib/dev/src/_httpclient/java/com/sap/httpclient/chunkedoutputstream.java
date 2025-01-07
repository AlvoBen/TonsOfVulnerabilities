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

import com.sap.httpclient.uri.EncodingUtil;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implements HTTP chunking support. Chunk default size is 2048 bytes.
  *
 * @author Nikolai Neichev
 */
public class ChunkedOutputStream extends OutputStream {

  private static final byte CRLF[] = new byte[]{(byte) 13, (byte) 10};

  /**
   * End chunk
   */
  private static final byte ENDCHUNK[] = CRLF;

  /**
   * byte 0
   */
  private static final byte[] ZERO = new byte[]{(byte) '0'};

  /**
   * underlying output stream
   */
  private OutputStream stream = null;

  /**
   * cached bytes
   */
  private byte[] cache;

  private int cachePosition = 0;

  private boolean wroteFinalChunk = false;

  /**
   * Wraps a output stream and chunks the outgoing data.
   *
   * @param stream to wrap
   * @param bufferSize minimum chunk size (excluding last chunk)
   */
  public ChunkedOutputStream(OutputStream stream, int bufferSize) {
    this.cache = new byte[bufferSize];
    this.stream = stream;
  }

  /**
   * Wraps a output stream and chunks the outgoing data.
   *
   * @param stream the stream to wrap
   */
  public ChunkedOutputStream(OutputStream stream) {
    this(stream, 2048);
  }

  /**
   * Writes the cache out onto the underlying stream
   *
   * @throws IOException in an I/O exception occurs
   */
  protected void flushCache() throws IOException {
    if (cachePosition > 0) {
      byte chunkHeader[] = EncodingUtil.getASCIIBytes(Integer.toHexString(cachePosition) + "\r\n");
      stream.write(chunkHeader, 0, chunkHeader.length);
      stream.write(cache, 0, cachePosition);
      stream.write(ENDCHUNK, 0, ENDCHUNK.length);
      cachePosition = 0;
    }
  }

  /**
   * Writes the cache and appendBytes to the underlying stream as one large chunk
   *
   * @param appendBytes the bytes to append
   * @param off the append bytes offset
   * @param len the append bytes length
   * @throws IOException if an IO exception occurs
   */
  protected void flushCacheWithAppend(byte appendBytes[], int off, int len) throws IOException {
    byte chunkHeader[] = EncodingUtil.getASCIIBytes(Integer.toHexString(cachePosition + len) + "\r\n");
    stream.write(chunkHeader, 0, chunkHeader.length);
    stream.write(cache, 0, cachePosition);
    stream.write(appendBytes, off, len);
    stream.write(ENDCHUNK, 0, ENDCHUNK.length);
    cachePosition = 0;
  }

  /**
   * Writes the final chunk.
   * @throws IOException if an I/O exception occurs
   */
  protected void writeFinalChunk() throws IOException {
    stream.write(ZERO, 0, ZERO.length);
    stream.write(CRLF, 0, CRLF.length);
    stream.write(ENDCHUNK, 0, ENDCHUNK.length);
  }

  /**
   * Must be called to ensure the internal cache is flushed and the closing chunk is written.
   *
   * @throws IOException if an I/O error ocures
   */
  public void finish() throws IOException {
    if (!wroteFinalChunk) {
      flushCache();
      writeFinalChunk();
      wroteFinalChunk = true;
    }
  }

  /**
   * Write the specified byte to the outgoing stream.
   *
   * @param b The byte to be written
   * @throws IOException if an I/O error occurs
   */
  public void write(int b) throws IOException {
    cache[cachePosition] = (byte) b;
    cachePosition++;
    if (cachePosition == cache.length) flushCache();
  }

  /**
   * Writes the byte array.
   *
   * @param bytes the data to be written
   * @throws IOException if an I/O exception occurs
   */
  public void write(byte bytes[]) throws IOException {
    this.write(bytes, 0, bytes.length);
  }

  /**
   * Writes the sub byte array.
   *
   * @param bytes the data to be written
   * @param off the data offset
   * @param len the data length
   * @throws IOException if an I/O exception occurs
   */
  public void write(byte bytes[], int off, int len) throws IOException {
    if (len >= cache.length - cachePosition) {
      flushCacheWithAppend(bytes, off, len);
    } else {
      System.arraycopy(bytes, off, cache, cachePosition, len);
      cachePosition += len;
    }
  }

  /**
   * Flushes the underlying stream.
   *
   * @throws IOException if an I/O error ocures
   */
  public void flush() throws IOException {
    stream.flush();
  }

  /**
   * Closes the chunked stream, but does NOT close the underlying stream.
   *
   * @throws IOException if an I/O error ocures
   */
  public void close() throws IOException {
    finish();
    super.close();
  }
}