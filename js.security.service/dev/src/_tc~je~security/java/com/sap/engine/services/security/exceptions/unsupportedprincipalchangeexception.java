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
 * Created on 28.10.2008 by i027020
 *   
 */
 
package com.sap.engine.services.security.exceptions;

/**
 * @author Ralin Chimev
 *
 */
public class UnsupportedPrincipalChangeException extends SecurityException {

  private static final long serialVersionUID = -7730310919268507518L;
  
  public UnsupportedPrincipalChangeException(String message) {
    super(message);
  }
}
