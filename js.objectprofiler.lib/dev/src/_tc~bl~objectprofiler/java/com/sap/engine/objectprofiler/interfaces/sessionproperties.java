package com.sap.engine.objectprofiler.interfaces;

import java.io.Serializable;

/**
 * Copyright (c) 2001 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 * <p/>
 * User: Pavel Bonev
 * Date: 2005-6-23
 * Time: 11:26:24
 */
public class SessionProperties implements Serializable {
  public static final int TYPE_UNKNOWN = -1;
  public static final int TYPE_SESSION_CONTEXT = 0;
  public static final int TYPE_DOMAIN = 1;
  public static final int TYPE_SESSION = 2;

  public static final String EMPTY_STRING = "J2EE Engine";

  private String id = null;
  private String path = null;
  private String dateTime = null;
  private long timestamp = -1;
  private int type = TYPE_UNKNOWN;

  public SessionProperties(String id, String path, int type) {
    this.id = id;
    this.path = path;
    this.type = type;
  }

  public SessionProperties(String id, String path, int type, long timestamp) {
    this.id = id;
    this.path = path;
    this.type = type;
    this.timestamp = timestamp;
  }


  public long getTimestamp() {
    return timestamp;
  }

  public int getType() {
    return type;
  }

  public String getID() {
    return id;
  }

  public String getPath() {
    return path;
  }

  public String getDateTime() {
    return dateTime;
  }

  public void setDateTime(String dateTime) {
    this.dateTime = dateTime;
  }

  public String toString() {
    return id;
  }

  public static SessionProperties buildRoot() {
    return new SessionProperties(EMPTY_STRING, null, SessionProperties.TYPE_UNKNOWN);
  }
}
