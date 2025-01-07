/*
 * Copyright (c) 2006-2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import static com.sap.engine.services.httpserver.server.Log.getExceptionStackTrace;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

import com.sap.engine.services.httpserver.CommunicationConstants;
import com.sap.engine.services.httpserver.exceptions.HttpException;
import com.sap.engine.services.httpserver.interfaces.ErrorData;
import com.sap.engine.services.httpserver.interfaces.SupportabilityData;
import com.sap.engine.services.httpserver.interfaces.client.Response;
import com.sap.engine.services.httpserver.interfaces.io.HttpInputStream;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.lib.ProtocolParser;
import com.sap.engine.services.httpserver.lib.ResponseCodes;
import com.sap.engine.services.httpserver.lib.Responses;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.lib.protocol.HeaderNames;
import com.sap.engine.services.httpserver.lib.protocol.HeaderValues;
import com.sap.engine.services.httpserver.lib.protocol.Methods;
import com.sap.engine.services.httpserver.lib.util.Ascii;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.lib.util.CharArrayUtils;
import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.server.errorreport.ErrorReportInfoBean;
import com.sap.engine.services.httpserver.server.io.ByteArrayOutputStream;
import com.sap.engine.services.httpserver.server.io.HttpOutputStream;
import com.sap.engine.session.Session;

/**
 * Make response for received request!
 *
 * @author Galin Galchev
 * @version 4.0
 */
public class ResponseImpl implements Response, Constants {
  private Client client = null;
  private GzipCompression gzipCompression = null;

  private int status_code = ResponseCodes.code_ok;
  private byte[] reason_phrase = ResponseCodes._ok;
  private MimeHeaders headers = null;
  private HttpOutputStream httpOutputStream = null;
  private ByteArrayOutputStream byteArrayOutputStream = null;

  private boolean connectionKeepAlive = false;
  private byte connectionFlag = CommunicationConstants.RESPONSE_FLAG_KEEP_ALIVE;
  private boolean sendfile = true; //true if have file to send
  private boolean isHEAD = false;
  private boolean done = false;
  private byte[] buffer = null;
  private boolean isEncoded = false;
  private boolean connectionClosed = false;
  private byte[] changeLocation = null;
  private boolean name_replaced = false;
  private int responseLength = -1;
  private boolean schemeChanged = false;
  private boolean locked = false;
  /**
   * used by DSR to indicated if the resource is for the static resource or not
   */
  private boolean isStaticResourceServed = false;
  /*
   * Used for monitoring.
   */
  private boolean cachedReply = false;
  private boolean notSendAnyMore = false;
  private int ldtCounter = 0;
  private boolean ldtUsed = false;
  private boolean ldtNotified = false;
  private boolean ldtAlive = false;
  //NOTE!!!! if you add any variable please add it to init and clone methods

  public ResponseImpl(Client client) {
    this.client = client;
    this.headers = new MimeHeaders();
    this.httpOutputStream = new HttpOutputStream(client);
    this.byteArrayOutputStream = new ByteArrayOutputStream();
    this.gzipCompression = new GzipCompression(client.getWorkingDir(), ServiceContext.getServiceContext().getHttpProperties().getCompressedProperties());
  }

  public void recycle() {
    headers.clear();
    status_code = ResponseCodes.code_ok;
    reason_phrase = ResponseCodes._ok;
    isHEAD = false;
    connectionFlag = CommunicationConstants.RESPONSE_FLAG_KEEP_ALIVE;
    done = false;
    buffer = null;
    isEncoded = false;
    connectionClosed = false;
    sendfile = true; //true if have file to send
    changeLocation = null;
    name_replaced = false;
    responseLength = -1;
    schemeChanged = false;
    locked = false;
    connectionKeepAlive = false;
    cachedReply = false;
    httpOutputStream.reset();
    byteArrayOutputStream.reset();
    headers.clear();
    gzipCompression.init();
    notSendAnyMore = false;
    ldtCounter = 0;
    ldtUsed = false;
    ldtNotified = false;
    ldtAlive = false;
    isStaticResourceServed = false;
  }

  public void init(ResponseImpl oldResponse) {
    status_code = oldResponse.status_code;
    reason_phrase = oldResponse.reason_phrase;
    headers = (MimeHeaders)oldResponse.headers.clone();
    oldResponse.httpOutputStream = new HttpOutputStream(oldResponse.client);
    oldResponse.byteArrayOutputStream = new ByteArrayOutputStream();
    connectionFlag = oldResponse.connectionFlag;
    sendfile = oldResponse.sendfile; //true if have file to send
    isHEAD = oldResponse.isHEAD;
    done = oldResponse.done;
    buffer = oldResponse.buffer;
    isEncoded = oldResponse.isEncoded;
    connectionClosed = oldResponse.connectionClosed;
    changeLocation = oldResponse.changeLocation;
    name_replaced = oldResponse.name_replaced;
    responseLength = oldResponse.responseLength;
    schemeChanged = oldResponse.schemeChanged;
    connectionKeepAlive = oldResponse.connectionKeepAlive;
    cachedReply = oldResponse.cachedReply;
    notSendAnyMore = oldResponse.notSendAnyMore;
    ldtCounter = oldResponse.ldtCounter;
    ldtUsed = oldResponse.ldtUsed;
    ldtNotified = oldResponse.ldtNotified;
    ldtAlive = oldResponse.ldtAlive;
    oldResponse.locked = true;
    isStaticResourceServed = oldResponse.isStaticResourceServed;
  }

  private void setStatusCode(int status_code) {
    this.status_code = status_code;
  }

  private void setStatusMessage(byte[] reason_phrase) {
    this.reason_phrase = reason_phrase;
  }

  public byte getConnectionFlag() {
    return connectionFlag;
  }

  public void putHeader(byte[] name, byte[] value) {
    headers.putHeader(name, value);
  }

  public MimeHeaders getHeaders() {
    return headers;
  }

  public boolean isPersistentConnection() {
    return connectionKeepAlive;
  }

  public void setPersistentConnection(boolean keepAlive) {
    this.connectionKeepAlive = keepAlive;
    if (keepAlive) {
      this.connectionFlag = CommunicationConstants.RESPONSE_FLAG_KEEP_ALIVE;
    } else {
      this.connectionFlag = CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION;
      if (client.getRequest().getRequestLine().getHttpMajorVersion() >= 1
          && client.getRequest().getRequestLine().getHttpMinorVersion() >= 1) {
        headers.putHeader(HeaderNames.hop_header_connection_, HeaderValues.close_);
      }
    }
  }

