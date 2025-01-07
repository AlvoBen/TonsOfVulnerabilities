﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jun 26 13:55:46 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webservices;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}service-ref_handlerType
 */
public  class ServiceRef_handlerType implements java.io.Serializable {

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}display-name
  private java.util.ArrayList _f_DisplayName = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}display-name
   */
  public void setDisplayName(com.sap.engine.lib.descriptors5.webservices.DisplayNameType[] _DisplayName) {
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
  public com.sap.engine.lib.descriptors5.webservices.DisplayNameType[] getDisplayName() {
    com.sap.engine.lib.descriptors5.webservices.DisplayNameType[] result = new com.sap.engine.lib.descriptors5.webservices.DisplayNameType[_f_DisplayName.size()];
    _f_DisplayName.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}icon
  private java.util.ArrayList _f_Icon = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}icon
   */
  public void setIcon(com.sap.engine.lib.descriptors5.webservices.IconType[] _Icon) {
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
  public com.sap.engine.lib.descriptors5.webservices.IconType[] getIcon() {
    com.sap.engine.lib.descriptors5.webservices.IconType[] result = new com.sap.engine.lib.descriptors5.webservices.IconType[_f_Icon.size()];
    _f_Icon.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}handler-name
  private com.sap.engine.lib.descriptors5.webservices.String _f_HandlerName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}handler-name
   */
  public void setHandlerName(com.sap.engine.lib.descriptors5.webservices.String _HandlerName) {
    this._f_HandlerName = _HandlerName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}handler-name
   */
  public com.sap.engine.lib.descriptors5.webservices.String getHandlerName() {
    return this._f_HandlerName;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}handler-class
  private com.sap.engine.lib.descriptors5.webservices.FullyQualifiedClassType _f_HandlerClass;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}handler-class
   */
  public void setHandlerClass(com.sap.engine.lib.descriptors5.webservices.FullyQualifiedClassType _HandlerClass) {
    this._f_HandlerClass = _HandlerClass;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}handler-class
   */
  public com.sap.engine.lib.descriptors5.webservices.FullyQualifiedClassType getHandlerClass() {
    return this._f_HandlerClass;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}init-param
  private java.util.ArrayList _f_InitParam = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}init-param
   */
  public void setInitParam(com.sap.engine.lib.descriptors5.webservices.ParamValueType[] _InitParam) {
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
  public com.sap.engine.lib.descriptors5.webservices.ParamValueType[] getInitParam() {
    com.sap.engine.lib.descriptors5.webservices.ParamValueType[] result = new com.sap.engine.lib.descriptors5.webservices.ParamValueType[_f_InitParam.size()];
    _f_InitParam.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}soap-header
  private java.util.ArrayList _f_SoapHeader = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}soap-header
   */
  public void setSoapHeader(com.sap.engine.lib.descriptors5.webservices.XsdQNameType[] _SoapHeader) {
    this._f_SoapHeader.clear();
    if (_SoapHeader != null) {
      for (int i=0; i<_SoapHeader.length; i++) {
        if (_SoapHeader[i] != null)
          this._f_SoapHeader.add(_SoapHeader[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}soap-header
   */
  public com.sap.engine.lib.descriptors5.webservices.XsdQNameType[] getSoapHeader() {
    com.sap.engine.lib.descriptors5.webservices.XsdQNameType[] result = new com.sap.engine.lib.descriptors5.webservices.XsdQNameType[_f_SoapHeader.size()];
    _f_SoapHeader.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}soap-role
  private java.util.ArrayList _f_SoapRole = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}soap-role
   */
  public void setSoapRole(com.sap.engine.lib.descriptors5.webservices.String[] _SoapRole) {
    this._f_SoapRole.clear();
    if (_SoapRole != null) {
      for (int i=0; i<_SoapRole.length; i++) {
        if (_SoapRole[i] != null)
          this._f_SoapRole.add(_SoapRole[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}soap-role
   */
  public com.sap.engine.lib.descriptors5.webservices.String[] getSoapRole() {
    com.sap.engine.lib.descriptors5.webservices.String[] result = new com.sap.engine.lib.descriptors5.webservices.String[_f_SoapRole.size()];
    _f_SoapRole.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}port-name
  private java.util.ArrayList _f_PortName = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}port-name
   */
  public void setPortName(com.sap.engine.lib.descriptors5.webservices.String[] _PortName) {
    this._f_PortName.clear();
    if (_PortName != null) {
      for (int i=0; i<_PortName.length; i++) {
        if (_PortName[i] != null)
          this._f_PortName.add(_PortName[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}port-name
   */
  public com.sap.engine.lib.descriptors5.webservices.String[] getPortName() {
    com.sap.engine.lib.descriptors5.webservices.String[] result = new com.sap.engine.lib.descriptors5.webservices.String[_f_PortName.size()];
    _f_PortName.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ServiceRef_handlerType)) return false;
    ServiceRef_handlerType typed = (ServiceRef_handlerType) object;
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
    com.sap.engine.lib.descriptors5.webservices.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    com.sap.engine.lib.descriptors5.webservices.DisplayNameType[] _f_DisplayName2 = typed.getDisplayName();
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
    com.sap.engine.lib.descriptors5.webservices.IconType[] _f_Icon1 = this.getIcon();
    com.sap.engine.lib.descriptors5.webservices.IconType[] _f_Icon2 = typed.getIcon();
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
    if (this._f_HandlerName != null) {
      if (typed._f_HandlerName == null) return false;
      if (!this._f_HandlerName.equals(typed._f_HandlerName)) return false;
    } else {
      if (typed._f_HandlerName != null) return false;
    }
    if (this._f_HandlerClass != null) {
      if (typed._f_HandlerClass == null) return false;
      if (!this._f_HandlerClass.equals(typed._f_HandlerClass)) return false;
    } else {
      if (typed._f_HandlerClass != null) return false;
    }
    com.sap.engine.lib.descriptors5.webservices.ParamValueType[] _f_InitParam1 = this.getInitParam();
    com.sap.engine.lib.descriptors5.webservices.ParamValueType[] _f_InitParam2 = typed.getInitParam();
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
    com.sap.engine.lib.descriptors5.webservices.XsdQNameType[] _f_SoapHeader1 = this.getSoapHeader();
    com.sap.engine.lib.descriptors5.webservices.XsdQNameType[] _f_SoapHeader2 = typed.getSoapHeader();
    if (_f_SoapHeader1 != null) {
      if (_f_SoapHeader2 == null) return false;
      if (_f_SoapHeader1.length != _f_SoapHeader2.length) return false;
      for (int i1 = 0; i1 < _f_SoapHeader1.length ; i1++) {
        if (_f_SoapHeader1[i1] != null) {
          if (_f_SoapHeader2[i1] == null) return false;
          if (!_f_SoapHeader1[i1].equals(_f_SoapHeader2[i1])) return false;
        } else {
          if (_f_SoapHeader2[i1] != null) return false;
        }
      }
    } else {
      if (_f_SoapHeader2 != null) return false;
    }
    com.sap.engine.lib.descriptors5.webservices.String[] _f_SoapRole1 = this.getSoapRole();
    com.sap.engine.lib.descriptors5.webservices.String[] _f_SoapRole2 = typed.getSoapRole();
    if (_f_SoapRole1 != null) {
      if (_f_SoapRole2 == null) return false;
      if (_f_SoapRole1.length != _f_SoapRole2.length) return false;
      for (int i1 = 0; i1 < _f_SoapRole1.length ; i1++) {
        if (_f_SoapRole1[i1] != null) {
          if (_f_SoapRole2[i1] == null) return false;
          if (!_f_SoapRole1[i1].equals(_f_SoapRole2[i1])) return false;
        } else {
          if (_f_SoapRole2[i1] != null) return false;
        }
      }
    } else {
      if (_f_SoapRole2 != null) return false;
    }
    com.sap.engine.lib.descriptors5.webservices.String[] _f_PortName1 = this.getPortName();
    com.sap.engine.lib.descriptors5.webservices.String[] _f_PortName2 = typed.getPortName();
    if (_f_PortName1 != null) {
      if (_f_PortName2 == null) return false;
      if (_f_PortName1.length != _f_PortName2.length) return false;
      for (int i1 = 0; i1 < _f_PortName1.length ; i1++) {
        if (_f_PortName1[i1] != null) {
          if (_f_PortName2[i1] == null) return false;
          if (!_f_PortName1[i1].equals(_f_PortName2[i1])) return false;
        } else {
          if (_f_PortName2[i1] != null) return false;
        }
      }
    } else {
      if (_f_PortName2 != null) return false;
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
    com.sap.engine.lib.descriptors5.webservices.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    if (_f_DisplayName1 != null) {
      for (int i1 = 0; i1 < _f_DisplayName1.length ; i1++) {
        if (_f_DisplayName1[i1] != null) {
          result+= _f_DisplayName1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.webservices.IconType[] _f_Icon1 = this.getIcon();
    if (_f_Icon1 != null) {
      for (int i1 = 0; i1 < _f_Icon1.length ; i1++) {
        if (_f_Icon1[i1] != null) {
          result+= _f_Icon1[i1].hashCode();
        }
      }
    }
    if (this._f_HandlerName != null) {
      result+= this._f_HandlerName.hashCode();
    }
    if (this._f_HandlerClass != null) {
      result+= this._f_HandlerClass.hashCode();
    }
    com.sap.engine.lib.descriptors5.webservices.ParamValueType[] _f_InitParam1 = this.getInitParam();
    if (_f_InitParam1 != null) {
      for (int i1 = 0; i1 < _f_InitParam1.length ; i1++) {
        if (_f_InitParam1[i1] != null) {
          result+= _f_InitParam1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.webservices.XsdQNameType[] _f_SoapHeader1 = this.getSoapHeader();
    if (_f_SoapHeader1 != null) {
      for (int i1 = 0; i1 < _f_SoapHeader1.length ; i1++) {
        if (_f_SoapHeader1[i1] != null) {
          result+= _f_SoapHeader1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.webservices.String[] _f_SoapRole1 = this.getSoapRole();
    if (_f_SoapRole1 != null) {
      for (int i1 = 0; i1 < _f_SoapRole1.length ; i1++) {
        if (_f_SoapRole1[i1] != null) {
          result+= _f_SoapRole1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.webservices.String[] _f_PortName1 = this.getPortName();
    if (_f_PortName1 != null) {
      for (int i1 = 0; i1 < _f_PortName1.length ; i1++) {
        if (_f_PortName1[i1] != null) {
          result+= _f_PortName1[i1].hashCode();
        }
      }
    }
    return result;
  }
}
