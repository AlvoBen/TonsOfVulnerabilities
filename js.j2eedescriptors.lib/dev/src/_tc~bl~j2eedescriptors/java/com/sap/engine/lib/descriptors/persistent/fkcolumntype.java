﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:07:41 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.persistent;

/**
 * Schema complexType Java representation.
 * Represents type {}fk-columnType
 */
public  class FkColumnType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Element field for element {}column-name
  private java.lang.String _f_ColumnName;
  /**
   * Set method for element {}column-name
   */
  public void setColumnName(java.lang.String _ColumnName) {
    this._f_ColumnName = _ColumnName;
  }
  /**
   * Get method for element {}column-name
   */
  public java.lang.String getColumnName() {
    return this._f_ColumnName;
  }

  // Element field for element {}pk-field-name
  private java.lang.String _f_PkFieldName;
  /**
   * Set method for element {}pk-field-name
   */
  public void setPkFieldName(java.lang.String _PkFieldName) {
    this._f_PkFieldName = _PkFieldName;
  }
  /**
   * Get method for element {}pk-field-name
   */
  public java.lang.String getPkFieldName() {
    return this._f_PkFieldName;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof FkColumnType)) return false;
    FkColumnType typed = (FkColumnType) object;
    if (this._f_ColumnName != null) {
      if (typed._f_ColumnName == null) return false;
      if (!this._f_ColumnName.equals(typed._f_ColumnName)) return false;
    } else {
      if (typed._f_ColumnName != null) return false;
    }
    if (this._f_PkFieldName != null) {
      if (typed._f_PkFieldName == null) return false;
      if (!this._f_PkFieldName.equals(typed._f_PkFieldName)) return false;
    } else {
      if (typed._f_PkFieldName != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._f_ColumnName != null) {
      result+= this._f_ColumnName.hashCode();
    }
    if (this._f_PkFieldName != null) {
      result+= this._f_PkFieldName.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