  public void setEncoded(boolean isEncoded) {
    this.isEncoded = isEncoded;
  }

  public boolean isEncoded() {
    return isEncoded;
  }

  public void done() {
    this.done = true;
  }
  
  public boolean isDone() {
    return done;
  }
  
  public void setChangeLocation(byte[] changeLocation) {
    this.changeLocation = changeLocation;
  }

  public boolean isLocked() {
    return locked;
  }

  // ------------------- RESPONSE ---------------------

  public void setResponseCode(int statusCode) {
    if (locked) {
      return;
    }
    setStatusCode(statusCode);
  }

  public void sendResponse(byte[] responseByte, int offset, int length)
      throws IOException {
    if (locked) { return; }
    client.send(responseByte, offset, length);
  }

  public void flush() throws IOException {
    if (locked) { return; }
    client.flush();
  }

  public void sendResponse(int responseCode) {
    if (locked) {
      return;
    }
    String aliasName = "";
    if (client.getRequestAnalizer().getRequestPathMappings().getAliasName() != null) {
      aliasName = client.getRequestAnalizer().getRequestPathMappings().getAliasName().toString();
    }
    setStatusCode(responseCode);
    setStatusMessage(ResponseCodes.reasonBytes(responseCode, aliasName));
    sendResponse();
  }

  /**
   * Sends an error response to the client using the specified status.
   * By default the server creates the response to look like an HTML-formatted
   * error page containing the specified message and details, setting the
   * content type to "text/html", leaving cookies and other headers unmodified.
   * Based on <code>ErrorData.isHtmlAllowed()</code> constructs the error page with html allowed or not.
   * If there is custom error handler the method will delegate the error processing to it.
   *
   * <p>If the response has already been committed, this method throws an
   * IllegalStateException. After using this method, the response should be
   * considered to be committed and should not be written to.</p>
   *
   * @param errorData the error data.
   * @see For more info see <code>com.sap.engine.services.httpserver.interfaces.ErrorData</code>
   */
  public void sendError(ErrorData errorData) {
    if (locked) {
      return;
    }

    boolean customErrorHandlerExists = true;
    
    if (client.getRequestAnalizer().getRequestPathMappings().getRealPath() == null) {
      try {
        client.getRequestAnalizer().findAndInitAlias();
      } catch (Exception e) {
        // $JL-EXC$
        customErrorHandlerExists = false;
      }     
    }

    if (client.getRequestAnalizer().getRequestPathMappings().getAliasName() == null) {
      customErrorHandlerExists = false;
    }
    
    if (customErrorHandlerExists) {
      headers.putHeader(HeaderNames.entity_header_cache_control_, HeaderValues.no_cache_);
      headers.putIntHeader(HeaderNames.entity_header_expires_, 0);
      
      client.getRequestAnalizer().setErrorData(errorData);
      
      if (ServiceContext.getServiceContext().getHttpProvider().getWebContainer() == null) {
        customErrorHandlerExists = false;
      } else if (!ServiceContext.getServiceContext().getHttpProvider().getWebContainer().handleError(client.getRequestAnalizer())) {
        customErrorHandlerExists = false;
      }
    }
    
    if (!customErrorHandlerExists) {
      sendResponse(constructError(errorData).getBytes());
    }
  }//end of sendError(ErrorData errorData)

  public void sendApplicationStopped(byte[] body){
    if (locked) {
      return;
    }
    connectionFlag = CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION;
    String aliasName = "";
    if (client.getRequestAnalizer().getRequestPathMappings().getAliasName() != null) {
      aliasName = client.getRequestAnalizer().getRequestPathMappings().getAliasName().toString();
    }
    status_code = ResponseCodes.code_service_unavailable;
    reason_phrase = ResponseCodes.reasonBytes(ResponseCodes.code_service_unavailable, aliasName);
    headers.putHeader(HeaderNames.hop_header_connection_, HeaderValues.close_);
    headers.putHeader(HeaderNames.entity_header_pragma_, HeaderValues.no_cache_);
    headers.putHeader(HeaderNames.entity_header_cache_control_, HeaderValues.no_cache_);
    headers.putIntHeader(HeaderNames.entity_header_expires_, 0);
    sendResponse(body);
  }

  public void setSchemeHttps() {
    schemeChanged = true;
  }

  public boolean isSchemeChanged() {
    return schemeChanged;
  }

  public byte[] getResponsePhrase() {
    return reason_phrase;
  }

  /**
   * Check the file extension and return it's type
   * @param file        file name
   * @return                MIME type
   */
  private byte[] getContentType(String file) {
    char[] filename = file.toCharArray();
    int i = CharArrayUtils.lastIndexOf(filename, '.');
    if (i == -1) {
      return HeaderValues.content_unknown_;
    }
    for (int j = filename.length - 1; j >= i; j--) {
      filename[j] = (char)Ascii.toLower(filename[j]);
    }
    String contentType = null;
    char[] ext = new char[filename.length - i];
    System.arraycopy(filename, i, ext, 0, ext.length);
    // This char buffer contains the file extension without dot,
    // that is required by HttpHandler.checkMIME(..) method
    char[] extWithoutDot = new char[filename.length - i - 1];
    System.arraycopy(filename, i + 1, extWithoutDot, 0, extWithoutDot.length);
    if (ServiceContext.getServiceContext().getHttpProvider().getWebContainer() != null) {
      contentType = ServiceContext.getServiceContext().getHttpProvider()
        .getWebContainer().checkMIME(extWithoutDot,
          client.getRequestAnalizer().getRequestPathMappings().getAliasName());
    }
    if (contentType == null) {
      contentType = ServiceContext.getServiceContext().getHttpProperties()
        .getMimeMappings().getMimeType(new String(ext));
    }
    if (contentType == null) {
      return HeaderValues.content_unknown_;
    } else {
      return contentType.getBytes();
    }
  }

  /**
   * Check if file is modified since given date
   * @return        true if it is
   */
  private boolean checkIfModified(File f) {
    long date1 = client.getRequestAnalizer().getDateHeader(HeaderNames.request_header_if_modified_since_);
    if (date1 != -1) {
      return f.lastModified() - date1 > 1000;
    }
    return true;
  }

