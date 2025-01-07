﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:00:46 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.web;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}mime-mappingType
 */
public  class MimeMappingType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/j2ee}extension
  private com.sap.engine.lib.descriptors.j2ee.String _f_Extension;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}extension
   */
  public void setExtension(com.sap.engine.lib.descriptors.j2ee.String _Extension) {
    this._f_Extension = _Extension;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}extension
   */
  public com.sap.engine.lib.descriptors.j2ee.String getExtension() {
    return this._f_Extension;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}mime-type
  private com.sap.engine.lib.descriptors.web.MimeTypeType _f_MimeType;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}mime-type
   */
  public void setMimeType(com.sap.engine.lib.descriptors.web.MimeTypeType _MimeType) {
    this._f_MimeType = _MimeType;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}mime-type
   */
  public com.sap.engine.lib.descriptors.web.MimeTypeType getMimeType() {
    return this._f_MimeType;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof MimeMappingType)) return false;
    MimeMappingType typed = (MimeMappingType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    if (this._f_Extension != null) {
      if (typed._f_Extension == null) return false;
      if (!this._f_Extension.equals(typed._f_Extension)) return false;
    } else {
      if (typed._f_Extension != null) return false;
    }
    if (this._f_MimeType != null) {
      if (typed._f_MimeType == null) return false;
      if (!this._f_MimeType.equals(typed._f_MimeType)) return false;
    } else {
      if (typed._f_MimeType != null) return false;
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
    if (this._f_Extension != null) {
      result+= this._f_Extension.hashCode();
    }
    if (this._f_MimeType != null) {
      result+= this._f_MimeType.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
