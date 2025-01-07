﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Fri Apr 22 10:18:13 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.jaxrpcmapping;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}wsdl-return-value-mappingType
 */
public  class WsdlReturnValueMappingType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/j2ee}method-return-value
  private com.sap.engine.lib.descriptors.j2ee.FullyQualifiedClassType _f_MethodReturnValue;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}method-return-value
   */
  public void setMethodReturnValue(com.sap.engine.lib.descriptors.j2ee.FullyQualifiedClassType _MethodReturnValue) {
    this._f_MethodReturnValue = _MethodReturnValue;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}method-return-value
   */
  public com.sap.engine.lib.descriptors.j2ee.FullyQualifiedClassType getMethodReturnValue() {
    return this._f_MethodReturnValue;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}wsdl-message
  private com.sap.engine.lib.descriptors.jaxrpcmapping.WsdlMessageType _f_WsdlMessage;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}wsdl-message
   */
  public void setWsdlMessage(com.sap.engine.lib.descriptors.jaxrpcmapping.WsdlMessageType _WsdlMessage) {
    this._f_WsdlMessage = _WsdlMessage;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}wsdl-message
   */
  public com.sap.engine.lib.descriptors.jaxrpcmapping.WsdlMessageType getWsdlMessage() {
    return this._f_WsdlMessage;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}wsdl-message-part-name
  private com.sap.engine.lib.descriptors.jaxrpcmapping.WsdlMessagePartNameType _f_WsdlMessagePartName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}wsdl-message-part-name
   */
  public void setWsdlMessagePartName(com.sap.engine.lib.descriptors.jaxrpcmapping.WsdlMessagePartNameType _WsdlMessagePartName) {
    this._f_WsdlMessagePartName = _WsdlMessagePartName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}wsdl-message-part-name
   */
  public com.sap.engine.lib.descriptors.jaxrpcmapping.WsdlMessagePartNameType getWsdlMessagePartName() {
    return this._f_WsdlMessagePartName;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof WsdlReturnValueMappingType)) return false;
    WsdlReturnValueMappingType typed = (WsdlReturnValueMappingType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    if (this._f_MethodReturnValue != null) {
      if (typed._f_MethodReturnValue == null) return false;
      if (!this._f_MethodReturnValue.equals(typed._f_MethodReturnValue)) return false;
    } else {
      if (typed._f_MethodReturnValue != null) return false;
    }
    if (this._f_WsdlMessage != null) {
      if (typed._f_WsdlMessage == null) return false;
      if (!this._f_WsdlMessage.equals(typed._f_WsdlMessage)) return false;
    } else {
      if (typed._f_WsdlMessage != null) return false;
    }
    if (this._f_WsdlMessagePartName != null) {
      if (typed._f_WsdlMessagePartName == null) return false;
      if (!this._f_WsdlMessagePartName.equals(typed._f_WsdlMessagePartName)) return false;
    } else {
      if (typed._f_WsdlMessagePartName != null) return false;
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
    if (this._f_MethodReturnValue != null) {
      result+= this._f_MethodReturnValue.hashCode();
    }
    if (this._f_WsdlMessage != null) {
      result+= this._f_WsdlMessage.hashCode();
    }
    if (this._f_WsdlMessagePartName != null) {
      result+= this._f_WsdlMessagePartName.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
