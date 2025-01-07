/**
 * Copyright (c) 2008 by SAP Labs Bulgaria,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * 
 * Created on Apr 25, 2008 by i045045
 *   
 */
 
package com.sap.engine.services.security.login;

import iaik.security.random.SecRandom;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import com.sap.engine.lib.lang.Convert;
import com.sap.engine.lib.security.Base64;
import com.sap.engine.services.security.SecurityServerFrame;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.SimpleLogger;

public class MarkIDGenerator {

  private static final Location LOCATION = Location.getLocation(MarkIDGenerator.class);
  
  /**
   * Generates MarkId cookee value
   * @return id len=10
   */

  public static String generateMarkID() {
    return generateID(10, 4, 2, 0);
  }

 
  private static String generateID(int woSAPBytesLen, int SHA1BytesLen, int nanoTimeBytesLen, int nodeIDBytesLen) {
    SecureRandom secRandom = SecRandom.getDefault(); 
    byte[] nodeIDByteArr = new byte[4];
    int nodeIDint = SecurityServerFrame.getServiceContext().getClusterContext().getClusterMonitor().getCurrentParticipant().getClusterId();
    Convert.writeIntToByteArr(nodeIDByteArr, 0, nodeIDint);
 
    
//    final int woSAPBytesLen = 30;
//    final int SHA1BytesLen = 20;
//    final int nanoTimeBytesLen = 6; // long has 8 bytes and we cut the 2 most highest bytes
//    final int nodeIDBytesLen = 4;
    byte[] forBase64EncByteArr = new byte[woSAPBytesLen];

    // getSHA1 bits
    byte[] shaRandomBytesArr = new byte[SHA1BytesLen];  
    secRandom.nextBytes( shaRandomBytesArr);
    System.arraycopy(shaRandomBytesArr, 0, forBase64EncByteArr, 0, SHA1BytesLen);
    
    // Get milliseconds
    try {
      fillBytesFromNanos(forBase64EncByteArr, SHA1BytesLen);
    } catch (UnsupportedEncodingException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000196", "Error while encoding byte array.", e);
      return null;
    }
    
    // Get NodeID
    //fillNodeIDBytes(forBase64EncByteArr, base64SHA1BytesLen + base64nanoTimeBytesLen);
    System.arraycopy(nodeIDByteArr, 0, forBase64EncByteArr, SHA1BytesLen + nanoTimeBytesLen, nodeIDBytesLen);
    
    // Base64
    byte[] base64EncByteArr = null;
    try {
      base64EncByteArr = Base64.encode(forBase64EncByteArr, Base64.VARIANT_URL);
    } catch (Exception e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000196", "Error while encoding byte array.", e);
      return null;
    }
    StringBuffer sb = null;
    try {
      sb = new StringBuffer(new String(base64EncByteArr, "ISO-8859-1"));
    } catch (UnsupportedEncodingException e) {
      SimpleLogger.traceThrowable(Severity.ERROR, LOCATION, "ASJ.secsrv.000197", "session", e);
      return null;
    }
    
    //removed because off MarkID length
    //sb.append("_SAP");
    
    if (LOCATION.beDebug()) {
      LOCATION.debugT("ID: " + sb.toString());
    }
    return sb.toString();
  } // generateSessionID()

  /**
   * Fills passed byte array with lowest 6 bytes from the nano time
   * @param forEncodingByteArr The byte array where to fill cut time 
   * @param base64nanoTimeBytesLen Starting offset in the array
   * @throws UnsupportedEncodingException 
   */
  private static void fillBytesFromNanos(byte[] forEncodingByteArr, int startOffset) throws UnsupportedEncodingException {
    
    long currentTimeNanos = System.nanoTime();  
    int lowerInt = (int) currentTimeNanos;
    short next2HigherBytes = (short)(currentTimeNanos >> 32);
    
    Convert.writeIntToByteArr(forEncodingByteArr, startOffset, lowerInt);
    Convert.writeShortToByteArr(forEncodingByteArr, startOffset + 4, next2HigherBytes);
  } // fillBytesFromNanos
}