  /**
   * Insert status line and header.
   */
  private byte[] insertStatusLine(byte close, boolean keepHeaders) {
    if (client.getRequest().getRequestLine().isSimpleRequest()) {
      return new byte[0];
    }
    client.getRequest().getBody();
    checkEOBody();
    httpOutputStream.reset();
    if (keepHeaders) {
      httpOutputStream.keepAll();
    }
    headers.putDateHeader(HeaderNames.entity_header_date_);
    httpOutputStream.write(HTTP_11);
    if (ResponseCodes.status_code_byte[status_code] == null) {
      ResponseCodes.status_code_byte[status_code] = (" " + status_code).getBytes();
    }
    httpOutputStream.write(ResponseCodes.status_code_byte[status_code]);
    httpOutputStream.write(32);
    httpOutputStream.write(reason_phrase);
    httpOutputStream.write(13);
    httpOutputStream.write(10);
    if (connectionFlag != CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION) {
      if (client.getRequest().getRequestLine().getHttpMajorVersion() == 0
              || client.getRequest().getRequestLine().getHttpMajorVersion() == 1
              &&  client.getRequest().getRequestLine().getHttpMinorVersion() == 0) {
        if (!headers.containsHeader(HeaderNames.hop_header_connection)) {
          headers.putHeader(HeaderNames.hop_header_connection_, HeaderValues.keep_alive_);
        }
      }
    }
    try {
      headers.write(httpOutputStream);
    } catch (IOException io) {
      Log.logWarning("ASJ.http.000086",
        "Error in writing HTTP response headers to client output stream.", io, null, null, null);
    }
    httpOutputStream.write(13);
    httpOutputStream.write(10);
    httpOutputStream.flush(close);
    return httpOutputStream.toByteArray();
  }

  /**
   * Send response header + file.
   * @param f        file to send
   */
  private void sendResponse(File f) {
    byte[] head = null;
    int readed;
    RandomAccessFile r = null;
    String fName = f.getName();
    String str_Content_Type = new String(getContentType(fName));
    if (isEncoded) {
      isEncoded = ServiceContext.getServiceContext().getHttpProperties().getCompressedProperties().isGzip(fName, str_Content_Type);
    }

    if (isEncoded) {
      if ((ServiceContext.getServiceContext().getHttpProperties().getCompressedProperties().getMaximumCompressedURLLength() > -1) &&
         (ServiceContext.getServiceContext().getHttpProperties().getCompressedProperties().getMaximumCompressedURLLength() <
         getCompressedURLLength(client.getRequestAnalizer().getHostPropertiesInternal().getHostName(),
         client.getRequest().getRequestLine().getUrlDecoded().toString(), f.getName()))) {
           isEncoded = false;
      }
    }

    long r_len = -1;
    try {
    try {
      r = new RandomAccessFile(f, "r");
      r_len = r.length();
    } catch (IOException ioe) {
      String logID = Log.logWarning("ASJ.http.000350", "File opening failed.", ioe, client.getIP(), null, null);
   	  HttpException exc = new HttpException(HttpException.HTTP_READFILE_ERROR);
      SupportabilityData supportabilityData = new SupportabilityData(true, getExceptionStackTrace(ioe), logID);
      if (supportabilityData.getMessageId().equals("")) {
        supportabilityData.setMessageId("com.sap.ASJ.http.000350");
      }
      //TODO : Vily G : if there is no DC and CSN in the supportability data
      //and we are responsible for the problem then set our DC and CSN
      //otherwise leave them empty
      ErrorData errorData = new ErrorData(ResponseCodes.code_internal_server_error,
        exc.getLocalizedMessage(), Log.formatException(ioe, logID), false, supportabilityData);
      errorData.setErrorByCode(false);
      errorData.setException(ioe);
      sendError(errorData);
      return;
    }
    if (isEncoded) {
      if (ServiceContext.getServiceContext().getHttpProperties().getCompressedProperties().getMinGZipLength() >= 0
          && r_len > ServiceContext.getServiceContext().getHttpProperties().getCompressedProperties().getMinGZipLength()) {
        try {
          r.close();
          r = gzipCompression.compressFile(client.getRequestAnalizer().getHostPropertiesInternal().getHostName(),
              client.getRequest().getRequestLine().getUrlDecoded().toString(), f, headers,  client.getRequestAnalizer().getTranslationTable());
          r_len = r.length();
        } catch (IOException e) {
          Log.logWarning("ASJ.http.000217", "Cannot compress file <{0}>.",
            new Object[]{client.getRequest().getRequestLine().getUrlDecoded().toString()}, e, client.getIP(), null, null);
          //the file cannot be compressed
        }
      } else {
        isEncoded = false;
      }
    }
    if (isHEAD) {
      insertStatusLine(connectionFlag, false);
      return;
    }
    head = insertStatusLine(CommunicationConstants.RESPONSE_FLAG_NOOP, true);
    try {
      r.seek(0);
      buffer = new byte[ServiceContext.getServiceContext().getHttpProperties().getFileBufferSize()];
      readed = r.read(buffer);
    } catch (IOException io) {
      String logID = Log.logWarning("ASJ.http.000087", "Reading from file failed.", io, client.getIP(), null, null);
      SupportabilityData supportabilityData = new SupportabilityData(true, getExceptionStackTrace(io), logID);
      if (supportabilityData.getMessageId().equals("")) {
        supportabilityData.setMessageId("com.sap.ASJ.http.000087");
      }
      //TODO : Vily G : if there is no DC and CSN in the supportability data
      //and we are responsible for the problem then set our DC and CSN
      //otherwise leave them empty
      ErrorData errorData = new ErrorData(ResponseCodes.code_internal_server_error,
        Responses.mess13, Log.formatException(io, logID), false, supportabilityData);
      errorData.setErrorByCode(false);
      errorData.setException(io);
      sendError(errorData);
      return;
    }
    if (readed == -1) {
      if (!isConnectionMarkedClosed()) {
        markConnectionClosed();
      }
    }
    while (readed > -1) {
      if (readed < buffer.length) {
        client.send(buffer, 0, readed, connectionFlag);
        if ((connectionFlag & CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION) == CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION && !isConnectionMarkedClosed()) {
          markConnectionClosed();
          break;
        }
      } else {
        client.send(buffer, 0, readed, (byte) 0);
      }

      buffer = new byte[ServiceContext.getServiceContext().getHttpProperties().getFileBufferSize()];
      try {
        readed = r.read(buffer);
      } catch (IOException t) {
        Log.logWarning("ASJ.http.000088", "File read for long data transfer failed.", t, client.getIP(), null, null);
        return;
      }
    }

      if (readed == -1) {
        if ((connectionFlag & CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION) == CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION && !isConnectionMarkedClosed()) {
          markConnectionClosed();
        }
      }
    } finally {
      try {
        r.close();
      } catch (IOException t) {
        Log.logError("ASJ.http.000218",
          "Cannot close file {0} after sending the response.", new Object[]{f.getName()}, t, client.getIP(), null, null);
      }
    }
  }

