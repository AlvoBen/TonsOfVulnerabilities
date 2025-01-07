﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:07:41 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.persistent;

/**
 * Schema complexType Java representation.
 * Represents type {}TargetSQLType
 */
public  class TargetSQLType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Attribute field for attribute {}name
  private com.sap.engine.lib.descriptors.persistent.NameAttribute _a_Name;
  /**
   * Set method for attribute {}name
   */
  public void setName(com.sap.engine.lib.descriptors.persistent.NameAttribute _Name) {
    this._a_Name = _Name;
  }
  /**
   * Get method for attribute {}name
   */
  public com.sap.engine.lib.descriptors.persistent.NameAttribute getName() {
    return _a_Name;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof TargetSQLType)) return false;
    TargetSQLType typed = (TargetSQLType) object;
    if (this._a_Name != null) {
      if (typed._a_Name == null) return false;
      if (!this._a_Name.equals(typed._a_Name)) return false;
    } else {
      if (typed._a_Name != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_Name != null) {
      result+= this._a_Name.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
