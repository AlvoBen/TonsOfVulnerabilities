/*
 * Copyright (c) 2000-2009 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_REQUEST;
import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_TRACE_REQUEST;
import static com.sap.engine.services.httpserver.server.Log.LOCATION_REUQEST_PRESERVATION;
import static com.sap.engine.services.httpserver.server.Log.getExceptionStackTrace;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantLock;

import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.engine.lib.util.ConcurrentHashMapObjectObject;
import com.sap.engine.lib.util.HashMapObjectLong;
import com.sap.engine.services.httpserver.exceptions.HttpException;
import com.sap.engine.services.httpserver.exceptions.ParseException;
import com.sap.engine.services.httpserver.interfaces.ErrorData;
import com.sap.engine.services.httpserver.interfaces.HttpParameters;
import com.sap.engine.services.httpserver.interfaces.RequestPathMappings;
import com.sap.engine.services.httpserver.interfaces.SupportabilityData;
import com.sap.engine.services.httpserver.interfaces.client.Request;
import com.sap.engine.services.httpserver.interfaces.client.Response;
import com.sap.engine.services.httpserver.interfaces.properties.HostProperties;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.lib.ProtocolParser;
import com.sap.engine.services.httpserver.lib.ResponseCodes;
import com.sap.engine.services.httpserver.lib.Responses;
import com.sap.engine.services.httpserver.lib.protocol.HeaderNames;
import com.sap.engine.services.httpserver.lib.protocol.HeaderValues;
import com.sap.engine.services.httpserver.lib.protocol.Methods;
import com.sap.engine.services.httpserver.lib.util.Ascii;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.lib.util.CharArrayUtils;
import com.sap.engine.services.httpserver.lib.util.HexPrinter;
import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.server.hosts.Host;
import com.sap.engine.services.httpserver.server.impl.RequestLineImpl;
import com.sap.engine.services.httpserver.server.preservation.ReleaseManager;
import com.sap.engine.services.httpserver.server.properties.IProxyConfiguration;
import com.sap.engine.services.httpserver.server.properties.ProxyConfiguration;
import com.sap.engine.services.httpserver.server.properties.ReverseProxyMappings;
import com.sap.engine.session.runtime.http.HttpSessionRequest;
import com.sap.engine.system.ThreadWrapper;

/**
 * Analyze request!
 * Get all request fields!
 * Find file or replace file with default file if necessary!
 *
 * @author Galin Galchev
 * @version 4.0
 */
public class RequestAnalizer implements HttpParameters, Constants, Cloneable {
	private Client client = null;

  /**
   * Default HTTP header buffer size.
   */
  public static final int DEFAULT_HTTP_HEADER_BUFFER_SIZE = 48 * 1024;

  private RequestLineImpl requestLine = null;
  private RequestImpl request = null;
  private ResponseImpl response = null;
  private Host host = null;
  private RequestPathMappings requestPathMappings = new RequestPathMappings();

  private StaticDataProcessor staticDataProcessor = null;

  private ErrorData errorData = null;

  private MessageBytes filename = null; //java filename of the requested file
  private MessageBytes aliasPathAndFileName = null;
  private boolean isProtected = false;
  private Object applicationSession = null;
  private boolean isApplicationCookie = false;
  private boolean isSessionCookie = false;
  private Hashtable requestAttributes = new Hashtable();
  private FCAConnection connection = null;
  private byte [] buf = null;
  protected int lastValid = 0;
  protected int pos = 0;
  private boolean isDebugRequest = false;
  private boolean isMemoryTrace = false;

  private SessionRequestImpl sessionRequest = null;

  private HashMapObjectLong timeStatisticsMap = new HashMapObjectLong();

  private ReentrantLock lock = new ReentrantLock();


  private boolean preserved;
  private Finalizer finalizer;

  //NOTE - if you add any variable add it into init, recycle and clone methods too!!!!!!!!!!!!!!!!!!

  public RequestAnalizer(Client client) {
    this.client = client;
    this.requestLine = new RequestLineImpl();
    this.request = new RequestImpl(client, requestLine);
    this.staticDataProcessor = new StaticDataProcessor();
    this.sessionRequest = new SessionRequestImpl();
  }


  /**
   * Get body and header from request.
   */
  public void init(FCAConnection connection, SslAttributesImpl sslAttributes,
      boolean isSsl) throws IOException {
    this.response = client.getResponse();
    this.requestLine.setScheme(isSsl);
    this.request.setSslAttributes(sslAttributes);
    this.connection = connection;
    int avail = 0;
    avail = connection.getInputStream().available();
    if (avail == 0) {
      avail = DEFAULT_HTTP_HEADER_BUFFER_SIZE;
    }
    buf = new byte[avail];
    staticDataProcessor.init(client, request, response);
  }

  protected void recycle() {
	try{
		lock.lock();
		request.clearInputStream();
		request.getHeaders().clear();
		filename = null;
		aliasPathAndFileName = null;
		isProtected = false;
		applicationSession = null;
		host = null;
		requestLine.reset();
		request.reset();
		requestPathMappings.init();
		isApplicationCookie = false;
		isSessionCookie = false;
		errorData = null;
		requestAttributes.clear();
		lastValid = 0;
		pos = 0;
		isDebugRequest = false;
		isMemoryTrace = false;
		endSessionRequest();
		timeStatisticsMap.clear();
		preserved = false;
	}catch(Exception e){
		if (LOCATION_REUQEST_PRESERVATION.beWarning()){
      Log.traceWarning(LOCATION_REUQEST_PRESERVATION, "ASJ.http.000347",
        "Request recycle:", e, null, null, null);
		}
   }finally{
		lock.unlock();
   }
  }

