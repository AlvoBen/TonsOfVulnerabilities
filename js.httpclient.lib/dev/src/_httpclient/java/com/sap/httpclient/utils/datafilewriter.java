/*
 * Copyright (c) 2002 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.utils;

import com.sap.httpclient.HttpMethod;
import com.sap.tc.logging.Location;

import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;

/**
 * Used for writing a method response to a file.
 *
 * @author Nikolai Neichev
 */
public class DataFileWriter {

  /**
   * Log object for this class.
   */
  private static final Location LOG = Location.getLocation(DataFileWriter.class);

  private String filePathName;

  /**
   * Constructor
   * @param pathName a default path name
   */
  public DataFileWriter(String pathName) {
    this.filePathName = pathName;
  }

  /**
   * Resets the output file path name
   * @param pathName the path name
   */
  public void setPathName(String pathName) {
    this.filePathName = pathName;
  }

  /**
   * Writes the response body ot the method to the previously specified file
   * @param method the http method
   * @return true if the body is written successfully, false if not
   * @throws IllegalStateException if the method is not executed
   */
  public boolean responseBodyToFile(HttpMethod method) {
    return responseBodyToFile(method, filePathName);
  }

  /**
   * Writes the response body ot the method to the specified file
   * @param method the method
   * @param pathName the specified file
   * @return true if the body is written successfully, false if not
   * @throws IllegalStateException if the method is not executed
   */
  public static boolean responseBodyToFile(HttpMethod method, String pathName) {
    if (!method.isRequestSent()) {
      throw new IllegalStateException("The method is not executed.");
    } else {
      LOG.infoT("Writing method response to file : " + pathName);
    }
    try {
      RandomAccessFile raf = new RandomAccessFile(pathName, "rw");
      InputStream is = method.getResponseBodyAsStream();
      byte[] buffer = new byte[1024];
      while (is.available() > 0) {
        int _read = is.read(buffer);
        raf.write(buffer, 0, _read);
      }
      raf.close();
      return true;
    } catch (FileNotFoundException fnfe) { // $JL-EXC$
      LOG.errorT("Unable to write/create file : " + pathName);
      return false;
    } catch (IOException fnfe) { // $JL-EXC$
      LOG.errorT("Can't read method response...");
      return false;
    }
  }

}