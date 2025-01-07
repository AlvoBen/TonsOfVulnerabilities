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

//import com.inqmy.frame.container.log.LogContext;
/**
 * RemoteFile provides functionality for upload/ download of files located on different
 * logical servers. These servers may be positioned on one physical machine or may be on remote ones.
 * RemoteFile is responsible for the synchronization of the transferred files, too.
 *
 * @author
 * @version 4.0.0
 */
public class RemoteFile implements Serializable {

  private File local;
  private FileData remote;//$JL-SER$
  private int bufferSize;

  /**
   * Constructor of the class with the local and remote files specified.
   *
   * @param   local   the local file.
   * @param   remote  FileData object representing the remote file.
   */
  public RemoteFile(File local, FileData remote) {
    this.local = local;
    this.remote = remote;
    bufferSize = 16384;
  }

  /**
   * Uploads local file to the server.
   * Path to the local and remote files are specified in the constructor.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public synchronized void upload() throws IOException {
    //open local
    RandomAccessFile in = new RandomAccessFile(local, "r");
    in.seek(0);
    //open remote
    remote.openWrite();
    byte[] buffer = new byte[bufferSize];
    int read = in.read(buffer);

    while (read != -1) {
      remote.write(buffer, 0, read);
      read = in.read(buffer);
    }

    //TODO    FileFrame.log(LogContext.INFO, "Upload file " + local.getName());
    //close remote
    remote.closeWrite();
    //close local
    in.close();
  }

  /**
   * Downloads remote file to the local server.
   * Path to the local and remote files are specified in the constructor.
   *
   * @exception   IOException  if some problem while reading/ writing occurs.
   */
  public synchronized void download() throws IOException {
    //open local
    if (!local.getParentFile().exists()) {
      local.getParentFile().mkdirs();
    }

    RandomAccessFile out = new RandomAccessFile(local, "rw");
    out.seek(0);
    //open remote
    remote.openRead();
    byte[] data = remote.read();

    while (data != null) {
      out.write(data);
      data = remote.read();
    }

    out.setLength(out.getFilePointer());
    //TODO    FileFrame.log(LogContext.INFO, "Download file " + local.getName());
    //close remote
    remote.closeRead();
    //close local
    out.close();
  }

  /**
   * Gets buffer size of the local file for transferring data to the remote one.
   *
   * @return   the buffer size.
   */
  public int getBufferSize() {
    return bufferSize;
  }

  /**
   * Sets buffer size of the local file for transferring data to the remote one.
   *
   * @param   bufferSize  the buffer size to be set.
   */
  public void setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
  }

  /**
   * Gets buffer size of the remote file for transferring data to the local one.
   *
   * @return  the buffer size.
   */
  public int getRemoteBufferSize() {
    return remote.getBufferSize();
  }

  /**
   * Sets buffer size of the remote file for transferring data to the local one.
   *
   * @param   size  the buffer size to be set.
   */
  public void setRemoteBufferSize(int size) {
    remote.setBufferSize(size);
  }

}