  private int getCompressedURLLength(String host, String fileName, String sourceFile) {
    String zipFileName = (client.getWorkingDir() + File.separator + host + File.separator + fileName).replace(ParseUtils.separatorChar, File.separatorChar);
    if (zipFileName.endsWith(File.separator)) {
      zipFileName = zipFileName + sourceFile;
    }
    zipFileName += ".gzip";
    return zipFileName.length();
  }

  /**
   * Send response header + file (from start position to finish position) - used for resuming.
   * @param f            file to send
   * @param start        start position
   * @param finish       end position
   */
  private void sendResponse(File f, int start, int finish) {
    if (isHEAD) {
      insertStatusLine(connectionFlag, false);
      return;
    } else {
      insertStatusLine(CommunicationConstants.RESPONSE_FLAG_NOOP, false);
    }

    int readed;
    int rest = finish - start + 1;
    RandomAccessFile r = null;
    try {
      r = new RandomAccessFile(f, "r");
      r.seek(start);
    } catch (IOException t) {
      String logID = Log.logWarning("ASJ.http.000089",
        "File read open on file [{0}] failed.", new Object[]{f}, t, client.getIP(), null, null);
   	  HttpException exc = new HttpException(HttpException.HTTP_OPENFILE_ERROR);
      SupportabilityData supportabilityData = new SupportabilityData(true, getExceptionStackTrace(t), logID);
      if (supportabilityData.getMessageId().equals("")) {
        supportabilityData.setMessageId("com.sap.ASJ.http.000089");
      }
      //TODO : Vily G : if there is no DC and CSN in the supportability data
      //and we are responsible for the problem then set our DC and CSN
      //otherwise leave them empty
      ErrorData errorData = new ErrorData(ResponseCodes.code_internal_server_error,
        exc.getLocalizedMessage(), Log.formatException(t, logID), false, supportabilityData);
      errorData.setErrorByCode(false);
      errorData.setException(t);
      sendError(errorData);
      return;
    }
    buffer = new byte[ServiceContext.getServiceContext().getHttpProperties().getFileBufferSize()];
    try {
      readed = r.read(buffer);
    } catch (IOException t) {
      String logID = Log.logWarning("ASJ.http.000102", "Read from file [{0}] failed.",
        new Object[]{f}, t, client.getIP(), null, null);
      SupportabilityData supportabilityData = new SupportabilityData(true, getExceptionStackTrace(t), logID);
      if (supportabilityData.getMessageId().equals("")) {
        supportabilityData.setMessageId("com.sap.ASJ.http.000102");
      }
      //TODO : Vily G : if there is no DC and CSN in the supportability data
      //and we are responsible for the problem then set our DC and CSN
      //otherwise leave them empty
      ErrorData errorData = new ErrorData(ResponseCodes.code_internal_server_error,
        Responses.mess12, Log.formatException(t, logID), false, supportabilityData);
      errorData.setErrorByCode(false);
      errorData.setException(t);
      sendError(errorData);
      return;
    }

    while ((rest > 0) && (readed > -1)) {
      if (rest > readed) {
        if (readed < buffer.length) {
          client.send(buffer, 0, readed, connectionFlag);
          if ((connectionFlag & CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION) == CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION && !isConnectionMarkedClosed()) {
            markConnectionClosed();
          }

          break;
        } else {
          client.send(buffer, 0, readed, (byte) 0);
        }
      } else {
        client.send(buffer, 0, rest, connectionFlag);
        if ((connectionFlag & CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION) == CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION && !isConnectionMarkedClosed()) {
          markConnectionClosed();
        }
        break;
      }

      rest -= readed;
      buffer = new byte[ServiceContext.getServiceContext().getHttpProperties().getFileBufferSize()];
      try {
        readed = r.read(buffer);
      } catch (IOException t) {
        Log.logWarning("ASJ.http.000090", "Reading from file for long data transfer failed.", t, client.getIP(), null, null);
        return;
      }
    }

    if (readed == -1) {
      if ((connectionFlag & CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION) == CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION && !isConnectionMarkedClosed()) {
        markConnectionClosed();
      }
    }

    try {
      r.close();
    } catch (IOException t) {
      Log.logWarning("ASJ.http.000103",
        "Cannot close file [{0}] after sending the response.", new Object[]{f.getName()}, t, client.getIP(), null, null);
    }
  }

  /**
   * Send response (just header).
   */
  private void sendResponse() {
    if (ServiceContext.getServiceContext().getHttpProperties().getUseServerHeader()) {
      headers.putHeader(HeaderNames.response_header_server_, client.getRequestAnalizer().getHostDescriptor().getVersion());
    }
    insertStatusLine(connectionFlag, false);
  }

  /**
   * Send response header + string - use for HTTP errors.
   * @param message        string to send
   */
  public void sendResponse(byte[] message) {
    if (status_code == ResponseCodes.code_not_found) {
      String alias = null;
      if (client.getRequestAnalizer().getRequestPathMappings().getAliasName() == null) {
        alias = "/";
      } else {
        alias = client.getRequestAnalizer().getRequestPathMappings().getAliasName().toString();
      }
      headers.putHeader(HeaderNames.propriatory_sap_isc_etag_, HeaderValues.getSapIscEtag(alias));
    }
    headers.putHeader(HeaderNames.entity_header_content_type_, HeaderValues.text_html_);
    headers.putIntHeader(HeaderNames.entity_header_content_length_, message.length);
    if (ServiceContext.getServiceContext().getHttpProperties().getUseServerHeader()) {
      headers.putHeader(HeaderNames.response_header_server_, ServiceContext
        .getServiceContext().getHttpHosts().getVersion());
    }
    if (isHEAD) {
      insertStatusLine(connectionFlag, false);
      return;
    } else {
      insertStatusLine(CommunicationConstants.RESPONSE_FLAG_NOOP, false);
    }
    client.send(message, 0, message.length, connectionFlag);
    if ((connectionFlag & CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION) == CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION && !isConnectionMarkedClosed()) {
      markConnectionClosed();
    } else if (connectionFlag != 0) {
      notSendAnyMore = true;
    }
  }

