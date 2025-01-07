/*
 * Copyright (c) 2006-2007 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP_TRACE_RESPONSE;
import static com.sap.engine.services.httpserver.server.Log.LOCATION_HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.sap.bc.proj.jstartup.fca.FCAConnection;
import com.sap.engine.frame.cluster.ClusterElement;
import com.sap.engine.services.httpserver.DSRHttpRequestContext;
import com.sap.engine.services.httpserver.interfaces.client.Request;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.lib.util.HexPrinter;
import com.sap.engine.services.httpserver.server.preservation.ReleaseManager;

public class Client {
  private String serviceWorkDir = null;

  private RequestAnalizer request = null;
  private ResponseImpl response = null;
  private DSRHttpRequestContextImpl dsrHttpRequestContext;
  private int icm_id = -1;
  private int client_id = -1;
  private byte[] clientIP = new byte[4];
  private int port = -1;
  private int remotePort = -1;

  private FCAConnection connection = null;
    
  // since the <CR><LF><CR><LF> character sequence could be split up between
  // 2 calls to the trace() method, we will store the last 3 characters
  // of the previous trace call (if it existed)
  private String previousTraceEnding = "";
  
  // this variable is true when the contents of the trace seem to be suggesting that
  // what is being traced are the HTTP headers
  private boolean doingHttpHeaders = true;

  
  private ReleaseManager releaseManager;

  public Client() {
    this.serviceWorkDir = ServiceContext.getServiceContext().getApplicationServiceContext().getServiceState().getWorkingDirectoryName();
    this.request = new RequestAnalizer(this);
    this.response = new ResponseImpl(this);
  }

  public void initClientIP(byte[] request, int offset, int length) {
	if (request.length == 16) {
		clientIP = new byte[16];
	    System.arraycopy(request, offset + length - 16, clientIP, 0, 16);
	}
	else {
	  clientIP = new byte[4];
    System.arraycopy(request, offset + length - 4, clientIP, 0, 4);
	}
  }

  public void init(FCAConnection connection, int port, int icm_id,
      int client_id, boolean isSsl, SslAttributesImpl sslAttributes, ReleaseManager releaseManager) 
      throws IOException {
    this.connection = connection; 
    this.client_id = client_id;
    this.port = port;
    this.remotePort = connection.getPort();
    this.icm_id = icm_id;
    this.clientIP = connection.getInetAddress().getAddress();
    request.init(connection, sslAttributes, isSsl);
    dsrHttpRequestContext = null;
    this.releaseManager = releaseManager;
  }

  /**
   * Reads the request line and request headers from the request byte array and initialize the request object.
   */
  public boolean initialize() {
   return request.initialize();
  }

  /**
   * Clears all resources used in processing the request and returns the pooled objects into pools.
   */
  public void finish() {
    try {
      if (connection != null) {
        try {
          OutputStream os = connection.getOutputStream();
          if (os != null) {
            try {
              os.close();
            } catch(IOException e) {
              Log.traceWarning(LOCATION_HTTP, "ASJ.http.000357", "Error in closing FCA output stream.", e, getIP(), null, null);
            }
          }
          InputStream is = connection.getInputStream();
          if (is != null) {
            try {
              is.close();
            } catch (IOException e) {
              Log.traceWarning(LOCATION_HTTP, "ASJ.http.000358", "Error in closing FCA input stream.", e, getIP(), null, null);
            }
          }
        } catch (Exception e) {
          Log.traceWarning(LOCATION_HTTP, "ASJ.http.000069", "Error in closing FCA streams.", e, getIP(), null, null);
        }
        connection.close();
      }
    } catch (Exception _) {
      Log.traceWarning(LOCATION_HTTP, "ASJ.http.000070", "Error in closing FCA connection.", _, getIP(), null, null);
    }
    
    
    if (!request.isPreserved()){
    	request.recycle();
    }else{
    	request.releasePreservationLock();
    }
    //request.endSessionRequest();
    response.recycle();
  }

  public DSRHttpRequestContext getDsrHttpRequestContext() {
    if (dsrHttpRequestContext == null) {
      dsrHttpRequestContext = new DSRHttpRequestContextImpl();
      dsrHttpRequestContext.init(getRequest());
    }
    return dsrHttpRequestContext;
  }

  public int getClientId() {
    return client_id;
  }

  /**
   * Actually it is ICM node ID
   * 
   * @return
   */
  public int getDispatcherId() {
    return icm_id;
  }

  public byte[] getIP() {
    if (!ServiceContext.getServiceContext().getHttpProperties().isUseIPv6Format() && clientIP.length == 16) {
      return ParseUtils.convertFromIPv4MappedAddress(clientIP);
    }
    return clientIP;
  }

  public int getPort() {
    return port;
  }

  public int getRemotePort() {
    return remotePort;
  }

  public RequestAnalizer getRequestAnalizer() {
    return request;
  }

  public Request getRequest() {
    return request.getRequest();
  }

  public ResponseImpl getResponse() {
    return response;
  }

  public void send(byte[] msg, int off, int len, byte connectionFlag) {
    try {
      connection.getOutputStream().write(msg, off, len);
      connection.getOutputStream().flush();
      trace(msg, off, len);
    } catch (Exception e) {
      Log.logWarning("ASJ.http.000071", "Data sending failed.", e, getIP(), null, null);
    }
  }

  /**
   * Sends the given bytes starting from the given offset with the given length
   * to the client
   * 
   * <p>For the difference of other send methods this one throws
   * <code>java.io.IOException</code> because it is used by
   * <code>java.servlet.ServletOutputStream</code> implementation and any
   * thrown exception have to be propagated up to the servlet</p>
   * 
   * @param msg
   * the bytes to send
   * 
   * @param off
   * the offset where to start
   * 
   * @param len
   * the length of the bytes to send
   * 
   * @throws IOException
   * if underlying output stream throws an <code>IOException</code>
   */
  public void send(byte[] msg, int off, int len) throws IOException {
    connection.getOutputStream().write(msg, off, len);
    trace(msg, off, len);
  }

  public void flush() throws IOException {
    connection.getOutputStream().flush();
  }

  public ClusterElement[] getClusterElements() {
    return ServiceContext.getServiceContext().getApplicationServiceContext().getClusterContext().getClusterMonitor().getParticipants();
  }

  public ClusterElement getCurrentClusterElement() {
    return ServiceContext.getServiceContext().getApplicationServiceContext().getClusterContext().getClusterMonitor().getCurrentParticipant();
  }

  public String getWorkingDir() {
    return serviceWorkDir;
  }
  /**
   * @return
   */
  public FCAConnection getConnection() {
    return connection;
  }

  /**
   * Dumps the response in the traces. 
   * For severity "debug" the dump is in HEX format.
   * For severity "path" the dump is in String format.
   * For severity "info" the dump is in String format, but consists only of the HTTP headers.
   * @param msg
   * @param off
   * @param len
   */
  private void trace(byte[] msg, int off, int len){
    if (LOCATION_HTTP_TRACE_RESPONSE.beDebug()) {
    	LOCATION_HTTP_TRACE_RESPONSE.debugT("CLIENT: "+getClientId()+", REPLY:\r\n" + HexPrinter.toString(msg, off, len));
    }else if (LOCATION_HTTP_TRACE_RESPONSE.bePath()) {
    	LOCATION_HTTP_TRACE_RESPONSE.pathT("CLIENT: "+getClientId()+", REPLY:\r\n" + new String(msg, off, len));
    } else if (LOCATION_HTTP_TRACE_RESPONSE.beInfo()) {
    	String response = new String(msg, off, len);

    	// last chars of the previous trace (which could contain a substring of "\r\n\r"), combined
    	// with the contents of the current trace
    	String combinedResponse = this.previousTraceEnding + response;
    	int previousTraceEndingLength = this.previousTraceEnding.length();
    	// if response begins with the HTTP header, we will assume that this is the first trace of the
    	// response, and therefore we clear the previousTraceEnding string
    	if (response.startsWith("HTTP/")) {
    		previousTraceEnding = "";
    		this.doingHttpHeaders = true;
    	}
    	
    	if (this.doingHttpHeaders) {
    		// index of where the headers are separated from the body
    		int separatorIndex = combinedResponse.indexOf("\r\n\r\n");
    		if (separatorIndex > -1) {
    			// trace everything before the \r\n\r\n, i.e. what is assumed to be the headers
    			LOCATION_HTTP_TRACE_RESPONSE.infoT("CLIENT: "+getClientId()+", REPLY:\r\n"
    					+ combinedResponse.substring(previousTraceEndingLength, separatorIndex));
    			
    			this.doingHttpHeaders = false;
    		} else {
    			// output everything if we're doing headers and there is no \r\n\r\n
    			LOCATION_HTTP_TRACE_RESPONSE.infoT("CLIENT: "+getClientId()+", REPLY:\r\n"
    					+ response);
    		}
    	}
    	
    	// now we set the previousTraceEnding to the last 3 characters of this trace
    	int endIndex = combinedResponse.length();
    	int beginIndex = Math.max(0, endIndex - 3);
    	this.previousTraceEnding = combinedResponse.substring(beginIndex, endIndex);    	
    }
  }

  public ReleaseManager getReleaseManager() {
		return releaseManager;
	}
}
