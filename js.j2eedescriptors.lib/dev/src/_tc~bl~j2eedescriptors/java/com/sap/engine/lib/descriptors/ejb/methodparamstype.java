﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:07:06 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ejb;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}method-paramsType
 */
public  class MethodParamsType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/j2ee}method-param
  private java.util.ArrayList _f_MethodParam = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}method-param
   */
  public void setMethodParam(com.sap.engine.lib.descriptors.j2ee.JavaTypeType[] _MethodParam) {
    this._f_MethodParam.clear();
    if (_MethodParam != null) {
      for (int i=0; i<_MethodParam.length; i++) {
        if (_MethodParam[i] != null)
          this._f_MethodParam.add(_MethodParam[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}method-param
   */
  public com.sap.engine.lib.descriptors.j2ee.JavaTypeType[] getMethodParam() {
    com.sap.engine.lib.descriptors.j2ee.JavaTypeType[] result = new com.sap.engine.lib.descriptors.j2ee.JavaTypeType[_f_MethodParam.size()];
    _f_MethodParam.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof MethodParamsType)) return false;
    MethodParamsType typed = (MethodParamsType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors.j2ee.JavaTypeType[] _f_MethodParam1 = this.getMethodParam();
    com.sap.engine.lib.descriptors.j2ee.JavaTypeType[] _f_MethodParam2 = typed.getMethodParam();
    if (_f_MethodParam1 != null) {
      if (_f_MethodParam2 == null) return false;
      if (_f_MethodParam1.length != _f_MethodParam2.length) return false;
      for (int i1 = 0; i1 < _f_MethodParam1.length ; i1++) {
        if (_f_MethodParam1[i1] != null) {
          if (_f_MethodParam2[i1] == null) return false;
          if (!_f_MethodParam1[i1].equals(_f_MethodParam2[i1])) return false;
        } else {
          if (_f_MethodParam2[i1] != null) return false;
        }
      }
    } else {
      if (_f_MethodParam2 != null) return false;
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
    com.sap.engine.lib.descriptors.j2ee.JavaTypeType[] _f_MethodParam1 = this.getMethodParam();
    if (_f_MethodParam1 != null) {
      for (int i1 = 0; i1 < _f_MethodParam1.length ; i1++) {
        if (_f_MethodParam1[i1] != null) {
          result+= _f_MethodParam1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
