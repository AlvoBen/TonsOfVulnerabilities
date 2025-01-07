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

import com.sap.engine.services.iiop.system.CommunicationLayer;
import org.omg.CORBA.ORB;

/**
 * This class implements GIOP message header.
 * It is the base class of all GIOP messages available in CORBA 2.2 specification.
 * It also contains most of the constants used in GIOP protocol. This class is used
 * for parsing/creating GIOP message headers and reading/writing GIOP messages.
 *
 * @author Nikolai Neichev
 * @version 4.0
 */
public class NoReplyMessage extends OutgoingMessage {

  public static NoReplyMessage _this;

  static {
    NoReplyMessage._this = new NoReplyMessage(CommunicationLayer.getORB());
  }

  protected NoReplyMessage(ORB orb) {
    super(orb, null);
  }

  public int getRequestId() {
    return -1;
  }

  protected void writeGIOPHeader() {
    //do nothing
  }

  protected void writeMessageHeader() {
    //do nothing
  }

}

