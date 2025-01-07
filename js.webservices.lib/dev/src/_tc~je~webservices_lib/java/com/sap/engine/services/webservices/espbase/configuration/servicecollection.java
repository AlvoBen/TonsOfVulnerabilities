﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Tue Feb 22 14:02:04 EET 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.services.webservices.espbase.configuration;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/710/ws/configuration-descriptor}ServiceCollection
 */
public  class ServiceCollection implements java.io.Serializable,java.lang.Cloneable {

  // Element field for element {}Service
  private java.util.ArrayList _f_Service = new java.util.ArrayList();
  /**
   * Set method for element {}Service
   */
  public void setService(com.sap.engine.services.webservices.espbase.configuration.Service[] _Service) {
    this._f_Service.clear();
    if (_Service != null) {
      for (int i=0; i<_Service.length; i++) {
        if (_Service[i] != null)
          this._f_Service.add(_Service[i]);
      }
    }
  }
  /**
   * Get method for element {}Service
   */
  public com.sap.engine.services.webservices.espbase.configuration.Service[] getService() {
    com.sap.engine.services.webservices.espbase.configuration.Service[] result = new com.sap.engine.services.webservices.espbase.configuration.Service[_f_Service.size()];
    _f_Service.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ServiceCollection)) return false;
    ServiceCollection typed = (ServiceCollection) object;
    com.sap.engine.services.webservices.espbase.configuration.Service[] _f_Service1 = this.getService();
    com.sap.engine.services.webservices.espbase.configuration.Service[] _f_Service2 = typed.getService();
    if (_f_Service1 != null) {
      if (_f_Service2 == null) return false;
      if (_f_Service1.length != _f_Service2.length) return false;
      for (int i1 = 0; i1 < _f_Service1.length ; i1++) {
        if (_f_Service1[i1] != null) {
          if (_f_Service2[i1] == null) return false;
          if (!_f_Service1[i1].equals(_f_Service2[i1])) return false;
        } else {
          if (_f_Service2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Service2 != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    com.sap.engine.services.webservices.espbase.configuration.Service[] _f_Service1 = this.getService();
    if (_f_Service1 != null) {
      for (int i1 = 0; i1 < _f_Service1.length ; i1++) {
        if (_f_Service1[i1] != null) {
          result+= _f_Service1[i1].hashCode();
        }
      }
    }
    return result;
  }
}
