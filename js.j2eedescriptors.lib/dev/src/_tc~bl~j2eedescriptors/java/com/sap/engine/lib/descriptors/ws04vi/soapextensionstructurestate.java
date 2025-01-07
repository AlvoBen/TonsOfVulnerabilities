﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 11:42:02 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04vi;

/**
 * Schema complexType Java representation.
 * Represents type {http://xml.sap.com/2002/10/metamodel/vi}SoapExtensionStructureState
 */
public  class SoapExtensionStructureState extends com.sap.engine.lib.descriptors.ws04vi.AbstractExtensionState implements java.io.Serializable,java.lang.Cloneable {

  // Attribute field for attribute {}unorderedFields
  private boolean _a_UnorderedFields = false;
  /**
   * Set method for attribute {}unorderedFields
   */
  public void setUnorderedFields(boolean _UnorderedFields) {
    this._a_UnorderedFields = _UnorderedFields;
  }
  /**
   * Get method for attribute {}unorderedFields
   */
  public boolean isUnorderedFields() {
    return _a_UnorderedFields;
  }

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
    if (super.equals(object) == false) return false;
    if (object == null) return false;
    if (!(object instanceof SoapExtensionStructureState)) return false;
    SoapExtensionStructureState typed = (SoapExtensionStructureState) object;
    if (this._a_UnorderedFields != typed._a_UnorderedFields) return false;
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
    int result = super.hashCode();
    if (this._a_UnorderedFields) result += 1;
    if (this._a_Name != null) {
      result+= this._a_Name.hashCode();
    }
    return result;
  }
}
