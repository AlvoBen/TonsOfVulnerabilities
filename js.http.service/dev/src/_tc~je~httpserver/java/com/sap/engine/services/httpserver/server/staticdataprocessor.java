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

import com.sap.engine.services.httpserver.lib.*;
import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.interfaces.exceptions.ParseException;

import java.io.File;
import java.io.IOException;

public class StaticDataProcessor {
  private Client client = null;
  private ResponseImpl response = null;
  private RequestImpl request = null;
  private HttpFile httpFile = new HttpFile();

  public void init(Client client, RequestImpl request, ResponseImpl response) {
    this.client = client;
    this.request = request;
    this.response = response;
    httpFile.init(client);
  }

  /*
   * This method finds the requested file on the disk. Initialize it. take its cannonical paths.
   * Then checks if the request is corect - i.e. if the requested file is in the root directoy of the application,
   * if the requested file type or directory is forbidden, if the requested file name doesn't match the name of the file.
   *
   * @param filename  the name of the requested file as found in the request UTI
   * @return          HttpFile representing the requested file on the file system
   * or null if the request path doesn't match the file
   */
  public HttpFile getFileForRequest() {
    if (ByteArrayUtils.indexOf(ResponseImpl.allowed1, request.getRequestLine().getMethod()) < 0) {
      return null;
    }
    try {
      httpFile.initFile();
    } catch (IOException e) {
      Log.logWarning("ASJ.http.000100", 
        "Cannot open the requested file [{0}]. " +
        "Possible reason: the file does not exist, cannot be accessed, or is in use by another process.", 
        new Object[]{httpFile.getFileNameCannonical()}, e, request.getClientIP(), null, null);
      return null;
    }
    if (!httpFile.requestMathesFileName()) {
      return null;
    }
    if (!isRequestedFileAllowed()) {
      return null;
    }
    return httpFile;
  }

  public void initForwarding() {
    //if filename is directory looking for default names or dirlist
    if (!httpFile.isDirectory()) {
      return;
    }
    forwardToDirOrIndexPages();
  }

  // ------------------------ PRIVATE ------------------------

  /**
   * Check if request file is in root directory or in aliases directories.
   * @return             true if file is in these directories
   */
  private boolean isRequestedFileAllowed() {
    httpFile.setRootDirectory(client.getRequestAnalizer().getHostProperties().getRootDir().getBytes());
    // if is IN the directory .. i.e. checks for ../../...
    if (!httpFile.isInRootDirectory()) {
      httpFile.setRootDirectory(client.getRequestAnalizer().getRequestPathMappings().getAliasValue().getBytes());
      if (!httpFile.isInRootDirectory()) {
        return false;
      }
    }
    // /web-inf/* and /meta-inf/*; *.jsp
    if (httpFile.isInWebForbiddenDirs() || httpFile.hasForbiddenExtension()) {
      return false;
    }
    return true;
  }

  private void forwardToDirOrIndexPages() {
    MessageBytes requestUrl = request.getRequestLine().getUrlDecoded();
    if (requestUrl.charAt(requestUrl.length() - 1) == ParseUtils.separatorChar) {
      forwardToIndexPages();
    } else {
      //forward to the directory + "/"
      if (request.getRequestLine().isEncoded()) {
        try {
          MessageBytes location = request.getRequestLine().getUrlNotDecoded();
          location.appendAfter(ParseUtils.separatorBytes);
          response.setChangeLocation(ProtocolParser.makeAbsolute(
              location.toStringUTF8(),
              new MessageBytes(),
              client.getRequest().getScheme(),
              client.getRequest().getHost(),
              client.getRequest().getPort()).getBytes());
          return;
        } catch (ParseException e) {
          Log.logError("ASJ.http.000221", "Cannot parse the request URL. Cannot redirect to directory.", e, client.getIP(), null, null);
        }
      }
      requestUrl.appendAfter(ParseUtils.separatorBytes);
      response.setChangeLocation(ProtocolParser.makeAbsolute(
          requestUrl.toString(),
          new MessageBytes(),
          client.getRequest().getScheme(),
          client.getRequest().getHost(),
          client.getRequest().getPort()).getBytes());
    }
  }

  private void forwardToIndexPages() {
    File indexFile = null;
    int len = ServiceContext.getServiceContext().getHttpProperties().getInfernames().length;
    for (int j = 0; j < len; j++) {
      indexFile = new File(httpFile.getIOFileNameCannonical(), ServiceContext.getServiceContext().getHttpProperties().getInfernames()[j]);
      if (indexFile.exists()) {
        MessageBytes location = request.getRequestLine().getUrlDecoded();
        location.appendAfter(ServiceContext.getServiceContext().getHttpProperties().getInfernames()[j].getBytes());
        response.setChangeLocation(ProtocolParser.makeAbsolute(
            location.toString(),
            new MessageBytes(),
            client.getRequest().getScheme(),
            client.getRequest().getHost(),
            client.getRequest().getPort()).getBytes());
        return;
      }
    }
  }
}
