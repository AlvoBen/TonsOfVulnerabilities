/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * This software is the confidentonlyial and proprietary information
 * Information and shall use it  in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.CORBA;

import org.omg.CosTSPortability.*;
import org.omg.CORBA.TSIdentificationPackage.*;

public class TSIdentificationImpl implements org.omg.CORBA.TSIdentification {

  private Sender senderOTS;
  private Receiver receiverOTS;

  /** identify_sender is called by the OTS during initialization
   to register its Sender callback interface with the ORB.
   identify_sender may throw a AlreadyIdentified exception if
   the registration has already been done previously.
   */
  public void identify_sender(Sender senderOTS) throws NotAvailable, AlreadyIdentified {
    if (this.senderOTS != null) {
      throw new AlreadyIdentified();
    }

    this.senderOTS = senderOTS;
  }

  /** identify_receiver is called by the OTS during initialization
   to register its Receiver callback interface with the ORB.
   identify_receiver may throw a AlreadyIdentified exception if
   the registration has already been done previously.
   */
  public void identify_receiver(Receiver receiverOTS) throws NotAvailable, AlreadyIdentified {
    if (this.receiverOTS != null) {
      throw new AlreadyIdentified();
    }

    this.receiverOTS = receiverOTS;
  }

  public Sender getSender() {
    return senderOTS;
  }

  public Receiver getReceiver() {
    return receiverOTS;
  }

}

