/*
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.msp;

import java.util.*;

import com.sap.engine.boot.SystemProperties;
import com.sap.engine.frame.cluster.message.MessageServerBridge;

/**
 * Helper class for MSP Service
 */
public class MSPProcessor {


  /**
   * Stores the new line separator used by the commands for output
   */
  public static String newLineSeparator = SystemProperties.getProperty("line.separator");

  /**
   * Stores the core monitor used to access the MSMonitor
   */
  private final MessageServerBridge msBridge;

  public MSPProcessor(MessageServerBridge msBridge) {
    this.msBridge = msBridge;
  }

  /**
   * Represents the tree map in a "nice" printable form
   *
   * @param treeMap   The tree map to represent
   * @return   The new representation in a "string" form
   */
  public String representTreeMap(TreeMap treeMap, boolean guiRequest) {
    // Initialize and sort the entries in the map
    StringBuffer returnString = new StringBuffer();
    Iterator iter = treeMap.keySet().iterator();

    // Find the max length of the entries
    int maxLength = 0;
    int currentLength = 0;
    while (iter.hasNext()) {
      if ((currentLength = iter.next().toString().length()) > maxLength) {
        maxLength = currentLength;
      }
    }

    // Produce the return string
    iter = treeMap.keySet().iterator();
    while (iter.hasNext()) {
      Object obj = iter.next();
      if (!guiRequest) {
        returnString.append("\t");
      }
      returnString.append(obj);
      currentLength = obj.toString().length();
      for(int i = currentLength; i < maxLength; i++) {
        returnString.append(" ");
      }
      returnString.append(" = ");
      returnString.append(treeMap.get(obj));
      returnString.append(newLineSeparator);
    }
    return returnString.toString();
  }

  /**
   * Represents the client statistic in a "nice" printable form
   *
   * @param treeMap   The tree map to represent
   * @return   The new representation in a "string" form
   */
  public String representClientStat(TreeMap treeMap, boolean guiRequest) {
    // Initialize and sort the entries in the map
    StringBuffer returnString = new StringBuffer();

    // Find the max length of the entries
	  boolean activeClientStatistic = false;
    String stateStr = (String)treeMap.get("isActivated");
    if (stateStr != null) {
    	Boolean state = new Boolean(stateStr);
    	activeClientStatistic = state.booleanValue();
	    treeMap.remove("isActivated");
	  }

    returnString.append("Message Server Client Statistic is ");
    if (activeClientStatistic == true) 
    	returnString.append("activated");
    else
    	returnString.append("deactivated");

    // Find the max length of the entries
    String[] columnNames = getColumnNames((String)treeMap.get("HEADER_NAME"));
    if (columnNames == null) return returnString.toString();
    treeMap.remove("HEADER_NAME");
    int[] columnLength = getColumnLength((String)treeMap.get("HEADER_LENGTH"));
    if (columnLength == null) return returnString.toString();
    treeMap.remove("HEADER_LENGTH");

    returnString.append(newLineSeparator);
    returnString.append(newLineSeparator);
    

    // column header
		for( int ndx = 0; ndx < columnNames.length; ndx++) {
      returnString.append("|");
      returnString.append(columnNames[ndx]);
      int currentLength = columnNames[ndx].length();
      for(int i = currentLength; i < columnLength[ndx]; i++) {
        returnString.append(" ");
      }
		}
    returnString.append("|");
    returnString.append(newLineSeparator);

		// header line
		for( int ndx = 0; ndx < columnNames.length; ndx++) {
      returnString.append("|");
      for(int i = 0; i < columnLength[ndx]; i++) {
        returnString.append("-");
      }
		}
    returnString.append("|");
    returnString.append(newLineSeparator);

    // Produce the return string
    Iterator iter = treeMap.keySet().iterator();
    while (iter.hasNext()) {
    	int ndx = 0;
    	
    	String obj = (String)iter.next();

      // Search for properties
			StringTokenizer strTokenizer = new StringTokenizer((String)treeMap.get(obj), ";");
			String token;

			boolean isSingle = true;
			while(strTokenizer.hasMoreTokens() == true && ndx < columnNames.length) {
	   		token = strTokenizer.nextToken();
	   		
	      returnString.append("|");

	   		switch (ndx) {
	   			case 1: isSingle = token.startsWith("Single");
	   					break;
	   			case 3: if (!isSingle) token = "";
	   			    break;
	   			case 4: if (isSingle) token = "";
	   			    break; 
	   			case 5: if (isSingle) token = "";
	   			    break; 
	   		}
	   		
	      if (token.length() <= columnLength[ndx]) {
	      	if (token.length() == 0) {
			      for(int i = 0; i < columnLength[ndx]; i++) {
			        returnString.append("*");
			      }
	      	}
	      	else {
			      returnString.append(token);
			      int currentLength = token.length();
			      for(int i = currentLength; i < columnLength[ndx]; i++) {
			        returnString.append(" ");
			      }
	      	}
	      }
	      else {
		      returnString.append(token.substring(0, columnLength[ndx]));
	      }
	   	 	ndx++;
			}
      returnString.append("|");
      returnString.append(newLineSeparator);
    }
    return returnString.toString();
  }

