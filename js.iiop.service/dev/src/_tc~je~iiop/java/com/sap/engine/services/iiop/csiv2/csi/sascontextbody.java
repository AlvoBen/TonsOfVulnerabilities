package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.portable.IDLEntity;

public final class SASContextBody implements IDLEntity {

  public SASContextBody() {
    __uninitialized = true;
  }

  public short discriminator() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      return __discriminator;
    }
  }

  public EstablishContext establish_msg() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifyestablish_msg(__discriminator);
      return ___establish_msg;
    }
  }

  public void establish_msg(EstablishContext establishcontext) {
    __discriminator = 0;
    ___establish_msg = establishcontext;
    __uninitialized = false;
  }

  private void verifyestablish_msg(short word0) {
    if (word0 != 0) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  public CompleteEstablishContext complete_msg() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifycomplete_msg(__discriminator);
      return ___complete_msg;
    }
  }

  public void complete_msg(CompleteEstablishContext completeestablishcontext) {
    __discriminator = 1;
    ___complete_msg = completeestablishcontext;
    __uninitialized = false;
  }

  private void verifycomplete_msg(short word0) {
    if (word0 != 1) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  public ContextError error_msg() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifyerror_msg(__discriminator);
      return ___error_msg;
    }
  }

  public void error_msg(ContextError contexterror) {
    __discriminator = 4;
    ___error_msg = contexterror;
    __uninitialized = false;
  }

  private void verifyerror_msg(short word0) {
    if (word0 != 4) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  public MessageInContext in_context_msg() {
    if (__uninitialized) {
      throw new BAD_OPERATION();
    } else {
      verifyin_context_msg(__discriminator);
      return ___in_context_msg;
    }
  }

  public void in_context_msg(MessageInContext messageincontext) {
    __discriminator = 5;
    ___in_context_msg = messageincontext;
    __uninitialized = false;
  }

  private void verifyin_context_msg(short word0) {
    if (word0 != 5) {
      throw new BAD_OPERATION();
    } else {
      return;
    }
  }

  public void _default() {
    __discriminator = -32768;
    __uninitialized = false;
  }

  public void _default(short word0) {
    verifyDefault(word0);
    __discriminator = word0;
    __uninitialized = false;
  }

  private void verifyDefault(short word0) {
    switch (word0) {
      case 0: // '\0'
      case 1: // '\001'
      case 4: // '\004'
      case 5: // '\005'
      {
        throw new BAD_OPERATION();
      }
      case 2: // '\002'
      case 3: // '\003'
      default: {
        return;
      }
    }
  }

  private EstablishContext ___establish_msg;
  private CompleteEstablishContext ___complete_msg;
  private ContextError ___error_msg;
  private MessageInContext ___in_context_msg;
  private short __discriminator;
  private boolean __uninitialized;

}

