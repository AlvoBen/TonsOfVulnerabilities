﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jun 26 13:55:46 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webservices;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}persistence-context-refType
 */
public  class PersistenceContextRefType implements java.io.Serializable {

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
  public void setDescription(com.sap.engine.lib.descriptors5.webservices.DescriptionType[] _Description) {
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
  public com.sap.engine.lib.descriptors5.webservices.DescriptionType[] getDescription() {
    com.sap.engine.lib.descriptors5.webservices.DescriptionType[] result = new com.sap.engine.lib.descriptors5.webservices.DescriptionType[_f_Description.size()];
    _f_Description.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}persistence-context-ref-name
  private com.sap.engine.lib.descriptors5.webservices.JndiNameType _f_PersistenceContextRefName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}persistence-context-ref-name
   */
  public void setPersistenceContextRefName(com.sap.engine.lib.descriptors5.webservices.JndiNameType _PersistenceContextRefName) {
    this._f_PersistenceContextRefName = _PersistenceContextRefName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}persistence-context-ref-name
   */
  public com.sap.engine.lib.descriptors5.webservices.JndiNameType getPersistenceContextRefName() {
    return this._f_PersistenceContextRefName;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}persistence-unit-name
  private com.sap.engine.lib.descriptors5.webservices.String _f_PersistenceUnitName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}persistence-unit-name
   */
  public void setPersistenceUnitName(com.sap.engine.lib.descriptors5.webservices.String _PersistenceUnitName) {
    this._f_PersistenceUnitName = _PersistenceUnitName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}persistence-unit-name
   */
  public com.sap.engine.lib.descriptors5.webservices.String getPersistenceUnitName() {
    return this._f_PersistenceUnitName;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}persistence-context-type
  private com.sap.engine.lib.descriptors5.webservices.PersistenceContextTypeType _f_PersistenceContextType;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}persistence-context-type
   */
  public void setPersistenceContextType(com.sap.engine.lib.descriptors5.webservices.PersistenceContextTypeType _PersistenceContextType) {
    this._f_PersistenceContextType = _PersistenceContextType;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}persistence-context-type
   */
  public com.sap.engine.lib.descriptors5.webservices.PersistenceContextTypeType getPersistenceContextType() {
    return this._f_PersistenceContextType;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}persistence-property
  private java.util.ArrayList _f_PersistenceProperty = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}persistence-property
   */
  public void setPersistenceProperty(com.sap.engine.lib.descriptors5.webservices.PropertyType[] _PersistenceProperty) {
    this._f_PersistenceProperty.clear();
    if (_PersistenceProperty != null) {
      for (int i=0; i<_PersistenceProperty.length; i++) {
        if (_PersistenceProperty[i] != null)
          this._f_PersistenceProperty.add(_PersistenceProperty[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}persistence-property
   */
  public com.sap.engine.lib.descriptors5.webservices.PropertyType[] getPersistenceProperty() {
    com.sap.engine.lib.descriptors5.webservices.PropertyType[] result = new com.sap.engine.lib.descriptors5.webservices.PropertyType[_f_PersistenceProperty.size()];
    _f_PersistenceProperty.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}mapped-name
  private com.sap.engine.lib.descriptors5.webservices.XsdStringType _f_MappedName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}mapped-name
   */
  public void setMappedName(com.sap.engine.lib.descriptors5.webservices.XsdStringType _MappedName) {
    this._f_MappedName = _MappedName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}mapped-name
   */
  public com.sap.engine.lib.descriptors5.webservices.XsdStringType getMappedName() {
    return this._f_MappedName;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}injection-target
  private java.util.ArrayList _f_InjectionTarget = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}injection-target
   */
  public void setInjectionTarget(com.sap.engine.lib.descriptors5.webservices.InjectionTargetType[] _InjectionTarget) {
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
  public com.sap.engine.lib.descriptors5.webservices.InjectionTargetType[] getInjectionTarget() {
    com.sap.engine.lib.descriptors5.webservices.InjectionTargetType[] result = new com.sap.engine.lib.descriptors5.webservices.InjectionTargetType[_f_InjectionTarget.size()];
    _f_InjectionTarget.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof PersistenceContextRefType)) return false;
    PersistenceContextRefType typed = (PersistenceContextRefType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors5.webservices.DescriptionType[] _f_Description1 = this.getDescription();
    com.sap.engine.lib.descriptors5.webservices.DescriptionType[] _f_Description2 = typed.getDescription();
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
    if (this._f_PersistenceContextRefName != null) {
      if (typed._f_PersistenceContextRefName == null) return false;
      if (!this._f_PersistenceContextRefName.equals(typed._f_PersistenceContextRefName)) return false;
    } else {
      if (typed._f_PersistenceContextRefName != null) return false;
    }
    if (this._f_PersistenceUnitName != null) {
      if (typed._f_PersistenceUnitName == null) return false;
      if (!this._f_PersistenceUnitName.equals(typed._f_PersistenceUnitName)) return false;
    } else {
      if (typed._f_PersistenceUnitName != null) return false;
    }
    if (this._f_PersistenceContextType != null) {
      if (typed._f_PersistenceContextType == null) return false;
      if (!this._f_PersistenceContextType.equals(typed._f_PersistenceContextType)) return false;
    } else {
      if (typed._f_PersistenceContextType != null) return false;
    }
    com.sap.engine.lib.descriptors5.webservices.PropertyType[] _f_PersistenceProperty1 = this.getPersistenceProperty();
    com.sap.engine.lib.descriptors5.webservices.PropertyType[] _f_PersistenceProperty2 = typed.getPersistenceProperty();
    if (_f_PersistenceProperty1 != null) {
      if (_f_PersistenceProperty2 == null) return false;
      if (_f_PersistenceProperty1.length != _f_PersistenceProperty2.length) return false;
      for (int i1 = 0; i1 < _f_PersistenceProperty1.length ; i1++) {
        if (_f_PersistenceProperty1[i1] != null) {
          if (_f_PersistenceProperty2[i1] == null) return false;
          if (!_f_PersistenceProperty1[i1].equals(_f_PersistenceProperty2[i1])) return false;
        } else {
          if (_f_PersistenceProperty2[i1] != null) return false;
        }
      }
    } else {
      if (_f_PersistenceProperty2 != null) return false;
    }
    if (this._f_MappedName != null) {
      if (typed._f_MappedName == null) return false;
      if (!this._f_MappedName.equals(typed._f_MappedName)) return false;
    } else {
      if (typed._f_MappedName != null) return false;
    }
    com.sap.engine.lib.descriptors5.webservices.InjectionTargetType[] _f_InjectionTarget1 = this.getInjectionTarget();
    com.sap.engine.lib.descriptors5.webservices.InjectionTargetType[] _f_InjectionTarget2 = typed.getInjectionTarget();
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
    com.sap.engine.lib.descriptors5.webservices.DescriptionType[] _f_Description1 = this.getDescription();
    if (_f_Description1 != null) {
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          result+= _f_Description1[i1].hashCode();
        }
      }
    }
    if (this._f_PersistenceContextRefName != null) {
      result+= this._f_PersistenceContextRefName.hashCode();
    }
    if (this._f_PersistenceUnitName != null) {
      result+= this._f_PersistenceUnitName.hashCode();
    }
    if (this._f_PersistenceContextType != null) {
      result+= this._f_PersistenceContextType.hashCode();
    }
    com.sap.engine.lib.descriptors5.webservices.PropertyType[] _f_PersistenceProperty1 = this.getPersistenceProperty();
    if (_f_PersistenceProperty1 != null) {
      for (int i1 = 0; i1 < _f_PersistenceProperty1.length ; i1++) {
        if (_f_PersistenceProperty1[i1] != null) {
          result+= _f_PersistenceProperty1[i1].hashCode();
        }
      }
    }
    if (this._f_MappedName != null) {
      result+= this._f_MappedName.hashCode();
    }
    com.sap.engine.lib.descriptors5.webservices.InjectionTargetType[] _f_InjectionTarget1 = this.getInjectionTarget();
    if (_f_InjectionTarget1 != null) {
      for (int i1 = 0; i1 < _f_InjectionTarget1.length ; i1++) {
        if (_f_InjectionTarget1[i1] != null) {
          result+= _f_InjectionTarget1[i1].hashCode();
        }
      }
    }
    return result;
  }
}
