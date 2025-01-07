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
package com.sap.engine.services.jndi;

/**
 * Simple implementation of javax.naming.spi.ObjectFactory
 *
 * @author Nikolay Tankov, Petio Petev
 * @version 4.0.0
 */

import java.util.Hashtable;
import javax.naming.*;
import javax.naming.spi.*;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class AppclientObjectFactory implements ObjectFactory {

  private final static Location LOG_LOCATION = Location.getLocation(AppclientObjectFactory.class);

  /**
   * get Initialcontext and lookup  real Object
   *
   * @param obj Reference
   * @param name Name
   * @param context not used
   * @param hash hashtable with properties
   * @return real Object
   * @throws Exception if exception occured during lookup
   */
  public Object getObjectInstance(Object obj, Name name, Context context, Hashtable hash) throws Exception {
    hash.put("appclient", "true");
    hash.remove("server");
    hash.remove("domain");
    Object user = hash.remove(Context.SECURITY_PRINCIPAL);
    Object pass = hash.remove(Context.SECURITY_CREDENTIALS);
    InitialContext ctx = new InitialContext(hash);

    if (user != null) {
      hash.put(Context.SECURITY_PRINCIPAL, user);
    }

    if (pass != null) {
      hash.put(Context.SECURITY_CREDENTIALS, pass);
    }

    try {
      return ctx.lookup(((javax.naming.Reference) obj).getClassName());
    } catch (ClassCastException cce) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", cce);
      return ctx.lookup(name);
    }
  }

}