  public void endSessionRequest(){
	  if (sessionRequest.isRequested()) {
		  if(LOCATION_REUQEST_PRESERVATION.beDebug()){
			    String tid = "- id -"+Thread.currentThread().getId();
				LOCATION_REUQEST_PRESERVATION.debugT("end session request "+tid);
			}
		  	sessionRequest.endRequest(0);
	  }
  }

  public Object clone() {
    Client newClient = new Client();
    System.arraycopy(client.getIP(), 0, newClient.getIP(), 0, client.getIP().length);
    try {
      newClient.init(client.getConnection(), client.getPort(), client
        .getDispatcherId(), client.getClientId(), requestLine.isSecure(),
        (SslAttributesImpl) request.getSslAttributes(),client.getReleaseManager());
    } catch (IOException e) {
      // Initialization failed
      Log.logWarning("ASJ.http.000081", "Object cloning failed.", e, client.getIP(), null, null);
      return null;
    }
    newClient.getRequestAnalizer().filename = filename;
    newClient.getRequestAnalizer().aliasPathAndFileName = aliasPathAndFileName;
    newClient.getRequestAnalizer().requestLine = (RequestLineImpl)requestLine.clone();
    newClient.getRequestAnalizer().isProtected = isProtected;
    newClient.getRequestAnalizer().applicationSession = applicationSession;

    if (sessionRequest.isRequested()) {
    	try {
				newClient.getRequestAnalizer().getSessionRequest().doSessionRequest(sessionRequest.getSessionDomain(), sessionRequest.getSessionId());
    	} catch (IllegalArgumentException illegalArg) {
    		Log.logWarning("ASJ.http.000082", "The session request was ended.", illegalArg, requestLine.getHost(), null, null);
    	}
    }

    newClient.getRequestAnalizer().host = host;
    newClient.getRequestAnalizer().isApplicationCookie = isApplicationCookie;
    newClient.getRequestAnalizer().isSessionCookie = isSessionCookie;
    newClient.getRequestAnalizer().isDebugRequest = isDebugRequest;
    newClient.getRequestAnalizer().isMemoryTrace = isMemoryTrace;
    if (errorData != null) {
      newClient.getRequestAnalizer().errorData = (ErrorData)errorData.clone();
    }
    newClient.getRequestAnalizer().requestAttributes = (Hashtable)requestAttributes.clone();
    newClient.getRequestAnalizer().timeStatisticsMap = (HashMapObjectLong)timeStatisticsMap.clone();

    //init path info
    newClient.getRequestAnalizer().requestPathMappings = new RequestPathMappings();
    newClient.getRequestAnalizer().requestPathMappings.setAlias(requestPathMappings.getAliasName(), requestPathMappings.getAliasValue(), requestPathMappings.isZoneExactAlias());
    newClient.getRequestAnalizer().requestPathMappings.setPathInfo(requestPathMappings.getPathInfo());
    newClient.getRequestAnalizer().requestPathMappings.setServletPath(requestPathMappings.getServletPath());
    newClient.getRequestAnalizer().requestPathMappings.setServletName(requestPathMappings.getServletName());
    newClient.getRequestAnalizer().requestPathMappings.setRealPath(requestPathMappings.getRealPath());
    newClient.getRequestAnalizer().requestPathMappings.setFilterChain(requestPathMappings.getFilterChain());
    newClient.getResponse().init(client.getResponse());
    ((RequestImpl)newClient.getRequest()).init((RequestImpl)client.getRequest());
    ((RequestLineImpl)newClient.getRequest().getRequestLine()).init((RequestLineImpl)client.getRequest().getRequestLine());
    newClient.getRequestAnalizer().staticDataProcessor.init(newClient, (RequestImpl)newClient.getRequest(), newClient.getResponse());
    return newClient.getRequestAnalizer();
  }

  public Request getRequest() {
    return request;
  }

  public Response getResponse() {
    return response;
  }

  public byte[] getHeader(byte[] name) {
    return request.getHeaders().getHeader(name);
  }

  public long getDateHeader(byte[] name) {
    return request.getHeaders().getDateHeader(name);
  }

  public MessageBytes getFilename() {
    return filename;
  }

  public MessageBytes getFilename1() {
    return aliasPathAndFileName;
  }

  /**
   * Fill the internal buffer using data from the underlying input stream.
   *
   * @return false if at end of stream
   */
  protected boolean fill() throws IOException {
    int nRead = 0;
    if (lastValid == buf.length) {
      throw new IOException("Request header too large");
    }
    nRead = connection.getInputStream().read(buf, pos, buf.length - lastValid);
    if (nRead > 0) {
      trace(buf, pos, nRead);
      lastValid = pos + nRead;
    }
    return (nRead >= 0);
  }

