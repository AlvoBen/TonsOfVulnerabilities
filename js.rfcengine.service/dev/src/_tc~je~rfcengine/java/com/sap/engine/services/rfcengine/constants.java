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
package com.sap.engine.services.rfcengine;

public class Constants {

  /**
   * Turns ON or OFF the debug dumps
   */
  public final static boolean DEBUG = false;
  /**
   * Used for cluster messaging in the runtime interface
   */
  public static final byte CHANGE_TO_LOCAL = 1;
  public static final byte CHANGE_TO_GLOBAL = 2;
  public static final byte CHANGE_BUNDLE = 3;
  public static final byte REMOVE_BUNDLE = 4;
  /**
   * Constants for accesing configuration field
   */
  public static final int LOGON_CLIENT = 0;
  public static final int LOGON_USER = 1;
  public static final int LOGON_PASSWORD = 2;
  public static final int LOGON_LANGUAGE = 3;
  public static final int APPLICATION_SERVER_HOST = 4;
  public static final int SYSTEM_NUMBER = 5;
  public static final int GATEWAY_HOST = 6;
  public static final int GATEWAY_SERVER = 7;
  public static final int PROGRAM_ID = 8;

  public static final int AUTHENTICATION_ONLY = 1;
  public static final int INTEGRITY_PROTECTION = 2;
  public static final int PRIVACY_PROTECTION = 3;
  public static final int DEFAULT_PROTECTION = 8;
  public static final int MAXIMUM_PROTECTION = 9;
  
  public static final int Repository_Destination = 9;

}

