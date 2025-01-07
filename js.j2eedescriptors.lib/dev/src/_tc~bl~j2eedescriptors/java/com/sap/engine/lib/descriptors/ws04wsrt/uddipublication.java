﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 12:49:12 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04wsrt;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/630/soap/java/descriptors/ws-runtime-descriptor}UDDIPublication
 */
public  class UDDIPublication implements java.io.Serializable,java.lang.Cloneable {

  // Attribute field for attribute {}inquiryURL
  private java.lang.String _a_InquiryURL;
  /**
   * Set method for attribute {}inquiryURL
   */
  public void setInquiryURL(java.lang.String _InquiryURL) {
    this._a_InquiryURL = _InquiryURL;
  }
  /**
   * Get method for attribute {}inquiryURL
   */
  public java.lang.String getInquiryURL() {
    return _a_InquiryURL;
  }

  // Attribute field for attribute {}publishURL
  private java.lang.String _a_PublishURL;
  /**
   * Set method for attribute {}publishURL
   */
  public void setPublishURL(java.lang.String _PublishURL) {
    this._a_PublishURL = _PublishURL;
  }
  /**
   * Get method for attribute {}publishURL
   */
  public java.lang.String getPublishURL() {
    return _a_PublishURL;
  }

  // Element field for element {}serviceKey
  private java.lang.String _f_ServiceKey;
  /**
   * Set method for element {}serviceKey
   */
  public void setServiceKey(java.lang.String _ServiceKey) {
    this._f_ServiceKey = _ServiceKey;
  }
  /**
   * Get method for element {}serviceKey
   */
  public java.lang.String getServiceKey() {
    return this._f_ServiceKey;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof UDDIPublication)) return false;
    UDDIPublication typed = (UDDIPublication) object;
    if (this._a_InquiryURL != null) {
      if (typed._a_InquiryURL == null) return false;
      if (!this._a_InquiryURL.equals(typed._a_InquiryURL)) return false;
    } else {
      if (typed._a_InquiryURL != null) return false;
    }
    if (this._a_PublishURL != null) {
      if (typed._a_PublishURL == null) return false;
      if (!this._a_PublishURL.equals(typed._a_PublishURL)) return false;
    } else {
      if (typed._a_PublishURL != null) return false;
    }
    if (this._f_ServiceKey != null) {
      if (typed._f_ServiceKey == null) return false;
      if (!this._f_ServiceKey.equals(typed._f_ServiceKey)) return false;
    } else {
      if (typed._f_ServiceKey != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_InquiryURL != null) {
      result+= this._a_InquiryURL.hashCode();
    }
    if (this._a_PublishURL != null) {
      result+= this._a_PublishURL.hashCode();
    }
    if (this._f_ServiceKey != null) {
      result+= this._f_ServiceKey.hashCode();
    }
    return result;
  }
}