  /**
   * Dumps the request in the traces.
   * For severity "debug" the dump is in HEX format.
   * For severity "path" the dump is in String format.
   * For severity "info" the dump is in String format, and only the HTTP headers are traced.
   * @param msg
   * @param off
   * @param len
   */
  private void trace(byte[] msg, int off, int len){
    if (LOCATION_HTTP_TRACE_REQUEST.beDebug()) {
    	LOCATION_HTTP_TRACE_REQUEST.debugT("CLIENT: "+client.getClientId()+", REQUEST:\r\n" + HexPrinter.toString(msg, off, len));
    }else if (LOCATION_HTTP_TRACE_REQUEST.bePath()) {
    	LOCATION_HTTP_TRACE_REQUEST.pathT("CLIENT: "+client.getClientId()+", REQUEST:\r\n" + new String(msg, off, len));
    }else if (LOCATION_HTTP_TRACE_REQUEST.beInfo()) {
      String response = new String(msg, off, len);
      int endIndex = response.indexOf("\r\n\r\n");
      if (endIndex == -1) {
    	  endIndex = response.length();
      } else {
    	  endIndex = endIndex + 2;   // the 2 is for "\r\n"
      }
      LOCATION_HTTP_TRACE_REQUEST.infoT("CLIENT: "+client.getClientId()+", REQUEST:\r\n" + response.substring(0, endIndex));
    }
  }

  public boolean initialize() {
    try {
      // Skipping blank lines
      byte chr = 0;
      do {
        // Read new bytes if needed
        if (pos >= lastValid) {
          if (!fill()) {
            throw new EOFException("eof.error");
          }
        }
        chr = buf[pos++];
      } while ((chr == Constants.CR) || (chr == Constants.LF));
      pos--;
      // Mark the current buffer position

      int inputOffset = requestLine.parseRequestLine(buf, pos, lastValid);
      int offset = pos;
      int numberRNs = pos;
      int length = buf.length - (inputOffset - offset);
      offset = inputOffset;
      if (!requestLine.isSimpleRequest()) {
        inputOffset = request.getHeaders().init(buf, offset, length);
        length = length - (inputOffset - offset)- numberRNs;
        offset = inputOffset;
      }
      byte[] newInput = null;
      if (length > 0) {
        newInput = new byte[length];
        System.arraycopy(buf, offset, newInput, 0, newInput.length);
      } else {
        newInput = new byte[0];
      }
      request.setInput(newInput);
      host = ServiceContext.getServiceContext().getHttpHosts().getHost(request.getHostBytes());
      if (host == null) {
        host = ServiceContext.getServiceContext().getHttpHosts().getHost(defaultBytes);
      }
      request.initSslAttributes();
    } catch (OutOfMemoryError e) {
      throw e;
    } catch (ThreadDeath e) {
      throw e;
    } catch (Throwable t) {
			if (host == null) {
        host = ServiceContext.getServiceContext().getHttpHosts().getHost(defaultBytes);
      }
			String logID = Log.logWarning("ASJ.http.000083",
			  "Cannot parse an http request. Http error response [400 Bad Request] will be returned.",
			  t, requestLine.getHost(), null, null);
			HttpException ex = new HttpException(HttpException.HTTP_PROCESSING_ERROR);
      SupportabilityData supportabilityData = new SupportabilityData(true, getExceptionStackTrace(t), logID);
      if (supportabilityData.getMessageId().equals("")) {
        supportabilityData.setMessageId("com.sap.ASJ.http.0000083");
      }
      //TODO : Vily G : if there is no DC and CSN in the supportability data
      //and we are responsible for the problem then set our DC and CSN
      //otherwise leave them empty
      response.sendError(new ErrorData(ResponseCodes.code_bad_request,
        ex.getLocalizedMessage(), Log.formatException(t, logID), false, supportabilityData));
      return false;
    }
    return true;
  }

  public String getHostName() {
    return host.getHostName();
  }

  public HostProperties getHostProperties() {
    return host.getHostProperties();
  }

  public HostProperties getHostPropertiesInternal() {
    return host.getHostProperties();
  }

  public Host getHostDescriptor() {
    return host;
  }

  public ConcurrentHashMapObjectObject getTranslationTable() {
    return host.getTranslationTable();
  }

  public void connectionClosed() {
    request.connectionClosed();
  }

  // TODO: Replace call to this method from web container with something smarter
  public void sendChangeSchemaToSSlAfterCheckMap() {
    requestLine.getFullUrl().appendBefore(getSslHostPortScheme().getBytes());
    response.sendChangeLocation(requestLine.getFullUrl().getBytes());
    response.done();
  }

