/*
 * Copyright (c) 2000-2007 by SAP AG, Walldorf.,
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
import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_SSL_ATTRIBUTES;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.ServletInputStream;

import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.lib.util.ArrayObject;
import com.sap.engine.services.httpserver.CommunicationConstants;
import com.sap.engine.services.httpserver.interfaces.client.Request;
import com.sap.engine.services.httpserver.interfaces.client.RequestLine;
import com.sap.engine.services.httpserver.interfaces.client.SslAttributes;
import com.sap.engine.services.httpserver.interfaces.properties.ProxyServersProperties;
import com.sap.engine.services.httpserver.lib.CookieParser;
import com.sap.engine.services.httpserver.lib.Cookies;
import com.sap.engine.services.httpserver.lib.HttpCookie;
import com.sap.engine.services.httpserver.lib.ResponseCodes;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.lib.protocol.HeaderNames;
import com.sap.engine.services.httpserver.lib.protocol.HeaderValues;
import com.sap.engine.services.httpserver.lib.util.Ascii;
import com.sap.engine.services.httpserver.lib.util.ByteArrayUtils;
import com.sap.engine.services.httpserver.lib.util.MessageBytes;
import com.sap.engine.services.httpserver.server.impl.RequestLineImpl;
import com.sap.engine.services.httpserver.server.io.ChunkedInputStream;
import com.sap.engine.services.httpserver.server.io.HttpInputStreamImpl;
import com.sap.engine.services.httpserver.server.properties.IProxyConfiguration;
import com.sap.engine.services.httpserver.server.properties.ProxyConfiguration;
import com.sap.engine.services.httpserver.server.properties.ProxyConfigurationOld;
import com.sap.engine.services.httpserver.server.properties.ProxyMappings;
import com.sap.engine.services.httpserver.server.properties.ReverseProxyMappings;

public class RequestImpl implements Request {
  private Client client = null;

  private RequestLineImpl requestLine = null;
  private MimeHeaders headers = null;
  private HttpInputStreamImpl inputStream = null;
  private ChunkedInputStream chunkedInputStream = null;
  private SslAttributesImpl sslAttributes = null;

  private Cookies cookies = new Cookies();
  //contains the name of the host as specified in the request or null if host name is missing
  private byte[] hostBytes = null;
  //contains the name of the host as specified in the request or the I-net address of the dispatcher
  private String host = null;
  private int port = -1;
  private int remotePort = -1;
  private boolean portParsed = false;
  private boolean remotePortParsed = false;

  private boolean parsedCookies = false;
  private boolean hostBytesParsed = false;
  private boolean readBodyFromStream = false;
  private boolean acceptGzipEncoding = false;
  private MessageBytes paramsBody = null;
  private byte[] input = null;
  private String scheme = "http";
  
  private boolean hasSSLHeaders;

  private IProxyConfiguration sslProxyConfiguration = null;
  private IProxyConfiguration plainProxyConfiguration = null;
  
  private IProxyConfiguration proxyConfiguration = null;
  private boolean isSessionSizeCalculationEnabled = false;
  
  public RequestImpl(Client client, RequestLineImpl requestLine) {
    this.requestLine = requestLine;
    this.client = client;
    this.headers = new MimeHeaders();
  }

  public void reset() {
    inputStream = null;
    chunkedInputStream = null;
    sslAttributes = null;
    readBodyFromStream = false;
    acceptGzipEncoding = false;
    paramsBody = null;
    input = null;
    parsedCookies = false;
    hostBytes = null;
    host = null;
    hostBytesParsed = false;
    port = -1;
    remotePort = -1;
    portParsed = false;
    remotePortParsed = false;
    scheme = "http";
    headers.clear();
    cookies.reset();
  }

  public void init(RequestImpl oldRequest) {
    requestLine.init(oldRequest.requestLine);
    if (oldRequest.sslAttributes != null) {
      sslAttributes = new SslAttributesImpl();
      sslAttributes.init(oldRequest.sslAttributes, client);
    }
    headers = (MimeHeaders)oldRequest.headers.clone();
    cookies.init(oldRequest.cookies);
    inputStream = oldRequest.inputStream;
    chunkedInputStream = oldRequest.chunkedInputStream;
    parsedCookies = oldRequest.parsedCookies;
    readBodyFromStream = oldRequest.readBodyFromStream;
    acceptGzipEncoding = oldRequest.acceptGzipEncoding;
    paramsBody = oldRequest.paramsBody;
    input = oldRequest.input;
    hostBytes = oldRequest.hostBytes;
    hostBytesParsed = oldRequest.hostBytesParsed;
    port = oldRequest.port;
    remotePort = oldRequest.port;
    portParsed = oldRequest.portParsed;
    remotePortParsed = oldRequest.remotePortParsed;
    scheme = oldRequest.scheme;
  }

  protected void clearInputStream() {
    if (inputStream != null) {
      inputStream.releaseBuffers();
    }
  }

  protected IProxyConfiguration getPlainProxyConfiguration() {
    if (!hostBytesParsed) {
      //parseHostBytes();
      parseHostBytesProxyMappings();
    }
    return plainProxyConfiguration;
  }
  
  protected IProxyConfiguration getSslProxyConfiguration() {
    if (!hostBytesParsed) {
      //parseHostBytes();
      parseHostBytesProxyMappings();
    }
    
    return sslProxyConfiguration;
  }
  
  public void setSslAttributes(SslAttributesImpl sslAttributes) {
    this.sslAttributes = sslAttributes;
  }

  /**
   * Initialize the SSL attributes first from request headers.
   * 
   * First checks if there is Client certificate header,
   * if yes continues with loading certificate data only from headers.
   * If not, checks if there is key size and cipher suite headers
   * and if found, loads them from headers and does not search further in FCA,
   * if not found:
   * - if one of key size and cipher suite is missing - logs an error and does 
   * not search further in FCA
   * - if both are missing in headers - continues with searching SSL data
   * only in FCA. 
   */
  public void initSslAttributes() {
    if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
      Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, "Initially sslAttributes = " + sslAttributes,
          getClientId(), "RequestImpl", "initSslAttributes");
    }
    //As by the contract with ICM - always search for SSL headers:
    hasSSLHeaders = checkForSSLHeaders();
    if (hasSSLHeaders) {
      //Load SSL data from request headers
        this.sslAttributes = new SslAttributesImpl();
        this.sslAttributes.setClient(client);
        this.sslAttributes.loadSSLAttributesFromHeaders();
        if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
          Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, "SSL Attributes loaded from headers.", getClientId(), "RequestImpl", "initSslAttributes");
        }
    } else {
      //No SSL data in request headers
      if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
        Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, "Cannot find SSL headers in the request.", getClientId(), "RequestImpl", "initSslAttributes");
      }
      if (client.getConnection().isSecure()) { //the connection to the ICM
        //Then search SSL data in FCA - it may be direct connection from client to ICM)
        sslAttributes = new SslAttributesImpl();
        sslAttributes.setClient(client);
        sslAttributes.loadSSLAttributesFromFCA();
        if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
          Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, "SSL Attributes loaded from FCA.", getClientId(), "RequestImpl", "initSslAttributes");
        }
      } else {
        //skip FCA and no SSLAttribtues
        if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
          Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES, 
              "No SSL attributes: not found in headers and not searched in FCA, because connection.isSecure() = " + client.getConnection().isSecure() + "; sslAttributes = " + sslAttributes,
              getClientId(), "RequestImpl", "initSslAttributes");
        }
        return;
      }
    }
  }//initSslAttributes()

  public void setInput(byte[] input) {
    this.input = input;
  }

  public RequestLine getRequestLine() {
    return requestLine;
  }

  public MimeHeaders getHeaders() {
    return headers;
  }

  public ServletInputStream getBody() {
    if (inputStream == null) {
      readBody();
    }
    if (chunkedInputStream != null) {
      return chunkedInputStream;
    }
    return inputStream;
  }

  public SslAttributes getSslAttributes() {
    return sslAttributes;
  }

  public byte[] getClientIP() {
    return client.getIP();
  }

  public int getClientId() {
    return client.getClientId();
  }

  /*
   * Return Only host.
   */
  public String getHost() {
    if (host == null) {
      parseHost();
    }
    return host;
  }

  public HttpCookie getSessionCookie(boolean urlSessionTracking) {
    if (!parsedCookies) {
      parseCookies(urlSessionTracking);
      parsedCookies = true;
    }
    HttpCookie sessionCookie = cookies.getSessionCookie();
    if (sessionCookie.getName() == null) {
      return null;
    }
    return sessionCookie;
  }

  public ArrayObject getApplicationCookies(boolean urlSessionTracking) {
    if (!parsedCookies) {
      parseCookies(urlSessionTracking);
      parsedCookies = true;
    }
    return cookies.getApplicationCookies();
  }

  public ArrayObject getCookies(boolean urlSessionTracking) {
    if (!parsedCookies) {
      parseCookies(urlSessionTracking);
      parsedCookies = true;
    }
    return cookies.getCookies();
  }

  public boolean isGzipEncoding() {
    return acceptGzipEncoding;
  }

  public int getDispatcherId() {
    return client.getDispatcherId();
  }

  public boolean isSessionSizeEnabled() {
    return isSessionSizeCalculationEnabled;
  }
  
  public void setSessionSizeEnabled(boolean isSessionSizeCalculationEnabled) {
    this.isSessionSizeCalculationEnabled = isSessionSizeCalculationEnabled;
  }
  
  // ------------------------ PROTECTED ------------------------

  protected void connectionClosed() {
    if (inputStream != null) {
      inputStream.connectionClosed();
    }
  }

  protected void setAcceptGZipEncoding(boolean acceptGZipEncoding) {
    this.acceptGzipEncoding = acceptGZipEncoding;
  }

  protected void readBody() {
    if (paramsBody == null) {
      paramsBody = readFormData(input);
    }
  }

  protected byte[] getHostBytes() {
    if (!hostBytesParsed) {
      //parseHostBytes();
      parseHostBytesProxyMappings();
    }
    return hostBytes;
  }

  public int getPort() {
    if (!portParsed) {
      parsePort();
    }
    return port;
  }

  public int getRemotePort() {
    if (!remotePortParsed) {
      parseRemotePort();
    }
    return remotePort;
  }

  public String getScheme() {
    if (!hostBytesParsed) {
      //parseHostBytes();
      parseHostBytesProxyMappings();
    }
    return scheme;
  }

  protected MessageBytes getRequestParametersBody() {
    if (paramsBody == null && inputStream == null) {
      readBody();
    }
    if (!readBodyFromStream) {
      try {
        readBodyFromStream();
      } catch (OutOfMemoryError e) {
        throw e;
      } catch (ThreadDeath e) {
        throw e;
      } catch (Throwable e) {
        Log.logWarning("ASJ.http.000084", "Cannot read request body.", e, client.getIP(), null, null);
      }
      readBodyFromStream = true;
    }
    return paramsBody;
  }

  protected void setScheme(String schemeFromProxy) {
    this.scheme = schemeFromProxy;
  }
 
  // ------------------------ PRIVATE ------------------------

  private void readBodyFromStream() throws IOException {
    int content_len = headers.getIntHeader(HeaderNames.entity_header_content_length_);
    if (content_len > 0) {
      byte[] buf = new byte[content_len];
      int ptr = 0;
      int all = 0;
      while (all < content_len && (ptr = inputStream.read(buf, all, buf.length - all)) != -1) {
        all += ptr;
      }
      paramsBody = new MessageBytes(buf);
    } else {
      paramsBody = new MessageBytes();
      byte[] buf = new byte[512];
      int ptr = 0;
      int all = 0;
      while ((ptr = inputStream.read(buf, all, buf.length - all)) != -1) {
        all += ptr;
        if (all == buf.length) {
          paramsBody.appendAfter(buf);
          ptr = 0;
          all = 0;
        }
      }
      if (all > 0) {
        byte[] bufff = new byte[all];
        System.arraycopy(buf, 0, bufff, 0, all);
        paramsBody.appendAfter(bufff);
      }
    }
  }

  private MessageBytes readFormData(byte[] inputstream) {
    byte[] transferEncoding = headers.getHeader(HeaderNames.hop_header_transfer_encoding_);
    if (ByteArrayUtils.equalsBytes(HeaderValues.chunked_, transferEncoding)) {
      inputStream = new HttpInputStreamImpl(inputstream, inputstream.length, client);
      chunkedInputStream = new ChunkedInputStream(inputStream);
      send100();
      return null;
    } else {
      long content_len = headers.getLongHeader(HeaderNames.entity_header_content_length_);
      if (content_len <= 0) {
        readBodyFromStream = true;
        return new MessageBytes();
      }
      if (content_len <= inputstream.length) {
        byte[] formData = new byte[(int)content_len];
        System.arraycopy(inputstream, 0, formData,  0, (int)content_len);
        inputStream = new HttpInputStreamImpl((int)content_len, formData, (int)content_len, client);
        return new MessageBytes(formData, 0, (int)content_len);
      } else {
        inputStream = new HttpInputStreamImpl(content_len, inputstream, inputstream.length, client);
        send100();
        return null;
      }
    }
  }

  /**
   * If urlSessionTracking is true (i.e. the application is configured to use cookies from URL)
   * gets the J2EE Engine-specific cookies from the URL only.
   *
   * If the http property UrlSessionTrackingForAllCookies is true and the http property DisableURLSessionTracking is false,
   * does not parse the J2EE Engine-specific cookies from the headers; then if also urlSessionTracking is true,
   * does not parse any cookies from the headers.
   *
   * If the http property DisableURLSessionTracking is true, does not parse the J2EE Engine-specific cookies from the URL.
   *
   * History:
   *
   * 1. First behavior:
   * If urlSessionTracking is on, parses J2EE Engine-specific cookies from the URL and no other cookies are parsed from the headers.
   * If urlSessionTracking is false, parses all the cookies from the headers, and if no cookies are found in the headers,
   * then parses the J2EE Engine-specific cookies from the URL.
   * 2. Next changes (UrlSessionTrackingForAllCookies):
   * If UrlSessionTrackingForAllCookies is true - same behavior as before.
   * If UrlSessionTrackingForAllCookies is false (default value) - parses the cookies from the headers, except the
   * J2EE Engine-specific ones, which are parsed from the URL if no other cookies are found in the headers
   * or if urlSessionTracking is on.
   * 3. Next changes (DisableURLSessionTracking): Does not parse any cookies from the URL.
   *
   * @param urlSessionTracking
   */
  private void parseCookies(boolean urlSessionTracking) {
    boolean disableURLSessionTracking = ServiceContext.getServiceContext().getHttpProperties().isURLSessionTrackingDisabled();
    if (urlSessionTracking && ServiceContext.getServiceContext().getHttpProperties().urlSessionTrackingForAllCookies()
    		&& !disableURLSessionTracking) {
      CookieParser.parseCookiesFromURL(requestLine.getFullUrl(), cookies);
      return;
    }
    CookieParser.parseCookies(headers, urlSessionTracking, cookies);

    if (disableURLSessionTracking) {
      if (LOCATION_HTTP_REQUEST.beDebug()) {
      	LOCATION_HTTP_REQUEST.debugT("RequestImpl.parseCookies(boolean)",
            "Will not parse cookies from URL because of enabled property DisableURLSessionTracking : "
            + String.valueOf(disableURLSessionTracking)
            + " and urlSessionTracking : " + urlSessionTracking + " for client ID : " + client.getClientId() + " and dispatcher ID: "
            + client.getDispatcherId());
      }
      return;
    }

    if (cookies.getCookies().size() == 0) {
      CookieParser.parseCookiesFromURL(requestLine.getFullUrl(), cookies);
    } else if (urlSessionTracking) {
      //remove session and lb cookies if any
      if (cookies.getCookies() != null) {
        Object[] cookiesArray = cookies.getCookies().toArray();
        for (int i = 0; i < cookiesArray.length; i++) {
          if (((HttpCookie)cookiesArray[i]).getName().equalsIgnoreCase(CookieParser.jsessionid_cookie)) {
            cookies.getCookies().remove(cookiesArray[i]);
          } else if (((HttpCookie)cookiesArray[i]).getName().startsWith(CookieParser.app_cookie_prefix)) {
            cookies.getCookies().remove(cookiesArray[i]);
          }
        }
      }
      cookies.clearSessionCookie();
      cookies.clearApplicationCookies();
      CookieParser.parseCookiesFromURL(requestLine.getFullUrl(), cookies);
    }
  }

  /**
   * Defines host, port and scheme with respect to SAP J2EE engine internal
   * rules (reverese proxy mappings, proxy mappings and protocol header )and RFC 2616 (host header and
   * request URL)
   */
  private void parseHostBytesProxyMappings() {
    int resultReverseProxy = parseReverseProxyMapping();
    
    //if proxy mapping are parsed correctly return
    if (resultReverseProxy == 0) { 
      hostBytesParsed = true;
      return; 
    }   
    
    // In case of incomplete and override false or missing
    // proxy mapping tries to get scheme from protocol header
    if (resultReverseProxy != 1) {
      String schemeFromProxy = getHeaders().getHeader(
        ServiceContext.getServiceContext().getHttpProperties()
        .getProxyServersProperties().getProtocolHeaderName());
      if (schemeFromProxy != null) {
        requestLine.setScheme(schemeFromProxy.toLowerCase());        
      }
      scheme = requestLine.getScheme();
    }
    
    // In case of an absolute request-URI
    if ((hostBytes = requestLine.getHost()) != null) {
      port = requestLine.getPort();
      remotePort = client.getRemotePort();
      host = new String(hostBytes);
      remotePortParsed = true;
      hostBytesParsed = true;
      portParsed = true;
      return;
    }    
    
    // Tries to define host and port from host header
    byte[] hostName = headers.getHeader(HeaderNames.request_header_host_);
    if (hostName == null) {
      // Missing host header.
      // Tries to define host from request URL
      hostBytes = requestLine.getHost();
      if (hostBytes == null) {
        // Request URL isn't absolute
        // Defines host and port from default proxy mappings if
        // proxy is found and is not completed or proxy is not found or 
        if (resultReverseProxy == 1 || resultReverseProxy == 3) {
          parseDefaultReverseProxyMappings();
        }
        //when resultReverseProxy ==2
        else {
          //TODO - check if default proxy mappings should be added parseReverseProxyMapping(boolean)
          parseReverseProxyMapping();
          String schemeFromProxy = getHeaders().getHeader(
          ServiceContext.getServiceContext().getHttpProperties()
           .getProxyServersProperties().getProtocolHeaderName());
          if (schemeFromProxy != null) {
            requestLine.setScheme(schemeFromProxy.toLowerCase());
            setScheme(requestLine.getScheme());
          }
        }
      } else {
        port = requestLine.getPort();  
        //TODO - check before it was host = null - Why may be bug
        host = new String(hostBytes);
      }
    } else {
      host = null;

      // Parses host header to define host name and port
      int ind = ByteArrayUtils.lastIndexOf(hostName, hostName.length, (byte) ':');
      
      //Check also for header with IPv6 on port 80 or 443, where the port is not present in header, 
      //but there is symbol ":" as part of IPv6 in Host header. For instance:
      //Host: [1::a:b:c:10]
      int indexHostIPv6 = -1;
      //In case of no ":" at all (IPv4 without port) or there is ":", but before symbol "]", not after it => we have default port 80 or 443.
      if (ind == -1 || ( indexHostIPv6 = ByteArrayUtils.indexOf(hostName, (byte)']') ) > 0 && ind < indexHostIPv6) {
          // Host header contains only host name thus
          // port is the default one according scheme
          hostBytes = hostName;
          port = (requestLine.isSecure()) ? 443 : 80;
      } else {
        //Default case, we have host and port in "Host" header
        hostBytes = new byte[ind]; 
        System.arraycopy(hostName, 0, hostBytes, 0, ind);
        if (ind < hostName.length) {
          port = Ascii.asciiArrToIntNoException(
            hostName, ind + 1, hostName.length - ind - 1);
          if (port == -1) {
            port = client.getPort();
          }
        }
      }
    }

    remotePort = client.getRemotePort();
    remotePortParsed = true;
    hostBytesParsed = true;
    portParsed = true;   
  }

  private void parseHost() {
    byte[] hostName = getHostBytes();
    if (hostName != null) {
      host = new String(hostName);
    } else {
      int dispatcherId = client.getDispatcherId();

      port = client.getPort();
      //for CTS. don't remove this ... please
      ClusterElement[] ce = client.getClusterElements();
      for (int i = 0; i < ce.length; i++) {
        if (ce[i].getClusterId() == dispatcherId) {
          host = ce[i].getAddress().getHostName();
          return;
        }
      }

      InetAddress addr = client.getCurrentClusterElement().getAddress();
      if (addr == null) {
        host = "localhost";
        Log.logWarning("ASJ.http.000085", 
          "Cannot retrieve local host name.", client.getIP(), null, null);
      }
      else {
        host = addr.getHostName();
      }
    }
  }
  
  /**
   * Applies default proxy mapping if any with respect to
   * result from <code>parseProxyMappings()</code> method call
   */
  private void parseDefaultReverseProxyMappings() {
    ProxyConfigurationOld pc = HttpServerFrame.getDefaultReverseProxyMappings(client.getPort());
    if (pc == null) { return ; }
    host = pc.getHost();
    port = pc.getPort();
  }

  private int parseReverseProxyMapping() {    
    //clear proxy configuration for the request
    //because the RequestImpl is pooled and proxyConfiguration could contain wrong data
    proxyConfiguration = null;
    //find proxy configuration for the request icm port
    IProxyConfiguration proxyConf = ReverseProxyMappings.findProxyConfiguration(headers, Integer.toString(client.getPort()));
    
    //if proxyConf == null search for ALL ports
    if (proxyConf == null) {
      proxyConf = ReverseProxyMappings.findProxyConfiguration(headers, "ALL");
    }
    
    //search in old proxy mappings - ProxyMapping property
    if (proxyConf == null) {
      proxyConf = ReverseProxyMappings.findOldProxyMapping(client.getPort());
    }
    
    IProxyConfiguration replacedProxyConfiguration = null;
    boolean isReplaced = false;
    boolean isHttp = true;
    
    if (proxyConf != null) {
      String requestToProxyScheme = proxyConf.getScheme(headers, client
          .getPort());
      if (proxyConf instanceof ProxyConfiguration) {
        // it is possible plain to be ssl - if the proxy request is through
        replacedProxyConfiguration = proxyConf;
        ProxyConfiguration tmpProxyConf= (ProxyConfiguration)proxyConf;
        if ("http".equalsIgnoreCase(requestToProxyScheme)) {
          if (tmpProxyConf.getHttpProxyReplaceConf() != null) {
            replacedProxyConfiguration = tmpProxyConf.getHttpProxyReplaceConf();
            isReplaced = true;
          }
        } else if ("https".equalsIgnoreCase(requestToProxyScheme)) {
          if (tmpProxyConf.getSslReplaceProxyConf() != null) {
            replacedProxyConfiguration = tmpProxyConf.getSslReplaceProxyConf();
            isReplaced = true;
            isHttp = false;
          }
        }
      }
      
      proxyConfiguration = proxyConf;
      if (isReplaced) {
        host = replacedProxyConfiguration.getHost();
        if (isHttp) {
          port = replacedProxyConfiguration.getPlainPort();
          scheme = "http";
        }
        else {
          port = replacedProxyConfiguration.getSslPort();
          scheme = "https";
        }
      }
      else {      
        port = proxyConf.getPort(headers, client.getPort());
        host = proxyConf.getHost();
        scheme = proxyConf.getScheme(headers, client.getPort());
      }
      if (!proxyConf.isOverride()){
        return 2;
      }
      
      if (port > -1 && host != null && scheme != null) {
        return 0;
      }
      return 1;
    }
    else {
      return 3;
    }
  }
  
  private void parsePort() {
    if (!hostBytesParsed) {
      //parseHostBytes();
      parseHostBytesProxyMappings();
    }
  }

  private void parseRemotePort() {
    if (!hostBytesParsed) {
      //parseHostBytes();
      parseHostBytesProxyMappings();
    }
  }

  private void send100() {
    if (requestLine.getHttpMajorVersion() == 0) {
      return;
    }
    byte[] expectValue = getHeaders().getHeader(HeaderNames.request_header_expect_);
    if (expectValue == null) {
      return;
    }
    expectValue = ByteArrayUtils.trim(expectValue);
    if (!ByteArrayUtils.equalsIgnoreCase(expectValue, HeaderValues._100_continue_)) {
      return;
    }
    byte[] code = ResponseCodes.status_code_byte[ResponseCodes.code_continue];
    byte[] reason = ResponseCodes.reasonBytes(ResponseCodes.code_continue, "");
    byte[] response = new byte[Constants.HTTP_11.length + code.length + 1 + reason.length + 4];
    int off = 0;
    System.arraycopy(Constants.HTTP_11, 0, response, 0, Constants.HTTP_11.length);
    off += Constants.HTTP_11.length;
    System.arraycopy(code, 0, response, off, code.length);
    off += code.length;
    response[off++] = ' ';
    System.arraycopy(reason, 0, response, off, reason.length);
    off += reason.length;
    response[off++] = '\r';
    response[off++] = '\n';
    response[off++] = '\r';
    response[off++] = '\n';
    client.send(response, 0, response.length,
      CommunicationConstants.RESPONSE_FLAG_NOOP);
  }
  
  /**
   * Searches for SSL data in request headers, i.e.
   * cipher suite and key size characterize an SSL connection,
   * so their presence as request headers determine there was
   * and SSL connection from the client, whose data was forwarded
   * as request headers and there is no need to search further in FCA.
   */
  private boolean checkForSSLHeaders() {
      boolean hasSSLHeaders = false;
      ProxyServersProperties proxyServersProperties = ServiceContext.getServiceContext().getHttpProperties().getProxyServersProperties();
      MimeHeaders headers = client.getRequest().getHeaders();
      byte[] suiteHeaderName = proxyServersProperties.getClientCipherSuiteHeaderName();
      byte[] keySizeHeaderName = proxyServersProperties.getClientKeySizeHeaderName();
      byte[] certHeaderName = proxyServersProperties.getClientCertificateHeaderName();
      
      hasSSLHeaders = (suiteHeaderName != null && headers.getHeader(suiteHeaderName) != null) ||
        (keySizeHeaderName != null && headers.getHeader(keySizeHeaderName) != null) ||
        (certHeaderName != null && headers.getHeader(certHeaderName) != null);

      if (LOCATION_HTTP_SSL_ATTRIBUTES.bePath()) {
        Log.tracePath(LOCATION_HTTP_SSL_ATTRIBUTES,
            "Check for SSL headers: SSL headers " + (hasSSLHeaders ? "" : "NOT ") + "found; Checked headers: " + 
            getTraceMsg("suiteHeaderName", suiteHeaderName, headers) + ", " +
            getTraceMsg("keySizeHeaderName", keySizeHeaderName, headers) + ", " +
            getTraceMsg("certHeaderName", certHeaderName, headers), 
            client.getClientId(), "RequestImpl", "checkForSSLHeaders()");
      }
      return hasSSLHeaders;
  }//checkForSSLHeaders()


  public IProxyConfiguration getProxyConfiguration() {
    return proxyConfiguration;
  }
  
  private String getTraceMsg(String headerDescription, byte[] headerNameBytes, MimeHeaders headers) {
    String result = headerDescription + " = [name=[";
    if (headerNameBytes != null) {
      byte[] value = headers.getHeader(headerNameBytes);
      result += new String(headerNameBytes) + 
        "], value=[" + (value != null ? new String(value) : "null") + "]]";
    } else {
      result += "null]";
    }
    return result;
  }

}
