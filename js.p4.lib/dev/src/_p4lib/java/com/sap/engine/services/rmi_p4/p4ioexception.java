package com.sap.engine.services.rmi_p4;

import java.io.IOException;
import java.io.PrintStream;
import java.text.MessageFormat;

public class P4IOException extends IOException  {

  static final long serialVersionUID = -4924696734592217810L;

  private Throwable ex = null;

  public P4IOException(String str, Throwable t) {
    super(str);
    this.ex = t;
  }

  public P4IOException() {
    super();
  }

  public P4IOException(String str) {
    super(str);
  }

  public P4IOException(String str, Throwable thr, Object[] args) {
    super(P4IOException.getMessage(str, args));
    this.ex = thr;
  }

  public String toString() {
    return super.toString();
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

  public void printStackTrace(PrintStream s) {
    if (ex == null) {
      super.printStackTrace(s);
    } else {
      synchronized (s) {
        s.println(this);
        ex.printStackTrace(s);
      }
    }
  }

  public void printStackTrace(java.io.PrintWriter pw) {
    if (ex == null) {
      super.printStackTrace(pw);
    } else {
      synchronized (pw) {
        pw.println(this);
        ex.printStackTrace(pw);
      }
    }

  }

  public void printStackTrace() {
    printStackTrace(System.err);
  }

}

