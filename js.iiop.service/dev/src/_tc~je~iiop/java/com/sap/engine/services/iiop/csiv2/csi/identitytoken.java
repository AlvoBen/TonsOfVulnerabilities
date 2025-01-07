package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.portable.IDLEntity;

public final class IdentityToken implements IDLEntity {

  public IdentityToken() {
    __uninitialized = true;
  }

  public int discriminator() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      return __discriminator;
    }
  }

  public boolean absent() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifyabsent(__discriminator);
      return ___absent;
    }
  }

  public void absent(boolean flag) {
    __discriminator = 0;
    ___absent = flag;
    __uninitialized = false;
  }

  private void verifyabsent(int i) {
    if (i != 0) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  public boolean anonymous() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifyanonymous(__discriminator);
      return ___anonymous;
    }
  }

  public void anonymous(boolean flag) {
    __discriminator = 1;
    ___anonymous = flag;
    __uninitialized = false;
  }

  private void verifyanonymous(int i) {
    if (i != 1) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  public byte[] principal_name() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifyprincipal_name(__discriminator);
      return ___principal_name;
    }
  }

  public void principal_name(byte abyte0[]) {
    __discriminator = 2;
    ___principal_name = abyte0;
    __uninitialized = false;
  }

  private void verifyprincipal_name(int i) {
    if (i != 2) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  public byte[] certificate_chain() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifycertificate_chain(__discriminator);
      return ___certificate_chain;
    }
  }

  public void certificate_chain(byte abyte0[]) {
    __discriminator = 4;
    ___certificate_chain = abyte0;
    __uninitialized = false;
  }

  private void verifycertificate_chain(int i) {
    if (i != 4) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  public byte[] dn() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifydn(__discriminator);
      return ___dn;
    }
  }

  public void dn(byte abyte0[]) {
    __discriminator = 8;
    ___dn = abyte0;
    __uninitialized = false;
  }

  private void verifydn(int i) {
    if (i != 8) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  public byte[] id() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifyid(__discriminator);
      return ___id;
    }
  }

  public void id(byte abyte0[]) {
    __discriminator = 0;
    ___id = abyte0;
    __uninitialized = false;
  }

  private void verifyid(int i) {
    if (i == 0 || i == 1 || i == 2 || i == 4 || i == 8) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  private boolean ___absent;
  private boolean ___anonymous;
  private byte ___principal_name[];
  private byte ___certificate_chain[];
  private byte ___dn[];
  private byte ___id[];
  private int __discriminator;
  private boolean __uninitialized;

}