  // TODO: Replace call to this method from web container with something smarter
  public void startServlet(MessageBytes servletName) {
    if (requestPathMappings.getServletPath() == null) {
      if (requestLine.isEncoded()) {
        requestPathMappings.setServletPath(requestLine.getUrlDecoded().toStringUTF8());
      } else {
        requestPathMappings.setServletPath(requestLine.getUrlDecoded().toString());
      }
      if (!requestPathMappings.getAliasName().equals(ParseUtils.separatorBytes) || requestPathMappings.getZoneName() != null) {
        MessageBytes aliasName = new MessageBytes(requestPathMappings.getAliasName().getBytes());
        aliasName.replace((byte) File.separatorChar, ParseUtils.separatorByte);

        if (requestLine.getUrlDecoded().startsWith(aliasName) || requestLine.getUrlDecoded().startsWith("/" + aliasName)) {
          int idx = requestLine.getUrlDecoded().indexOf("/", requestLine.getUrlDecoded().indexOf(aliasName.toString()) + aliasName.length());
          if (idx > 0) {
            if (requestLine.isEncoded()) {
              requestPathMappings.setServletPath(requestLine.getUrlDecoded().toStringUTF8(idx));
            } else {
              requestPathMappings.setServletPath(requestLine.getUrlDecoded().toString(idx));
            }
          }
        }
      }
    } else {
      int indPath = requestLine.getFullUrl().indexOf((requestPathMappings.getServletPath() + ParseUtils.separator).getBytes());
      if (indPath > -1) {
        requestPathMappings.setPathInfo(requestLine.getFullUrl().toString(indPath + requestPathMappings.getServletPath().length()));
      }
    }
    //request.readBody();

    startServlet(servletName.toString());
    response.done();
  }

  /*
   * DO NOT CHANGE THE SIGNATURE!
   * Used by the dsr service
   */
  private void startServlet(String servletName) {
    ThreadWrapper.pushSubtask("Processing in Web Container", ThreadWrapper.TS_PROCESSING);
    try {
      ServiceContext.getServiceContext().getHttpProvider().getWebContainer().handleRequest(servletName, this);
    } finally {
      ThreadWrapper.popSubtask();
    }
  }

  public void findAndInitAlias() throws Exception {
    //replace HTTP separators with system file separators
    requestLine.getUrlDecoded().replace((byte)'/', ParseUtils.separatorByte);
    requestLine.getUrlDecoded().replace((byte)'\\', ParseUtils.separatorByte);
    //replace alias in path if exists
    aliasPathAndFileName = replaceAliases(requestLine.getUrlDecoded());
    int indx = aliasPathAndFileName.lastIndexOf(ParseUtils.separatorChar);
    requestPathMappings.setRealPath(aliasPathAndFileName.getBytes());
    if (indx > 0) {
      requestPathMappings.setRealPath(aliasPathAndFileName.getBytes(0, indx + 1));
    }
  }

  /**
   * Check url_decoded_ (if the file exists, is it a directory or is it in permissible path).
   * Change file location if file is found in other directory.
   * Get parameters from status line and initial some variables which are used for CGI scripts.
   */
  public HttpFile findRequestedFile() throws Exception {
    if (ByteArrayUtils.equalsBytes(requestLine.getMethod(), Methods._PUT)) {
      return null;
    }
    if (ServiceContext.getServiceContext().getHttpProvider().getWebContainer() == null) {
      if (requestPathMappings.getAliasName() != null
          && host.getHostProperties().isApplicationAlias(requestPathMappings.getAliasName().toString())) {
        response.sendError(new ErrorData(ResponseCodes.code_service_unavailable,
          Responses.mess1, Responses.mess2, false, new SupportabilityData()));//here we do not need user action
        response.done();
        return null;
      }
    }
    HttpFile httpFile = staticDataProcessor.getFileForRequest();
    if (httpFile != null) {
      this.filename = new MessageBytes(httpFile.getIOFileNameCannonicalBytes());
      staticDataProcessor.initForwarding();
    }
    return httpFile;
  }

  /////////////////////////////////////  methods from HttpParameters ///////////////////////////////////

  public MessageBytes getRequestParametersBody() {
    return request.getRequestParametersBody();
  }

  private String getSslHostPortScheme() {
    String scheme = request.getScheme();
    String host = request.getHost();
    int port = request.getPort();
    if (response.isSchemeChanged()) {
      IProxyConfiguration pconf = request.getSslProxyConfiguration();
      RequestUrlData regUrlData = null;
      if (pconf != null && pconf instanceof ProxyConfiguration){
          regUrlData = pconf.getSslUrlData("http");
      }
      else {
        if (ReverseProxyMappings.sslPortOld != null){
          regUrlData = ReverseProxyMappings.sslPortOld.getSslUrlData("http");
        }
        else {
          regUrlData = ReverseProxyMappings.getIcmSslPortHostScheme();
        }
      }
      if (regUrlData != null) {
        if (regUrlData.isOverride() && regUrlData.getHost() != null) { host = regUrlData.getHost();}
        int portPrConf = regUrlData.getPort();
        if (portPrConf > -1) { port = portPrConf;}
        scheme = regUrlData.getScheme();
      }
    }

    return ProtocolParser.makeAbsolute(scheme, host, port);
  } // getSSLPort

  public RequestPathMappings getRequestPathMappings() {
    return requestPathMappings;
  }

  public boolean isProtected() {
    return isProtected;
  }

  public void setProtected(boolean isProtected) {
    this.isProtected = isProtected;
  }

