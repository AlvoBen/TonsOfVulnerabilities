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

import com.sap.httpclient.NameValuePair;
import com.sap.httpclient.http.HeaderElement;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * A RequestData Object that contains a String.
 *
 * @author Nikolai Neichev
 */
public class StringRequestData implements RequestData {

  /**
   * The content
   */
  private byte[] content;

  /**
   * The charset
   */
  private String charset;

  /**
   * The content type (i.e. text/html; charset=EUC-JP).
   */
  private String contentType;

  /**
   * Creates a object with the specified content data
   *
   * @param content The content to set.
   */
  public StringRequestData(String content) {
    super();
    if (content == null) {
      throw new IllegalArgumentException("The content cannot be null");
    }
    this.contentType = null;
    this.charset = null;
    this.content = content.getBytes();
  }

  /**
   * Creates a new object with the specified content, content type, and charset.
   *
   * @param content The content to set.
   * @param contentType The type of the content
   * @param charset The charset of the content
	 * @throws java.io.UnsupportedEncodingException if the charset is not supported
   */
  public StringRequestData(String content, String contentType, String charset) throws UnsupportedEncodingException {
    super();
    if (content == null) {
      throw new IllegalArgumentException("The content is null");
    }
    this.contentType = contentType;
    this.charset = charset;
    if (contentType != null) {
      HeaderElement[] values = HeaderElement.parseElements(contentType);
      NameValuePair charsetPair = null;
      for (HeaderElement value : values) {
        if ((charsetPair = value.getParameterByName("charset")) != null) {
          break; // charset found
        }
      }
      if (charset == null && charsetPair != null) {
        this.charset = charsetPair.getValue(); // use the charset from the content type
      } else if (charset != null && charsetPair == null) {
        this.contentType = contentType + "; charset=" + charset;
      }
    }
    if (this.charset != null) {
      this.content = content.getBytes(this.charset);
    } else {
      this.content = content.getBytes();
    }
  }

  /**
   * Gets the content type
   * @return the content type
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Returns true
   * @return <code>true</code>
   */
  public boolean isRepeatable() {
    return true;
  }

  /**
   * Writes the data to the specified stream
   * @param out the specified stream
   * @throws IOException if an I/O exception occurs
   */
  public void writeRequest(OutputStream out) throws IOException {
    if (out == null) {
      throw new IllegalArgumentException("Output stream is null");
    }
    out.write(this.content);
    out.flush();
  }

  /**
   * Gets the content length
   * @return the length of the content.
   */
  public long getContentLength() {
    return this.content.length;
  }

  /**
   * Gets the content data
   * @return Returns the content.
   */
  public String getContent() {
    if (this.charset != null) {
      try {
        return new String(this.content, this.charset);
      } catch (UnsupportedEncodingException e) {
        return new String(this.content);
      }
    } else {
      return new String(this.content);
    }
  }

  /**
   * Gets the content charset
   * @return the content charset, <code>null</code> if not set
   */
  public String getCharset() {
    return charset;
  }
}