  /**
   * Generates and sends the response with the given message
   * The status line and headers has to be generated already
   * in advance. The headers are extracted from the http headers
   * and sent to the client
   *
   * @param message        string to send
   */
  public void makeAnswerMessage(byte[] message) {
    insertStatusLine(CommunicationConstants.RESPONSE_FLAG_NOOP, false);
    client.send(message, 0, message.length, connectionFlag);
    if ((connectionFlag & CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION) == CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION && !isConnectionMarkedClosed()) {
      markConnectionClosed();
    } else if (connectionFlag != 0) {
      notSendAnyMore = true;
    }
    done();
  }

  /**
   * Constructs error + comment in HTML format
   * DO NOT USE this method separately. This method is used only in sendError() method.
   * If you want to send error response, please use sendError() method.
   *
   * @param errorData the error data
   * @return HTML string representation of the error page.
   */
  public String constructError(ErrorData errorData) {
    int code = errorData.getErrorCode();
    String details = errorData.getAdditionalMessage();

    setConnectionType();
    String aliasName = "";
    if (client.getRequestAnalizer().getRequestPathMappings().getAliasName() != null) {
      aliasName = client.getRequestAnalizer().getRequestPathMappings().getAliasName().toString();
    }
    status_code = code;
    reason_phrase = ResponseCodes.reasonBytes(code, aliasName);
    if (code == ResponseCodes.code_not_found && !errorData.isHtmlAllowed()) {
      headers.putHeader(HeaderNames.propriatory_sap_isc_etag_, HeaderValues.getSapIscEtag(aliasName));
    }
    headers.putHeader(HeaderNames.entity_header_pragma_, HeaderValues.no_cache_);
    headers.putHeader(HeaderNames.entity_header_cache_control_, HeaderValues.no_cache_);
    headers.putIntHeader(HeaderNames.entity_header_expires_, 0);

    boolean detailedErrorResponse = ServiceContext.getServiceContext().getHttpProperties().isDetailedErrorResponse();
    boolean generateErrorReports = ServiceContext.getServiceContext().getHttpProperties().isGenerateErrorReports();
    String id = "";
    if (generateErrorReports && errorData.getSupportabilityData().isUserActionNeeded()) {
      if (errorData.getSupportabilityData().getLogId().equals("") && details != null) {
        int ind = details.indexOf("log ID");
        if (ind != -1) {
          errorData.getSupportabilityData().setLogId(details.substring(details.indexOf("[", ind), details.indexOf("]", ind) + 1));
        }
      }

      long time = System.currentTimeMillis();
      id = (time + client.getClientId()) + "";
      Session session = (Session) client.getRequestAnalizer().getApplicationSession();
      String jsessionId = (session != null) ? session.sessionId() : "";
      ServiceContext.getServiceContext().getHttpProvider().getErrorReportInfos().put(id,
        new ErrorReportInfoBean(ParseUtils.ipToString(client.getIP()), jsessionId, time, aliasName, code, errorData.getSupportabilityData()));
    } else {
      id = null;
    }

    return Responses.getErrorResponse(errorData, ServiceContext.getServiceContext().getHttpHosts().getVersion(),
      aliasName, detailedErrorResponse ? errorData.getSupportabilityData().getMessageId() : "",
      id != null ? "/@@@GenerateErrorReport@@@?id=" + id : null, 
      ServiceContext.getServiceContext().getHttpProperties().getTroubleShootingGuideURL(),
      ServiceContext.getServiceContext().getHttpProperties().getTroubleShootingGuideSearchURL(), null);
  }

  /**
   * If file is found but with other name or in other path send this response.
   * Used for start page or when have/missing separator in end of file
   *
   * @param filename filename with full path
   */
  protected void sendChangeLocation(byte[] filename) {
    ByteArrayUtils.replace(filename, (byte) File.separatorChar, ParseUtils.separatorByte);
    headers.putHeader(HeaderNames.response_header_location_,
                      ProtocolParser.makeAbsolute(
                          encodeUrl(new String(filename)),
                          client.getRequestAnalizer().getRequestPathMappings().getAliasName(),
                          client.getRequest().getScheme(), client.getRequest().getHost(), client.getRequest().getPort()).getBytes());
    status_code = ResponseCodes.code_found;
    reason_phrase = ResponseCodes._found;
    sendResponse();
  }

  private String encodeUrl(String url) {
    MessageBytes requestUrl = client.getRequest().getRequestLine().getFullUrl();
    int semicolon = requestUrl.indexOf(';');
    if (semicolon == -1 || url.indexOf(';') > -1) {
      return url;
    }
    int questionmmark = requestUrl.indexOf('?');
    if (questionmmark != -1 && questionmmark < semicolon) {
      return url;
    }
    String urlEncoding = requestUrl.toString(semicolon);
    if (questionmmark != -1) {
      urlEncoding = urlEncoding.substring(0, urlEncoding.indexOf('?'));
    }
    if (url.indexOf('?') > 0) {
      url = url.substring(0, url.indexOf('?')) + urlEncoding + url.substring(url.indexOf('?'));
    } else {
      url += urlEncoding;
    }
    return url;
  }

