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
package com.sap.engine.services.httpserver.server;

import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.interfaces.client.Request;

import java.io.File;
import java.io.IOException;

public class HttpFile {
  private Client client = null;
  private Request request = null;

  private String fileNameCannonical = null;
  private byte[] fileNameCannonicalBytes = null;
  private String ioFileNameCannonical = null;
  private byte[] ioFileNameCannonicalBytes = null;
  private File file = null;
  private boolean exists = false;
  private boolean existsParsed = false;
  private boolean isDirectory = false;
  private boolean isDirectoryParsed = false;
  private byte[] rootDirectory = null;

  public void init(Client client) {
    this.client = client;
    this.request = client.getRequest();
    init();
  }

  public void init() {
    fileNameCannonical = null;
    fileNameCannonicalBytes = null;
    ioFileNameCannonical = null;
    ioFileNameCannonicalBytes = null;
    file = null;
    exists = false;
    existsParsed = false;
    isDirectory = false;
    isDirectoryParsed = false;
    rootDirectory = null;
  }

  public String getFileNameCannonical() {
    return fileNameCannonical;
  }

  public byte[] getFileNameCannonicalBytes() {
    return fileNameCannonicalBytes;
  }

  public String getIOFileNameCannonical() {
    return ioFileNameCannonical;
  }

  public byte[] getIOFileNameCannonicalBytes() {
    return ioFileNameCannonicalBytes;
  }

  public boolean exists() {
    if (!existsParsed) {
      existsParsed = true;
      exists = file.exists();
    }
    return exists;
  }

  public boolean isDirectory() {
    if (!isDirectoryParsed) {
      isDirectoryParsed = true;
      isDirectory = file.isDirectory();
    }
    return isDirectory;
  }

  protected void initFile() throws IOException {
    if (request.getRequestLine().isEncoded()) {
      fileNameCannonical = client.getRequestAnalizer().getFilename1().toStringUTF8();
    } else {
      fileNameCannonical = client.getRequestAnalizer().getFilename1().toString();
    }
    fileNameCannonical = ParseUtils.canonicalizeFS(fileNameCannonical);
    if (fileNameCannonical.endsWith(":")) {
      fileNameCannonical += File.separator;
    }
    
    if (request.getRequestLine().isEncoded()){
      fileNameCannonicalBytes = ParseUtils.separatorsToFSEncoding(fileNameCannonical,"UTF-8");
      fileNameCannonical = new String(fileNameCannonicalBytes,"UTF-8");
    } else {
    fileNameCannonicalBytes = ParseUtils.separatorsToFS(fileNameCannonical);
    fileNameCannonical = new String(fileNameCannonicalBytes);
    }
    file = new File(fileNameCannonical);
    ioFileNameCannonical = file.getCanonicalPath();
    if (request.getRequestLine().isEncoded()){
        ioFileNameCannonicalBytes = ParseUtils.separatorsToFSEncoding(ioFileNameCannonical,"UTF-8");
        ioFileNameCannonical = new String(ioFileNameCannonicalBytes,"UTF-8");
    } else {
    ioFileNameCannonicalBytes = ParseUtils.separatorsToFS(ioFileNameCannonical);
    ioFileNameCannonical = new String(ioFileNameCannonicalBytes);
  }

  }

  protected boolean requestMathesFileName() {
    return exists() && ByteArrayUtils.equalsBytes(fileNameCannonicalBytes, ioFileNameCannonicalBytes);
  }

  protected void setRootDirectory(byte[] rootDirectory) {
    this.rootDirectory = new String(rootDirectory).replace('/', File.separatorChar).replace('\\', File.separatorChar).getBytes();
  }

  protected boolean isInRootDirectory() {
    return ByteArrayUtils.startsWith(ioFileNameCannonicalBytes, rootDirectory);
  }

  protected boolean isInWebForbiddenDirs() {
    return ByteArrayUtils.startsWithIgnoreCase(ioFileNameCannonicalBytes,
        (new String(rootDirectory) + File.separatorChar + "web-inf").getBytes())
        || ByteArrayUtils.startsWithIgnoreCase(ioFileNameCannonicalBytes,
            (new String(rootDirectory) + File.separatorChar + "meta-inf").getBytes());
  }

  protected boolean hasForbiddenExtension() {
    return ByteArrayUtils.endsWithIgnoreCase(ioFileNameCannonicalBytes, Constants.jspExtension);
  }
}