  /**
   * Represents the client statistic in a "nice" printable form
   *
   * @param treeMap   The tree map to represent
   * @return   The new representation in a "string" form
   */
  public String representTreeMapWithHeader(TreeMap treeMap, boolean guiRequest) {
    // Initialize and sort the entries in the map
    StringBuffer returnString = new StringBuffer();
    Iterator iter = treeMap.keySet().iterator();

    // Find the max length of the entries
    String[] columnNames = getColumnNames((String)treeMap.get("HEADER_NAME"));
    if (columnNames == null) return null;
    treeMap.remove("HEADER_NAME");
    int[] columnLength = getColumnLength((String)treeMap.get("HEADER_LENGTH"));
    if (columnLength == null) return null;
    treeMap.remove("HEADER_LENGTH");
    
		// column header 
	  if (!guiRequest) {
	    returnString.append("\t");
	  }
		for( int ndx = 0; ndx < columnNames.length; ndx++) {
      returnString.append("|");
      returnString.append(columnNames[ndx]);
      int currentLength = columnNames[ndx].length();
      for(int i = currentLength; i < columnLength[ndx]; i++) {
        returnString.append(" ");
      }
		}
    returnString.append("|");
    returnString.append(newLineSeparator);

		// header line
	  if (!guiRequest) {
	    returnString.append("\t");
	  }
		for( int ndx = 0; ndx < columnNames.length; ndx++) {
      returnString.append("|");
      for(int i = 0; i < columnLength[ndx]; i++) {
        returnString.append("-");
      }
		}
    returnString.append("|");
    returnString.append(newLineSeparator);

    // Produce the return string
    iter = treeMap.keySet().iterator();
    while (iter.hasNext()) {
    	String obj = (String)iter.next();

      if (!guiRequest) {
        returnString.append("\t");
      }

      // Search for properties
			StringTokenizer strTokenizer = new StringTokenizer((String)treeMap.get(obj), ";");
			String token;

    	int ndx = 0;
			while(strTokenizer.hasMoreTokens() == true && ndx < columnNames.length) {
	   		token = strTokenizer.nextToken();

		    returnString.append("|");
	      if (token.length() <= columnLength[ndx]) {
	      	if (token.length() == 0) {
			      for(int i = 0; i < columnLength[ndx]; i++) {
			        returnString.append("*");
			      }
	      	}
	      	else {
			      returnString.append(token);
			      int currentLength = token.length();
			      for(int i = currentLength; i < columnLength[ndx]; i++) {
			        returnString.append(" ");
			      }
	      	}
	      }
	      else {
		      returnString.append(token.substring(0, columnLength[ndx]));
	      }
	      
	      ndx++;
			}
      returnString.append("|");
      returnString.append(newLineSeparator);
    }
    return returnString.toString();
  }

  /**
   * Method getInfo.
   * The getInfo method returns a map which contains the internal message server
   * information (Release, SID, Patch Level...) as key/value pairs.
   *
   * @return TreeMap internal message server information.
   */
  public TreeMap getInformation() {
    return msBridge.getMSInfo();
  }

  /**
   * Method getParams.
   * The getParams method returns a map which contains the message server
   * parameters.
   *
   * @return TreeMap message server parameters
   */
  public TreeMap getParameters() {
    return msBridge.getMSParams();
  }

