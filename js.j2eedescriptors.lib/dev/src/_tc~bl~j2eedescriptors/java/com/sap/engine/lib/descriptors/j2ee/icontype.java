﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 09:59:28 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.j2ee;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}iconType
 */
public  class IconType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Attribute field for attribute {http://www.w3.org/XML/1998/namespace}lang
  private java.lang.String _a_Lang;
  /**
   * Set method for attribute {http://www.w3.org/XML/1998/namespace}lang
   */
  public void setLang(java.lang.String _Lang) {
    this._a_Lang = _Lang;
  }
  /**
   * Get method for attribute {http://www.w3.org/XML/1998/namespace}lang
   */
  public java.lang.String getLang() {
    return _a_Lang;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}small-icon
  private com.sap.engine.lib.descriptors.j2ee.PathType _f_SmallIcon;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}small-icon
   */
  public void setSmallIcon(com.sap.engine.lib.descriptors.j2ee.PathType _SmallIcon) {
    this._f_SmallIcon = _SmallIcon;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}small-icon
   */
  public com.sap.engine.lib.descriptors.j2ee.PathType getSmallIcon() {
    return this._f_SmallIcon;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}large-icon
  private com.sap.engine.lib.descriptors.j2ee.PathType _f_LargeIcon;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}large-icon
   */
  public void setLargeIcon(com.sap.engine.lib.descriptors.j2ee.PathType _LargeIcon) {
    this._f_LargeIcon = _LargeIcon;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}large-icon
   */
  public com.sap.engine.lib.descriptors.j2ee.PathType getLargeIcon() {
    return this._f_LargeIcon;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof IconType)) return false;
    IconType typed = (IconType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    if (this._a_Lang != null) {
      if (typed._a_Lang == null) return false;
      if (!this._a_Lang.equals(typed._a_Lang)) return false;
    } else {
      if (typed._a_Lang != null) return false;
    }
    if (this._f_SmallIcon != null) {
      if (typed._f_SmallIcon == null) return false;
      if (!this._f_SmallIcon.equals(typed._f_SmallIcon)) return false;
    } else {
      if (typed._f_SmallIcon != null) return false;
    }
    if (this._f_LargeIcon != null) {
      if (typed._f_LargeIcon == null) return false;
      if (!this._f_LargeIcon.equals(typed._f_LargeIcon)) return false;
    } else {
      if (typed._f_LargeIcon != null) return false;
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
    if (this._a_Lang != null) {
      result+= this._a_Lang.hashCode();
    }
    if (this._f_SmallIcon != null) {
      result+= this._f_SmallIcon.hashCode();
    }
    if (this._f_LargeIcon != null) {
      result+= this._f_LargeIcon.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
