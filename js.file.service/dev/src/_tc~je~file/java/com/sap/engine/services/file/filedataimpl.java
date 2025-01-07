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
import javax.rmi.*;

/**
 * Class FileDataImpl is the implementation of FileData interface.
 * It performs the actual upload/ download of a file by dividing transfered file in parts
 * according to the specified buffer size. Using provided by this class methods for
 * reading and writing byte array data RemoteFile class performs physical transfer of files.
 *
 * @author
 * @version 4.0.0
 */
public class FileDataImpl extends PortableRemoteObject implements FileData {

  private File file;
  private int bufferSize;
  private RandomAccessFile r;
  private int readPosition;
  private byte[] buffer;

  /**
   * Constructor of the class with the file for upload/ download specified.
   *
   * @param   file  file to be transfered.
   *
   * @exception   RemoteException  thrown if a remote problem occurs.
   */
  public FileDataImpl(File file) throws RemoteException {
    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }

    this.file = file;
    bufferSize = 16384;
    r = null;
  }

  /**
   * Opens file for reading.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void openRead() throws IOException {
    r = new RandomAccessFile(file, "r");
    r.seek(0);
    readPosition = 0;
    buffer = new byte[bufferSize];
  }

  /**
   * Opens file for writing.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void openWrite() throws IOException {
    r = new RandomAccessFile(file, "rw");
    r.seek(0);
  }

  /**
   * Closes file after it has been opened for reading.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void closeRead() throws IOException {
    r.close();
  }

  /**
   * Closes file after it has been opened for writing.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void closeWrite() throws IOException {
    r.setLength(r.getFilePointer());
    closeRead();
  }

  /**
   * Writes length bytes from the specified byte array starting at offset off to this file.
   *
   * @param   data    the data.
   * @param   off     the start offset in the data.
   * @param   length  the number of bytes to write.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public void write(byte[] data, int off, int length) throws IOException {
    r.write(data, off, length);
  }

  /**
   * Reads byte array data of a file. Number of bytes read is specified by buffer size.
   *
   * @return      byte array data read from this file.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public byte[] read() throws IOException {
    if (readPosition < r.length()) {
      if ((r.length() - readPosition) < bufferSize) {
        buffer = new byte[(int) r.length() - readPosition];
      }

      r.read(buffer);
      readPosition += buffer.length;
      return buffer;
    } else {
      return null;
    }
  }

  /**
   * Gets buffer size for reading data.
   *
   * @return   buffer size.
   */
  public int getBufferSize() {
    return bufferSize;
  }

  /**
   * Sets buffer size for reading data.
   *
   * @return   buffer size to be set.
   */
  public void setBufferSize(int size) {
    bufferSize = size;
  }

}