  /**
   * Method getStatistic.
   * The getStatistics method returns a map which contains the message server
   * statistics as key/value pairs. You have to switch on the statistics by
   * calling activateStatistic().
   *
   * @return TreeMap message server statistics.
   */
  public TreeMap getStatistic() {
    return msBridge.getMSStatistic();
  }

  /**
   * Method activateStatistic.
   * Activate the internal message server statistics.
   *
   */
  public void activateStatistic() {
    msBridge.activateMSStatistic();
  }

  /**
   * Method deactivateStatistic.
   * Deactivate the internal message server statistics.
   */
  public void deactivateStatistic() {
    msBridge.deactivateMSStatistic();
  }

  /**
   * Method resetStatistic.
   * Reset the internal message server statistics.
   */
  public void resetStatistic() {
    msBridge.resetMSStatistic();
  }

  /**
   * Method incrementTraceLevel.
   * Increment the internal message server trace level.
   */
  public void incrementTraceLevel() {
    msBridge.incrementMSTraceLevel();
  }

  /**
   * Method decrementTraceLevel.
   * Decrement the internal message server trace level.
   */
  public void decrementTraceLevel() {
    msBridge.decrementMSTraceLevel();
  }

  /**
   * Method resetTraceLevel.
   * Reset the internal message server trace level to the default value.
   */
  public void resetTraceLevel() {
    msBridge.resetMSTraceLevel();
  }

  /**
   * Method getHardwareId.
   * Get the hardware id for the licensing.
   *
   * @return String hardware id of the message server system.
   */
  public String getHardwareId() {
    return msBridge.getHardwareID();
  }

  /**
   * Method getSystemId.
   * Get the system id for the licensing.
   *
   * @return String system id of the message server.
   */
  public String getSystemId() {
    return msBridge.getSystemID();
  }

  /**
   * Method updateLocalClusterRepository.
   * Update the local cluster repository with the repository of the
   * message server. The method process all cluster events comming up
   * with the update.
   */
  public void updateLocalClusterRepository() {
    msBridge.updateMSLocalClusterRepository();
  }

  /**
   * Method getClientStatistic.
   * The getClientStatistic method returns a map which contains the message server
   * client statistics as key/value pairs. You have to switch on the statistics by
   * calling activateStatistic().
   *
   * @return TreeMap message server statistics.
   */
  public TreeMap getClientStatistic() {
    return msBridge.getMSClientStatistic();
  }

  /**
   * Method getServiceInfo.
   * The getServiceInfo method returns a map which contains the list of
   * registered services as key/value pairs. 
   *
   * @return TreeMap message server statistics.
   */
  public TreeMap getServiceInfo() {
    return msBridge.getMSServiceInfo();
  }

  /**
   * Method activateClientStatistic.
   * Activate the internal message server client statistics.
   *
   */
  public void activateClientStatistic() {
    msBridge.activateMSClientStatistic();
  }

  /**
   * Method deactivateClientStatistic.
   * Deactivate the internal message server client statistics.
   */
  public void deactivateClientStatistic() {
    msBridge.deactivateMSClientStatistic();
  }

  /**
   * Method resetClientStatistic.
   * Reset the internal message server client statistics.
   */
  public void resetClientStatistic() {
    msBridge.resetMSStatistic();
  }
  
  public String[] getColumnNames(String headerNameStr) {
  	if (headerNameStr == null) return null;
  	
    // get header length
    StringTokenizer strTokenizer = new StringTokenizer(headerNameStr, ";");
		String [] headerNames = new String[strTokenizer.countTokens()];
		
		int index = 0;
		while(strTokenizer.hasMoreTokens() == true) {
   		headerNames[index++] = strTokenizer.nextToken();
		}   		
  	
  	return headerNames;
  }

  public int[] getColumnLength(String headerLengthStr) {
  	if (headerLengthStr == null) return null;
  	
    // get header length
    StringTokenizer strTokenizer = new StringTokenizer(headerLengthStr, ";");
		int [] headerLength = new int[strTokenizer.countTokens()];
		
		int index = 0;
		while(strTokenizer.hasMoreTokens() == true) {
			Integer length = new Integer(strTokenizer.nextToken());
   		headerLength[index++] = length.intValue();
		}   		
  	
  	return headerLength;
  }
}
