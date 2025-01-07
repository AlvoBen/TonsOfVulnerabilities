﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 09 16:24:15 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.javaee;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}xsdNMTOKENType
 */
public  class XsdNMTOKENType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Simple content field
  private java.lang.String _f__value;
  /**
   * Set method for simple content.
   */
  public void set_value(java.lang.String __value) {
    this._f__value = __value;
  }
  /**
   * Get method for simple content.
   */
  public java.lang.String get_value() {
    return this._f__value;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof XsdNMTOKENType)) return false;
    XsdNMTOKENType typed = (XsdNMTOKENType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    if (this._f__value != null) {
      if (typed._f__value == null) return false;
      if (!this._f__value.equals(typed._f__value)) return false;
    } else {
      if (typed._f__value != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_Id != null) {
      result+= this._a_Id.hashCode();
    }
    if (this._f__value != null) {
      result+= this._f__value.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
