﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Oct 19 11:20:17 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ejb;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}container-transactionType
 */
public  class ContainerTransactionType implements java.io.Serializable {

  private static final long serialVersionUID = 1776877381384572683L;

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}method
  private java.util.ArrayList _f_Method = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}method
   */
  public void setMethod(com.sap.engine.lib.descriptors5.ejb.MethodType[] _Method) {
    this._f_Method.clear();
    if (_Method != null) {
      for (int i=0; i<_Method.length; i++) {
        if (_Method[i] != null)
          this._f_Method.add(_Method[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}method
   */
  public com.sap.engine.lib.descriptors5.ejb.MethodType[] getMethod() {
    com.sap.engine.lib.descriptors5.ejb.MethodType[] result = new com.sap.engine.lib.descriptors5.ejb.MethodType[_f_Method.size()];
    _f_Method.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}trans-attribute
  private com.sap.engine.lib.descriptors5.ejb.TransAttributeType _f_TransAttribute;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}trans-attribute
   */
  public void setTransAttribute(com.sap.engine.lib.descriptors5.ejb.TransAttributeType _TransAttribute) {
    this._f_TransAttribute = _TransAttribute;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}trans-attribute
   */
  public com.sap.engine.lib.descriptors5.ejb.TransAttributeType getTransAttribute() {
    return this._f_TransAttribute;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ContainerTransactionType)) return false;
    ContainerTransactionType typed = (ContainerTransactionType) object;
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
    com.sap.engine.lib.descriptors5.ejb.MethodType[] _f_Method1 = this.getMethod();
    com.sap.engine.lib.descriptors5.ejb.MethodType[] _f_Method2 = typed.getMethod();
    if (_f_Method1 != null) {
      if (_f_Method2 == null) return false;
      if (_f_Method1.length != _f_Method2.length) return false;
      for (int i1 = 0; i1 < _f_Method1.length ; i1++) {
        if (_f_Method1[i1] != null) {
          if (_f_Method2[i1] == null) return false;
          if (!_f_Method1[i1].equals(_f_Method2[i1])) return false;
        } else {
          if (_f_Method2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Method2 != null) return false;
    }
    if (this._f_TransAttribute != null) {
      if (typed._f_TransAttribute == null) return false;
      if (!this._f_TransAttribute.equals(typed._f_TransAttribute)) return false;
    } else {
      if (typed._f_TransAttribute != null) return false;
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
    com.sap.engine.lib.descriptors5.ejb.MethodType[] _f_Method1 = this.getMethod();
    if (_f_Method1 != null) {
      for (int i1 = 0; i1 < _f_Method1.length ; i1++) {
        if (_f_Method1[i1] != null) {
          result+= _f_Method1[i1].hashCode();
        }
      }
    }
    if (this._f_TransAttribute != null) {
      result+= this._f_TransAttribute.hashCode();
    }
    return result;
  }
}
