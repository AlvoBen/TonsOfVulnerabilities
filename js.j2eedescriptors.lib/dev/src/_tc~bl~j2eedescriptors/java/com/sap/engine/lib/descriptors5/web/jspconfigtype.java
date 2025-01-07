﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 09 16:24:16 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.web;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}jsp-configType
 */
public  class JspConfigType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}taglib
  private java.util.ArrayList _f_Taglib = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}taglib
   */
  public void setTaglib(com.sap.engine.lib.descriptors5.web.TaglibType[] _Taglib) {
    this._f_Taglib.clear();
    if (_Taglib != null) {
      for (int i=0; i<_Taglib.length; i++) {
        if (_Taglib[i] != null)
          this._f_Taglib.add(_Taglib[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}taglib
   */
  public com.sap.engine.lib.descriptors5.web.TaglibType[] getTaglib() {
    com.sap.engine.lib.descriptors5.web.TaglibType[] result = new com.sap.engine.lib.descriptors5.web.TaglibType[_f_Taglib.size()];
    _f_Taglib.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}jsp-property-group
  private java.util.ArrayList _f_JspPropertyGroup = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}jsp-property-group
   */
  public void setJspPropertyGroup(com.sap.engine.lib.descriptors5.web.JspPropertyGroupType[] _JspPropertyGroup) {
    this._f_JspPropertyGroup.clear();
    if (_JspPropertyGroup != null) {
      for (int i=0; i<_JspPropertyGroup.length; i++) {
        if (_JspPropertyGroup[i] != null)
          this._f_JspPropertyGroup.add(_JspPropertyGroup[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}jsp-property-group
   */
  public com.sap.engine.lib.descriptors5.web.JspPropertyGroupType[] getJspPropertyGroup() {
    com.sap.engine.lib.descriptors5.web.JspPropertyGroupType[] result = new com.sap.engine.lib.descriptors5.web.JspPropertyGroupType[_f_JspPropertyGroup.size()];
    _f_JspPropertyGroup.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof JspConfigType)) return false;
    JspConfigType typed = (JspConfigType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors5.web.TaglibType[] _f_Taglib1 = this.getTaglib();
    com.sap.engine.lib.descriptors5.web.TaglibType[] _f_Taglib2 = typed.getTaglib();
    if (_f_Taglib1 != null) {
      if (_f_Taglib2 == null) return false;
      if (_f_Taglib1.length != _f_Taglib2.length) return false;
      for (int i1 = 0; i1 < _f_Taglib1.length ; i1++) {
        if (_f_Taglib1[i1] != null) {
          if (_f_Taglib2[i1] == null) return false;
          if (!_f_Taglib1[i1].equals(_f_Taglib2[i1])) return false;
        } else {
          if (_f_Taglib2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Taglib2 != null) return false;
    }
    com.sap.engine.lib.descriptors5.web.JspPropertyGroupType[] _f_JspPropertyGroup1 = this.getJspPropertyGroup();
    com.sap.engine.lib.descriptors5.web.JspPropertyGroupType[] _f_JspPropertyGroup2 = typed.getJspPropertyGroup();
    if (_f_JspPropertyGroup1 != null) {
      if (_f_JspPropertyGroup2 == null) return false;
      if (_f_JspPropertyGroup1.length != _f_JspPropertyGroup2.length) return false;
      for (int i1 = 0; i1 < _f_JspPropertyGroup1.length ; i1++) {
        if (_f_JspPropertyGroup1[i1] != null) {
          if (_f_JspPropertyGroup2[i1] == null) return false;
          if (!_f_JspPropertyGroup1[i1].equals(_f_JspPropertyGroup2[i1])) return false;
        } else {
          if (_f_JspPropertyGroup2[i1] != null) return false;
        }
      }
    } else {
      if (_f_JspPropertyGroup2 != null) return false;
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
    com.sap.engine.lib.descriptors5.web.TaglibType[] _f_Taglib1 = this.getTaglib();
    if (_f_Taglib1 != null) {
      for (int i1 = 0; i1 < _f_Taglib1.length ; i1++) {
        if (_f_Taglib1[i1] != null) {
          result+= _f_Taglib1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.web.JspPropertyGroupType[] _f_JspPropertyGroup1 = this.getJspPropertyGroup();
    if (_f_JspPropertyGroup1 != null) {
      for (int i1 = 0; i1 < _f_JspPropertyGroup1.length ; i1++) {
        if (_f_JspPropertyGroup1[i1] != null) {
          result+= _f_JspPropertyGroup1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
