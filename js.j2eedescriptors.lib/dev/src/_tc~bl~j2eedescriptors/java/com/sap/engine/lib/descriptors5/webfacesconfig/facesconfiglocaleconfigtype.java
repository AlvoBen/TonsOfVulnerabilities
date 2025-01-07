﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Jun 14 11:13:11 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webfacesconfig;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}faces-config-locale-configType
 */
public  class FacesConfigLocaleConfigType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}default-locale
  private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigDefaultLocaleType _f_DefaultLocale;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}default-locale
   */
  public void setDefaultLocale(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigDefaultLocaleType _DefaultLocale) {
    this._f_DefaultLocale = _DefaultLocale;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}default-locale
   */
  public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigDefaultLocaleType getDefaultLocale() {
    return this._f_DefaultLocale;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}supported-locale
  private java.util.ArrayList _f_SupportedLocale = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}supported-locale
   */
  public void setSupportedLocale(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigSupportedLocaleType[] _SupportedLocale) {
    this._f_SupportedLocale.clear();
    if (_SupportedLocale != null) {
      for (int i=0; i<_SupportedLocale.length; i++) {
        if (_SupportedLocale[i] != null)
          this._f_SupportedLocale.add(_SupportedLocale[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}supported-locale
   */
  public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigSupportedLocaleType[] getSupportedLocale() {
    com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigSupportedLocaleType[] result = new com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigSupportedLocaleType[_f_SupportedLocale.size()];
    _f_SupportedLocale.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof FacesConfigLocaleConfigType)) return false;
    FacesConfigLocaleConfigType typed = (FacesConfigLocaleConfigType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    if (this._f_DefaultLocale != null) {
      if (typed._f_DefaultLocale == null) return false;
      if (!this._f_DefaultLocale.equals(typed._f_DefaultLocale)) return false;
    } else {
      if (typed._f_DefaultLocale != null) return false;
    }
    com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigSupportedLocaleType[] _f_SupportedLocale1 = this.getSupportedLocale();
    com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigSupportedLocaleType[] _f_SupportedLocale2 = typed.getSupportedLocale();
    if (_f_SupportedLocale1 != null) {
      if (_f_SupportedLocale2 == null) return false;
      if (_f_SupportedLocale1.length != _f_SupportedLocale2.length) return false;
      for (int i1 = 0; i1 < _f_SupportedLocale1.length ; i1++) {
        if (_f_SupportedLocale1[i1] != null) {
          if (_f_SupportedLocale2[i1] == null) return false;
          if (!_f_SupportedLocale1[i1].equals(_f_SupportedLocale2[i1])) return false;
        } else {
          if (_f_SupportedLocale2[i1] != null) return false;
        }
      }
    } else {
      if (_f_SupportedLocale2 != null) return false;
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
    if (this._f_DefaultLocale != null) {
      result+= this._f_DefaultLocale.hashCode();
    }
    com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigSupportedLocaleType[] _f_SupportedLocale1 = this.getSupportedLocale();
    if (_f_SupportedLocale1 != null) {
      for (int i1 = 0; i1 < _f_SupportedLocale1.length ; i1++) {
        if (_f_SupportedLocale1[i1] != null) {
          result+= _f_SupportedLocale1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
