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
 * FileTransfer interface provides methods for creating files remotely.
 * Actually this interface prepares the process of file upload/ download
 * through associating local files with remote ones.
 *
 * @author
 * @version 4.0.0
 */
public interface FileTransfer
  extends Remote {

  /**
   * Creates a remote file using a local one as a source file.
   *
   * @param   localFile   path to the local file.
   * @param   remoteFile  path to the remote file.
   *
   * @return  the created remote file.
   * @exception   RemoteException  thrown if a remote problem occurs.
   */
  public RemoteFile createRemoteFile(String localFile, String remoteFile) throws RemoteException;


  /**
   * Creates a remote file using a local one as a source file.
   *
   * @param   localFile     file located on local logical server.
   * @param   remoteFile    file located on remote logical server.
   * @return  the created remote file.
   * @exception   RemoteException  thrown if a remote problem occurs.
   */
  public RemoteFile createRemoteFile(File localFile, File remoteFile) throws RemoteException;

}

