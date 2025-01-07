package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.portable.IDLEntity;

public final class TLS_SEC_TRANS implements IDLEntity {

  public TLS_SEC_TRANS() {
    target_supports = 0;
    target_requires = 0;
    addresses = null;
  }

  public TLS_SEC_TRANS(short word0, short word1, TransportAddress atransportaddress[]) {
    target_supports = 0;
    target_requires = 0;
    addresses = null;
    target_supports = word0;
    target_requires = word1;
    addresses = atransportaddress;
  }

  public short target_supports;
  public short target_requires;
  public TransportAddress addresses[];

}

