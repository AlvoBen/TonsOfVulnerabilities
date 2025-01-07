﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 12 11:18:44 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ormapping;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/persistence/orm}map-key
 */
public  class MapKey implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Attribute field for attribute {}name
  private java.lang.String _a_Name;
  /**
   * Set method for attribute {}name
   */
  public void setName(java.lang.String _Name) {
    this._a_Name = _Name;
  }
  /**
   * Get method for attribute {}name
   */
  public java.lang.String getName() {
    return _a_Name;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof MapKey)) return false;
    MapKey typed = (MapKey) object;
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
