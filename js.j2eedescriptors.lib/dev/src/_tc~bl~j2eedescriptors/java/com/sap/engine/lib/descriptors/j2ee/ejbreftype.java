﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 09:59:28 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.j2ee;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}ejb-refType
 */
public  class EjbRefType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/j2ee}ejb-ref-name
  private com.sap.engine.lib.descriptors.j2ee.EjbRefNameType _f_EjbRefName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}ejb-ref-name
   */
  public void setEjbRefName(com.sap.engine.lib.descriptors.j2ee.EjbRefNameType _EjbRefName) {
    this._f_EjbRefName = _EjbRefName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}ejb-ref-name
   */
  public com.sap.engine.lib.descriptors.j2ee.EjbRefNameType getEjbRefName() {
    return this._f_EjbRefName;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}ejb-ref-type
  private com.sap.engine.lib.descriptors.j2ee.EjbRefTypeType _f_EjbRefType;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}ejb-ref-type
   */
  public void setEjbRefType(com.sap.engine.lib.descriptors.j2ee.EjbRefTypeType _EjbRefType) {
    this._f_EjbRefType = _EjbRefType;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}ejb-ref-type
   */
  public com.sap.engine.lib.descriptors.j2ee.EjbRefTypeType getEjbRefType() {
    return this._f_EjbRefType;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}home
  private com.sap.engine.lib.descriptors.j2ee.HomeType _f_Home;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}home
   */
  public void setHome(com.sap.engine.lib.descriptors.j2ee.HomeType _Home) {
    this._f_Home = _Home;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}home
   */
  public com.sap.engine.lib.descriptors.j2ee.HomeType getHome() {
    return this._f_Home;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}remote
  private com.sap.engine.lib.descriptors.j2ee.RemoteType _f_Remote;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}remote
   */
  public void setRemote(com.sap.engine.lib.descriptors.j2ee.RemoteType _Remote) {
    this._f_Remote = _Remote;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}remote
   */
  public com.sap.engine.lib.descriptors.j2ee.RemoteType getRemote() {
    return this._f_Remote;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}ejb-link
  private com.sap.engine.lib.descriptors.j2ee.EjbLinkType _f_EjbLink;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}ejb-link
   */
  public void setEjbLink(com.sap.engine.lib.descriptors.j2ee.EjbLinkType _EjbLink) {
    this._f_EjbLink = _EjbLink;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}ejb-link
   */
  public com.sap.engine.lib.descriptors.j2ee.EjbLinkType getEjbLink() {
    return this._f_EjbLink;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof EjbRefType)) return false;
    EjbRefType typed = (EjbRefType) object;
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
    if (this._f_EjbRefName != null) {
      if (typed._f_EjbRefName == null) return false;
      if (!this._f_EjbRefName.equals(typed._f_EjbRefName)) return false;
    } else {
      if (typed._f_EjbRefName != null) return false;
    }
    if (this._f_EjbRefType != null) {
      if (typed._f_EjbRefType == null) return false;
      if (!this._f_EjbRefType.equals(typed._f_EjbRefType)) return false;
    } else {
      if (typed._f_EjbRefType != null) return false;
    }
    if (this._f_Home != null) {
      if (typed._f_Home == null) return false;
      if (!this._f_Home.equals(typed._f_Home)) return false;
    } else {
      if (typed._f_Home != null) return false;
    }
    if (this._f_Remote != null) {
      if (typed._f_Remote == null) return false;
      if (!this._f_Remote.equals(typed._f_Remote)) return false;
    } else {
      if (typed._f_Remote != null) return false;
    }
    if (this._f_EjbLink != null) {
      if (typed._f_EjbLink == null) return false;
      if (!this._f_EjbLink.equals(typed._f_EjbLink)) return false;
    } else {
      if (typed._f_EjbLink != null) return false;
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
    if (this._f_EjbRefName != null) {
      result+= this._f_EjbRefName.hashCode();
    }
    if (this._f_EjbRefType != null) {
      result+= this._f_EjbRefType.hashCode();
    }
    if (this._f_Home != null) {
      result+= this._f_Home.hashCode();
    }
    if (this._f_Remote != null) {
      result+= this._f_Remote.hashCode();
    }
    if (this._f_EjbLink != null) {
      result+= this._f_EjbLink.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
