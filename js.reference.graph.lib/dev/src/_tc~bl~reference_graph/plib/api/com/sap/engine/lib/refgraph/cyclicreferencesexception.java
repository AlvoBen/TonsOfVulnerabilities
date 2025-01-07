/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.lib.refgraph;



/**
 *@author Luchesar Cekov
 */
public class CyclicReferencesException extends Exception {
  private static final long serialVersionUID = 9109920565524621278L;
  private final String message;
  
  public String getMessage() {
    return message;
  }
  
  
  public CyclicReferencesException(String[] cycles) {
    message = "One or more Cyclic reference detected during building application reference graph " + getCyclePath(cycles);
  }

  public static String getCyclePath(String[] cycles)  {
    StringBuffer resultErrorString = new StringBuffer();
    for (int i = 0; i < cycles.length; i++) {
      resultErrorString.append(cycles[i]);
      resultErrorString.append(System.getProperty("line.separator"));
    }

    return resultErrorString.toString();
  }

}
