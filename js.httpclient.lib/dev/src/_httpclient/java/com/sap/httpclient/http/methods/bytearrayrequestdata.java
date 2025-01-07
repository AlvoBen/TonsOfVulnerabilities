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

import java.io.IOException;
import java.io.OutputStream;

/**
 * A RequestData that contains an array of bytes.
 *
 * @author Nikolai Neichev
 */
public class ByteArrayRequestData implements RequestData {

  /**
   * The content
   */
  private byte[] content;

  /**
   * The content type
   */
  private String contentType;

  /**
   * Creates a new entity with the specified content.
   *
   * @param content The content to set.
   */
  public ByteArrayRequestData(byte[] content) {
    this(content, null);
  }

  /**
   * Creates a new entity with the specified content and content type.
   *
   * @param content     The content to set.
   * @param contentType The content type to set or <code>null</code>.
   */
  public ByteArrayRequestData(byte[] content, String contentType) {
    super();
    if (content == null) {
      throw new IllegalArgumentException("The content cannot be null");
    }
    this.content = content;
    this.contentType = contentType;
  }

  /**
   * Checks if the data is repeatable
   * @return <code>true</code>
   */
  public boolean isRepeatable() {
    return true;
  }

  /**
   * Gets the content type.  This will be used as the value for the "Content-Type" header.
   *
   * @return the content type
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Writes the request data to the specified stream.
   *
   * @param out the specified stream
   * @throws IOException if an I/O exception occurs
   */
  public void writeRequest(OutputStream out) throws IOException {
    out.write(content);
  }

  /**
   * Gets the request data length.
   *
   * @return the content length
   */
  public long getContentLength() {
    return content.length;
  }

  /**
   * Gets the contest as byte[]
   * @return the content.
   */
  public byte[] getContent() {
    return content;
  }

}