﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 09 16:24:16 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.web;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}filterType
 */
public  class FilterType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}display-name
  private java.util.ArrayList _f_DisplayName = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}display-name
   */
  public void setDisplayName(com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] _DisplayName) {
    this._f_DisplayName.clear();
    if (_DisplayName != null) {
      for (int i=0; i<_DisplayName.length; i++) {
        if (_DisplayName[i] != null)
          this._f_DisplayName.add(_DisplayName[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}display-name
   */
  public com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] getDisplayName() {
    com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] result = new com.sap.engine.lib.descriptors5.javaee.DisplayNameType[_f_DisplayName.size()];
    _f_DisplayName.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}icon
  private java.util.ArrayList _f_Icon = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}icon
   */
  public void setIcon(com.sap.engine.lib.descriptors5.javaee.IconType[] _Icon) {
    this._f_Icon.clear();
    if (_Icon != null) {
      for (int i=0; i<_Icon.length; i++) {
        if (_Icon[i] != null)
          this._f_Icon.add(_Icon[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}icon
   */
  public com.sap.engine.lib.descriptors5.javaee.IconType[] getIcon() {
    com.sap.engine.lib.descriptors5.javaee.IconType[] result = new com.sap.engine.lib.descriptors5.javaee.IconType[_f_Icon.size()];
    _f_Icon.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}filter-name
  private com.sap.engine.lib.descriptors5.web.FilterNameType _f_FilterName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}filter-name
   */
  public void setFilterName(com.sap.engine.lib.descriptors5.web.FilterNameType _FilterName) {
    this._f_FilterName = _FilterName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}filter-name
   */
  public com.sap.engine.lib.descriptors5.web.FilterNameType getFilterName() {
    return this._f_FilterName;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}filter-class
  private com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType _f_FilterClass;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}filter-class
   */
  public void setFilterClass(com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType _FilterClass) {
    this._f_FilterClass = _FilterClass;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}filter-class
   */
  public com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType getFilterClass() {
    return this._f_FilterClass;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}init-param
  private java.util.ArrayList _f_InitParam = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}init-param
   */
  public void setInitParam(com.sap.engine.lib.descriptors5.javaee.ParamValueType[] _InitParam) {
    this._f_InitParam.clear();
    if (_InitParam != null) {
      for (int i=0; i<_InitParam.length; i++) {
        if (_InitParam[i] != null)
          this._f_InitParam.add(_InitParam[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}init-param
   */
  public com.sap.engine.lib.descriptors5.javaee.ParamValueType[] getInitParam() {
    com.sap.engine.lib.descriptors5.javaee.ParamValueType[] result = new com.sap.engine.lib.descriptors5.javaee.ParamValueType[_f_InitParam.size()];
    _f_InitParam.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof FilterType)) return false;
    FilterType typed = (FilterType) object;
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
    com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] _f_DisplayName2 = typed.getDisplayName();
    if (_f_DisplayName1 != null) {
      if (_f_DisplayName2 == null) return false;
      if (_f_DisplayName1.length != _f_DisplayName2.length) return false;
      for (int i1 = 0; i1 < _f_DisplayName1.length ; i1++) {
        if (_f_DisplayName1[i1] != null) {
          if (_f_DisplayName2[i1] == null) return false;
          if (!_f_DisplayName1[i1].equals(_f_DisplayName2[i1])) return false;
        } else {
          if (_f_DisplayName2[i1] != null) return false;
        }
      }
    } else {
      if (_f_DisplayName2 != null) return false;
    }
    com.sap.engine.lib.descriptors5.javaee.IconType[] _f_Icon1 = this.getIcon();
    com.sap.engine.lib.descriptors5.javaee.IconType[] _f_Icon2 = typed.getIcon();
    if (_f_Icon1 != null) {
      if (_f_Icon2 == null) return false;
      if (_f_Icon1.length != _f_Icon2.length) return false;
      for (int i1 = 0; i1 < _f_Icon1.length ; i1++) {
        if (_f_Icon1[i1] != null) {
          if (_f_Icon2[i1] == null) return false;
          if (!_f_Icon1[i1].equals(_f_Icon2[i1])) return false;
        } else {
          if (_f_Icon2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Icon2 != null) return false;
    }
    if (this._f_FilterName != null) {
      if (typed._f_FilterName == null) return false;
      if (!this._f_FilterName.equals(typed._f_FilterName)) return false;
    } else {
      if (typed._f_FilterName != null) return false;
    }
    if (this._f_FilterClass != null) {
      if (typed._f_FilterClass == null) return false;
      if (!this._f_FilterClass.equals(typed._f_FilterClass)) return false;
    } else {
      if (typed._f_FilterClass != null) return false;
    }
    com.sap.engine.lib.descriptors5.javaee.ParamValueType[] _f_InitParam1 = this.getInitParam();
    com.sap.engine.lib.descriptors5.javaee.ParamValueType[] _f_InitParam2 = typed.getInitParam();
    if (_f_InitParam1 != null) {
      if (_f_InitParam2 == null) return false;
      if (_f_InitParam1.length != _f_InitParam2.length) return false;
      for (int i1 = 0; i1 < _f_InitParam1.length ; i1++) {
        if (_f_InitParam1[i1] != null) {
          if (_f_InitParam2[i1] == null) return false;
          if (!_f_InitParam1[i1].equals(_f_InitParam2[i1])) return false;
        } else {
          if (_f_InitParam2[i1] != null) return false;
        }
      }
    } else {
      if (_f_InitParam2 != null) return false;
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
    com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    if (_f_DisplayName1 != null) {
      for (int i1 = 0; i1 < _f_DisplayName1.length ; i1++) {
        if (_f_DisplayName1[i1] != null) {
          result+= _f_DisplayName1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.javaee.IconType[] _f_Icon1 = this.getIcon();
    if (_f_Icon1 != null) {
      for (int i1 = 0; i1 < _f_Icon1.length ; i1++) {
        if (_f_Icon1[i1] != null) {
          result+= _f_Icon1[i1].hashCode();
        }
      }
    }
    if (this._f_FilterName != null) {
      result+= this._f_FilterName.hashCode();
    }
    if (this._f_FilterClass != null) {
      result+= this._f_FilterClass.hashCode();
    }
    com.sap.engine.lib.descriptors5.javaee.ParamValueType[] _f_InitParam1 = this.getInitParam();
    if (_f_InitParam1 != null) {
      for (int i1 = 0; i1 < _f_InitParam1.length ; i1++) {
        if (_f_InitParam1[i1] != null) {
          result+= _f_InitParam1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
