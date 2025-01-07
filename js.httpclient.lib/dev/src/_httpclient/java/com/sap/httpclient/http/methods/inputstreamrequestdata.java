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
package com.sap.httpclient.http.methods;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A RequestData that contains an InputStream.
 *
 * @author Nikolai Neichev
 */
public class InputStreamRequestData implements RequestData {

  /**
   * The content length will be calculated automatically. This implies buffering of the content.
   */
  public static final int CONTENT_LENGTH_AUTO = -2;

  private static final Location LOG = Location.getLocation(InputStreamRequestData.class);

  private long contentLength;

  private InputStream content;

  /**
   * The buffered request body, if any.
   */
  private byte[] buffer = null;

  /**
   * The content type
   */
  private String contentType;

  /**
   * Creates a new InputStreamRequestData with the specified content
   *
   * @param content The content to set.
   */
  public InputStreamRequestData(InputStream content) {
    this(content, null);
  }

  /**
   * Creates a new InputStreamRequestData with the specified content, content type
   *
   * @param content     The content to set.
   * @param contentType The type of the content, or <code>null</code>.
   */
  public InputStreamRequestData(InputStream content, String contentType) {
    this(content, CONTENT_LENGTH_AUTO, contentType);
  }

  /**
   * Creates a new InputStreamRequestData with the specified content and content length.
   *
   * @param content       The content to set.
   * @param contentLength The content size in bytes or a negative number if not known.
   */
  public InputStreamRequestData(InputStream content, long contentLength) {
    this(content, contentLength, null);
  }

  /**
   * Creates a new InputStreamRequestData with the specified content, content length, and content type.
   *
   * @param content       The content to set.
   * @param contentLength The content size in bytes or a negative number if not known.
   * @param contentType   The type of the content, or <code>null</code>.
   */
  public InputStreamRequestData(InputStream content, long contentLength, String contentType) {
    if (content == null) {
      throw new IllegalArgumentException("The content cannot be null");
    }
    this.content = content;
    this.contentLength = contentLength;
    this.contentType = contentType;
  }

  public String getContentType() {
    return contentType;
  }

  /**
   * Buffers request body incoming stream.
   */
  private void bufferContent() {
    if (this.buffer != null) {
      return; // already buffered
    }
    if (this.content != null) {
      try {
        ByteArrayOutputStream tmp = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int l;
        while ((l = this.content.read(data)) >= 0) {
          tmp.write(data, 0, l);
        }
        this.buffer = tmp.toByteArray();
        this.content = null;
        this.contentLength = buffer.length;
      } catch (IOException e) {
        LOG.traceThrowableT(Severity.ERROR, e.getMessage(), e);
        this.buffer = null;
        this.content = null;
        this.contentLength = 0;
      }
    }
  }

  /**
   * Checks if {@link #writeRequest(OutputStream)} can be called more than once.
   *
   * @return <tt>true</tt> if the data can be written more than once, <tt>false</tt> otherwise.
   */
  public boolean isRepeatable() {
    return buffer != null;
  }

  public void writeRequest(OutputStream out) throws IOException {
    if (content != null) {
      byte[] tmp = new byte[4096];
      int i;
      while ((i = content.read(tmp)) >= 0) {
        out.write(tmp, 0, i);
      }
    } else if (buffer != null) {
      out.write(buffer);
    } else {
      throw new IllegalStateException("Content must be set before entity is written");
    }
  }

  /**
   * Gets the content length.  If the content length has not been set, the content will be
   * buffered to determine the actual content length.
   */
  public long getContentLength() {
    if (contentLength == CONTENT_LENGTH_AUTO && buffer == null) {
      bufferContent();
    }
    return contentLength;
  }

  /**
   * @return Returns the content.
   */
  public InputStream getContent() {
    return content;
  }

}