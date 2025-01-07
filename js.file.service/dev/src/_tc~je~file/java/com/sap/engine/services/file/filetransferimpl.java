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
 * Class FileTransferImpl is the implementation of FileTransfer interface.
 * It is the main class through which File Service works.
 * FileTransferImpl prepares the process of file upload/ download
 * through associating local files with remote ones.
 *
 * @author
 * @version 4.0.0
 */
public class FileTransferImpl extends PortableRemoteObject implements FileTransfer {

  /**
   * Empty constructor of the class.
   *
   * @exception   RemoteException  if a remote problem occurs.
   */
  public FileTransferImpl() throws RemoteException {

  }

  /**
   * Creates a remote file using a local one as a source file.
   * It is recommended to use this method when source and destination
   * files are located on remote servers. Relative paths should begin
   * from server work directory.
   *
   * @param   localFile   path to the local file.
   * @param   remoteFile  path to the remote file;
   *                      prefer using relative instead of absolute path.
   *
   * @return  the created remote file.
   * @exception   RemoteException  thrown if a remote problem occurs.
   */
  public RemoteFile createRemoteFile(String localFile, String remoteFile) throws RemoteException {
    String tempName = remoteFile;

    if (remoteFile != null) {
      tempName = remoteFile.replace('\\', '/');
    }

    return new RemoteFile(new File(localFile), new FileDataImpl(new File(tempName)));
  }

  /**
   * Creates a remote file using a local one as a source file.
   * Use this method only if source and destination files are located on one physical server.
   * Both absolute and relative paths are allowed. Relative path should begin
   * from server work directory.
   *
   * @param   localFile     file located on local logical server.
   * @param   remoteFile    file located on remote logical server.
   * @return  the created remote file.
   * @exception   RemoteException  thrown if a remote problem occurs.
   */
  public RemoteFile createRemoteFile(File localFile, File remoteFile) throws RemoteException {
    File tempName = null;

    if (remoteFile != null) {
      tempName = new File(remoteFile.getPath().replace('\\', '/'));
    }

    return new RemoteFile(localFile, new FileDataImpl(tempName));
  }

}

