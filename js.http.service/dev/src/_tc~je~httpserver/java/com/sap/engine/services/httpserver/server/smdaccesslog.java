package com.sap.engine.services.httpserver.server;

import java.util.Iterator;

import com.sap.engine.services.httpserver.lib.*;
import com.sap.engine.services.httpserver.lib.util.*;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class SmdAccessLog {
  private static final byte[] logStr1_1 = " - ".getBytes();
  private static final byte[] logStr1_2 = " [".getBytes();
  private static final byte[] logStr1_2_1 = "] ".getBytes();
  private static final byte[] logStr1_2_2 = "]".getBytes();
  private static final byte[] logStr2 = "] \"".getBytes();
  private static final byte[] logStr3 = "\"".getBytes();
  private static final byte[] logStr4 = " ".getBytes();
  private static final String delimeter = " : ";
  private static final byte[] logStr5 = delimeter.getBytes();
  private static final byte[] logStr6 = " {".getBytes();
  private static final byte[] logStr7 = "}".getBytes();
  private static final byte[] default_user = "-".getBytes();

  private static Location locationResponseSMD = null;

  public static void init() {
    locationResponseSMD = Location.getLocation(Constants.LOCATION_RESPONSES_SMD);
  }

  /**
   * Save request in logfile.
   * 
   * @param requestLine request to save 
   * @param ip  client ip
   * @param responseCode  response status code
   * @param responseLength  length of returned data in response body
   * @param processTime the response time of the request; if -1 no response time will be logged
   * @param logHeader the header whose value to be written at the end of the line in http access log
   * @param logIsStatic when true information about the type of the requested resource (static file or dynamic resource) is logged
   * @param isStatic  denotes whether the requested resource is dynamic or static in case logIsStatic is enabled
   * @param requestHeaders the line containing request headers names and values in SMD specific log format
   * @param responseHeaders the line containing response headers names and values in SMD specific log format
   */
  public static void addRequestLog(byte[] requestLine, byte[] ip, int responseCode, int responseLength, long processTime, String logHeader, boolean logIsStatic, boolean isStatic, byte[] requestHeaders, byte[] responseHeaders) {
    if (responseLength <= -1) {
      responseLength = 0;
    }
    byte[] resLength = Ascii.intToAsciiArr(responseLength);
    byte[] hostName = ParseUtils.inetAddressByteToString(ip);
    boolean logRequestResponseHeaders = ServiceContext.getServiceContext().getHttpProperties().isLogRequestResponseHeaders();

    if (ResponseCodes.status_code_byte[responseCode] == null) {
      ResponseCodes.status_code_byte[responseCode] = (" " + responseCode).getBytes();
    }
    
    byte[] logHeaderBytes = logHeader.getBytes();

    try {
      byte[] processTimeStr = null;
      int logHeaderValueLogLength = 0;
      if (logHeaderBytes != null) {
        logHeaderValueLogLength = 4 + logHeaderBytes.length;
      }
      if (logIsStatic) {
        logHeaderValueLogLength += 5;//"s[" + "t"|"f" + "]"
      }
      byte[] responsePhrase =  ResponseCodes.reason(responseCode,"").trim().getBytes();
      //begin with the request length
      int log_message_length = hostName.length + logStr5.length + requestLine.length;
      
      // check for the headers
      if (logRequestResponseHeaders && requestHeaders.length > 0){ 
        log_message_length += logStr6.length + requestHeaders.length + logStr7.length;
      }
        
      // if SMD case - add [response code repsonse phrase]
        
      log_message_length += logStr1_2.length;
      log_message_length += ResponseCodes.status_code_byte[responseCode].length - 1;
      log_message_length += logStr4.length;
      log_message_length += responsePhrase.length;
      log_message_length += logStr1_2_1.length;
        
      //check for response
      log_message_length += resLength.length;
        
      //check for request processing time
      if (processTime  != -1) {
        processTimeStr = Ascii.intToAsciiArr((int)processTime);
        log_message_length += 3 + processTimeStr.length;
      } 
      
      //add log header value
      if (logRequestResponseHeaders && responseHeaders.length > 0){
        log_message_length += logStr6.length + responseHeaders.length + logStr7.length;
      }
      
      log_message_length +=logHeaderValueLogLength;
        
        
      byte [] logEntryTest = new byte[log_message_length];
         
      int idx = 0;
      System.arraycopy(hostName, 0, logEntryTest, idx, hostName.length);
      idx += hostName.length;
      System.arraycopy(logStr5, 0, logEntryTest, idx, logStr5.length);
      idx += logStr5.length;
      System.arraycopy(requestLine, 0, logEntryTest, idx, requestLine.length);
      idx += requestLine.length;
       
        
      if (logRequestResponseHeaders && requestHeaders.length > 0){
        System.arraycopy(logStr6, 0, logEntryTest, idx, logStr6.length);
        idx += logStr6.length;
        System.arraycopy(requestHeaders, 0, logEntryTest, idx, requestHeaders.length);
        idx += requestHeaders.length;
        System.arraycopy(logStr7, 0, logEntryTest, idx, logStr7.length);
        idx += logStr7.length;
      }
        
      // if SMD case add response status code and response phrase
      System.arraycopy(logStr1_2, 0, logEntryTest, idx, logStr1_2.length);
      idx += logStr1_2.length;
        
      System.arraycopy(ResponseCodes.status_code_byte[responseCode], 1, logEntryTest, idx, ResponseCodes.status_code_byte[responseCode].length-1);
      idx += ResponseCodes.status_code_byte[responseCode].length -1;
          
      System.arraycopy(logStr4, 0, logEntryTest, idx, logStr4.length);
      idx += logStr4.length;
         
      System.arraycopy(responsePhrase, 0, logEntryTest, idx, responsePhrase.length);
      idx += responsePhrase.length;
                    
      System.arraycopy(logStr1_2_1, 0, logEntryTest, idx, logStr1_2_1.length);
      idx += logStr1_2_1.length;
        
      System.arraycopy(resLength, 0, logEntryTest, idx, resLength.length);
      idx += resLength.length;
        
      if (processTimeStr != null) {
        logEntryTest[idx++] = ' ';
        logEntryTest[idx++] = '[';
        System.arraycopy(processTimeStr, 0, logEntryTest, idx, processTimeStr.length);
        idx += processTimeStr.length;
        logEntryTest[idx++] = ']';
      }
        
      if (logRequestResponseHeaders && responseHeaders.length > 0){
        System.arraycopy(logStr6, 0, logEntryTest, idx, logStr6.length);
        idx += logStr6.length;
        System.arraycopy(responseHeaders, 0, logEntryTest, idx, responseHeaders.length);
        idx += responseHeaders.length;
        System.arraycopy(logStr7, 0, logEntryTest, idx, logStr7.length);
        idx += logStr7.length;
      }
        
      if (logHeaderBytes != null) {
        logEntryTest[idx++] = ' ';
        logEntryTest[idx++] = 'h';
        logEntryTest[idx++] = '[';
        System.arraycopy(logHeaderBytes, 0, logEntryTest, idx, logHeaderBytes.length);
        idx += logHeaderBytes.length;
        logEntryTest[idx++] = ']';
      }
      if (logIsStatic) {
        logEntryTest[idx++] = ' ';
        logEntryTest[idx++] = 's';
        logEntryTest[idx++] = '[';
        if (isStatic) {
          logEntryTest[idx++] = 't';
        } else {
          logEntryTest[idx++] = 'f';
        }
        logEntryTest[idx++] = ']';
      }

      locationResponseSMD.setEffectiveSeverity(Severity.INFO); // ensures that this trace will be written with INFO severity.
      locationResponseSMD.infoT(new String(logEntryTest));
           
    } catch (Exception e) {
      //Log.logError("Cannot create SMD access log for an http request.", e, null);
      if (requestLine != null) {
        Log.logError("ASJ.http.000219", "Cannot write HTTP access log (SMD) for HTTP request [{0}].", 
          new Object[]{new String(requestLine)}, e, null, null, null);
      } else {
        Log.logError("ASJ.http.000220", "Cannot write HTTP access log (SMD) for HTTP request [unknown].", e, null, null, null);
      }
    }
    
  }

  /**
   * If SMD log is turned off, the SMD log file should be released from the Log controller in order to be deleted.
   *
   */
  public static void closeSMDLog() {
    Iterator iter = locationResponseSMD.getAllLogs().iterator();
    if( iter != null) {
      while (iter.hasNext()) {// there should be only one log - the SMD log.        
        com.sap.tc.logging.Log currentLog = (com.sap.tc.logging.Log) iter.next();
        currentLog.close(); // when the log is closed and the file deleted manually from local file system,
        // every following request for logging will create a new file.          
      }
    }
  }

}
