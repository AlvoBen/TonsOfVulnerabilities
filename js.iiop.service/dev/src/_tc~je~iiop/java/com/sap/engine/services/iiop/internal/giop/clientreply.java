/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidential and proprietary information
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.internal.giop;

import org.omg.IOP.ServiceContext;
import com.sap.engine.services.iiop.CORBA.CodeSetChooser;

/**
 * This is the base class for all client reply messages.
 *
 * @author Georgy Stanev
 * @version 4.0
 */
public abstract class ClientReply extends OutgoingMessage {

  protected boolean exception;
  public ServiceContext[] replyContexts;

  protected ClientReply(org.omg.CORBA.ORB orb, CodeSetChooser codeSetChooser) {
    super(orb, codeSetChooser);
  }

  /**
   * Writes exception
   *
   * @param   type  Specifies USER or SYSTEM type exception
   */
  protected abstract void writeExceptionMessageHeader(byte type);

}

