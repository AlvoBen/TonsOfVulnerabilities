﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 09 16:24:15 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.javaee;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}lifecycle-callbackType
 */
public  class LifecycleCallbackType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Element field for element {http://java.sun.com/xml/ns/javaee}lifecycle-callback-class
  private com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType _f_LifecycleCallbackClass;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}lifecycle-callback-class
   */
  public void setLifecycleCallbackClass(com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType _LifecycleCallbackClass) {
    this._f_LifecycleCallbackClass = _LifecycleCallbackClass;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}lifecycle-callback-class
   */
  public com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType getLifecycleCallbackClass() {
    return this._f_LifecycleCallbackClass;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}lifecycle-callback-method
  private com.sap.engine.lib.descriptors5.javaee.JavaIdentifierType _f_LifecycleCallbackMethod;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}lifecycle-callback-method
   */
  public void setLifecycleCallbackMethod(com.sap.engine.lib.descriptors5.javaee.JavaIdentifierType _LifecycleCallbackMethod) {
    this._f_LifecycleCallbackMethod = _LifecycleCallbackMethod;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}lifecycle-callback-method
   */
  public com.sap.engine.lib.descriptors5.javaee.JavaIdentifierType getLifecycleCallbackMethod() {
    return this._f_LifecycleCallbackMethod;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof LifecycleCallbackType)) return false;
    LifecycleCallbackType typed = (LifecycleCallbackType) object;
    if (this._f_LifecycleCallbackClass != null) {
      if (typed._f_LifecycleCallbackClass == null) return false;
      if (!this._f_LifecycleCallbackClass.equals(typed._f_LifecycleCallbackClass)) return false;
    } else {
      if (typed._f_LifecycleCallbackClass != null) return false;
    }
    if (this._f_LifecycleCallbackMethod != null) {
      if (typed._f_LifecycleCallbackMethod == null) return false;
      if (!this._f_LifecycleCallbackMethod.equals(typed._f_LifecycleCallbackMethod)) return false;
    } else {
      if (typed._f_LifecycleCallbackMethod != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._f_LifecycleCallbackClass != null) {
      result+= this._f_LifecycleCallbackClass.hashCode();
    }
    if (this._f_LifecycleCallbackMethod != null) {
      result+= this._f_LifecycleCallbackMethod.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
