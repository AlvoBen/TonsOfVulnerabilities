/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.httpclient.http.methods.webdav;

import com.sap.httpclient.HttpMethod;
import com.sap.httpclient.uri.EncodingUtil;
import com.sap.httpclient.http.methods.RequestData;
import com.sap.httpclient.http.methods.StringRequestData;
import com.sap.httpclient.http.Header;


/**
 * Implements the HTTP webdav LOCK method.
 *
 * @author Nikolai Neichev
 */
public class LOCK extends PropertyXMLRequest {

  public static String SHARED = "shared";
  public static String EXCLUSIVE = "exclusive";
  public static String TIMEOUT_INFINITY = "Infinite, Second-4100000000";

  private String lockScope = SHARED;
  private String lockOwner = null;
  private boolean refreshLock = true;

  /**
   * No-arg constructor.
   */
  public LOCK() {
  }

  /**
   * Constructor specifying a URI.
   *
   * @param uri either an absolute or relative URI
   */
  public LOCK(String uri) {
    super(uri);
  }

  /**
   * Returns <tt>"LOCK"</tt>.
   *
   * @return <tt>"LOCK"</tt>
   */
  public String getName() {
    return HttpMethod.METHOD_LOCK;
  }

  /**
   * Sets the LOCK request timeout header as specified
   * Use LOCK.TIMEOUT_INFINITY for infinite timeout
   * @param timeout String representing the Timeout header value
   */
  public void setTimeoutHeader(String timeout) {
    setRequestHeader(Header.TIMEOUT, timeout);
  }

  /**
   * Sets the lock timeout as specified
   * @param seconds number representing the Timeout header value in seconds
   */
  public void setTimeout(long seconds) {
    String timeout = "Second-" + seconds;
    setRequestHeader(Header.TIMEOUT, timeout);
  }

  /**
   * Sets the owner of this LOCK request.
   * @param owner  the owner.
   */
  public void setLockOwner(String owner) {
    this.lockOwner = EncodingUtil.encodeToXml(owner);
    refreshLock = false;
  }

  /**
   * Sets the lockscope of this LOCK request.
   * @param scope  Use LOCK.SHARED ot LOCK.EXCLUSIVE
   */
  public void setLockScope(String scope) {
    if ( (!scope.equals(SHARED)) &&
         (!scope.equals(EXCLUSIVE)) ) {
      throw new IllegalArgumentException("Incorrect scope : " + scope);
         }
    this.lockScope = scope;
    refreshLock = false;
  }

  /**
   * Makes this LOCK request a 'refresh lock' request
   */
  public void beRefreshLock() {
    refreshLock = true;
    this.lockOwner = null;
    this.lockScope = SHARED;
  }

  /**
   * Sets the IF header
   * @param ifHeaderValue the header value
   */
  public void setIfHeader(String ifHeaderValue) {
    setRequestHeader(Header.IF, ifHeaderValue);
  }

  /**
   * Prepares the request data.
   *
   * @return A request entity for this PROPFIND request.
   */
	public RequestData generateRequestData() {
    if (refreshLock) { // it's a refresh lock, so there is no request body
      return null;
    }
    if (!isDataSet()) {
      StringBuilder body = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
      body.append("<D:lockinfo xmlns:D=\"DAV:\">");
      body.append("<D:lockscope><D:").append(lockScope).append("/></D:lockscope>");
      body.append("<D:locktype><D:write/></D:locktype>");
      if (lockOwner != null) {
        body.append("<D:owner><D:href>").append(lockOwner).append("</D:href></D:owner>");
      }
      body.append("</D:lockinfo>");
      RequestData data = new StringRequestData(body.toString());
      setRequestData(data);
      return data;
    } else {
      return super.generateRequestData();
    }
	}

//  public static void main(String[] args) {
//    LOCK l = new LOCK();
//    l.setLockOwner("http://nwn.sap.com");
//    l.setLockScope(LOCK.EXCLUSIVE);
//    StringRequestData data = (StringRequestData) l.getRequesData();
//    System.out.println(data.getContent());
//  }

}