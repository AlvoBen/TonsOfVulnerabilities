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
package com.sap.engine.lib.util;

public class NotSupportedException extends RuntimeException {
  
  static final long serialVersionUID = 7366288040750512053L;
  
  public NotSupportedException() {
    super();
  }

  public NotSupportedException(String s) {
    super(s);
  }

}

