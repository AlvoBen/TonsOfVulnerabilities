﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Jun 14 11:13:11 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webfacesconfig;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}faces-config-navigation-rule-extensionType
 */
public  class FacesConfigNavigationRuleExtensionType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Wildcard 'any' field
  private java.util.ArrayList _f__any = new java.util.ArrayList();
  public void set_any(javax.xml.soap.SOAPElement[] __any) {
    this._f__any.clear();
    if (__any != null) {
      for (int i=0; i<__any.length; i++) {
        if (__any[i] != null)
          this._f__any.add(__any[i]);
      }
    }
  }
  public javax.xml.soap.SOAPElement[] get_any() {
    javax.xml.soap.SOAPElement[] result = new javax.xml.soap.SOAPElement[_f__any.size()];
    _f__any.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof FacesConfigNavigationRuleExtensionType)) return false;
    FacesConfigNavigationRuleExtensionType typed = (FacesConfigNavigationRuleExtensionType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    javax.xml.soap.SOAPElement[] _f__any1 = this.get_any();
    javax.xml.soap.SOAPElement[] _f__any2 = typed.get_any();
    if (_f__any1 != null) {
      if (_f__any2 == null) return false;
      if (_f__any1.length != _f__any2.length) return false;
      for (int i1 = 0; i1 < _f__any1.length ; i1++) {
        if (_f__any1[i1] != null) {
          if (_f__any2[i1] == null) return false;
          if (!_f__any1[i1].toString().equals(_f__any2[i1].toString())) return false;
        } else {
          if (_f__any2[i1] != null) return false;
        }
      }
    } else {
      if (_f__any2 != null) return false;
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
    javax.xml.soap.SOAPElement[] _f__any1 = this.get_any();
    if (_f__any1 != null) {
      for (int i1 = 0; i1 < _f__any1.length ; i1++) {
        if (_f__any1[i1] != null) {
          result+= _f__any1[i1].toString().hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
