﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jun 26 13:55:46 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webservices;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}handler-chainsType
 */
public  class HandlerChainsType implements java.io.Serializable {

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}handler-chain
  private java.util.ArrayList _f_HandlerChain = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}handler-chain
   */
  public void setHandlerChain(com.sap.engine.lib.descriptors5.webservices.HandlerChainType[] _HandlerChain) {
    this._f_HandlerChain.clear();
    if (_HandlerChain != null) {
      for (int i=0; i<_HandlerChain.length; i++) {
        if (_HandlerChain[i] != null)
          this._f_HandlerChain.add(_HandlerChain[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}handler-chain
   */
  public com.sap.engine.lib.descriptors5.webservices.HandlerChainType[] getHandlerChain() {
    com.sap.engine.lib.descriptors5.webservices.HandlerChainType[] result = new com.sap.engine.lib.descriptors5.webservices.HandlerChainType[_f_HandlerChain.size()];
    _f_HandlerChain.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof HandlerChainsType)) return false;
    HandlerChainsType typed = (HandlerChainsType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors5.webservices.HandlerChainType[] _f_HandlerChain1 = this.getHandlerChain();
    com.sap.engine.lib.descriptors5.webservices.HandlerChainType[] _f_HandlerChain2 = typed.getHandlerChain();
    if (_f_HandlerChain1 != null) {
      if (_f_HandlerChain2 == null) return false;
      if (_f_HandlerChain1.length != _f_HandlerChain2.length) return false;
      for (int i1 = 0; i1 < _f_HandlerChain1.length ; i1++) {
        if (_f_HandlerChain1[i1] != null) {
          if (_f_HandlerChain2[i1] == null) return false;
          if (!_f_HandlerChain1[i1].equals(_f_HandlerChain2[i1])) return false;
        } else {
          if (_f_HandlerChain2[i1] != null) return false;
        }
      }
    } else {
      if (_f_HandlerChain2 != null) return false;
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
    com.sap.engine.lib.descriptors5.webservices.HandlerChainType[] _f_HandlerChain1 = this.getHandlerChain();
    if (_f_HandlerChain1 != null) {
      for (int i1 = 0; i1 < _f_HandlerChain1.length ; i1++) {
        if (_f_HandlerChain1[i1] != null) {
          result+= _f_HandlerChain1[i1].hashCode();
        }
      }
    }
    return result;
  }
}
