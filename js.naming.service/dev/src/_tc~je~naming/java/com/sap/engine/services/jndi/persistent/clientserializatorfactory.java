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
package com.sap.engine.services.jndi.persistent;

import java.io.*;

import com.sap.engine.services.jndi.implclient.ClientContext;

/**
 * Serialization factory
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public class ClientSerializatorFactory extends SerializatorFactory {

  /**
   * Gets new CPO input stream
   *
   * @param bais Input stream to use
   * @param cc Client context to use
   * return The input stream requested
   * @throws IOException Thrown if a problem occurs.
   */
  public CPOInputStream getNewCPOInputStream(ByteArrayInputStream bais, ClientContext cc) throws java.io.IOException {
    return new ClientCPOInputStream(bais, cc);
  }

  ;

  /**
   * Gets new CPO output stream
   *
   * @param baos Output stream to use
   * @param cc Client context to use
   * return The output stream requested
   * @throws IOException Thrown if a problem occurs.
   */
  public CPOOutputStream getNewCPOOutputStream(ByteArrayOutputStream baos, ClientContext cc) throws java.io.IOException {
    return null;
  }

  ;
}

