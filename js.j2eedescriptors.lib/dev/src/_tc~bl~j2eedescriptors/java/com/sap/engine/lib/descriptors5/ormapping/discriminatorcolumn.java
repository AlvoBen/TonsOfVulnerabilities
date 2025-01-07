﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 12 11:18:44 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ormapping;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/persistence/orm}discriminator-column
 */
public  class DiscriminatorColumn implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Attribute field for attribute {}discriminator-type
  private java.lang.String _a_DiscriminatorType;
  /**
   * Set method for attribute {}discriminator-type
   */
  public void setDiscriminatorType(java.lang.String _DiscriminatorType) {
    this._a_DiscriminatorType = _DiscriminatorType;
  }
  /**
   * Get method for attribute {}discriminator-type
   */
  public java.lang.String getDiscriminatorType() {
    return _a_DiscriminatorType;
  }

  // Attribute field for attribute {}column-definition
  private java.lang.String _a_ColumnDefinition;
  /**
   * Set method for attribute {}column-definition
   */
  public void setColumnDefinition(java.lang.String _ColumnDefinition) {
    this._a_ColumnDefinition = _ColumnDefinition;
  }
  /**
   * Get method for attribute {}column-definition
   */
  public java.lang.String getColumnDefinition() {
    return _a_ColumnDefinition;
  }

  // Attribute field for attribute {}length
  private java.lang.Integer _a_Length;
  /**
   * Set method for attribute {}length
   */
  public void setLength(java.lang.Integer _Length) {
    this._a_Length = _Length;
  }
  /**
   * Get method for attribute {}length
   */
  public java.lang.Integer getLength() {
    return _a_Length;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof DiscriminatorColumn)) return false;
    DiscriminatorColumn typed = (DiscriminatorColumn) object;
    if (this._a_Name != null) {
      if (typed._a_Name == null) return false;
      if (!this._a_Name.equals(typed._a_Name)) return false;
    } else {
      if (typed._a_Name != null) return false;
    }
    if (this._a_DiscriminatorType != null) {
      if (typed._a_DiscriminatorType == null) return false;
      if (!this._a_DiscriminatorType.equals(typed._a_DiscriminatorType)) return false;
    } else {
      if (typed._a_DiscriminatorType != null) return false;
    }
    if (this._a_ColumnDefinition != null) {
      if (typed._a_ColumnDefinition == null) return false;
      if (!this._a_ColumnDefinition.equals(typed._a_ColumnDefinition)) return false;
    } else {
      if (typed._a_ColumnDefinition != null) return false;
    }
    if (this._a_Length != null) {
      if (typed._a_Length == null) return false;
      if (!this._a_Length.equals(typed._a_Length)) return false;
    } else {
      if (typed._a_Length != null) return false;
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
    if (this._a_DiscriminatorType != null) {
      result+= this._a_DiscriminatorType.hashCode();
    }
    if (this._a_ColumnDefinition != null) {
      result+= this._a_ColumnDefinition.hashCode();
    }
    if (this._a_Length != null) {
      result+= this._a_Length.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
