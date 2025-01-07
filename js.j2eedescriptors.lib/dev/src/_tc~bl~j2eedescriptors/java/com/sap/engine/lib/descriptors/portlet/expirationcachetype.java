﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 13 17:05:09 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.portlet;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}expiration-cacheType
 */
public  class ExpirationCacheType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Simple content field
  private int _f__value;
  /**
   * Set method for simple content.
   */
  public void set_value(int __value) {
    this._f__value = __value;
  }
  /**
   * Get method for simple content.
   */
  public int get_value() {
    return this._f__value;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ExpirationCacheType)) return false;
    ExpirationCacheType typed = (ExpirationCacheType) object;
    if (this._f__value != typed._f__value) return false;
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    result+= (int) this._f__value;
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
