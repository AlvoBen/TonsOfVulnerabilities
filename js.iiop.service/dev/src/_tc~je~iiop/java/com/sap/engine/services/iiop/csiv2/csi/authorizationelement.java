package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.portable.IDLEntity;

public final class AuthorizationElement implements IDLEntity {

  public int the_type;
  public byte the_element[];

  public AuthorizationElement() {
    the_type = 0;
    the_element = null;
  }

  public AuthorizationElement(int i, byte abyte0[]) {
    the_type = 0;
    the_element = null;
    the_type = i;
    the_element = abyte0;
  }

}

