package com.sap.engine.services.iiop.csiv2.GSSUP;

import org.omg.CORBA.portable.IDLEntity;

public final class InitialContextToken implements IDLEntity {

  public InitialContextToken() {
    username = null;
    password = null;
    target_name = null;
  }

  public InitialContextToken(byte abyte0[], byte abyte1[], byte abyte2[]) {
    username = null;
    password = null;
    target_name = null;
    username = abyte0;
    password = abyte1;
    target_name = abyte2;
  }

  public byte username[];
  public byte password[];
  public byte target_name[];

}

