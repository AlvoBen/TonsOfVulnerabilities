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
import com.sap.engine.services.httpserver.lib.util.*;
import com.sap.engine.interfaces.security.SecurityContextObject;
import com.sap.engine.interfaces.security.SecuritySession;
import com.sap.engine.frame.core.thread.ThreadContext;
import com.sap.tc.logging.Category;
import com.sap.tc.logging.Location;

public class HttpAccessLog {
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

  private static final String CATEGORY_ACCESS_LOG = "System/HttpAccess/Access";
  private static final String CATEGORY_CLF_ACCESS_LOG = "System/HttpAccess/CommonLogFormat";

  private static Date clfDateUtil = null;
  private static Category categoryAccessLog = null;
  private static Category categoryCLFAccessLog = null;
  private static Location locationResponse = null;
  private static Location locationResponseCLF = null;

  public static void init(Date clfDateUtilParam) {
    clfDateUtil = clfDateUtilParam;
    categoryAccessLog = Category.getCategory(Category.getRoot(), CATEGORY_ACCESS_LOG);
    categoryCLFAccessLog = Category.getCategory(Category.getRoot(), CATEGORY_CLF_ACCESS_LOG);
    locationResponse = Location.getLocation(Constants.LOCATION_RESPONSES);
    locationResponseCLF = Location.getLocation(Constants.LOCATION_RESPONSES_CLF);
  }

	/**
	 * Save request in logfile.
   * 
   * @param requestLine	request to save 
   * @param ip  client ip
   * @param responseCode	response status code
   * @param responseLength	length of returned data in response body
   * @param processTime	the response time of the request; if -1 no response time will be logged
   * @param logHeader	the header whose value to be written at the end of the line in http access log
   * @param logIsStatic when true information about the type of the requested resource (static file or dynamic resource) is logged
   * @param isStatic	denotes whether the requested resource is dynamic or static in case logIsStatic is enabled
   * @param requestHeaders the line containing request headers names and values in SMD specific log format
   * @param responseHeaders the line containing response headers names and values in SMD specific log format
   */
	public static void addRequestLog(byte[] requestLine, byte[] ip,
                                   int responseCode, int responseLength,
                                   long processTime, String logHeader, boolean logIsStatic, boolean isStatic, byte[] requestHeaders, byte[] responseHeaders) {
		if (responseLength <= -1) {
			responseLength = 0;
		}
		byte[] resLength = Ascii.intToAsciiArr(responseLength);
		byte[] hostName = ParseUtils.inetAddressByteToString(ip);
		byte[] logEntry = null;
		boolean logRequestResponseHeaders = ServiceContext.getServiceContext().getHttpProperties().isLogRequestResponseHeaders();

		if (ResponseCodes.status_code_byte[responseCode] == null) {
			ResponseCodes.status_code_byte[responseCode] = (" " + responseCode).getBytes();
			
		}
		
		requestLine = MaskUtils.maskRequestLine(requestLine);
		byte[] logHeaderBytes = null;
		if (logHeader != null) {
			logHeaderBytes = MaskUtils.maskHeader(ServiceContext.getServiceContext().getHttpProperties().getLogHeaderValue().getBytes(), logHeader.getBytes());
		}
		
		
		
		if (!ServiceContext.getServiceContext().getHttpProperties().logInCLF()) {
			//the format of the log message differs from the CommonLogFormat
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
				
				//				adding response code
  			log_message_length += ResponseCodes.status_code_byte[responseCode].length + logStr4.length;
			
				
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
				
				System.arraycopy(ResponseCodes.status_code_byte[responseCode], 0, logEntryTest, idx, ResponseCodes.status_code_byte[responseCode].length);
				idx += ResponseCodes.status_code_byte[responseCode].length;
				System.arraycopy(logStr4, 0, logEntryTest, idx, logStr4.length);
				idx += logStr4.length;
				
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
				categoryAccessLog.infoT(locationResponse, new String(logEntryTest));
				   
			} catch (Exception e) {
				Log.logError("ASJ.http.000167", 
				  "Cannot create http access log for an http request.", e, null, null, null);
			}
		} else {
			//the format of the log message is the CommonLogFormat
			try {
				byte[] dateBytes = clfDateUtil.getDateCLF();
				byte[] user = getUser();
				logEntry = new byte[hostName.length + logStr1_1.length + user.length + logStr1_2.length
						+ dateBytes.length + logStr2.length
						+ requestLine.length + logStr3.length + ResponseCodes.status_code_byte[responseCode].length + logStr4.length + resLength.length];
				int index = 0;
				System.arraycopy(hostName, 0, logEntry, index, hostName.length);
				index += hostName.length;
				System.arraycopy(logStr1_1, 0, logEntry, index, logStr1_1.length);
				index += logStr1_1.length;
				System.arraycopy(user, 0, logEntry, index, user.length);
				index += user.length;
				System.arraycopy(logStr1_2, 0, logEntry, index, logStr1_2.length);
				index += logStr1_2.length;
				System.arraycopy(dateBytes, 0, logEntry, index, dateBytes.length);
				index += dateBytes.length;
				System.arraycopy(logStr2, 0, logEntry, index, logStr2.length);
				index += logStr2.length;
				System.arraycopy(requestLine, 0, logEntry, index, requestLine.length);
				index += requestLine.length;
				System.arraycopy(logStr3, 0, logEntry, index, logStr3.length);
				index += logStr3.length;
				System.arraycopy(ResponseCodes.status_code_byte[responseCode], 0, logEntry, index, ResponseCodes.status_code_byte[responseCode].length);
				index += ResponseCodes.status_code_byte[responseCode].length;
				System.arraycopy(logStr4, 0, logEntry, index, logStr4.length);
				index += logStr4.length;
				System.arraycopy(resLength, 0, logEntry, index, resLength.length);
				index += resLength.length;
				categoryCLFAccessLog.infoT(locationResponseCLF, new String(logEntry));
				
			} catch (Exception e) {
				Log.logError("ASJ.http.000168", 
				  "Cannot create a log in Common Log Format for an http request.", e, null, null, null);
			}
		}
		
	}
	
	
	

	private static byte[] getUser() {
		ThreadContext localTC = ServiceContext.getServiceContext().getThreadSystem().getThreadContext();
		SecurityContextObject securityContextObject = (SecurityContextObject)localTC.getContextObject(localTC.getContextObjectId(SecurityContextObject.NAME));
		SecuritySession ss = securityContextObject.getSession();
		if ((ss != null) && (ss.getAuthenticationConfiguration() != null)) {
			String user = ss.getPrincipal().getName();
			if (user != null) {
				return user.getBytes();
			}
		}
		return default_user;
	}

}
