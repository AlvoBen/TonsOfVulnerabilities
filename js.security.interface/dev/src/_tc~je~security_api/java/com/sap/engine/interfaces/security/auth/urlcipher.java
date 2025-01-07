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
 * Created on Feb 12, 2008 by i045045
 *   
 */
 
package com.sap.engine.interfaces.security.auth;
/**
 * This class is for internal use only!
 * @author i045045
 */
public abstract class URLCipher {
  
  private static URLCipher cipher;

  public static URLCipher getInstance() {
    return cipher;
  }
  
  public static void setInstance(URLCipher cipherImpl) {
    if (cipher == null) {
      cipher = cipherImpl;
    }
  }
  
  protected abstract String encryptURL(String url);
  
  protected abstract String decryptURL(String url);
}
