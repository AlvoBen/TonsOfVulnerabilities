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
package com.sap.engine.services.jndi.implclient;

import javax.naming.*;

/**
 * Listener for client events
 *
 * @author Petio Petev, Panayot Dobrikov
 * @version 4.00
 */
public final class ClientNameParser implements NameParser {

  /**
   * Parses a name
   *
   * @param name Name to parse
   * @throws NamingException Thrown when a problem occures.
   */
  public Name parse(String name) throws NamingException {
    return new CompositeName(name);
  }

}

