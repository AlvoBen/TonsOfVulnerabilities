package com.sap.engine.lib.xml.parser.helpers;

public abstract class Encoding {

  public final static int NEEDS_MORE_DATA = -2;
  public final static int UNSUPPORTED_CHAR = -1;

  public abstract int process(byte ch);

  public abstract int reverseEncode(byte result[], int ch);

  public abstract CharArray getName();

  public String toString() {
    return getName().toString();
  }

}

