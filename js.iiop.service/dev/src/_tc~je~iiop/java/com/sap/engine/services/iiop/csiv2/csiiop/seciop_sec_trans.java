package com.sap.engine.services.iiop.csiv2.CSIIOP;

import org.omg.CORBA.portable.IDLEntity;

public final class SECIOP_SEC_TRANS implements IDLEntity {

  public SECIOP_SEC_TRANS() {
    target_supports = 0;
    target_requires = 0;
    mech_oid = null;
    target_name = null;
    addresses = null;
  }

  public SECIOP_SEC_TRANS(short word0, short word1, byte abyte0[], byte abyte1[], TransportAddress atransportaddress[]) {
    target_supports = 0;
    target_requires = 0;
    mech_oid = null;
    target_name = null;
    addresses = null;
    target_supports = word0;
    target_requires = word1;
    mech_oid = abyte0;
    target_name = abyte1;
    addresses = atransportaddress;
  }

  public short target_supports;
  public short target_requires;
  public byte mech_oid[];
  public byte target_name[];
  public TransportAddress addresses[];

}

