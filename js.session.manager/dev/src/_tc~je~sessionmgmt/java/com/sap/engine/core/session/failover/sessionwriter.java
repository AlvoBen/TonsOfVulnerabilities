/*
 * Copyright (c) 2003 by SAP Labs Bulgaria,
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP Labs Bulgaria. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.core.session.failover;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Author: georgi-s
 * Date: 2005-6-1
 */
public class SessionWriter extends ObjectOutputStream {

  public SessionWriter(OutputStream out) throws IOException {
    super(out);
  }

  protected void annotateClass(Class cl) throws java.io.IOException {
    ClassLoader loader = cl.getClassLoader();
    String loaderName = null;
    if (loader != null) {
      loaderName = DefFailoverConfiguration.loadContext.getName(loader);
    }
    if (loaderName != null) {
      writeObject(loaderName);
    } else {
      writeObject("NoName");
    }
  }

    protected void annotateProxyClass(Class cl) throws java.io.IOException {
    ClassLoader loader = cl.getClassLoader();
    String loaderName = null;

    if (loader != null) {
      loaderName = DefFailoverConfiguration.loadContext.getName(loader);
    }
    if (loaderName != null) {
      writeObject(loaderName);
    } else {
      writeObject("NoName");
    }
  }
}
