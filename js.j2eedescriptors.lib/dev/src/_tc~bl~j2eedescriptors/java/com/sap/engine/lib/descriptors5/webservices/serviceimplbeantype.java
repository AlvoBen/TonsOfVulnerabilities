﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jun 26 13:55:46 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webservices;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}service-impl-beanType
 */
public  class ServiceImplBeanType implements java.io.Serializable {

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

  // // Active choise field
  private int _c_validField = 0;
  private com.sap.engine.lib.descriptors5.webservices.EjbLinkType _f_EjbLink;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}ejb-link
   */
  public void setEjbLink(com.sap.engine.lib.descriptors5.webservices.EjbLinkType _EjbLink) {
    if (this._c_validField != 0 && this._c_validField != 1) {
      this.unsetContent();
    }
    this._f_EjbLink = _EjbLink;
    this._c_validField = 1;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}ejb-link
   */
  public com.sap.engine.lib.descriptors5.webservices.EjbLinkType getEjbLink() {
    if (this._c_validField != 1) {
      return null;
    }
    return this._f_EjbLink;
  }
  /**
   * Check method for element {http://java.sun.com/xml/ns/javaee}ejb-link
   */
  public boolean isSetEjbLink() {
    return (this._c_validField ==1);
  }
  private com.sap.engine.lib.descriptors5.webservices.ServletLinkType _f_ServletLink;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}servlet-link
   */
  public void setServletLink(com.sap.engine.lib.descriptors5.webservices.ServletLinkType _ServletLink) {
    if (this._c_validField != 0 && this._c_validField != 2) {
      this.unsetContent();
    }
    this._f_ServletLink = _ServletLink;
    this._c_validField = 2;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}servlet-link
   */
  public com.sap.engine.lib.descriptors5.webservices.ServletLinkType getServletLink() {
    if (this._c_validField != 2) {
      return null;
    }
    return this._f_ServletLink;
  }
  /**
   * Check method for element {http://java.sun.com/xml/ns/javaee}servlet-link
   */
  public boolean isSetServletLink() {
    return (this._c_validField ==2);
  }
  /**
   * Common get method for choice type.
   */
  public java.lang.Object getContent() {
    switch (this._c_validField) {
      case 1: return this.getEjbLink();
      case 2: return this.getServletLink();
    }
    return null;
  }
  /**
   * Returns true if this choice has content set.
   */
  public boolean isSetContent() {
    return (this._c_validField == 0);
  }
  /**
   * Clears choice content.
   */
  public void unsetContent() {
    switch (this._c_validField) {
      case  1: {
        this._f_EjbLink = null;
        break;
      }
      case  2: {
        this._f_ServletLink = null;
        break;
      }
    }
    this._c_validField = 0;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ServiceImplBeanType)) return false;
    ServiceImplBeanType typed = (ServiceImplBeanType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    if (this._c_validField != typed._c_validField) return false;
    switch (this._c_validField) {
      case 1: {
        if (this._f_EjbLink != null) {
          if (typed._f_EjbLink == null) return false;
          if (!this._f_EjbLink.equals(typed._f_EjbLink)) return false;
        } else {
          if (typed._f_EjbLink != null) return false;
        }
        break;
      }
      case 2: {
        if (this._f_ServletLink != null) {
          if (typed._f_ServletLink == null) return false;
          if (!this._f_ServletLink.equals(typed._f_ServletLink)) return false;
        } else {
          if (typed._f_ServletLink != null) return false;
        }
        break;
      }
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
    switch (this._c_validField) {
      case 1: {
        if (this._f_EjbLink != null) {
          result+= this._f_EjbLink.hashCode();
        }
        result = result * this._c_validField;
        break;
      }
      case 2: {
        if (this._f_ServletLink != null) {
          result+= this._f_ServletLink.hashCode();
        }
        result = result * this._c_validField;
        break;
      }
    }
    return result;
  }
}
