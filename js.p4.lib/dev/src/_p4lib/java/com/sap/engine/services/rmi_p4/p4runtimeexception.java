package com.sap.engine.services.rmi_p4;

import java.text.MessageFormat;

public class P4RuntimeException extends RuntimeException {

  static final long serialVersionUID = 2405844906489680022L;

  public P4RuntimeException() {
    super();
  }

  public P4RuntimeException(String str) {
    super(str);
  }

  public P4RuntimeException(String str, Throwable t) {
    super(str, t);
  }

  public P4RuntimeException(String str, Throwable thr, Object[] args) {
    super(P4RuntimeException.getMessage(str, args), thr);
  }

  public static String getMessage(String msg, Object args[]) {
    String message = msg;
    if (args != null && args.length > 0 && msg != null) {
      MessageFormat mf = new MessageFormat("");
      mf.applyPattern(msg);
      message = mf.format(args);
    }
    return message;
  }

}

