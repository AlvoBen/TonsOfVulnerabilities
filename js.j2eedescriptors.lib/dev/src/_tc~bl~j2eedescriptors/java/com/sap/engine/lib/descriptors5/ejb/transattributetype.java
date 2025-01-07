﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Oct 19 11:20:17 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ejb;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}trans-attributeType
 */
public  class TransAttributeType extends com.sap.engine.lib.descriptors5.javaee.String implements java.io.Serializable {

  private static final long serialVersionUID = -8788654049585494307L;

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
    if (!(object instanceof TransAttributeType)) return false;
    TransAttributeType typed = (TransAttributeType) object;
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
}
