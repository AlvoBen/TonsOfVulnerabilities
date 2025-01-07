package com.sap.engine.lib.xsl.xslt;

public final class IntHolder {

  private int akey = -1;

  public IntHolder reuse(int i) {
    akey = i;
    return this;
  }

  public void set(int i) {
    akey = i;
  }

  public int hashCode() {
    return akey;
  }

  public boolean equals(Object o) {
    if (o instanceof IntHolder && ((IntHolder) o).akey == akey) {
      return true;
    } else {
      return false;
    }
  }

  public int key() {
    return akey;
  }

}

