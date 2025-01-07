﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 09 16:24:15 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.javaee;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}ejb-local-refType
 */
public  class EjbLocalRefType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}description
  private java.util.ArrayList _f_Description = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}description
   */
  public void setDescription(com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _Description) {
    this._f_Description.clear();
    if (_Description != null) {
      for (int i=0; i<_Description.length; i++) {
        if (_Description[i] != null)
          this._f_Description.add(_Description[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}description
   */
  public com.sap.engine.lib.descriptors5.javaee.DescriptionType[] getDescription() {
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] result = new com.sap.engine.lib.descriptors5.javaee.DescriptionType[_f_Description.size()];
    _f_Description.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}ejb-ref-name
  private com.sap.engine.lib.descriptors5.javaee.EjbRefNameType _f_EjbRefName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}ejb-ref-name
   */
  public void setEjbRefName(com.sap.engine.lib.descriptors5.javaee.EjbRefNameType _EjbRefName) {
    this._f_EjbRefName = _EjbRefName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}ejb-ref-name
   */
  public com.sap.engine.lib.descriptors5.javaee.EjbRefNameType getEjbRefName() {
    return this._f_EjbRefName;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}ejb-ref-type
  private com.sap.engine.lib.descriptors5.javaee.EjbRefTypeType _f_EjbRefType;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}ejb-ref-type
   */
  public void setEjbRefType(com.sap.engine.lib.descriptors5.javaee.EjbRefTypeType _EjbRefType) {
    this._f_EjbRefType = _EjbRefType;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}ejb-ref-type
   */
  public com.sap.engine.lib.descriptors5.javaee.EjbRefTypeType getEjbRefType() {
    return this._f_EjbRefType;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}local-home
  private com.sap.engine.lib.descriptors5.javaee.LocalHomeType _f_LocalHome;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}local-home
   */
  public void setLocalHome(com.sap.engine.lib.descriptors5.javaee.LocalHomeType _LocalHome) {
    this._f_LocalHome = _LocalHome;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}local-home
   */
  public com.sap.engine.lib.descriptors5.javaee.LocalHomeType getLocalHome() {
    return this._f_LocalHome;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}local
  private com.sap.engine.lib.descriptors5.javaee.LocalType _f_Local;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}local
   */
  public void setLocal(com.sap.engine.lib.descriptors5.javaee.LocalType _Local) {
    this._f_Local = _Local;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}local
   */
  public com.sap.engine.lib.descriptors5.javaee.LocalType getLocal() {
    return this._f_Local;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}ejb-link
  private com.sap.engine.lib.descriptors5.javaee.EjbLinkType _f_EjbLink;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}ejb-link
   */
  public void setEjbLink(com.sap.engine.lib.descriptors5.javaee.EjbLinkType _EjbLink) {
    this._f_EjbLink = _EjbLink;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}ejb-link
   */
  public com.sap.engine.lib.descriptors5.javaee.EjbLinkType getEjbLink() {
    return this._f_EjbLink;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}mapped-name
  private com.sap.engine.lib.descriptors5.javaee.XsdStringType _f_MappedName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}mapped-name
   */
  public void setMappedName(com.sap.engine.lib.descriptors5.javaee.XsdStringType _MappedName) {
    this._f_MappedName = _MappedName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}mapped-name
   */
  public com.sap.engine.lib.descriptors5.javaee.XsdStringType getMappedName() {
    return this._f_MappedName;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}injection-target
  private java.util.ArrayList _f_InjectionTarget = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}injection-target
   */
  public void setInjectionTarget(com.sap.engine.lib.descriptors5.javaee.InjectionTargetType[] _InjectionTarget) {
    this._f_InjectionTarget.clear();
    if (_InjectionTarget != null) {
      for (int i=0; i<_InjectionTarget.length; i++) {
        if (_InjectionTarget[i] != null)
          this._f_InjectionTarget.add(_InjectionTarget[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}injection-target
   */
  public com.sap.engine.lib.descriptors5.javaee.InjectionTargetType[] getInjectionTarget() {
    com.sap.engine.lib.descriptors5.javaee.InjectionTargetType[] result = new com.sap.engine.lib.descriptors5.javaee.InjectionTargetType[_f_InjectionTarget.size()];
    _f_InjectionTarget.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof EjbLocalRefType)) return false;
    EjbLocalRefType typed = (EjbLocalRefType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _f_Description1 = this.getDescription();
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _f_Description2 = typed.getDescription();
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
    if (this._f_LocalHome != null) {
      if (typed._f_LocalHome == null) return false;
      if (!this._f_LocalHome.equals(typed._f_LocalHome)) return false;
    } else {
      if (typed._f_LocalHome != null) return false;
    }
    if (this._f_Local != null) {
      if (typed._f_Local == null) return false;
      if (!this._f_Local.equals(typed._f_Local)) return false;
    } else {
      if (typed._f_Local != null) return false;
    }
    if (this._f_EjbLink != null) {
      if (typed._f_EjbLink == null) return false;
      if (!this._f_EjbLink.equals(typed._f_EjbLink)) return false;
    } else {
      if (typed._f_EjbLink != null) return false;
    }
    if (this._f_MappedName != null) {
      if (typed._f_MappedName == null) return false;
      if (!this._f_MappedName.equals(typed._f_MappedName)) return false;
    } else {
      if (typed._f_MappedName != null) return false;
    }
    com.sap.engine.lib.descriptors5.javaee.InjectionTargetType[] _f_InjectionTarget1 = this.getInjectionTarget();
    com.sap.engine.lib.descriptors5.javaee.InjectionTargetType[] _f_InjectionTarget2 = typed.getInjectionTarget();
    if (_f_InjectionTarget1 != null) {
      if (_f_InjectionTarget2 == null) return false;
      if (_f_InjectionTarget1.length != _f_InjectionTarget2.length) return false;
      for (int i1 = 0; i1 < _f_InjectionTarget1.length ; i1++) {
        if (_f_InjectionTarget1[i1] != null) {
          if (_f_InjectionTarget2[i1] == null) return false;
          if (!_f_InjectionTarget1[i1].equals(_f_InjectionTarget2[i1])) return false;
        } else {
          if (_f_InjectionTarget2[i1] != null) return false;
        }
      }
    } else {
      if (_f_InjectionTarget2 != null) return false;
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
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _f_Description1 = this.getDescription();
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
    if (this._f_LocalHome != null) {
      result+= this._f_LocalHome.hashCode();
    }
    if (this._f_Local != null) {
      result+= this._f_Local.hashCode();
    }
    if (this._f_EjbLink != null) {
      result+= this._f_EjbLink.hashCode();
    }
    if (this._f_MappedName != null) {
      result+= this._f_MappedName.hashCode();
    }
    com.sap.engine.lib.descriptors5.javaee.InjectionTargetType[] _f_InjectionTarget1 = this.getInjectionTarget();
    if (_f_InjectionTarget1 != null) {
      for (int i1 = 0; i1 < _f_InjectionTarget1.length ; i1++) {
        if (_f_InjectionTarget1[i1] != null) {
          result+= _f_InjectionTarget1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
