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

import javax.naming.spi.InitialContextFactory;
import javax.naming.Context;
import javax.naming.NamingException;
import java.util.Hashtable;

public class InitialReplicatingContextFactoryImpl implements InitialContextFactory {

  public Context getInitialContext(Hashtable env) throws NamingException {
    env.put("Replicate", "true");
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
    Context initialContext = (new InitialContextFactoryImpl()).getInitialContext(env);
    return initialContext;
  }

}

