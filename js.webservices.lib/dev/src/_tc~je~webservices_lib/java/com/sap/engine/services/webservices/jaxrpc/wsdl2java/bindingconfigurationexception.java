﻿/*
 * Copyright (c) 2002 by SAP Labs Sofia AG.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Sofia AG.
 */
package com.sap.engine.services.webservices.jaxrpc.wsdl2java;

/**
 * This exception is raised when binding is not configured properly.
 *
 * @author Chavdar Baikov (chavdar.baikov@sap.com)
 * @version 6.30
 */
public class BindingConfigurationException extends Exception {
    
  public BindingConfigurationException() {
    super();
  }
  
  public BindingConfigurationException(String message) {
    super(message);
  }
}
