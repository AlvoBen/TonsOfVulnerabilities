﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:00:46 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.web;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}auth-constraintType
 */
public  class AuthConstraintType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/j2ee}description
  private java.util.ArrayList _f_Description = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}description
   */
  public void setDescription(com.sap.engine.lib.descriptors.j2ee.DescriptionType[] _Description) {
    this._f_Description.clear();
    if (_Description != null) {
      for (int i=0; i<_Description.length; i++) {
        if (_Description[i] != null)
          this._f_Description.add(_Description[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}description
   */
  public com.sap.engine.lib.descriptors.j2ee.DescriptionType[] getDescription() {
    com.sap.engine.lib.descriptors.j2ee.DescriptionType[] result = new com.sap.engine.lib.descriptors.j2ee.DescriptionType[_f_Description.size()];
    _f_Description.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}role-name
  private java.util.ArrayList _f_RoleName = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}role-name
   */
  public void setRoleName(com.sap.engine.lib.descriptors.j2ee.RoleNameType[] _RoleName) {
    this._f_RoleName.clear();
    if (_RoleName != null) {
      for (int i=0; i<_RoleName.length; i++) {
        if (_RoleName[i] != null)
          this._f_RoleName.add(_RoleName[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}role-name
   */
  public com.sap.engine.lib.descriptors.j2ee.RoleNameType[] getRoleName() {
    com.sap.engine.lib.descriptors.j2ee.RoleNameType[] result = new com.sap.engine.lib.descriptors.j2ee.RoleNameType[_f_RoleName.size()];
    _f_RoleName.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof AuthConstraintType)) return false;
    AuthConstraintType typed = (AuthConstraintType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors.j2ee.DescriptionType[] _f_Description1 = this.getDescription();
    com.sap.engine.lib.descriptors.j2ee.DescriptionType[] _f_Description2 = typed.getDescription();
    if (_f_Description1 != null) {
      if (_f_Description2 == null) return false;
      if (_f_Description1.length != _f_Description2.length) return false;
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          if (_f_Description2[i1] == null) return false;
          if (!_f_Description1[i1].equals(_f_Description2[i1])) return false;
        } else {
          if (_f_Description2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Description2 != null) return false;
    }
    com.sap.engine.lib.descriptors.j2ee.RoleNameType[] _f_RoleName1 = this.getRoleName();
    com.sap.engine.lib.descriptors.j2ee.RoleNameType[] _f_RoleName2 = typed.getRoleName();
    if (_f_RoleName1 != null) {
      if (_f_RoleName2 == null) return false;
      if (_f_RoleName1.length != _f_RoleName2.length) return false;
      for (int i1 = 0; i1 < _f_RoleName1.length ; i1++) {
        if (_f_RoleName1[i1] != null) {
          if (_f_RoleName2[i1] == null) return false;
          if (!_f_RoleName1[i1].equals(_f_RoleName2[i1])) return false;
        } else {
          if (_f_RoleName2[i1] != null) return false;
        }
      }
    } else {
      if (_f_RoleName2 != null) return false;
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
    com.sap.engine.lib.descriptors.j2ee.DescriptionType[] _f_Description1 = this.getDescription();
    if (_f_Description1 != null) {
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          result+= _f_Description1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors.j2ee.RoleNameType[] _f_RoleName1 = this.getRoleName();
    if (_f_RoleName1 != null) {
      for (int i1 = 0; i1 < _f_RoleName1.length ; i1++) {
        if (_f_RoleName1[i1] != null) {
          result+= _f_RoleName1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
