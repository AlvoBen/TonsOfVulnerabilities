﻿package com.sap.engine.services.httpserver.interfaces.properties;

/*
 * Copyright (c) 2008 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */

public interface ProxyServersProperties {

  public byte[] getClientCertificateHeaderName();

  public String getClientCertificateChainHeaderPrefix();

  public byte[] getClientKeySizeHeaderName();

  public byte[] getClientCipherSuiteHeaderName();

  public String getProtocolHeaderName();
  
  public void loadICMProperties();

}
