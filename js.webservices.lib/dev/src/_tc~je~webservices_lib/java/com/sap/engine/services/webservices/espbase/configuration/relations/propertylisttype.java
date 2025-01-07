﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Fri May 26 15:19:50 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.services.webservices.espbase.configuration.relations;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/710/ws/property-relations}PropertyListType
 */
public  class PropertyListType implements java.io.Serializable {

  // Element field for element {}property
  private java.util.ArrayList _f_Property = new java.util.ArrayList();
  /**
   * Set method for element {}property
   */
  public void setProperty(com.sap.engine.services.webservices.espbase.configuration.relations.PropertyType[] _Property) {
    this._f_Property.clear();
    if (_Property != null) {
      for (int i=0; i<_Property.length; i++) {
        if (_Property[i] != null)
          this._f_Property.add(_Property[i]);
      }
    }
  }
  /**
   * Get method for element {}property
   */
  public com.sap.engine.services.webservices.espbase.configuration.relations.PropertyType[] getProperty() {
    com.sap.engine.services.webservices.espbase.configuration.relations.PropertyType[] result = new com.sap.engine.services.webservices.espbase.configuration.relations.PropertyType[_f_Property.size()];
    _f_Property.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof PropertyListType)) return false;
    PropertyListType typed = (PropertyListType) object;
    com.sap.engine.services.webservices.espbase.configuration.relations.PropertyType[] _f_Property1 = this.getProperty();
    com.sap.engine.services.webservices.espbase.configuration.relations.PropertyType[] _f_Property2 = typed.getProperty();
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
    com.sap.engine.services.webservices.espbase.configuration.relations.PropertyType[] _f_Property1 = this.getProperty();
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
