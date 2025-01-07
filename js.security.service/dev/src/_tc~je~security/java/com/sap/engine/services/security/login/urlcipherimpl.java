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
 
package com.sap.engine.services.security.login;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import com.sap.engine.interfaces.security.auth.URLCipher;
import com.sap.engine.services.security.Util;
import com.sap.security.core.util.Base64;

public class URLCipherImpl extends URLCipher {

  public String encryptURL(String url) {
    String encryptedURL = Base64.encode(Util.encrypt(url));

    try {
      encryptedURL = URLEncoder.encode(encryptedURL, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    
    return encryptedURL;
  }

  public String decryptURL(String url) {
    try {
      url = URLDecoder.decode(url, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    
    byte[] decryptedURL  =  Util.decrypt(Base64.decode(url));
    
    try {
      return new String(decryptedURL, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    
    return url;
  }
}
