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
package com.sap.engine.services.httpserver.lib;

import javax.servlet.http.Cookie;

import com.sap.engine.services.httpserver.server.ServiceContext;

public class HttpCookie {
  private String name = null;
  private String value = null;
  private int maxAge = -1;
  private int version  = 0;
  private String domain = null;
  private String path = null;
  private String comment = null;
  private boolean secure = false;
  private boolean httpOnly = true;

  public HttpCookie() {
  }

  public HttpCookie(String name, String value) {
  	ParseUtils.errorOnCRLF(value);
  	ParseUtils.errorOnCRLF(name);
    this.name = name;
    this.value = value;  
    if (ServiceContext.getServiceContext().getHttpProperties().getSystemCookiesHTTPSProtection()) {
      secure = true;
    } else {
      secure = false;
    }
    if (ServiceContext.getServiceContext().getHttpProperties().getSystemCookiesDataProtection()) {
      httpOnly = true;
    } else {
      httpOnly = false;
    }    
  }

  public void setName(String name) {
  	ParseUtils.errorOnCRLF(name);
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setValue(String value) {
  	ParseUtils.errorOnCRLF(value);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setMaxAge(int maxAge) {
    this.maxAge = maxAge;
  }

  public int getMaxAge() {
    return maxAge;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getVersion() {
    return version;
  }

  public void setDomain(String domain) {
  	ParseUtils.errorOnCRLF(domain);
    this.domain = domain;
  }

  public String getDomain() {
    return domain;
  }

  public void setPath(String path) {
  	ParseUtils.errorOnCRLF(path);
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public void setComment(String comment) {
  	ParseUtils.errorOnCRLF(comment);
    this.comment = comment;
  }

  public String getComment() {
    return comment;
  }

  public void setSecure(boolean secure) {
    this.secure = secure;
  }

  public boolean getSecure() {
    return secure;
  }

  /**
   * @return Returns the httpOnly
   */
  public boolean isHttpOnly() {
    return httpOnly;
  }
  
  /**
   * @param httpOnly The httpOnly to set.
   */
  public void setHttpOnly(boolean httpOnly) {
    this.httpOnly = httpOnly;
  }  
  
  public Cookie toCookie() {
    Cookie result = new Cookie(name, value);
    result.setMaxAge(maxAge);
    result.setVersion(version);
    if (domain != null) {
      result.setDomain(domain);
    }
    if (path != null) {
      result.setPath(path);
    }
    if (comment != null) {
      result.setComment(comment);
    }    
    return result;
  }
}
