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
package com.sap.engine.services.httpserver;

public interface CommunicationConstants {
  //request
  public static final int REQUEST_TYPE_BODY_PART = 0;
  public static final int REQUEST_TYPE_NEW_REQUEST_NO_BODY = 1;
  public static final int REQUEST_TYPE_NEW_REQUEST = 2;
  public static final int REQUEST_TYPE_CONNECTION_CLOSED = 3;

  public static final byte[] REQUEST_TYPE_BODY_PART_ = new byte[] {REQUEST_TYPE_BODY_PART};
  public static final byte[] REQUEST_TYPE_NEW_REQUEST_NO_BODY_ = new byte[] {REQUEST_TYPE_NEW_REQUEST_NO_BODY};
  public static final byte[] REQUEST_TYPE_NEW_REQUEST_ = new byte[] {REQUEST_TYPE_NEW_REQUEST};
  public static final byte[] REQUEST_TYPE_CONNECTION_CLOSED_ = new byte[] {REQUEST_TYPE_CONNECTION_CLOSED};

  public static final byte[] REQUEST_NO_SSL_ = new byte[]{0};
  public static final byte[] REQUEST_SSL_ = new byte[]{1};

  //response
  public static final byte RESPONSE_FLAG_NOOP = 0;
  public static final byte RESPONSE_FLAG_CLOSE_CONNECTION = 1;
  public static final byte RESPONSE_FLAG_KEEP_ALIVE = 2;
  public static final byte RESPONSE_FLAG_READ_BODY = 16;

  //sendInQueue
  public static final int READ = 1;
  public static final int STOP_READ = 2;

  //internal communication
  public static final int MESSAGE_ETAG_REMOVE = 1;
  public static final int MESSAGE_APP_STARTED = 3;
  public static final int MESSAGE_APP_STOPPED = 4;
  public static final int MESSAGE_CLEAR_ALL = 5;
  public static final int MESSAGE_ACCEPT_CLIENT_CERT = 6;
  public static final int MESSAGE_URL_SESSION_TRACKING = 7;
  public static final int MESSAGE_SERVER_NODE_INITIALIZED = 8;
	public static final int MESSAGE_GROUP_SEPARATOR = 9;
  public static final int MESSAGE_PORT = 512;
  public static final int MESSAGE_REMOVE_SESSION = 523;
}
