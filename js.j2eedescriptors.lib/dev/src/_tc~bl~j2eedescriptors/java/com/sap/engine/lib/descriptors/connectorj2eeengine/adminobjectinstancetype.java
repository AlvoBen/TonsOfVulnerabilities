﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jan 30 18:27:10 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.connectorj2eeengine;

/**
 * Schema complexType Java representation.
 * Represents type {}adminobject-instanceType
 */
public  class AdminobjectInstanceType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {}jndi-name
  private com.sap.engine.lib.descriptors.connectorj2eeengine.XsdStringType _f_JndiName;
  /**
   * Set method for element {}jndi-name
   */
  public void setJndiName(com.sap.engine.lib.descriptors.connectorj2eeengine.XsdStringType _JndiName) {
    this._f_JndiName = _JndiName;
  }
  /**
   * Get method for element {}jndi-name
   */
  public com.sap.engine.lib.descriptors.connectorj2eeengine.XsdStringType getJndiName() {
    return this._f_JndiName;
  }

  // Element field for element {}config-property
  private java.util.ArrayList _f_ConfigProperty = new java.util.ArrayList();
  /**
   * Set method for element {}config-property
   */
  public void setConfigProperty(com.sap.engine.lib.descriptors.connectorj2eeengine.ConfigPropertyType[] _ConfigProperty) {
    this._f_ConfigProperty.clear();
    if (_ConfigProperty != null) {
      for (int i=0; i<_ConfigProperty.length; i++) {
        if (_ConfigProperty[i] != null)
          this._f_ConfigProperty.add(_ConfigProperty[i]);
      }
    }
  }
  /**
   * Get method for element {}config-property
   */
  public com.sap.engine.lib.descriptors.connectorj2eeengine.ConfigPropertyType[] getConfigProperty() {
    com.sap.engine.lib.descriptors.connectorj2eeengine.ConfigPropertyType[] result = new com.sap.engine.lib.descriptors.connectorj2eeengine.ConfigPropertyType[_f_ConfigProperty.size()];
    _f_ConfigProperty.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof AdminobjectInstanceType)) return false;
    AdminobjectInstanceType typed = (AdminobjectInstanceType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    if (this._f_JndiName != null) {
      if (typed._f_JndiName == null) return false;
      if (!this._f_JndiName.equals(typed._f_JndiName)) return false;
    } else {
      if (typed._f_JndiName != null) return false;
    }
    com.sap.engine.lib.descriptors.connectorj2eeengine.ConfigPropertyType[] _f_ConfigProperty1 = this.getConfigProperty();
    com.sap.engine.lib.descriptors.connectorj2eeengine.ConfigPropertyType[] _f_ConfigProperty2 = typed.getConfigProperty();
    if (_f_ConfigProperty1 != null) {
      if (_f_ConfigProperty2 == null) return false;
      if (_f_ConfigProperty1.length != _f_ConfigProperty2.length) return false;
      for (int i1 = 0; i1 < _f_ConfigProperty1.length ; i1++) {
        if (_f_ConfigProperty1[i1] != null) {
          if (_f_ConfigProperty2[i1] == null) return false;
          if (!_f_ConfigProperty1[i1].equals(_f_ConfigProperty2[i1])) return false;
        } else {
          if (_f_ConfigProperty2[i1] != null) return false;
        }
      }
    } else {
      if (_f_ConfigProperty2 != null) return false;
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
    if (this._f_JndiName != null) {
      result+= this._f_JndiName.hashCode();
    }
    com.sap.engine.lib.descriptors.connectorj2eeengine.ConfigPropertyType[] _f_ConfigProperty1 = this.getConfigProperty();
    if (_f_ConfigProperty1 != null) {
      for (int i1 = 0; i1 < _f_ConfigProperty1.length ; i1++) {
        if (_f_ConfigProperty1[i1] != null) {
          result+= _f_ConfigProperty1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
