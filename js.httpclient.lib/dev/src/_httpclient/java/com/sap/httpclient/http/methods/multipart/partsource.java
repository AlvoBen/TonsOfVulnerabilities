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

import java.io.IOException;
import java.io.InputStream;

/**
 * An interface for providing access to data when posting MultiPart messages.
 *
 * @author Nikolai Neichev
 */
public interface PartSource {

  /**
   * Gets the length of the source.
   *
   * @return the length
   */
  long getLength();

  /**
   * Gets the name of the source
   *
   * @return the fileName used for posting a MultiPart file part
   */
  String getFileName();

  /**
   * Gets a new InputStream for reading this source.
   *
   * @return a new InputStream
   * @throws IOException if an error occurs when creating the InputStream
   */
  InputStream createInputStream() throws IOException;

}