  /**
   *  Replace this filename with alias if need
   *
   *  @param fname filename to replace
   *  @return replaced filename
   */
  public MessageBytes replaceAliases(MessageBytes fname) {
    if (fname == null || fname.equals("") || fname.equals(ParseUtils.separatorBytes)) {
      if (host.getHostProperties().isApplicationAliasEnabled(ParseUtils.separator)) {
        String value = host.getHostProperties().getAliasValue(ParseUtils.separator);
        if (value == null) {
          value = "";
        }
        requestPathMappings.setAlias(new MessageBytes(ParseUtils.separatorBytes), value,
							ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().isExactAlias(ParseUtils.separator));
        if (requestPathMappings.isZoneExactAlias()) {
          requestPathMappings.setZoneName(ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().getZoneName(ParseUtils.separator), true);
        }
        return new MessageBytes(requestPathMappings.getAliasValue().getBytes());
      } else if (host.getHostProperties().getAliasValue(ParseUtils.separator) != null) {
        requestPathMappings.setAlias(new MessageBytes(ParseUtils.separatorBytes),
            host.getHostProperties().getAliasValue(ParseUtils.separator),
							ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().isExactAlias(ParseUtils.separator));
        if (requestPathMappings.isZoneExactAlias()) {
          requestPathMappings.setZoneName(ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().getZoneName(ParseUtils.separator), true);
        }
        return new MessageBytes(requestPathMappings.getAliasValue().getBytes());
      } else {
        requestPathMappings.setAlias(requestPathMappings.getAliasName(), host.getHostProperties().getRootDir(),
							requestPathMappings.getAliasName() != null &&
							ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().isExactAlias(requestPathMappings.getAliasName().toString()));
        if (requestPathMappings.isZoneExactAlias() && requestPathMappings.getAliasName() != null) {
          requestPathMappings.setZoneName(ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().getZoneName(requestPathMappings.getAliasName().toString()), true);
        }
        return new MessageBytes(host.getHostProperties().getRootDir().getBytes());
      }
    }
    MessageBytes res = findAliases(fname);
    if (res != null) {
      if (!res.endsWith(ParseUtils.separator) && !res.endsWith(File.separator)) {
        res.appendAfter(ParseUtils.separatorBytes);
      }
      return res;
    }
    if (host.getHostProperties().isApplicationAliasEnabled(ParseUtils.separator)) {
      String value = host.getHostProperties().getAliasValue(ParseUtils.separator);
      if (value == null) {
        value = "";
      }
      requestPathMappings.setAlias(new MessageBytes("/".getBytes()), value,
            ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().isExactAlias("/"));
      if (requestPathMappings.isZoneExactAlias()) {
        requestPathMappings.setZoneName(ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().getZoneName(ParseUtils.separator), true);
      }
      MessageBytes m = new MessageBytes(requestPathMappings.getAliasValue().getBytes());
      m.appendAfter(ParseUtils.separatorBytes);
      m.appendAfter(fname.getBytes());
      return m;
    } else if (host.getHostProperties().getAliasValue(ParseUtils.separator) != null) {
      requestPathMappings.setAlias(new MessageBytes("/".getBytes()), host.getHostProperties().getAliasValue(ParseUtils.separator),
						ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().isExactAlias(ParseUtils.separator));
      if (requestPathMappings.isZoneExactAlias()) {
        requestPathMappings.setZoneName(ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().getZoneName(ParseUtils.separator), true);
      }
      MessageBytes m = new MessageBytes(requestPathMappings.getAliasValue().getBytes());
      m.appendAfter(ParseUtils.separatorBytes);
      m.appendAfter(fname.getBytes());
      return m;
    } else {
      requestPathMappings.setAlias(null, host.getHostProperties().getRootDir(), false);
      MessageBytes m = new MessageBytes(requestPathMappings.getAliasValue().getBytes());
      m.appendAfter(ParseUtils.separatorBytes);
      m.appendAfter(fname.getBytes());
      return m;
    }
  }

