﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 11:45:02 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04wsd;

/**
 * Schema complexType Java representation.
 * Represents type {http://xml.sap.com/2002/10/metamodel/wsd}FunctionState
 */
public  class FunctionState implements java.io.Serializable,java.lang.Cloneable {

  // Attribute field for attribute {}name
  private java.lang.String _a_Name;
  /**
   * Set method for attribute {}name
   */
  public void setName(java.lang.String _Name) {
    this._a_Name = _Name;
  }
  /**
   * Get method for attribute {}name
   */
  public java.lang.String getName() {
    return _a_Name;
  }

  // Element field for element {http://xml.sap.com/2002/10/metamodel/wsd}Function.SoapApplication
  private com.sap.engine.lib.descriptors.ws04wsd.FunctionSoapApplication _f_FunctionSoapApplication;
  /**
   * Set method for element {http://xml.sap.com/2002/10/metamodel/wsd}Function.SoapApplication
   */
  public void setFunctionSoapApplication(com.sap.engine.lib.descriptors.ws04wsd.FunctionSoapApplication _FunctionSoapApplication) {
    this._f_FunctionSoapApplication = _FunctionSoapApplication;
  }
  /**
   * Get method for element {http://xml.sap.com/2002/10/metamodel/wsd}Function.SoapApplication
   */
  public com.sap.engine.lib.descriptors.ws04wsd.FunctionSoapApplication getFunctionSoapApplication() {
    return this._f_FunctionSoapApplication;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof FunctionState)) return false;
    FunctionState typed = (FunctionState) object;
    if (this._a_Name != null) {
      if (typed._a_Name == null) return false;
      if (!this._a_Name.equals(typed._a_Name)) return false;
    } else {
      if (typed._a_Name != null) return false;
    }
    if (this._f_FunctionSoapApplication != null) {
      if (typed._f_FunctionSoapApplication == null) return false;
      if (!this._f_FunctionSoapApplication.equals(typed._f_FunctionSoapApplication)) return false;
    } else {
      if (typed._f_FunctionSoapApplication != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_Name != null) {
      result+= this._a_Name.hashCode();
    }
    if (this._f_FunctionSoapApplication != null) {
      result+= this._f_FunctionSoapApplication.hashCode();
    }
    return result;
  }
}
