﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:03:54 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ejbj2eeengine;

/**
 * Schema complexType Java representation.
 * Represents type {}resource-refType
 */
public  class ResourceRefType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Element field for element {}res-ref-name
  private java.lang.String _f_ResRefName;
  /**
   * Set method for element {}res-ref-name
   */
  public void setResRefName(java.lang.String _ResRefName) {
    this._f_ResRefName = _ResRefName;
  }
  /**
   * Get method for element {}res-ref-name
   */
  public java.lang.String getResRefName() {
    return this._f_ResRefName;
  }

  // Element field for element {}res-link
  private java.lang.String _f_ResLink;
  /**
   * Set method for element {}res-link
   */
  public void setResLink(java.lang.String _ResLink) {
    this._f_ResLink = _ResLink;
  }
  /**
   * Get method for element {}res-link
   */
  public java.lang.String getResLink() {
    return this._f_ResLink;
  }

  // Element field for element {}non-transactional
  private com.sap.engine.lib.descriptors.ejbj2eeengine.NonTransactional _f_NonTransactional;
  /**
   * Set method for element {}non-transactional
   */
  public void setNonTransactional(com.sap.engine.lib.descriptors.ejbj2eeengine.NonTransactional _NonTransactional) {
    this._f_NonTransactional = _NonTransactional;
  }
  /**
   * Get method for element {}non-transactional
   */
  public com.sap.engine.lib.descriptors.ejbj2eeengine.NonTransactional getNonTransactional() {
    return this._f_NonTransactional;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ResourceRefType)) return false;
    ResourceRefType typed = (ResourceRefType) object;
    if (this._f_ResRefName != null) {
      if (typed._f_ResRefName == null) return false;
      if (!this._f_ResRefName.equals(typed._f_ResRefName)) return false;
    } else {
      if (typed._f_ResRefName != null) return false;
    }
    if (this._f_ResLink != null) {
      if (typed._f_ResLink == null) return false;
      if (!this._f_ResLink.equals(typed._f_ResLink)) return false;
    } else {
      if (typed._f_ResLink != null) return false;
    }
    if (this._f_NonTransactional != null) {
      if (typed._f_NonTransactional == null) return false;
      if (!this._f_NonTransactional.equals(typed._f_NonTransactional)) return false;
    } else {
      if (typed._f_NonTransactional != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._f_ResRefName != null) {
      result+= this._f_ResRefName.hashCode();
    }
    if (this._f_ResLink != null) {
      result+= this._f_ResLink.hashCode();
    }
    if (this._f_NonTransactional != null) {
      result+= this._f_NonTransactional.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