  /**
   * If request is for part of file (resuming), check for it and send the part of file.
   * @param f        file to send
   * @return         true if send part of file.
   */
  private boolean checkRangeAndSend(File f) {
    byte[] temp = client.getRequestAnalizer().getHeader(HeaderNames.request_header_range_);
    String s = null;

    if (temp != null) {
      s = new String(temp);
    }

    String s1;
    String s2;
    int start, finish;
    int flen = (int) f.length();

    if (s != null) {
      s = s.toUpperCase();

      if (s.indexOf("BYTES") > -1) {
        s = s.substring(s.indexOf("=") + 1).trim();
        int i = s.indexOf("-");
        s1 = s.substring(0, i);
        s2 = s.substring(i + 1);

        try {
					if (i == 0) {
					  finish = flen - 1;
            start = finish - (new Integer(s2)).intValue();
					} else if (i == (s.length() - 1)) {
					  start = (new Integer(s1)).intValue();
					  finish = flen - 1;
					} else {
					  start = (new Integer(s1)).intValue();
					  finish = (new Integer(s2)).intValue();
					}
				} catch (NumberFormatException e) {
		    	//thrown when multiple ranges
		    	if (isValidRangeSet(s)) {
		    		Log.logWarning("ASJ.http.000091",
		    		  "Range [{0}] not supported. Ignoring range header.", new Object[]{s}, e, client.getIP(), null, null);
						return false;
		    	} else {
            // TODO: Move this code outside the request, cause it isn't
            // a good idea response to respond by its self
            // Returns bad request response, cause range header is wrong
            String logID = Log.logWarning("ASJ.http.000092",
              "HTTP request parsing and processing failed. HTTP error response [400 Bad Request] will be returned.",
              e, client.getRequest().getHost().getBytes(), null, null);
            HttpException ex = new HttpException(HttpException.HTTP_PROCESSING_ERROR);
            SupportabilityData supportabilityData = new SupportabilityData(true, getExceptionStackTrace(e), logID);
            if (supportabilityData.getMessageId().equals("")) {
              supportabilityData.setMessageId("com.sap.ASJ.http.0000092");
            }
            //TODO : Vily G : if there is no DC and CSN in the supportability data
            //and we are responsible for the problem then set our DC and CSN
            //otherwise leave them empty
            sendError(new ErrorData(ResponseCodes.code_bad_request, ex.getLocalizedMessage(),
              Log.formatException(e, logID), false, supportabilityData));
            return true;
          }
        }

        if (finish > flen) {
          finish = flen;
        }

        if (start > finish) {
          return false;
        }

        status_code = ResponseCodes.code_partial_content;
        reason_phrase = ResponseCodes._partial_content;
        headers.putIntHeader(HeaderNames.entity_header_content_length_, (finish - start + 1));
        headers.putHeader(HeaderNames.entity_header_content_range_, ("bytes " + start + "-" + finish + "/" + flen).getBytes());
        sendResponse(f, start, finish);
        return true;
      }
    }

    return false;
  }

  /**
   * Make listing of directory and send it.
   */
  private void listDirectory() {
    String filename; //The name of the directory to be listed
    if (client.getRequest().getRequestLine().isEncoded()) {
      filename = client.getRequestAnalizer().getFilename1().toStringUTF8();
    } else {
      filename = client.getRequestAnalizer().getFilename1().toString();
    }
    filename = ParseUtils.canonicalizeFS(filename);
    if (filename.endsWith(":")) {
      filename = filename + File.separator;
    }
    boolean first = true; //Shows that is the first content to send in the response together with headers.
    byte[] messByte = new byte[ServiceContext.getServiceContext().getHttpProperties().getFileBufferSize()];
    File[] files;
    File f = new File(filename);
    files = f.listFiles();

    boolean flag = false; //Shows whether the requested directory for listing is the application root

    if (client.getRequestAnalizer().getRequestPathMappings().getAliasName() != null) {
      //ConcurrentHashMapObjectObject alias = request.getDescriptor().getAliases();
      String pth = client.getRequestAnalizer().getHostPropertiesInternal().getAliasValue(client.getRequestAnalizer().getRequestPathMappings().getAliasName().toString());
      if (pth != null) {
        if (pth.equals(f.toString().replace(File.separatorChar, ParseUtils.separatorChar))) {
          flag = true;
        }
      }
    } else {
      if (client.getRequestAnalizer().getHostPropertiesInternal().getRootDir().equals(f.toString().replace(File.separatorChar, ParseUtils.separatorChar))) {
        flag = true;
      }
    }

    String parrentDirectory = null;
    if (client.getRequest().getRequestLine().isEncoded())  {
      try {
        parrentDirectory = ParseUtils.canonicalize(new String(client.getRequest().getRequestLine().getUrlDecoded().getBytes(0,
          client.getRequest().getRequestLine().getUrlDecoded().length() - 1), "UTF-8"));
      } catch (UnsupportedEncodingException e) {
        parrentDirectory = ParseUtils.canonicalize(new String(client.getRequest().getRequestLine().getUrlDecoded().getBytes(0,
          client.getRequest().getRequestLine().getUrlDecoded().length() - 1)));
      }
    } else {
      parrentDirectory = ParseUtils.canonicalize(new String(client.getRequest().getRequestLine().getUrlDecoded().getBytes(0,
        client.getRequest().getRequestLine().getUrlDecoded().length() - 1)));
    }

    byte[] tmpBytes = Responses.getDirectoryHead(client.getRequestAnalizer().getHostDescriptor().getVersion(),
        (client.getRequest().getRequestLine().isEncoded() ? ParseUtils.canonicalize(client.getRequest().getRequestLine().getUrlDecoded().toStringUTF8()): ParseUtils.canonicalize(client.getRequest().getRequestLine().getUrlDecoded().toString())),
        (flag ? null :  parrentDirectory.substring(0, parrentDirectory.lastIndexOf(ParseUtils.separatorChar) + 1)));
    System.arraycopy(tmpBytes, 0, messByte, 0, tmpBytes.length);
    int point = tmpBytes.length; //Offset in the Buffer

    for (int i = 0; i < files.length; i++) {
      if (flag) {
        if (files[i].getName().equalsIgnoreCase("META-INF") || files[i].getName().equalsIgnoreCase("WEB-INF")) {
          continue;
        }
      }
      byte[] dateArr = new byte[17];
      client.getRequestAnalizer().getHostDescriptor().getDate().getDateCLF(dateArr, 0, files[i].lastModified());

      if (files[i].isDirectory()) {
        tmpBytes = Responses.getDirectoryLine(files[i].getName(), new String(dateArr));
      } else {
        int size = (int) files[i].length();
        if (size == 0 ) {
          tmpBytes = Responses.getFileLine(files[i].getName(), Responses.getZero() , new String(dateArr));
        } else if (size < 1024) {
          tmpBytes = Responses.getFileLine(files[i].getName(), (size / 100 > 0 ? "0." + size / 100 : "0.1" ) , new String(dateArr));
        } else {
          size = size / 1024;
          tmpBytes = Responses.getFileLine(files[i].getName(), "" + size , new String(dateArr));
        }
      }

      if (point + tmpBytes.length > messByte.length) {
      	//Send the buffer and write the line in new buffer:
      	first = sendToClient(first, false, messByte, point);
        messByte = new byte[ServiceContext.getServiceContext().getHttpProperties().getFileBufferSize()];
        point = 0;
      }
      System.arraycopy(tmpBytes, 0, messByte, point, tmpBytes.length);
      point += tmpBytes.length;
    } //for

    if (point + Responses.getTableEnd().length > messByte.length) {
    	//Send the buffer and write the tableEnd in new buffer:
    	first = sendToClient(first, false, messByte, point);
    	messByte = new byte[ServiceContext.getServiceContext().getHttpProperties().getFileBufferSize()];
      point = 0;
    }
    System.arraycopy(Responses.getTableEnd(), 0, messByte, point, Responses.getTableEnd().length);
    point += Responses.getTableEnd().length;
    first = sendToClient(first, true, messByte, point);
  }

