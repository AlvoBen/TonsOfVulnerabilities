package com.sap.engine.services.rmi_p4;

import com.sap.engine.frame.core.thread.ContextObject;
import com.sap.engine.frame.core.thread.Transferable;

public class P4ContextObject implements ContextObject, Transferable {

  public final static String NAME = "P4";
  private byte[] data = null;
  private P4ContextObject parent = null;
  private P4ContextObject init = null;

  public P4ContextObject() {
    //init = this;
  }

  public P4ContextObject(P4ContextObject parent) {
    this.parent = parent;
  }

  public ContextObject childValue(ContextObject parent, ContextObject child) {
    if (child == null) {
      child = new P4ContextObject((P4ContextObject) parent);
    } else {
      ((P4ContextObject) child).parent = (P4ContextObject) parent;
    }

    return child;
  }

  public ContextObject getInitialValue() {
    if (init != null) {
      return new P4ContextObject(init);
    } else {
      return new P4ContextObject();
    }
  }

  public void empty() {
    data = null;
    init = null;
    parent = null;
  }

  public int size() {
    if (init != null) {
      return init.data.length;
    } else {
      return 16;
    }
  }

  public void store(byte[] to, int offset) {
    if (init != null) {
      System.arraycopy(init.data, 0, to, offset, init.data.length);
    } else {
      if (data != null) {
        System.arraycopy(data, 0, to, offset, data.length);
      } else {
        data = new byte[16];
        System.arraycopy(data, 0, to, offset, data.length);
      }
    }
  }

  public void load(byte[] from, int offset) {
    if (java.util.Arrays.equals(data, from)) {
      return;
    }
    if (data != null) {
      System.arraycopy(from, offset, data, 0, size());
    } else {
      data = new byte[16];
      System.arraycopy(from, offset, data, 0, size());
    }
    parent = null;
  }

  public String toString() {
    if (data != null) {
      return "P4 Context : " + new String(data);
    } else {
      return "P4 Context : null";
    }
  }
}