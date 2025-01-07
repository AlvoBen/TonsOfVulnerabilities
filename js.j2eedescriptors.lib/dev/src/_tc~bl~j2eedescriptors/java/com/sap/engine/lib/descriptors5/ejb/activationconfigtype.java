﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Oct 19 11:20:17 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ejb;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}activation-configType
 */
public  class ActivationConfigType implements java.io.Serializable {

  private static final long serialVersionUID = 3985035242627693157L;

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
  public void setDescription(com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _Description) {
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
  public com.sap.engine.lib.descriptors5.javaee.DescriptionType[] getDescription() {
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] result = new com.sap.engine.lib.descriptors5.javaee.DescriptionType[_f_Description.size()];
    _f_Description.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}activation-config-property
  private java.util.ArrayList _f_ActivationConfigProperty = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}activation-config-property
   */
  public void setActivationConfigProperty(com.sap.engine.lib.descriptors5.ejb.ActivationConfigPropertyType[] _ActivationConfigProperty) {
    this._f_ActivationConfigProperty.clear();
    if (_ActivationConfigProperty != null) {
      for (int i=0; i<_ActivationConfigProperty.length; i++) {
        if (_ActivationConfigProperty[i] != null)
          this._f_ActivationConfigProperty.add(_ActivationConfigProperty[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}activation-config-property
   */
  public com.sap.engine.lib.descriptors5.ejb.ActivationConfigPropertyType[] getActivationConfigProperty() {
    com.sap.engine.lib.descriptors5.ejb.ActivationConfigPropertyType[] result = new com.sap.engine.lib.descriptors5.ejb.ActivationConfigPropertyType[_f_ActivationConfigProperty.size()];
    _f_ActivationConfigProperty.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ActivationConfigType)) return false;
    ActivationConfigType typed = (ActivationConfigType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _f_Description1 = this.getDescription();
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _f_Description2 = typed.getDescription();
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
    com.sap.engine.lib.descriptors5.ejb.ActivationConfigPropertyType[] _f_ActivationConfigProperty1 = this.getActivationConfigProperty();
    com.sap.engine.lib.descriptors5.ejb.ActivationConfigPropertyType[] _f_ActivationConfigProperty2 = typed.getActivationConfigProperty();
    if (_f_ActivationConfigProperty1 != null) {
      if (_f_ActivationConfigProperty2 == null) return false;
      if (_f_ActivationConfigProperty1.length != _f_ActivationConfigProperty2.length) return false;
      for (int i1 = 0; i1 < _f_ActivationConfigProperty1.length ; i1++) {
        if (_f_ActivationConfigProperty1[i1] != null) {
          if (_f_ActivationConfigProperty2[i1] == null) return false;
          if (!_f_ActivationConfigProperty1[i1].equals(_f_ActivationConfigProperty2[i1])) return false;
        } else {
          if (_f_ActivationConfigProperty2[i1] != null) return false;
        }
      }
    } else {
      if (_f_ActivationConfigProperty2 != null) return false;
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
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _f_Description1 = this.getDescription();
    if (_f_Description1 != null) {
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          result+= _f_Description1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.ejb.ActivationConfigPropertyType[] _f_ActivationConfigProperty1 = this.getActivationConfigProperty();
    if (_f_ActivationConfigProperty1 != null) {
      for (int i1 = 0; i1 < _f_ActivationConfigProperty1.length ; i1++) {
        if (_f_ActivationConfigProperty1[i1] != null) {
          result+= _f_ActivationConfigProperty1[i1].hashCode();
        }
      }
    }
    return result;
  }
}
