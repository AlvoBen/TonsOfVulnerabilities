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
package com.sap.engine.services.httpserver.interfaces.client;

import java.security.cert.X509Certificate;

public interface SslAttributes {
  public X509Certificate[] getCertificates();

  public String getCipherSuite();

  public int getKeySize();
}