  private MessageBytes findAliases(MessageBytes fname) {
    //todo - da se prenapishe
    if (fname.length() == 0) {
      MessageBytes fnameTemp = new MessageBytes(new byte[0]);
      fname = fnameTemp;
    } else if (fname.charAt(0) == ParseUtils.separatorChar) {
      MessageBytes fnameTemp = new MessageBytes(fname.getBytes(1, fname.length() - 1));
      fname = fnameTemp;
    }
    int i = fname.indexOf(ParseUtils.separator);
    if (i == -1) {
      String alpr = host.getHostProperties().getAliasValue(fname.toString());
      if (alpr != null) {
        requestPathMappings.setAlias(fname, alpr,
        			ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().isExactAlias(fname.toString()));
        if (requestPathMappings.isZoneExactAlias()) {
          requestPathMappings.setZoneName(ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().getZoneName(fname.toString()), true);
        }
        MessageBytes pth = new MessageBytes(Ascii.getBytes(alpr));
        if (!fname.equals("")) {
          response.setChangeLocation(fname.addByteAtBeginAndEnd(ParseUtils.separatorByte, ParseUtils.separatorByte)); // sep + fname + sep;
          return pth;
        }
      }
    } else {
      MessageBytes part = new MessageBytes(fname.getBytes());
      int j = part.length();
      do {
        part = new MessageBytes(part.getBytes(0, j));
        String alpr = host.getHostProperties().getAliasValue(part.toString());
        if (alpr != null) {
          requestPathMappings.setAlias(new MessageBytes(part.getBytes()), alpr,
          			ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().isExactAlias(part.toString()));
          if (requestPathMappings.isZoneExactAlias()) {
            requestPathMappings.setZoneName(ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().getZoneName(part.toString()), true);
          }
          MessageBytes pth = new MessageBytes(Ascii.getBytes(alpr));
          if (!pth.equals("")) {
            pth.appendAfter(fname.getBytes(part.length()));
          }
          return pth;
        }
      } while ((j = part.lastIndexOf(ParseUtils.separatorByte)) > -1);
    }
    if (ServiceContext.getServiceContext().getHttpProperties().getZoneSeparator() == null) {
      return null;
    }
    int zoneInd = fname.indexOf(ServiceContext.getServiceContext().getHttpProperties().getZoneSeparator());
    if (zoneInd <= -1) {
      return null;
    }
    int endInd = fname.indexOf('/', zoneInd + 1);
    if (endInd <= -1) {
      endInd = fname.length();
    }
    String zone = new String(fname.getBytes(), zoneInd + ServiceContext.getServiceContext().getHttpProperties().getZoneSeparator().length(), endInd - zoneInd - ServiceContext.getServiceContext().getHttpProperties().getZoneSeparator().length());
    String fnameWithoutZone = new String(fname.getBytes(), 0, zoneInd) + new String(fname.getBytes(), endInd, fname.length() - endInd);
    MessageBytes result = findAliases(new MessageBytes(fnameWithoutZone.getBytes()));
    requestPathMappings.setZoneName(zone, false);
    if (result == null) {
      if (host.getHostProperties().isApplicationAliasEnabled(ParseUtils.separator)) {
        String value = host.getHostProperties().getAliasValue(ParseUtils.separator);
        if (value == null) {
          value = "";
        }
        requestPathMappings.setAlias(new MessageBytes("/".getBytes()), value,
							ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().isExactAlias("/"));
        if (requestPathMappings.isZoneExactAlias()) {
          requestPathMappings.setZoneName(ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().getZoneName("/"), true);
        }
        MessageBytes m = new MessageBytes(requestPathMappings.getAliasValue().getBytes());
        m.appendAfter(ParseUtils.separatorBytes);
        m.appendAfter(fnameWithoutZone.getBytes());
        return m;
      } else if (host.getHostProperties().getAliasValue(ParseUtils.separator) != null) {
        requestPathMappings.setAlias(new MessageBytes("/".getBytes()), host.getHostProperties().getAliasValue(ParseUtils.separator),
							ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().isExactAlias("/"));
        if (requestPathMappings.isZoneExactAlias()) {
          requestPathMappings.setZoneName(ServiceContext.getServiceContext().getHttpProvider().getZoneManagement().getZoneName("/"), true);
        }
        MessageBytes m = new MessageBytes(requestPathMappings.getAliasValue().getBytes());
        m.appendAfter(ParseUtils.separatorBytes);
        m.appendAfter(fnameWithoutZone.getBytes());
        return m;
      } else {
        requestPathMappings.setAlias(null, host.getHostProperties().getRootDir(), false);
        MessageBytes m = new MessageBytes(requestPathMappings.getAliasValue().getBytes());
        m.appendAfter(ParseUtils.separatorBytes);
        m.appendAfter(fnameWithoutZone.getBytes());
        return m;
      }
    }
    return result;
  }

  public void setApplicationSession(Object applicationSession) {
    this.applicationSession = applicationSession;
  }

  public Object getApplicationSession() {
    Object appSession = null;
    try {
      if (this.sessionRequest.isRequested()) {
        appSession = this.sessionRequest.getSession(false);
      }
  	} catch (IllegalStateException ilSt) {
  	  Log.traceError(LOCATION_HTTP_REQUEST, "ASJ.http.000359", "Error in getting http session", ilSt,requestLine.getHost(),
  			  null, null);
  	  appSession = null;
  	}
  	return appSession;
  }


  /* (non-Javadoc)
   * @see com.sap.engine.services.httpserver.interfaces.HttpParameters#getSessionFacade()
   */
  public HttpSessionRequest getSessionRequest() {
	  return this.sessionRequest;
  }

  public void setResponseLength(int totalCount) {
    response.setResponseLength(totalCount);
  }

  public void parseInitialHeaders() throws ParseException {
    if (isHttp11OrBigger() && requestLine.getHost() == null
        && request.getHeaders().getHeader(HeaderNames.request_header_host_) == null) {
      throw new ParseException(ParseException.HEADER_NOT_FOUND, new Object[]{"Host"});
    }
    initClientIp();
    readConnection();
    byte[] encoding = request.getHeaders().getHeader(HeaderNames.request_header_accpet_encoding_);
    request.setAcceptGZipEncoding(ProtocolParser.parseGzipAcceptEncodingHeader(encoding));
    if (request.isGzipEncoding()) {
      response.setEncoded(true);
    }
  }

