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
package com.sap.httpclient.http.methods.multipart;

import com.sap.httpclient.uri.EncodingUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Abstract class for one Part of a multipart post object.
 *
 * @author Nikolai Neichev
 */
public abstract class Part {

	/** The boundary */
  protected static final String BOUNDARY = "----------------314159265358979323846";

  /** The boundary as a byte array. */
  protected static final byte[] BOUNDARY_BYTES = EncodingUtil.getASCIIBytes(BOUNDARY);

  /**
   * The default boundary to be used if setBoundaryBytes(byte[]) has not
   * been called.
   */
  private static final byte[] DEFAULT_BOUNDARY_BYTES = BOUNDARY_BYTES;

  /**
   * Carriage return/linefeed
   */
  protected static final String CRLF = "\r\n";

  /**
   * Carriage return/linefeed as a byte array
   */
  protected static final byte[] CRLF_BYTES = EncodingUtil.getASCIIBytes(CRLF);

  /**
   * Content dispostion characters
   */
  protected static final String QUOTE = "\"";

  /**
   * Content dispostion as a byte array
   */
  protected static final byte[] QUOTE_BYTES = EncodingUtil.getASCIIBytes(QUOTE);

  /**
   * Extra characters
   */
  protected static final String EXTRA = "--";

  /**
   * Extra characters as a byte array
   */
  protected static final byte[] EXTRA_BYTES = EncodingUtil.getASCIIBytes(EXTRA);

  /**
   * Content dispostion characters
   */
  protected static final String CONTENT_DISPOSITION = "Content-Disposition: form-data; name=";

  /**
   * Content dispostion as a byte array
   */
  protected static final byte[] CONTENT_DISPOSITION_BYTES = EncodingUtil.getASCIIBytes(CONTENT_DISPOSITION);

  /**
   * Content type header
   */
  protected static final String CONTENT_TYPE = "Content-Type: ";

  /**
   * Content type header as a byte array
   */
  protected static final byte[] CONTENT_TYPE_BYTES = EncodingUtil.getASCIIBytes(CONTENT_TYPE);

  /**
   * Content charset
   */
  protected static final String CHARSET = "; charset=";

  /**
   * Content charset as a byte array
   */
  protected static final byte[] CHARSET_BYTES = EncodingUtil.getASCIIBytes(CHARSET);

  /**
   * Content type header
   */
  protected static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding: ";

  /**
   * Content type header as a byte array
   */
  protected static final byte[] CONTENT_TRANSFER_ENCODING_BYTES =
          EncodingUtil.getASCIIBytes(CONTENT_TRANSFER_ENCODING);

  /**
   * The ASCII bytes to use as the multipart boundary.
   */
  private byte[] boundaryBytes;

  /**
   * Return the name of this part.
   *
   * @return The name.
   */
  public abstract String getName();

  /**
   * Returns the content type of this part.
   *
   * @return the content type, or <code>null</code> to exclude the content type header
   */
  public abstract String getContentType();

  /**
   * Return the character encoding of this part.
   *
   * @return the character encoding, or <code>null</code> to exclude the character encoding header
   */
  public abstract String getCharSet();

  /**
   * Return the transfer encoding of this part.
   *
   * @return the transfer encoding, or <code>null</code> to exclude the transfer encoding header
   */
  public abstract String getTransferEncoding();

  /**
   * Gets the part boundary to be used.
   *
   * @return the part boundary as an array of bytes.
   */
  protected byte[] getPartBoundary() {
    if (boundaryBytes == null) {
      // custom boundary bytes have not been set, use the default.
      return DEFAULT_BOUNDARY_BYTES;
    } else {
      return boundaryBytes;
    }
  }

  /**
   * Sets the part boundary.  Only meant to be used by {@link Part#sendParts(OutputStream, Part[], byte[])}
   * and {@link Part#getLengthOfParts(Part[], byte[])}
   *
   * @param boundaryBytes An array of ASCII bytes.
   */
  void setPartBoundary(byte[] boundaryBytes) {
    this.boundaryBytes = boundaryBytes;
  }

  /**
   * Tests if this part can be sent more than once.
   *
   * @return <code>true</code> if {@link #sendData(OutputStream)} can be successfully called more than once.
   */
  public boolean isRepeatable() {
    return true;
  }

  /**
   * Write the start to the specified outgoing stream
   *
   * @param out The outgoing stream
   * @throws IOException If an IO problem occurs.
   */
  protected void sendStart(OutputStream out) throws IOException {
    out.write(EXTRA_BYTES);
    out.write(getPartBoundary());
    out.write(CRLF_BYTES);
  }

  /**
   * Write the content disposition header to the specified outgoing stream
   *
   * @param out The outgoing stream
   * @throws IOException If an IO problem occurs.
   */
  protected void sendDispositionHeader(OutputStream out) throws IOException {
    out.write(CONTENT_DISPOSITION_BYTES);
    out.write(QUOTE_BYTES);
    out.write(EncodingUtil.getASCIIBytes(getName()));
    out.write(QUOTE_BYTES);
  }

  /**
   * Write the content type header to the specified outgoing stream
   *
   * @param out The outgoing stream
   * @throws IOException If an IO problem occurs.
   */
  protected void sendContentTypeHeader(OutputStream out) throws IOException {
    String contentType = getContentType();
    if (contentType != null) {
      out.write(CRLF_BYTES);
      out.write(CONTENT_TYPE_BYTES);
      out.write(EncodingUtil.getASCIIBytes(contentType));
      String charSet = getCharSet();
      if (charSet != null) {
        out.write(CHARSET_BYTES);
        out.write(EncodingUtil.getASCIIBytes(charSet));
      }
    }
  }

