/*
 * Copyright (c) 2000 by InQMy Software AG.,
 * url: http://www.inqmy.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of InQMy Software AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with InQMy.
 */
package com.sap.engine.services.jndi;

/**
 * Simple implementation of javax.naming.spi.ObjectFactory
 *
 * @author Nikolay Tankov
 * @version 4.0.0
 */

import javax.naming.Context;
import java.util.Hashtable;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.naming.InitialContext;

import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;

public class ComponentObjectFactory implements ObjectFactory {

  private final static Location LOG_LOCATION = Location.getLocation(ComponentObjectFactory.class);

  /**
   * get Initialcontext and lookup  real Object
   *
   * @param obj Reference
   * @param name Name
   * @param context not used
   * @param hashtable hashtable with properties
   * @return real Object
   * @throws Exception if exception occured during lookup
   */
  public Object getObjectInstance(Object obj, Name name, Context context, Hashtable hashtable) throws Exception {
    hashtable.put("domain", "true");
    InitialContext ctx = new InitialContext(hashtable);
    try {
      return ctx.lookup(((javax.naming.Reference) obj).getClassName());
    } catch (ClassCastException cce) {
      LOG_LOCATION.traceThrowableT(Severity.PATH, "", cce);
      return ctx.lookup(name);
    }
  }

}//class                                                                                                                        

