/*
 * Copyright (c) 2004 by SAP AG, Walldorf., http://www.sap.com All rights
 * reserved.
 * 
 * This software is the confidential and proprietary information of SAP AG,
 * Walldorf. You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you entered
 * into with SAP.
 */
package com.sap.engine.lib.converter.impl;

import com.sap.engine.lib.converter.AbstractConverter;

/**
 * J2EE 1.4 converter for appclient descriptors.
 * 
 * No conversion (yet).
 * 
 * @author d037913
 */
public class AppClientConverter extends AbstractConverter {

  /*
   * (non-Javadoc)
   * 
   * @see com.sap.appchecker.lib.descriptors.IJ2EEDescriptorConverter#getType()
   */
  public int getType() {
    return APPCLIENT;
  }

}