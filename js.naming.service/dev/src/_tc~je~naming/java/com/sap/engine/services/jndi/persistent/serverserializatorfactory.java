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

public class ServerSerializatorFactory extends SerializatorFactory {

  /**
   * Gets a new CPO input stream
   *
   * @param bais Byte array input stream to use
   * @param cc Client context to work with
   * @return CPOInputStream requested
   * @throws IOException Thrown if a problem occures.
   */
  public CPOInputStream getNewCPOInputStream(ByteArrayInputStream bais, ClientContext cc) throws java.io.IOException {
    return new ServerCPOInputStream(bais, cc);
  }

  ;

  /**
   * Gets a new CPO output stream
   *
   * @param baos Byte array output stream to use
   * @param cc Client context to work with
   * @return CPOOutputStream requested
   * @throws IOException Thrown if a problem occures.
   */
  public CPOOutputStream getNewCPOOutputStream(ByteArrayOutputStream baos, ClientContext cc) throws java.io.IOException {
    return null;
  }

  ;
}