  private void initClientIp(){
    String headerName = ServiceContext.getServiceContext().getHttpProperties().getClientIpHeaderName();
    if (headerName == null || "".equals(headerName)){
      return;
    }
    String xForwardedFor = request.getHeaders().getHeader(headerName);
    if (xForwardedFor != null){
      boolean isIP = true;
      int points = 0;
      byte[] tmpDomain = new byte[4];
      String clientIps [] = new String[0];
      int ind = xForwardedFor.indexOf(',');
      int oldIndex = 0;
      if (ind > 0){
        String address;
        String [] tmp;
        while ((ind = xForwardedFor.indexOf(',', oldIndex)) > 0){
          address = xForwardedFor.substring(oldIndex, ind).trim();
          tmp = new String [clientIps.length + 1];
          System.arraycopy(clientIps,0,tmp,0,clientIps.length);
          tmp[tmp.length -1] = address;
          clientIps = tmp;
          oldIndex = ind+1;
        }
        tmp = new String [clientIps.length + 1];
        System.arraycopy(clientIps,0,tmp,0,clientIps.length);
        tmp[tmp.length -1] = xForwardedFor.substring(xForwardedFor.lastIndexOf(',') +1).trim();
        clientIps = tmp;
      } else {
        clientIps = new String [] {xForwardedFor.trim()};
      }
      for (int i = 0; i < clientIps.length; i++){
        if (!clientIps[i].equalsIgnoreCase("unknown")){
          char[] domain = clientIps[i].toCharArray();
          isIP = true;
          points = 0;
          for (int j = 0; j < domain.length; j++) {
            if (domain[j] == '.') {
              points++;
            } else if (!Character.isDigit(domain[j])) {
              isIP = false;
              break;
            }
          }
          if (isIP && points == 3) {
            char[] tmp = null;
            int k = 0;
            oldIndex = 0;
            ind = 0;
            points = 0;
            while (points < 4){
              oldIndex = ind+1;
              ind = CharArrayUtils.indexOf(domain, '.', oldIndex);
              if (ind > 0){
                tmp = new char[ind - oldIndex +1];
              }else{
                tmp = new char[domain.length - oldIndex + 1];
              }
              System.arraycopy(domain, oldIndex -1 , tmp, 0, tmp.length);
              ind++;
              points++;
              tmpDomain[k++] = (byte)((new Integer(new String(tmp))).intValue() & (byte)0xFF);
            }
            client.initClientIP(tmpDomain, 0, 4);
            break;
          }
          else if (ParseUtils.isIPv6LiteralAddress(clientIps[i])) {
              byte[] ipv6bytes;
              ipv6bytes = ParseUtils.textToNumericFormatV6(clientIps[i]);
              client.initClientIP(ipv6bytes, 0, ipv6bytes.length);
              break;
            }
        }
      }
    }
  }

  private void readConnection() {
    byte[] con = null;
    if (host.getHostProperties().isKeepAliveEnabled()) {
      con = request.getHeaders().getHeader(HeaderNames.hop_header_connection_);
    }
    if (con == null) {
      if (host.getHostProperties().isKeepAliveEnabled() && isHttp11OrBigger()) {
        response.setPersistentConnection(true);
      } else {
        response.putHeader(HeaderNames.hop_header_connection_, HeaderValues.close_);
        response.setPersistentConnection(false);
      }
    } else {
      response.setPersistentConnection(ProtocolParser.parseConnectionHeader(con, response.getHeaders()));
    }
  }

  private boolean isHttp11OrBigger() {
    return requestLine.getHttpMajorVersion() > 0 && requestLine.getHttpMinorVersion() > 0;
  }

  public void redirect(byte[] location) {
    if (!(ByteArrayUtils.startsWith(location, "http://".getBytes())
        || ByteArrayUtils.startsWith(location, "https://".getBytes()))) {
      String redirectLocation;
      if (!requestLine.isSecure()) {
        redirectLocation = ProtocolParser.makeAbsolute(request.getScheme(), request.getHost(), request.getPort());
      } else {
        redirectLocation = getSslHostPortScheme();
      }
      byte[] redirectPortBytes = redirectLocation.getBytes();
      byte[] locationNew = new byte[location.length + redirectPortBytes.length];
      System.arraycopy(redirectPortBytes, 0, locationNew, 0, redirectPortBytes.length);
      System.arraycopy(location, 0, locationNew, redirectPortBytes.length, location.length);
      location = locationNew;
    }
    response.sendChangeLocation(location);
    response.done();
  }

  public boolean isSetApplicationCookie() {
    return isApplicationCookie;
  }

  public boolean isSetSessionCookie() {
    return isSessionCookie;
  }

  public void setApplicationCookie(boolean isSet) {
    isApplicationCookie = isSet;
  }

  public void setSessionCookie(boolean isSet) {
    isSessionCookie = isSet;
  }

  public void setErrorData(ErrorData errorData) {
    this.errorData = errorData;
  }

  public ErrorData getErrorData() {
    return errorData;
  }

  public void setRequestAttribute(String name, Object value) {
    requestAttributes.put(name, value);
  }

  public Hashtable getRequestAttributes() {
    return requestAttributes;
  }

  public void setDebugRequest(boolean isDebugRequest) {
    this.isDebugRequest = isDebugRequest;
  }

  public boolean isDebugRequest() {
    return isDebugRequest;
  }

