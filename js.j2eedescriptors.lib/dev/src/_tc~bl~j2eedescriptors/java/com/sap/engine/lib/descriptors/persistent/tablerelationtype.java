﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:07:41 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.persistent;

/**
 * Schema complexType Java representation.
 * Represents type {}table-relationType
 */
public  class TableRelationType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Element field for element {}help-table
  private java.lang.String _f_HelpTable;
  /**
   * Set method for element {}help-table
   */
  public void setHelpTable(java.lang.String _HelpTable) {
    this._f_HelpTable = _HelpTable;
  }
  /**
   * Get method for element {}help-table
   */
  public java.lang.String getHelpTable() {
    return this._f_HelpTable;
  }

  // Element field for element {}table-relationship-role
  private com.sap.engine.lib.descriptors.persistent.TableRelationshipRoleType _f_TableRelationshipRole1;
  /**
   * Set method for element {}table-relationship-role
   */
  public void setTableRelationshipRole1(com.sap.engine.lib.descriptors.persistent.TableRelationshipRoleType _TableRelationshipRole1) {
    this._f_TableRelationshipRole1 = _TableRelationshipRole1;
  }
  /**
   * Get method for element {}table-relationship-role
   */
  public com.sap.engine.lib.descriptors.persistent.TableRelationshipRoleType getTableRelationshipRole1() {
    return this._f_TableRelationshipRole1;
  }

  // Element field for element {}table-relationship-role
  private com.sap.engine.lib.descriptors.persistent.TableRelationshipRoleType _f_TableRelationshipRole2;
  /**
   * Set method for element {}table-relationship-role
   */
  public void setTableRelationshipRole2(com.sap.engine.lib.descriptors.persistent.TableRelationshipRoleType _TableRelationshipRole2) {
    this._f_TableRelationshipRole2 = _TableRelationshipRole2;
  }
  /**
   * Get method for element {}table-relationship-role
   */
  public com.sap.engine.lib.descriptors.persistent.TableRelationshipRoleType getTableRelationshipRole2() {
    return this._f_TableRelationshipRole2;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof TableRelationType)) return false;
    TableRelationType typed = (TableRelationType) object;
    if (this._f_HelpTable != null) {
      if (typed._f_HelpTable == null) return false;
      if (!this._f_HelpTable.equals(typed._f_HelpTable)) return false;
    } else {
      if (typed._f_HelpTable != null) return false;
    }
    if (this._f_TableRelationshipRole1 != null) {
      if (typed._f_TableRelationshipRole1 == null) return false;
      if (!this._f_TableRelationshipRole1.equals(typed._f_TableRelationshipRole1)) return false;
    } else {
      if (typed._f_TableRelationshipRole1 != null) return false;
    }
    if (this._f_TableRelationshipRole2 != null) {
      if (typed._f_TableRelationshipRole2 == null) return false;
      if (!this._f_TableRelationshipRole2.equals(typed._f_TableRelationshipRole2)) return false;
    } else {
      if (typed._f_TableRelationshipRole2 != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._f_HelpTable != null) {
      result+= this._f_HelpTable.hashCode();
    }
    if (this._f_TableRelationshipRole1 != null) {
      result+= this._f_TableRelationshipRole1.hashCode();
    }
    if (this._f_TableRelationshipRole2 != null) {
      result+= this._f_TableRelationshipRole2.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
