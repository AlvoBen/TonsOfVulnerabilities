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

import com.sap.engine.lib.util.ArrayObject;

public class Cookies {
  private static final int MAX_APP_COOKIES = 4;
  private static final int MAX_COOKIES = 8;
  private ArrayObject cookies = new ArrayObject(4, 2);
  private HttpCookie sessionCookie = new HttpCookie();
  private ArrayObject applicationCookies = new ArrayObject(2, 2);

  public void reset() {
    if (cookies.size() > MAX_COOKIES) {
      cookies = new ArrayObject(4, 2);
    } else {
      cookies.clear();
    }
    if (applicationCookies.size() > MAX_APP_COOKIES) {
      applicationCookies = new ArrayObject(2, 2);
    } else {
      applicationCookies.clear();
    }
    sessionCookie.setName(null);
  }

  public void init(Cookies oldCookies) {
    sessionCookie.setName(oldCookies.sessionCookie.getName());
    sessionCookie.setValue(oldCookies.sessionCookie.getValue());
    sessionCookie.setComment(oldCookies.sessionCookie.getComment());
    sessionCookie.setDomain(oldCookies.sessionCookie.getDomain());
    sessionCookie.setMaxAge(oldCookies.sessionCookie.getMaxAge());
    sessionCookie.setPath(oldCookies.sessionCookie.getPath());
    sessionCookie.setSecure(oldCookies.sessionCookie.getSecure());
    sessionCookie.setVersion(oldCookies.sessionCookie.getVersion());
    sessionCookie.setHttpOnly(oldCookies.sessionCookie.isHttpOnly());
    for (int i = 0; i < oldCookies.cookies.size(); i++) {
       cookies.add(oldCookies.cookies.elementAt(i));
    }
    for (int i = 0; i < oldCookies.applicationCookies.size(); i++) {
       applicationCookies.add(oldCookies.applicationCookies.elementAt(i));
    }
  }

  public HttpCookie getSessionCookie() {
    return sessionCookie;
  }

  public void clearSessionCookie() {
    sessionCookie.setName(null);
  }

  public HttpCookie getCookie(String name) {
    if (name == null) {
      return null;
    }
    for (int i = 0; i < cookies.size(); i++) {
      HttpCookie httpCookie = (HttpCookie)cookies.elementAt(i);
      if (name.equalsIgnoreCase(httpCookie.getName())) {
        return httpCookie;
      }
    }
    return null;
  }

  public ArrayObject getCookies() {
    return cookies;
  }

  public int getCookiesSize() {
    return cookies.size();
  }

  public HttpCookie getCookie(int ind) {
    return (HttpCookie)cookies.elementAt(ind);
  }

  public HttpCookie getApplicationCookie(String name) {
    if (name == null) {
      return null;
    }
    for (int i = 0; i < applicationCookies.size(); i++) {
      HttpCookie httpCookie = (HttpCookie)applicationCookies.elementAt(i);
       if (name.equalsIgnoreCase(httpCookie.getName())) {
         return httpCookie;
       }
    }
    return null;
  }

  public ArrayObject getApplicationCookies() {
    return applicationCookies;
  }

  public int getApplicationCookiesSize() {
    return applicationCookies.size();
  }

  public void clearApplicationCookies() {
    applicationCookies.clear();
  }

  public HttpCookie getApplicationCookie(int ind) {
    return (HttpCookie)applicationCookies.elementAt(ind);
  }
}
