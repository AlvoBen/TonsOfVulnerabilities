﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jun 26 13:55:46 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webservices;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}param-valueType
 */
public  class ParamValueType implements java.io.Serializable {

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}description
  private java.util.ArrayList _f_Description = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}description
   */
  public void setDescription(com.sap.engine.lib.descriptors5.webservices.DescriptionType[] _Description) {
    this._f_Description.clear();
    if (_Description != null) {
      for (int i=0; i<_Description.length; i++) {
        if (_Description[i] != null)
          this._f_Description.add(_Description[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}description
   */
  public com.sap.engine.lib.descriptors5.webservices.DescriptionType[] getDescription() {
    com.sap.engine.lib.descriptors5.webservices.DescriptionType[] result = new com.sap.engine.lib.descriptors5.webservices.DescriptionType[_f_Description.size()];
    _f_Description.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}param-name
  private com.sap.engine.lib.descriptors5.webservices.String _f_ParamName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}param-name
   */
  public void setParamName(com.sap.engine.lib.descriptors5.webservices.String _ParamName) {
    this._f_ParamName = _ParamName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}param-name
   */
  public com.sap.engine.lib.descriptors5.webservices.String getParamName() {
    return this._f_ParamName;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}param-value
  private com.sap.engine.lib.descriptors5.webservices.XsdStringType _f_ParamValue;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}param-value
   */
  public void setParamValue(com.sap.engine.lib.descriptors5.webservices.XsdStringType _ParamValue) {
    this._f_ParamValue = _ParamValue;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}param-value
   */
  public com.sap.engine.lib.descriptors5.webservices.XsdStringType getParamValue() {
    return this._f_ParamValue;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ParamValueType)) return false;
    ParamValueType typed = (ParamValueType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors5.webservices.DescriptionType[] _f_Description1 = this.getDescription();
    com.sap.engine.lib.descriptors5.webservices.DescriptionType[] _f_Description2 = typed.getDescription();
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
    if (this._f_ParamName != null) {
      if (typed._f_ParamName == null) return false;
      if (!this._f_ParamName.equals(typed._f_ParamName)) return false;
    } else {
      if (typed._f_ParamName != null) return false;
    }
    if (this._f_ParamValue != null) {
      if (typed._f_ParamValue == null) return false;
      if (!this._f_ParamValue.equals(typed._f_ParamValue)) return false;
    } else {
      if (typed._f_ParamValue != null) return false;
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
    com.sap.engine.lib.descriptors5.webservices.DescriptionType[] _f_Description1 = this.getDescription();
    if (_f_Description1 != null) {
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          result+= _f_Description1[i1].hashCode();
        }
      }
    }
    if (this._f_ParamName != null) {
      result+= this._f_ParamName.hashCode();
    }
    if (this._f_ParamValue != null) {
      result+= this._f_ParamValue.hashCode();
    }
    return result;
  }
}
