/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
 
 package com.sap.engine.services.rmi_p4.exception;

public interface P4ExceptionConstants {

  public static final int P4_IOException = 0;

  public static final int P4_RuntimeException = 1;

  public static final int P4_ConnectionException = 2;

  public static final int Initialize_Exception = 3;

  public static final int P4_MarshalException = 4;

  public static final int P4_ParseRequestException = 5;

  public static final int SEVERITY_All = 0;//Severity.ALL
  public static final int SEVERITY_DEBUG = 100;//Severity.DEBUG
  public static final int SEVERITY_PATH = 200;//Severity.PATH
  public static final int SEVERITY_INFO = 300;//Severity.INFO
  public static final int SEVERITY_WARNING = 400;//Severity.WARNING
  public static final int SEVERITY_ERROR = 500;//Severity.ERROR
  public static final int SEVERITY_FATAL  = 600;//Severity.FATAL
  public static final int SEVERITY_GRUP = 800;//Severity.GROUP
  public static final int SEVERITY_MAX = 700;//Severity.MAX
}
