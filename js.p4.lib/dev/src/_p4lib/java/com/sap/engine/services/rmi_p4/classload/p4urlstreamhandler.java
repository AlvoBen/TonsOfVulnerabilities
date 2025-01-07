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
import java.net.URLStreamHandler;
import java.util.Hashtable;

/**
 * @author Mladen Droshev
 * @version 7.0
 */

public class P4URLStreamHandler extends URLStreamHandler {

  public P4URLStreamHandler() {
  }

  public Hashtable hashweak = null;

  public void setHashWeak(Hashtable hash) {
    this.hashweak = hash;
  }

  protected URLConnection openConnection(URL u) throws IOException {
    P4URLConnection con = new P4URLConnection(u);
    con.setInputStream((InputStream) this.hashweak.get(u));
    return con;
  }

}
