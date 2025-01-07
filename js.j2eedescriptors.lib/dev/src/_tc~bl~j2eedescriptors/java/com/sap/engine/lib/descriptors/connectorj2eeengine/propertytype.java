﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jan 30 18:27:10 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.connectorj2eeengine;

/**
 * Schema complexType Java representation.
 * Represents type {}propertyType
 */
public  class PropertyType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {}description
  private java.util.ArrayList _f_Description = new java.util.ArrayList();
  /**
   * Set method for element {}description
   */
  public void setDescription(java.lang.String[] _Description) {
    this._f_Description.clear();
    if (_Description != null) {
      for (int i=0; i<_Description.length; i++) {
        if (_Description[i] != null)
          this._f_Description.add(_Description[i]);
      }
    }
  }
  /**
   * Get method for element {}description
   */
  public java.lang.String[] getDescription() {
    java.lang.String[] result = new java.lang.String[_f_Description.size()];
    _f_Description.toArray(result);
    return result;
  }

  // Element field for element {}config-property-name
  private java.lang.String _f_ConfigPropertyName;
  /**
   * Set method for element {}config-property-name
   */
  public void setConfigPropertyName(java.lang.String _ConfigPropertyName) {
    this._f_ConfigPropertyName = _ConfigPropertyName;
  }
  /**
   * Get method for element {}config-property-name
   */
  public java.lang.String getConfigPropertyName() {
    return this._f_ConfigPropertyName;
  }

  // Element field for element {}config-property-value
  private java.lang.String _f_ConfigPropertyValue;
  /**
   * Set method for element {}config-property-value
   */
  public void setConfigPropertyValue(java.lang.String _ConfigPropertyValue) {
    this._f_ConfigPropertyValue = _ConfigPropertyValue;
  }
  /**
   * Get method for element {}config-property-value
   */
  public java.lang.String getConfigPropertyValue() {
    return this._f_ConfigPropertyValue;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof PropertyType)) return false;
    PropertyType typed = (PropertyType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    java.lang.String[] _f_Description1 = this.getDescription();
    java.lang.String[] _f_Description2 = typed.getDescription();
    if (_f_Description1 != null) {
      if (_f_Description2 == null) return false;
      if (_f_Description1.length != _f_Description2.length) return false;
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          if (_f_Description2[i1] == null) return false;
          if (!_f_Description1[i1].equals(_f_Description2[i1])) return false;
        } else {
          if (_f_Description2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Description2 != null) return false;
    }
    if (this._f_ConfigPropertyName != null) {
      if (typed._f_ConfigPropertyName == null) return false;
      if (!this._f_ConfigPropertyName.equals(typed._f_ConfigPropertyName)) return false;
    } else {
      if (typed._f_ConfigPropertyName != null) return false;
    }
    if (this._f_ConfigPropertyValue != null) {
      if (typed._f_ConfigPropertyValue == null) return false;
      if (!this._f_ConfigPropertyValue.equals(typed._f_ConfigPropertyValue)) return false;
    } else {
      if (typed._f_ConfigPropertyValue != null) return false;
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
    java.lang.String[] _f_Description1 = this.getDescription();
    if (_f_Description1 != null) {
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          result+= _f_Description1[i1].hashCode();
        }
      }
    }
    if (this._f_ConfigPropertyName != null) {
      result+= this._f_ConfigPropertyName.hashCode();
    }
    if (this._f_ConfigPropertyValue != null) {
      result+= this._f_ConfigPropertyValue.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
