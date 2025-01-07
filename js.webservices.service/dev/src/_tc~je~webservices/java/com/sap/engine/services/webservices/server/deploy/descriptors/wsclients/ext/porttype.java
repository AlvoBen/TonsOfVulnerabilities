﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Fri May 27 13:18:04 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.services.webservices.server.deploy.descriptors.wsclients.ext;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/710/wsclients/ws-clients-j2ee-engine-ext-descriptor}portType
 */
public  class PortType implements java.io.Serializable,java.lang.Cloneable {

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

  // Element field for element {}Property
  private java.util.ArrayList _f_Property = new java.util.ArrayList();
  /**
   * Set method for element {}Property
   */
  public void setProperty(com.sap.engine.services.webservices.server.deploy.descriptors.wsclients.ext.PropertyType[] _Property) {
    this._f_Property.clear();
    if (_Property != null) {
      for (int i=0; i<_Property.length; i++) {
        if (_Property[i] != null)
          this._f_Property.add(_Property[i]);
      }
    }
  }
  /**
   * Get method for element {}Property
   */
  public com.sap.engine.services.webservices.server.deploy.descriptors.wsclients.ext.PropertyType[] getProperty() {
    com.sap.engine.services.webservices.server.deploy.descriptors.wsclients.ext.PropertyType[] result = new com.sap.engine.services.webservices.server.deploy.descriptors.wsclients.ext.PropertyType[_f_Property.size()];
    _f_Property.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof PortType)) return false;
    PortType typed = (PortType) object;
    if (this._a_Name != null) {
      if (typed._a_Name == null) return false;
      if (!this._a_Name.equals(typed._a_Name)) return false;
    } else {
      if (typed._a_Name != null) return false;
    }
    com.sap.engine.services.webservices.server.deploy.descriptors.wsclients.ext.PropertyType[] _f_Property1 = this.getProperty();
    com.sap.engine.services.webservices.server.deploy.descriptors.wsclients.ext.PropertyType[] _f_Property2 = typed.getProperty();
    if (_f_Property1 != null) {
      if (_f_Property2 == null) return false;
      if (_f_Property1.length != _f_Property2.length) return false;
      for (int i1 = 0; i1 < _f_Property1.length ; i1++) {
        if (_f_Property1[i1] != null) {
          if (_f_Property2[i1] == null) return false;
          if (!_f_Property1[i1].equals(_f_Property2[i1])) return false;
        } else {
          if (_f_Property2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Property2 != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_Name != null) {
      result+= this._a_Name.hashCode();
    }
    com.sap.engine.services.webservices.server.deploy.descriptors.wsclients.ext.PropertyType[] _f_Property1 = this.getProperty();
    if (_f_Property1 != null) {
      for (int i1 = 0; i1 < _f_Property1.length ; i1++) {
        if (_f_Property1[i1] != null) {
          result+= _f_Property1[i1].hashCode();
        }
      }
    }
    return result;
  }
}
