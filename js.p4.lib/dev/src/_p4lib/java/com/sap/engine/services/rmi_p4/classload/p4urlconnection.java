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
package com.sap.engine.services.rmi_p4.classload;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author Mladen Droshev
 * @version 7.0
 */


public class P4URLConnection extends URLConnection {

  public P4URLConnection(URL url) {
    super(url);
  }

  public InputStream getInputStream() throws IOException {
    return this.in;
  }

  private InputStream in = null;

  public void setInputStream(InputStream in) {
    this.in = in;
  }

  public void connect() throws IOException {
  }

}
