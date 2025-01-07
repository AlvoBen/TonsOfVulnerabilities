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
import java.io.IOException;
import java.io.OutputStream;

/**
 * Simple string parameter for a multipart post
 *
 * @author Nikolai Neichev
 */
public class StringPart extends PartBase {

  /**
   * Default content encoding of string parameters.
   */
  private static final String DEFAULT_CONTENT_TYPE = "text/plain";

  /**
   * Default charset of string parameters
   */
  private static final String DEFAULT_CHARSET = "US-ASCII";

  /**
   * Default transfer encoding of string parameters
   */
  private static final String DEFAULT_TRANSFER_ENCODING = "8bit";

  /**
   * Contents of this StringPart.
   */
  private byte[] content;

  /**
   * The String value of this part.
   */
  private String value;

  /**
   * Constructor.
   *
   * @param name    The name of the part
   * @param value   the string to post
   * @param charset the charset to be used to encode the string
   */
  public StringPart(String name, String value, String charset) {
    super(name,
          DEFAULT_CONTENT_TYPE,
          charset == null ? DEFAULT_CHARSET : charset,
          DEFAULT_TRANSFER_ENCODING);
    if (value == null) {
      throw new IllegalArgumentException("value is null");
    }
    if (value.indexOf(0) != -1) {
      // See RFC 2048, 2.8. "8bit Data"
      throw new IllegalArgumentException("NULs found in string parts");
    }
    this.value = value;
  }

  /**
   * Constructor.
   *
   * @param name  The name of the part
   * @param value the string to post
   */
  public StringPart(String name, String value) {
    this(name, value, null);
  }

  /**
   * Gets the content in bytes.  Bytes are lazily created to allow the charset to be changed
   * after the part is created.
   *
   * @return the content in bytes
   */
  private byte[] getContent() {
    if (content == null) {
      content = EncodingUtil.getBytes(value, getCharSet());
    }
    return content;
  }

  /**
   * Writes the data to the specified OutputStream.
   *
   * @param out the OutputStream to write to
   * @throws IOException if there is a write error
   */
  protected void sendData(OutputStream out) throws IOException {
    out.write(getContent());
  }

  /**
   * Return the length of the data.
   *
   * @return The length of the data.
   * @throws IOException If an IO problem occurs
   */
  protected long lengthOfData() throws IOException {
    return getContent().length;
  }

  public void setCharSet(String charSet) {
    super.setCharSet(charSet);
    this.content = null;
  }

}