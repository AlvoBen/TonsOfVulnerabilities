﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 13:05:56 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04clientsrt;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/630/soap/java/descriptors/ws-clients-runtime-descriptor}WSClientRuntimeDescriptor
 */
public  class WSClientRuntimeDescriptor implements java.io.Serializable,java.lang.Cloneable {

  // Element field for element {}application-name
  private java.lang.String _f_ApplicationName;
  /**
   * Set method for element {}application-name
   */
  public void setApplicationName(java.lang.String _ApplicationName) {
    this._f_ApplicationName = _ApplicationName;
  }
  /**
   * Get method for element {}application-name
   */
  public java.lang.String getApplicationName() {
    return this._f_ApplicationName;
  }

  // Element field for element {}module-name
  private java.lang.String _f_ModuleName;
  /**
   * Set method for element {}module-name
   */
  public void setModuleName(java.lang.String _ModuleName) {
    this._f_ModuleName = _ModuleName;
  }
  /**
   * Get method for element {}module-name
   */
  public java.lang.String getModuleName() {
    return this._f_ModuleName;
  }

  // Element field for element {}component-descriptor
  private java.util.ArrayList _f_ComponentDescriptor = new java.util.ArrayList();
  /**
   * Set method for element {}component-descriptor
   */
  public void setComponentDescriptor(com.sap.engine.lib.descriptors.ws04clientsrt.ComponentDescriptorType[] _ComponentDescriptor) {
    this._f_ComponentDescriptor.clear();
    if (_ComponentDescriptor != null) {
      for (int i=0; i<_ComponentDescriptor.length; i++) {
        if (_ComponentDescriptor[i] != null)
          this._f_ComponentDescriptor.add(_ComponentDescriptor[i]);
      }
    }
  }
  /**
   * Get method for element {}component-descriptor
   */
  public com.sap.engine.lib.descriptors.ws04clientsrt.ComponentDescriptorType[] getComponentDescriptor() {
    com.sap.engine.lib.descriptors.ws04clientsrt.ComponentDescriptorType[] result = new com.sap.engine.lib.descriptors.ws04clientsrt.ComponentDescriptorType[_f_ComponentDescriptor.size()];
    _f_ComponentDescriptor.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof WSClientRuntimeDescriptor)) return false;
    WSClientRuntimeDescriptor typed = (WSClientRuntimeDescriptor) object;
    if (this._f_ApplicationName != null) {
      if (typed._f_ApplicationName == null) return false;
      if (!this._f_ApplicationName.equals(typed._f_ApplicationName)) return false;
    } else {
      if (typed._f_ApplicationName != null) return false;
    }
    if (this._f_ModuleName != null) {
      if (typed._f_ModuleName == null) return false;
      if (!this._f_ModuleName.equals(typed._f_ModuleName)) return false;
    } else {
      if (typed._f_ModuleName != null) return false;
    }
    com.sap.engine.lib.descriptors.ws04clientsrt.ComponentDescriptorType[] _f_ComponentDescriptor1 = this.getComponentDescriptor();
    com.sap.engine.lib.descriptors.ws04clientsrt.ComponentDescriptorType[] _f_ComponentDescriptor2 = typed.getComponentDescriptor();
    if (_f_ComponentDescriptor1 != null) {
      if (_f_ComponentDescriptor2 == null) return false;
      if (_f_ComponentDescriptor1.length != _f_ComponentDescriptor2.length) return false;
      for (int i1 = 0; i1 < _f_ComponentDescriptor1.length ; i1++) {
        if (_f_ComponentDescriptor1[i1] != null) {
          if (_f_ComponentDescriptor2[i1] == null) return false;
          if (!_f_ComponentDescriptor1[i1].equals(_f_ComponentDescriptor2[i1])) return false;
        } else {
          if (_f_ComponentDescriptor2[i1] != null) return false;
        }
      }
    } else {
      if (_f_ComponentDescriptor2 != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._f_ApplicationName != null) {
      result+= this._f_ApplicationName.hashCode();
    }
    if (this._f_ModuleName != null) {
      result+= this._f_ModuleName.hashCode();
    }
    com.sap.engine.lib.descriptors.ws04clientsrt.ComponentDescriptorType[] _f_ComponentDescriptor1 = this.getComponentDescriptor();
    if (_f_ComponentDescriptor1 != null) {
      for (int i1 = 0; i1 < _f_ComponentDescriptor1.length ; i1++) {
        if (_f_ComponentDescriptor1[i1] != null) {
          result+= _f_ComponentDescriptor1[i1].hashCode();
        }
      }
    }
    return result;
  }
}
