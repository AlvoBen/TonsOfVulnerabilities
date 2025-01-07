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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A PartSource that reads from a byte array.
 *
 * @author Nikolai Neichev
 */
public class ByteArrayPartSource implements PartSource {

  /**
   * Name of the source file.
   */
  private String fileName;

  /**
   * Byte array of the source file.
   */
  private byte[] bytes;

  /**
   * Constructor for ByteArrayPartSource.
   *
   * @param fileName the name of the file these bytes represent
   * @param bytes    the content of this part
   */
  public ByteArrayPartSource(String fileName, byte[] bytes) {
    this.fileName = fileName;
    this.bytes = bytes;
  }

  /**
   * Gets the length of the source.
   *
   * @return the length
   */
  public long getLength() {
    return bytes.length;
  }

  /**
   * Gets the name of the source
   *
   * @return the fileName used for posting a MultiPart file part
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Gets a new InputStream for reading this source.
   *
   * @return a new InputStream
   * @throws IOException if an error occurs when creating the InputStream
   */  
  public InputStream createInputStream() throws IOException {
    return new ByteArrayInputStream(bytes);
  }

}