  /**
   * First checks whether headers have been already set and if not sets them depending
   * whether this is the last part of the content.
   * Then sends the content of the buffer to the client.
   *
   * @param isFirst	true if no content has been sent to the client yet
   * @param isLast	true if no more content will be sent to the client
   * @param messByte	the buffer containing the content for sending to the client
   * @param point	the length of the content in the buffer
   * @return the new value of isFirst
   */
	private boolean sendToClient(boolean isFirst, boolean isLast, byte[] messByte, int point) {
		if (isFirst) {
			isFirst = false;
			headers.putHeader(HeaderNames.entity_header_content_type_, HeaderValues.text_html_);
			if (isLast) {
				if (!ByteArrayUtils.equalsBytes(HeaderValues.close_, headers.getHeader(HeaderNames.hop_header_connection_))) {
					headers.putIntHeader(HeaderNames.entity_header_content_length_, point);
				}
			}
			headers.putHeader(HeaderNames.entity_header_pragma_, HeaderValues.no_cache_);
			headers.putHeader(HeaderNames.entity_header_cache_control_, ServiceContext.getServiceContext().getHttpProperties().getCacheValidationTimeString().getBytes());

			if (client.getRequest().getRequestLine().getHttpMajorVersion() == 0
					|| client.getRequest().getRequestLine().getHttpMajorVersion() == 1 && client.getRequest().getRequestLine().getHttpMinorVersion() == 0) {
				headers.putDateHeader(HeaderNames.entity_header_expires_, System.currentTimeMillis() + ServiceContext.getServiceContext().getHttpProperties().getCacheValidationTime() * 1000);
			}
			if (ServiceContext.getServiceContext().getHttpProperties().getUseServerHeader()) {
				headers.putHeader(HeaderNames.response_header_server_, client.getRequestAnalizer().getHostDescriptor().getVersion());
			}
			if (!isLast) {
				headers.putHeader(HeaderNames.hop_header_connection_, HeaderValues.close_);
			}
			insertStatusLine(CommunicationConstants.RESPONSE_FLAG_NOOP, false);
		}
		if (isLast) {
			client.send(messByte, 0, point, CommunicationConstants.RESPONSE_FLAG_CLOSE_CONNECTION);
		} else {
			client.send(messByte, 0, point, (byte) 0);
		}
		return isFirst;
	} //sendToClient

  /**
   * Analyze GET request and send response.
   */
  private void answerGET(HttpFile httpFile) {
    if (changeLocation != null) {
      sendChangeLocation(changeLocation);
      return;
    }
    String filename = null;
    if (client.getRequestAnalizer().getFilename() != null) {
      filename = httpFile.getIOFileNameCannonical();
    }
    File f;
    if (httpFile != null && !httpFile.isDirectory()) {
      status_code = ResponseCodes.code_ok;
      reason_phrase = ResponseCodes._ok;
      f = new File(filename);
      if (ServiceContext.getServiceContext().getHttpProperties().getUseServerHeader()) {
        headers.putHeader(HeaderNames.response_header_server_, client.getRequestAnalizer().getHostDescriptor().getVersion());
      }
      sendfile = (checkIfModified(f) || name_replaced);

      if (sendfile) {
        headers.putHeader(HeaderNames.entity_header_content_type_, getContentType(filename));
        headers.putDateHeaderLM(HeaderNames.entity_header_last_modified_, f.lastModified());
        headers.putHeader(HeaderNames.entity_header_cache_control_, ServiceContext.getServiceContext().getHttpProperties().getCacheValidationTimeString().getBytes());
        if (client.getRequest().getRequestLine().getHttpMajorVersion() == 0
                || client.getRequest().getRequestLine().getHttpMajorVersion() == 1 && client.getRequest().getRequestLine().getHttpMinorVersion() == 0) {
          headers.putDateHeader(HeaderNames.entity_header_expires_, System.currentTimeMillis() + ServiceContext.getServiceContext().getHttpProperties().getCacheValidationTime() * 1000);
        }

        // Puts all unprotected resources in ICM cache
        // Second check is removed cause ICM is always in front
        if (!client.getRequestAnalizer().isProtected()) { // && client.getRequestAnalizer().isICM()) {
          headers.putHeader(HeaderNames.propriatory_sap_cache_control_,
            ("+" + ServiceContext.getServiceContext().getHttpProperties()
            .getSapCacheValidationTime()).getBytes());
          String aliasName = "";
          if (client.getRequestAnalizer().getRequestPathMappings().getAliasName() != null) {
            aliasName = client.getRequestAnalizer().getRequestPathMappings().getAliasName().toString();
          }
          headers.putHeader(HeaderNames.propriatory_sap_isc_etag_, HeaderValues.getSapIscEtag(aliasName));
        }

        headers.putIntHeader(HeaderNames.entity_header_content_length_, (int)f.length());

        if (!checkRangeAndSend(f)) {
          sendResponse(f);
        }
      } else {
        status_code = ResponseCodes.code_not_modified;
        reason_phrase = ResponseCodes._not_modified;
        headers.putHeader(HeaderNames.entity_header_cache_control_, ServiceContext.getServiceContext().getHttpProperties().getCacheValidationTimeString().getBytes());
        if (client.getRequest().getRequestLine().getHttpMajorVersion() == 0
                || client.getRequest().getRequestLine().getHttpMajorVersion() == 1 && client.getRequest().getRequestLine().getHttpMinorVersion() == 0) {
          headers.putDateHeader(HeaderNames.entity_header_expires_, System.currentTimeMillis() + ServiceContext.getServiceContext().getHttpProperties().getCacheValidationTime() * 1000);
        }
        sendResponse();
      }
      isStaticResourceServed = true;
    } else if (httpFile != null) {
      if (client.getRequestAnalizer().getHostPropertiesInternal().isList()) {
        status_code = ResponseCodes.code_ok;
        reason_phrase = ResponseCodes._ok;
        listDirectory();
        isStaticResourceServed = true;
      } else {
        sendError(new ErrorData(ResponseCodes.code_forbidden,
          Responses.mess3, Responses.mess4, false, new SupportabilityData()));//here we do not need user action
      }
    } else {
      if (!client.getRequestAnalizer().getHostDescriptor().rootExists() && (client.getRequestAnalizer().getRequestPathMappings().getAliasName() == null)) {
        Log.logWarning("ASJ.http.000093",
          "A root directory for http virtual host [{0}] is not specified or the specified directory does not exist. Correct the settings of this virtual host in Http Provider service. " +
          "Http error response [404 Not Found] will be returned for a request to this http virtual host.",
          new Object[]{client.getRequestAnalizer().getHostDescriptor().getHostName()}, null, null, null);
        sendError(new ErrorData(ResponseCodes.code_not_found,
          Responses.mess5, Responses.mess6, false, new SupportabilityData()));//here we do not need user action
      } else {
        String alias = "";
        if (client.getRequestAnalizer().getRequestPathMappings().getAliasName() != null) {
          alias = client.getRequestAnalizer().getRequestPathMappings().getAliasName().toString();
        }
        if (!alias.startsWith(ParseUtils.separator)) {
          alias = ParseUtils.separator + alias;
        }
        sendError(new ErrorData(ResponseCodes.code_not_found,
          Responses.mess5, Responses.mess7.replace("{URL}", alias), true, new SupportabilityData(true, "", "")));
      }
    }
  }

