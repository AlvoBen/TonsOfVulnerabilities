package com.sap.engine.services.httpserver.filters;

import java.io.IOException;
import java.util.Enumeration;

import com.sap.engine.services.httpserver.chain.FilterConfig;
import com.sap.engine.services.httpserver.chain.FilterException;
import com.sap.engine.services.httpserver.chain.HTTPRequest;
import com.sap.engine.services.httpserver.chain.HTTPResponse;
import com.sap.engine.services.httpserver.chain.HostChain;
import com.sap.engine.services.httpserver.chain.HostFilter;
import com.sap.engine.services.httpserver.interfaces.properties.HostProperties;
import com.sap.engine.services.httpserver.interfaces.properties.HttpProperties;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.lib.protocol.HeaderNames;
import com.sap.engine.services.httpserver.lib.util.MaskUtils;
import com.sap.engine.services.httpserver.server.Client;
import com.sap.engine.services.httpserver.server.Constants;
import com.sap.engine.services.httpserver.server.HttpAccessLog;
import com.sap.engine.services.httpserver.server.SmdAccessLog;

public class SmdResponseLogWriter extends HostFilter {
  
  /**
   * Helper zero length array of bytes
   */
  private static final byte[] ZERO_LENGTH_BYTE = new byte[0];

  @Override
  public void process(HTTPRequest request, HTTPResponse response,
      HostChain chain) throws FilterException, IOException {
    HttpProperties httpProps = chain.getServerScope().getHttpProperties();
    HostProperties hostProps = chain.getHostScope().getHostProperties();
    String logHeaderValue = httpProps.getLogHeaderValue();
    Client client = request.getClient(); String logHeader = null;
    // If response log is disabled for this host, SMD case is off or SMD
    // case is on but request don't have SMD header puts this filter aside
    if (!((Constants.SMDHeader).equalsIgnoreCase(logHeaderValue) && (client.getRequest().getHeaders().getHeader(logHeaderValue) != null))) {
      chain.proceed();
      return;
    }
    
    // Gets the request processing start time
    long processTime = System.currentTimeMillis();
    // Let other filters do that they want to do
    chain.process(request, response);
    // Calculates request processing time if it should be written to the log
    processTime = httpProps.logResponseTime() ? System.currentTimeMillis()
        - processTime : -1;
    // Indicates if mark that file is static should be written to the log
    boolean logIsStatic = httpProps.logIsStatic();
    byte[] requestH = null, responseH = null;
    if (httpProps.isLogRequestResponseHeaders()){
      // Reads all the headers that should be written to the log
      requestH = getHeadersLine(client.getRequest().getHeaders());
      responseH = getHeadersLine(client.getResponse().getHeaders());
    }
    // Reads the only header that should be written to the log
    logHeader = client.getRequest().getHeaders().getHeader(logHeaderValue);
    
    // TODO: Rewrite the next code cause now it is taken without any change
    // from ResponseImpl.makeAnswer() and ResponseImpl.responseLog() methods
    int length = response.getContentLength();
    length = (length != -1) ? length : client.getRequest().getHeaders()
        .getIntHeader(HeaderNames.entity_header_content_length_);
    // Writes collected data to the response log
    SmdAccessLog.addRequestLog(client.getRequest().getRequestLine()
        .toByteArray(), client.getIP(), response.getStatusCode(), length,
        processTime, logHeader, logIsStatic, false, requestH, responseH);
  }


  public void init(FilterConfig config) throws FilterException {
    
  }

  public void destroy() {
    // TODO Auto-generated method stub

  }
  
  /**
   * Writes all the passed headers to byte array in order to be written
   * to the response log
   * 
   * @param mheaders
   * a <code>MimeHeaders</code> with request or response HTTP header fields
   * 
   * @return
   * a byte array with all the passed headers written inside
   */
  private byte[] getHeadersLine(MimeHeaders mheaders) {
    byte[] res = ZERO_LENGTH_BYTE;
    StringBuffer tbuff = new StringBuffer(1000);
    String t = " : ";
    Enumeration e = mheaders.names();
    while (e != null && e.hasMoreElements()) {
      String hname = (String) e.nextElement();
      tbuff.append("[");
      tbuff.append(hname);
      tbuff.append(t);
      String[] hvalues = mheaders.getHeaders(hname);
      for (int idx = 0; hvalues != null && idx < hvalues.length; idx++) {
        byte[] tmp_value = MaskUtils.maskHeader(hname.getBytes(), hvalues[idx]
            .getBytes());
        tbuff.append(new String(tmp_value));
        if (hvalues.length > 1 && idx < hvalues.length - 1 && idx > 0) {
          tbuff.append(", ");
        }
      }
      tbuff.append("]");
    }
    if (tbuff.length() > 0) {
      res = tbuff.toString().getBytes();
    }
    return res;
  }

  private boolean isHeaderPresent(MimeHeaders mheaders){
    boolean found = false;
    if (mheaders != null){
      Enumeration e = mheaders.names();
      while (e != null && e.hasMoreElements()){
        String hn = (String) e.nextElement();
        if (hn.equalsIgnoreCase(Constants.SMDHeader)){
          found = true;
          break;
        }
      }  
    }
    return found;
  }

}
