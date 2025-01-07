﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:07:06 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ejb;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}method-intfType
 */
public  class MethodIntfType extends com.sap.engine.lib.descriptors.j2ee.String implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Attribute field for attribute {}id
  private java.lang.String _a_Id;
  /**
   * Set method for attribute {}id
   */
  public void setId(java.lang.String _Id) {
    this._a_Id = _Id;
  }
  /**
   * Get method for attribute {}id
   */
  public java.lang.String getId() {
    return _a_Id;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (super.equals(object) == false) return false;
    if (object == null) return false;
    if (!(object instanceof MethodIntfType)) return false;
    MethodIntfType typed = (MethodIntfType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = super.hashCode();
    if (this._a_Id != null) {
      result+= this._a_Id.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return super.get__ID();
  }
}
