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

import com.sap.engine.services.jndi.persistent.CPOInputStream;
import com.sap.engine.services.jndi.persistent.CPOOutputStream;
import com.sap.engine.services.jndi.implclient.ClientContext;

/**
 * Serializator factory
 *
 * @author Panayot Dobrikov
 * @version 4.00
 */
public abstract class SerializatorFactory {

  /**
   * Gets new CPO input stream
   *
   * @param bais Byte array input stream
   * @param cc Client context to use
   * @return CPOInputStream requested.
   * @throws IOException Thrown if a problem occurs.
   */
  public abstract CPOInputStream getNewCPOInputStream(ByteArrayInputStream bais, ClientContext cc) throws java.io.IOException;

  /**
   * Gets new CPO output stream
   *
   * @param baos Byte array output stream
   * @param cc Client context to use
   * @return CPOOutputStream requested.
   * @throws IOException Thrown if a problem occurs.
   */
  public abstract CPOOutputStream getNewCPOOutputStream(ByteArrayOutputStream baos, ClientContext cc) throws java.io.IOException;

}

