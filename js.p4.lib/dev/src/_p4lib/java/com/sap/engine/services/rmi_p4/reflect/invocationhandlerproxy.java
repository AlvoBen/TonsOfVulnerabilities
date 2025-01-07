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
package com.sap.engine.services.rmi_p4.reflect;

import com.sap.engine.services.rmi_p4.*;
import com.sap.engine.services.rmi_p4.exception.P4Logger;
import java.io.Serializable;
import java.io.ObjectStreamException;
import java.util.StringTokenizer;

/**
 *
 * @author Ivan Atanassov
 */
public class InvocationHandlerProxy implements Serializable {
  StubBaseInfo info;
  public static final long serialVersionUID = -1381123926645650527L;
  private static P4ObjectBroker broker;
  private String incomingProfile = null;

  static {
    broker = P4ObjectBroker.init();
  }

  public void setRemoteInfo(StubBaseInfo remoteInfo) {
    this.info = remoteInfo;
    this.incomingProfile = remoteInfo.getIncomingProfile();
  }

   public void setLocalInfo(RemoteObjectInfo remoteInfo) {
     this.info = StubBaseInfo.makeStubBaseInfo(remoteInfo);
  }
  private Object readResolve() throws ObjectStreamException {
    if(broker.isServerBroker() && broker.localStubsAllowed() && (info.ownerId == P4ObjectBroker.init().brokerId) && (info.server_id == broker.id)) {
      LocalInvocationHandler liHandler = new LocalInvocationHandler(info);
      return liHandler;
    } else {
      StubBaseInfo _info = (StubBaseInfo) info;
      //this proflie may not be valid when the object is deserialized
      _info.setIncomingProfile(incomingProfile);
      StubImpl base = new StubBaseImpl();
      base.p4_setInfo(_info);
      String type = "None";
      if (incomingProfile != null) {
        StringTokenizer tokenizer = new StringTokenizer(incomingProfile,":");
        tokenizer.nextToken();
        type = tokenizer.nextToken();
      }

      try {
        base.p4_setConnection(P4ObjectBroker.init().getConnection(type, base.p4_getInfo().connectionProfiles, base.p4_getInfo()));
      } catch (P4IOException io) {
        if (P4Logger.getLocation().beDebug()) {
          P4Logger.getLocation().debugT(P4Logger.exceptionTrace(io));
        }
        return null;
      }
      P4InvocationHandler p4Handler = new P4InvocationHandler();
      p4Handler.setInfo(base);
      return p4Handler;
    }
  }

}