  /**
   * Write the content transfer encoding header to the specified outgoing stream
   *
   * @param out The outgoing stream
   * @throws IOException If an IO problem occurs.
   */
  protected void sendTransferEncodingHeader(OutputStream out) throws IOException {
    String transferEncoding = getTransferEncoding();
    if (transferEncoding != null) {
      out.write(CRLF_BYTES);
      out.write(CONTENT_TRANSFER_ENCODING_BYTES);
      out.write(EncodingUtil.getASCIIBytes(transferEncoding));
    }
  }

  /**
   * Write the end of the header to the outgoing stream
   *
   * @param out The outgoing stream
   * @throws IOException If an IO problem occurs.
   */
  protected void sendEndOfHeader(OutputStream out) throws IOException {
    out.write(CRLF_BYTES);
    out.write(CRLF_BYTES);
  }

  /**
   * Write the data to the specified outgoing stream
   *
   * @param out The outgoing stream
   * @throws IOException If an IO problem occurs.
   */
  protected abstract void sendData(OutputStream out) throws IOException;

  /**
   * Return the length of the main content
   *
   * @return long The length.
   * @throws IOException If an IO problem occurs
   */
  protected abstract long lengthOfData() throws IOException;

  /**
   * Write the end data to the outgoing stream.
   *
   * @param out The outgoing stream
   * @throws IOException If an IO problem occurs.
   */
  protected void sendEnd(OutputStream out) throws IOException {
    out.write(CRLF_BYTES);
  }

  /**
   * Write all the data to the outgoing stream.
   * If you override this method make sure to override #length() as well
   *
   * @param out The outgoing stream
   * @throws IOException If an IO problem occurs.
   */
  public void send(OutputStream out) throws IOException {
    sendStart(out);
    sendDispositionHeader(out);
    sendContentTypeHeader(out);
    sendTransferEncodingHeader(out);
    sendEndOfHeader(out);
    sendData(out);
    sendEnd(out);
  }


  /**
   * Return the full length of all the data.
   * If you override this method make sure to override #send(OutputStream) as well
   *
   * @return long The length.
   * @throws IOException If an IO problem occurs
   */
  public long length() throws IOException {
    if (lengthOfData() < 0) {
      return -1;
    }
    ByteArrayOutputStream overhead = new ByteArrayOutputStream();
    sendStart(overhead);
    sendDispositionHeader(overhead);
    sendContentTypeHeader(overhead);
    sendTransferEncodingHeader(overhead);
    sendEndOfHeader(overhead);
    sendEnd(overhead);
    return overhead.size() + lengthOfData();
  }

  /**
   * Return a string representation of this object.
   *
   * @return A string representation of this object.
   */
  public String toString() {
    return this.getName();
  }

  /**
   * Write all parts and the last boundary to the specified outgoing stream.
   *
   * @param out   The stream to write to.
   * @param parts The parts to write.
   * @throws IOException If an I/O error occurs while writing the parts.
   */
  public static void sendParts(OutputStream out, final Part[] parts) throws IOException {
    sendParts(out, parts, DEFAULT_BOUNDARY_BYTES);
  }

  /**
   * Write all parts and the last boundary to the specified outgoing stream.
   *
   * @param out          The stream to write to.
   * @param parts        The parts to write.
   * @param partBoundary The ASCII bytes to use as the part boundary.
   * @throws IOException If an I/O error occurs while writing the parts.
   */
  public static void sendParts(OutputStream out, Part[] parts, byte[] partBoundary) throws IOException {
    if (parts == null) {
      throw new IllegalArgumentException("parts is null");
    }
    if (partBoundary == null || partBoundary.length == 0) {
      throw new IllegalArgumentException("partBoundary is empty");
    }
    for (Part part : parts) {
      // set the part boundary before the part is sent
      part.setPartBoundary(partBoundary);
      part.send(out);
    }
    out.write(EXTRA_BYTES);
    out.write(partBoundary);
    out.write(EXTRA_BYTES);
    out.write(CRLF_BYTES);
  }

  /**
   * Return the total sum of all parts and that of the last boundary
   *
   * @param parts The parts.
   * @return The total length
   * @throws IOException If an I/O error occurs while writing the parts.
   */
  public static long getLengthOfParts(Part[] parts) throws IOException {
    return getLengthOfParts(parts, DEFAULT_BOUNDARY_BYTES);
  }

  /**
   * Gets the length of the multipart message including the specified parts.
   *
   * @param parts        The parts.
   * @param partBoundary The ASCII bytes to use as the part boundary.
   * @return The total length
   * @throws IOException If an I/O error occurs while writing the parts.
   */
  public static long getLengthOfParts(Part[] parts, byte[] partBoundary) throws IOException {
    if (parts == null) {
      throw new IllegalArgumentException("parts is null");
    }
    long total = 0;
    for (Part part : parts) {
      // set the part boundary before we calculate the part's length
      part.setPartBoundary(partBoundary);
      long l = part.length();
      if (l < 0) {
        return -1;
      }
      total += l;
    }
    total += EXTRA_BYTES.length;
    total += partBoundary.length;
    total += EXTRA_BYTES.length;
    total += CRLF_BYTES.length;
    return total;
  }
}