  /**
   * Returns an absolute URI to the server root for the given scheme
   *
   * @param scheme
   * The required scheme
   *
   * @return
   * An absolute URI to the server root for the given scheme
   */
  public String getServerURL(String scheme) {
    if (scheme == null) {
      throw new IllegalArgumentException("Scheme can not be null");
    }
    //Add for old proxies
    int port = -1;
    IProxyConfiguration  pconf = request.getProxyConfiguration();
    String reqScheme = request.getScheme();
    String host = request.getHost();
    RequestUrlData regUrlData = null;

    if (scheme.equalsIgnoreCase("https")) {
      port = 443;
      if (pconf != null){
        if (pconf instanceof ProxyConfiguration){
          regUrlData = pconf.getSslUrlData(reqScheme);
        }
      }
      else {
        if (ReverseProxyMappings.sslPortOld != null){
          regUrlData = ReverseProxyMappings.sslPortOld.getSslUrlData(reqScheme);
        }
        else {
          regUrlData = ReverseProxyMappings.getIcmSslPortHostScheme();
        }
      }
    } else if (scheme.equalsIgnoreCase("http")) {
      port = 80;
      if (pconf != null){
        if (pconf instanceof ProxyConfiguration){
          regUrlData = pconf.getPlainUrlData(reqScheme);
        }
      }
      else {
        if (ReverseProxyMappings.plainPortOld != null){
          regUrlData = ReverseProxyMappings.plainPortOld.getPlainUrlData(reqScheme);
        }
        else {
          regUrlData = ReverseProxyMappings.getIcmPlainPortHostScheme();
        }
      }
    }
    // If there aren't any port mappings, even default one the returned
    // URL is constructed with this request host and default HTTP port
    if (regUrlData == null) {
      return ProtocolParser.makeAbsolute(scheme, host, port);
    } else {
      if (regUrlData.getPort() > -1) { port = regUrlData.getPort();}
      if (regUrlData.isOverride() && regUrlData.getHost() != null) { host = regUrlData.getHost(); }
      return ProtocolParser.makeAbsolute(scheme, host, port);
    }
  }

  // TODO: Check with Svilen and Georgi St.
  // TODO: Check client.getIP() that returns incorrect data at this moment
  public void enterSessionRequest() {
    sessionRequest.setMonInfo(connection.getSessionIdx());
  }


  public void exitSessionRequest() {
    sessionRequest.endRequest(0);
  }

  public HashMapObjectLong getTimeStatisticsMap() {
    return timeStatisticsMap;
  }//end of getTimeStatisticsMap()

  public int getTraceResponseTimeAbove() {
    return ServiceContext.getServiceContext().getHttpProperties().getTraceResponseTimeAbove();
  }//end of getTraceResponseTimeAbove()


  public boolean isMemoryTrace() {
    return isMemoryTrace;
  }


  public void setMemoryTrace(boolean isMemoryTrace) {
    this.isMemoryTrace = isMemoryTrace;
  }

  //This is a marker call from http thread to mark the request as being reserved

  public void preserve(boolean finalizer) {
	if(LOCATION_REUQEST_PRESERVATION.beDebug()){
		LOCATION_REUQEST_PRESERVATION.debugT("Preserving request");
	}
	preserved = true;
	try{
		this.lock.lock();
	}catch (Exception e){
		if (LOCATION_REUQEST_PRESERVATION.beWarning()){
      Log.traceWarning(LOCATION_REUQEST_PRESERVATION, "ASJ.http.000377",
        "RequestAnalizer.preserve({0}):", new Object[]{finalizer}, e, null, null, null);
		}
	}
	if (finalizer){
        this.finalizer = new Finalizer();
	}

  }


  public boolean isPreserved(){
	if(LOCATION_REUQEST_PRESERVATION.beDebug()){
		LOCATION_REUQEST_PRESERVATION.debugT("Request is preserved: "+preserved);
	}
	return preserved;
  }



	public void recycleReturn() {
		if(LOCATION_REUQEST_PRESERVATION.beDebug()){
			LOCATION_REUQEST_PRESERVATION.debugT("Releasing request: recycle and return in pool");
		}
		if (this.finalizer != null){
			finalizer.invokeEndSessionRequest(false);
			this.finalizer = null;
		}
		recycle();
		ReleaseManager release = client.getReleaseManager();
		release.returnOldInPool(this.client);
	}


	public void justNew() {
		if(LOCATION_REUQEST_PRESERVATION.beDebug()){
			LOCATION_REUQEST_PRESERVATION.debugT("Releasing request: new in the pool");
		}
		ReleaseManager release = client.getReleaseManager();
		release.returnNewInPool();

	}


	public void recycleNew() {
		if(LOCATION_REUQEST_PRESERVATION.beDebug()){
			LOCATION_REUQEST_PRESERVATION.debugT("Releasing request: recycle and new in the pool");
		}
		if (this.finalizer != null){
			finalizer.invokeEndSessionRequest(false);
			this.finalizer = null;
		}

		recycle();
		ReleaseManager release = client.getReleaseManager();
		release.returnNewInPool();

	}

  public void releasePreservationLock() {
		if(LOCATION_REUQEST_PRESERVATION.beDebug()){
			LOCATION_REUQEST_PRESERVATION.debugT("Releasing Preservation Lock");
		}
		try{
			this.lock.unlock();
		}catch (Exception e){
			if (LOCATION_REUQEST_PRESERVATION.beWarning()){
	      Log.traceWarning(LOCATION_REUQEST_PRESERVATION, "ASJ.http.000099",
	        "Release Preservation Lock: ", e, null, null, null);
			}
		}

	}


	class Finalizer{
		boolean invoke = true;
		@Override
		protected void finalize() throws Throwable {
			super.finalize();
			if (invoke){
				endSessionRequest();
			}
		}
		protected void invokeEndSessionRequest(boolean invoke){
			this.invoke = invoke;
		}

	}


	public void preserveWithFinalizer() {
		preserve(true);

	}


	public void preserveWithoutFinalizer() {
		preserve(false);
	}

}

