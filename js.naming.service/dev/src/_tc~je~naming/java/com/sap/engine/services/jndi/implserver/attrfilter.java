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
package com.sap.engine.services.jndi.implserver;

import javax.naming.*;
import javax.naming.directory.*;

/**
 *  Interface for checking if an Attributes object is suitable with a filter
 *
 * @author Petio Petev, Panayot Dobrikov
 * @version 4.00
 */
public interface AttrFilter {

  /**
   *  Check attributes
   *
   */
  public abstract boolean check(Attributes attributes) throws NamingException;

}