  /**
   * Analyze request method.
   */
  public void makeAnswer(HttpFile httpFile) {
    if (!done) {
      if (ByteArrayUtils.equalsBytes(client.getRequest().getRequestLine().getMethod(), Methods._GET)) {
        answerGET(httpFile);
      } else if (ByteArrayUtils.equalsBytes(client.getRequest().getRequestLine().getMethod(), Methods._POST)) {
        answerGET(httpFile);
      } else if (ByteArrayUtils.equalsBytes(client.getRequest().getRequestLine().getMethod(), Methods._PUT)) {
        sendError(new ErrorData(ResponseCodes.code_method_not_allowed,
          Responses.mess8, Responses.mess9, false, new SupportabilityData(true, "", "")));
      } else if (ByteArrayUtils.equalsBytes(client.getRequest().getRequestLine().getMethod(), Methods._HEAD)) {
        isHEAD = true;
        answerGET(httpFile);
      } else {
        headers.putHeader(HeaderNames.other_header_public_, allowed1);
        sendError(new ErrorData(ResponseCodes.code_not_implemented,
          Responses.mess10.replace("{METHOD}", new String(client.getRequest().getRequestLine().getMethod())), Responses.mess11, false,
          new SupportabilityData()));//we do not need user action
      }
    }
  }

  public boolean isConnectionMarkedClosed() {
    return connectionClosed;
  }

  private void markConnectionClosed() {
    connectionClosed = true;
  }

  public boolean isNotSendAnyMore() {
    return notSendAnyMore;
  }

  protected void setResponseLength(int totalCount) {
    this.responseLength = totalCount;
  }

  public int getResponseLength() {
    return responseLength;
  }

  public boolean isReplyFromCache() {
    return cachedReply;
  }

  public int getResponseCode() {
    return status_code;
  }

  private void checkEOBody() {
    if (status_code >= ResponseCodes.code_ok && status_code != ResponseCodes.code_no_content && status_code != ResponseCodes.code_not_modified) {
      if (getHeaders().getHeader(HeaderNames.entity_header_content_length_) == null
          && !HeaderValues.close.equalsIgnoreCase(getHeaders().getHeader(HeaderNames.hop_header_connection))
          && getHeaders().getHeader(HeaderNames.hop_header_transfer_encoding_) == null) {
        getHeaders().addIntHeader(HeaderNames.entity_header_content_length_, 0);
      }
    }
  }

  private void setConnectionType() {
    HttpInputStream requestBody = null;
    try {
      requestBody = (HttpInputStream)client.getRequest().getBody();
    } catch (ThreadDeath t) {
      throw t;
    } catch (OutOfMemoryError t) {
      throw t;
    } catch (Throwable t) {
      Log.logWarning("ASJ.http.000094", "Cannot get input stream for reading request body.", t, null, null, null);
    }
    if (requestBody != null && !requestBody.isEmpty()) {
      setPersistentConnection(false);
    }
  }

  /**
   * Parses the rest of the string that should match 'byte-range-set' from RFC.
   */
  private static boolean isValidRangeSet(String s) {
  	/*
  	 * Replaces:
  	 * 	String byteRangeSetElement = " *(-\\d+|\\d+-\\d*) *";
  	 * 	RegularExpression.matches(byteRangeSetElement + "(,(" + byteRangeSetElement + "| *))*", s)
  	 */
  	try {
			boolean hasValidRange = false;
			boolean hasError = false;
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == ' ' || s.charAt(i) == ',') {
					continue;
				}
				int j = s.indexOf('-',i); //j==-1 || i<=j<s.length()
				if (j == -1) { //remaining element without '-'
					hasError = true;
					break;
				}
				int k = j + 1;
				while (k < s.length() && s.charAt(k) >= '0' && s.charAt(k) <= '9') {
					k++;
				}
				if (j + 1 < k) { //there is a right number
					while (i < j && s.charAt(i) >= '0' && s.charAt(i) <= '9') {
						i++;
					}
					if (i < j) { //left side is not a number and is not empty
						hasError = true;
						break;
					}
					hasValidRange = true;
				} else { //there is no right number
					if (i == j) { //left side is empty
						hasError = true;
						break;
					}
					while (i < j && s.charAt(i) >= '0' && s.charAt(i) <= '9') {
						i++;
					}
					if (i < j) { //left side is not a number and is not empty
						hasError = true;
						break;
					}
					hasValidRange = true;
				}
				i = k;
				while (i < s.length() && s.charAt(i) == ' ') i++;
				if (i < s.length() && s.charAt(i) != ',') {
					hasError = true;
					break;
				}
			} //for
			return hasValidRange && !hasError;
  	} catch (Exception e) {
  		return false;
  	}
	}//isValidRangeSet

  public boolean isStaticResourceServed() {
    return isStaticResourceServed;
  }
}
