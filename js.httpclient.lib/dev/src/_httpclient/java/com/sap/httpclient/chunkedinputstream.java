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

import com.sap.httpclient.exception.HttpException;
import com.sap.httpclient.http.Header;
import com.sap.httpclient.http.HttpParser;
import com.sap.httpclient.uri.EncodingUtil;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Reads chunks from an underlying stream
 *
 * @author Nikolai Neichev
 */
public class ChunkedInputStream extends InputStream {

  /**
   * The wrapped inputstream
   */
  private InputStream in;

  /**
   * The chunk size
   */
  private int chunkSize;

  /**
   * The current position within the current chunk
   */
  private int pos;

  /**
   * True if we'are at the beginning of stream
   */
  private boolean bof = true;

  /**
   * True if we've reached the end of stream
   */
  private boolean eof = false;

  /**
   * True if this stream is closed
   */
  private boolean closed = false;

  /**
   * The method that this stream came from
   */
  private HttpMethod method = null;

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(ChunkedInputStream.class);

  /**
   * Constructs a ChunkedInputStream with specified underlying stream and HTTP method
   *
   * @param in the raw incoming stream
   * @param method the HTTP method to associate this incoming stream with
   */
  public ChunkedInputStream(final InputStream in, final HttpMethod method) {
    if (in == null) {
      throw new IllegalArgumentException("InputStream parameter is null");
    }
    this.in = in;
    this.method = method;
    this.pos = 0;
  }

  /**
   * Constructs a ChunkedInputStream
   *
   * @param in the raw incoming stream with specified underlying stream
   */
  public ChunkedInputStream(final InputStream in) {
    this(in, null);
  }

  /**
   * Reads a byte.
   *
   * @return -1 of the end of the stream has been reached or the next data
   *         byte
   * @throws IOException If an I/O problem occurs
   */
  public int read() throws IOException {
    // Returns all the data in a chunked stream in coalesced form. A chunk is followed by a CRLF.
    // The method returns -1 as soon as a chunksize of 0 is detected.
    // Trailer headers are read automcatically at the end of the stream and
    // can be obtained with the getResponseFooters() method.
    if (closed) {
      throw new IOException("Stream is closed.");
    }
    if (eof) {
      return -1;
    }
    if (pos >= chunkSize) {
      nextChunk();
      if (eof) {
        return -1;
      }
    }
    pos++;
    return in.read();
  }

  /**
   * Read bytes from the stream.
   *
   * @param b   The byte array that will hold the contents from the stream.
   * @param off The offset into the byte array at which bytes will start to be placed.
   * @param len the maximum number of bytes that can be returned.
   * @return The number of bytes returned or -1 if the end of stream has been reached.
   * @throws IOException if an I/O problem occurs.
   */
  public int read(byte[] b, int off, int len) throws IOException {
    if (closed) {
      throw new IOException("Attempted read from closed stream.");
    }
    if (eof) {
      return -1;
    }
    if (pos >= chunkSize) {
      nextChunk();
      if (eof) {
        return -1;
      }
    }
    len = Math.min(len, chunkSize - pos);
    int count = in.read(b, off, len);
    pos += count;
    return count;
  }

  /**
   * Read bytes from the stream.
   *
   * @param b The byte array that will hold the contents from the stream.
   * @return The number of bytes returned or -1 if the end of stream has been reached.
   * @throws IOException if an I/O problem occurs.
   */
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }

  /**
   * Read the CRLF terminator.
   *
   * @throws IOException If an IO error occurs.
   */
  private void readCRLF() throws IOException {
    int cr = in.read();
    int lf = in.read();
    if ((cr != '\r') || (lf != '\n')) {
      throw new IOException("CRLF expected at end of chunk: " + cr + "/" + lf);
    }
  }

  /**
   * Read the next chunk.
   *
   * @throws IOException If an I/O error occurs.
   */
  private void nextChunk() throws IOException {
    if (!bof) {
      readCRLF();
    }
    chunkSize = getChunkSizeFromInputStream(in);
    bof = false;
    pos = 0;
    if (chunkSize == 0) {
      eof = true;
      parseTrailerHeaders();
    }
  }

  /**
   * Gets the chunk size from the input stream, and positions the stream at the start of the next line.
   * Expected chunk size format is: 'chunksize(HEX);comment\r\n'
   *
   * @param in the new incoming stream.
   * @return the chunk size as integer
   * @throws IOException when the chunk size could not be parsed
   */
  private static int getChunkSizeFromInputStream(final InputStream in) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int state = 0; // 0 - normal, 1 - '\r' was read, 2 - inside quotes, -1 - end
    while (state != -1) {  // -1 stop
      int b = in.read();
      if (b == -1) {
        throw new IOException("End of chunked stream reached unexpectedly");
      }
      switch (state) { //$JL-SWITCH$
        case 0: // normal
          switch (b) {
            case '\r':
              state = 1;
              break;
            case '\"': // entering quotes
              state = 2;
              /* fall through */
            default:
              baos.write(b);
          }
          break;
        case 1: // \r was read
          if (b == '\n') { // \r\n read, end loop
            state = -1;
          } else { // not CRLF
            throw new IOException("Protocol violation: single new line(\\n) character in chunk size");
          }
          break;
        case 2: // inside quotes
          switch (b) {
            case '\\':
              b = in.read(); // escaped character
              baos.write(b);
              break;
            case '\"':
              state = 0; // going out of quotes
              /* fall through */
            default:
              baos.write(b);
          }
          break;
        default:
          throw new RuntimeException("assertion failed");
      }
    }
    //parse data
    String dataString = EncodingUtil.getASCIIString(baos.toByteArray());
    int separator = dataString.indexOf(';');
    dataString = (separator > 0) ? dataString.substring(0, separator).trim() : dataString.trim();
    int result;
    try {
      result = Integer.parseInt(dataString.trim(), 16);
    } catch (NumberFormatException e) {
      throw new IOException("Bad chunk size: " + dataString);
    }
    return result;
  }

  /**
   * Reads and stores the Trailer headers.
   *
   * @throws IOException If an I/O problem occurs
   */
  private void parseTrailerHeaders() throws IOException {
    ArrayList<Header> footers;
    try {
      String charset = "US-ASCII";
      if (this.method != null) {
        charset = this.method.getParams().getHttpElementCharset();
      }
      footers = HttpParser.parseHeaders(in, charset);
    } catch (HttpException e) {
      if (LOG.beDebug()) {
        LOG.traceThrowableT(Severity.DEBUG, "Error parsing trailer headers : ", e);
      }
      IOException ioe = new IOException(e.getMessage());
      ioe.initCause(e);
      throw ioe;
    }
    if (this.method != null) {
      for (Header footer : footers) {
        this.method.addResponseFooter(footer);
      }
    }
  }

  /**
   * Marks the stream as closed. Reads all of the remainig data, if any
   *
   * @throws IOException If an IO problem occurs.
   */
  public void close() throws IOException {
    if (!closed) {
      try {
        if (!eof) {
          exhaustInputStream(this);
        }
      } finally {
        eof = true;
        closed = true;
      }
    }
  }

  private static byte[] exhausterBuffer = new byte[1024];

  /**
   * Reads from the specified input stream, until EOF is reached
   *
   * @param inStream The {@link InputStream} to exhaust.
   * @throws IOException If an IO problem occurs
   */
  static void exhaustInputStream(InputStream inStream) throws IOException {
    // read and discard the remainder of the message
		//noinspection StatementWithEmptyBody
		while (inStream.read(exhausterBuffer) >= 0);
  }

  public int available() throws IOException {
    return in.available();
  }
}