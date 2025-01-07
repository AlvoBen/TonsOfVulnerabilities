﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 09 16:24:16 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webjsptld;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}validatorType
 */
public  class ValidatorType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}validator-class
  private com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType _f_ValidatorClass;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}validator-class
   */
  public void setValidatorClass(com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType _ValidatorClass) {
    this._f_ValidatorClass = _ValidatorClass;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}validator-class
   */
  public com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType getValidatorClass() {
    return this._f_ValidatorClass;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}init-param
  private java.util.ArrayList _f_InitParam = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}init-param
   */
  public void setInitParam(com.sap.engine.lib.descriptors5.javaee.ParamValueType[] _InitParam) {
    this._f_InitParam.clear();
    if (_InitParam != null) {
      for (int i=0; i<_InitParam.length; i++) {
        if (_InitParam[i] != null)
          this._f_InitParam.add(_InitParam[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}init-param
   */
  public com.sap.engine.lib.descriptors5.javaee.ParamValueType[] getInitParam() {
    com.sap.engine.lib.descriptors5.javaee.ParamValueType[] result = new com.sap.engine.lib.descriptors5.javaee.ParamValueType[_f_InitParam.size()];
    _f_InitParam.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ValidatorType)) return false;
    ValidatorType typed = (ValidatorType) object;
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
    if (this._f_ValidatorClass != null) {
      if (typed._f_ValidatorClass == null) return false;
      if (!this._f_ValidatorClass.equals(typed._f_ValidatorClass)) return false;
    } else {
      if (typed._f_ValidatorClass != null) return false;
    }
    com.sap.engine.lib.descriptors5.javaee.ParamValueType[] _f_InitParam1 = this.getInitParam();
    com.sap.engine.lib.descriptors5.javaee.ParamValueType[] _f_InitParam2 = typed.getInitParam();
    if (_f_InitParam1 != null) {
      if (_f_InitParam2 == null) return false;
      if (_f_InitParam1.length != _f_InitParam2.length) return false;
      for (int i1 = 0; i1 < _f_InitParam1.length ; i1++) {
        if (_f_InitParam1[i1] != null) {
          if (_f_InitParam2[i1] == null) return false;
          if (!_f_InitParam1[i1].equals(_f_InitParam2[i1])) return false;
        } else {
          if (_f_InitParam2[i1] != null) return false;
        }
      }
    } else {
      if (_f_InitParam2 != null) return false;
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
    if (this._f_ValidatorClass != null) {
      result+= this._f_ValidatorClass.hashCode();
    }
    com.sap.engine.lib.descriptors5.javaee.ParamValueType[] _f_InitParam1 = this.getInitParam();
    if (_f_InitParam1 != null) {
      for (int i1 = 0; i1 < _f_InitParam1.length ; i1++) {
        if (_f_InitParam1[i1] != null) {
          result+= _f_InitParam1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
