﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Tue Jul 19 13:29:42 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.applicationj2eeengine;

/**
 * Schema complexType Java representation.
 * Represents type {}start-upType
 */
public  class StartUpType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Attribute field for attribute {}mode
  private com.sap.engine.lib.descriptors.applicationj2eeengine.Mode _a_Mode;
  /**
   * Set method for attribute {}mode
   */
  public void setMode(com.sap.engine.lib.descriptors.applicationj2eeengine.Mode _Mode) {
    this._a_Mode = _Mode;
  }
  /**
   * Get method for attribute {}mode
   */
  public com.sap.engine.lib.descriptors.applicationj2eeengine.Mode getMode() {
    return _a_Mode;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof StartUpType)) return false;
    StartUpType typed = (StartUpType) object;
    if (this._a_Mode != null) {
      if (typed._a_Mode == null) return false;
      if (!this._a_Mode.equals(typed._a_Mode)) return false;
    } else {
      if (typed._a_Mode != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_Mode != null) {
      result+= this._a_Mode.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
