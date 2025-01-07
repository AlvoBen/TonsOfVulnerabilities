﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 13 17:05:09 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.portlet;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}resource-bundleType
 */
public  class ResourceBundleType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Simple content field
  private java.lang.String _f__value;
  /**
   * Set method for simple content.
   */
  public void set_value(java.lang.String __value) {
    this._f__value = __value;
  }
  /**
   * Get method for simple content.
   */
  public java.lang.String get_value() {
    return this._f__value;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ResourceBundleType)) return false;
    ResourceBundleType typed = (ResourceBundleType) object;
    if (this._f__value != null) {
      if (typed._f__value == null) return false;
      if (!this._f__value.equals(typed._f__value)) return false;
    } else {
      if (typed._f__value != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._f__value != null) {
      result+= this._f__value.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
