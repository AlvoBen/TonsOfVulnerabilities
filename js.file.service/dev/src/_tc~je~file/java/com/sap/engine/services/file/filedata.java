/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.file;

import java.io.*;
import java.rmi.*;

/**
 * FileData interface is responsible for the actual upload/ download of files.
 * It divides transfered file in parts according to the specified buffer size.
 * This interface provides methods for reading and writing of byte array data
 * through which RemoteFile class performs physical transfer of files.
 *
 * @author
 * @version 4.0.0
 */
public interface FileData
  extends Remote {

  /**
   * Reads byte array data of a file.
   *
   * @return      byte array data read from this file.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public byte[] read() throws IOException;


  /**
   * Writes length bytes from the specified byte array starting at offset off to this file.
   *
   * @param   data    the data.
   * @param   off     the start offset in the data.
   * @param   length  the number of bytes to write.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void write(byte[] data, int off, int length) throws IOException;


  /**
   * Opens file for reading.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void openRead() throws IOException;


  /**
   * Opens file for writing.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void openWrite() throws IOException;


  /**
   * Closes file after it has been opened for reading.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void closeRead() throws IOException;


  /**
   * Closes file after it has been opened for writing.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void closeWrite() throws IOException;


  /**
   * Gets buffer size for reading data.
   *
   * @return   buffer size.
   */
  public int getBufferSize();


  /**
   * Sets buffer size for reading data.
   *
   * @param   size  buffer size to be set.
   */
  public void setBufferSize(int size);

}

