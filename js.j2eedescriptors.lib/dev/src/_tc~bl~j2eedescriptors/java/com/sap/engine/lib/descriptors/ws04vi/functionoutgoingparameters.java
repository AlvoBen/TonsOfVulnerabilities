﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 11:42:02 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04vi;

/**
 * Schema complexType Java representation.
 * Represents type of namespace {http://xml.sap.com/2002/10/metamodel/vi} anonymous with xpath [/xsd:schema/xsd:complexType[16]/xsd:complexContent/xsd:extension/xsd:sequence/xsd:element[3]/xsd:complexType]
 */
public  class FunctionOutgoingParameters implements java.io.Serializable,java.lang.Cloneable {

  // Element field for element {http://xml.sap.com/2002/10/metamodel/vi}Parameter
  private java.util.ArrayList _f_Parameter = new java.util.ArrayList();
  /**
   * Set method for element {http://xml.sap.com/2002/10/metamodel/vi}Parameter
   */
  public void setParameter(com.sap.engine.lib.descriptors.ws04vi.ParameterState[] _Parameter) {
    this._f_Parameter.clear();
    if (_Parameter != null) {
      for (int i=0; i<_Parameter.length; i++) {
        if (_Parameter[i] != null)
          this._f_Parameter.add(_Parameter[i]);
      }
    }
  }
  /**
   * Get method for element {http://xml.sap.com/2002/10/metamodel/vi}Parameter
   */
  public com.sap.engine.lib.descriptors.ws04vi.ParameterState[] getParameter() {
    com.sap.engine.lib.descriptors.ws04vi.ParameterState[] result = new com.sap.engine.lib.descriptors.ws04vi.ParameterState[_f_Parameter.size()];
    _f_Parameter.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof FunctionOutgoingParameters)) return false;
    FunctionOutgoingParameters typed = (FunctionOutgoingParameters) object;
    com.sap.engine.lib.descriptors.ws04vi.ParameterState[] _f_Parameter1 = this.getParameter();
    com.sap.engine.lib.descriptors.ws04vi.ParameterState[] _f_Parameter2 = typed.getParameter();
    if (_f_Parameter1 != null) {
      if (_f_Parameter2 == null) return false;
      if (_f_Parameter1.length != _f_Parameter2.length) return false;
      for (int i1 = 0; i1 < _f_Parameter1.length ; i1++) {
        if (_f_Parameter1[i1] != null) {
          if (_f_Parameter2[i1] == null) return false;
          if (!_f_Parameter1[i1].equals(_f_Parameter2[i1])) return false;
        } else {
          if (_f_Parameter2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Parameter2 != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    com.sap.engine.lib.descriptors.ws04vi.ParameterState[] _f_Parameter1 = this.getParameter();
    if (_f_Parameter1 != null) {
      for (int i1 = 0; i1 < _f_Parameter1.length ; i1++) {
        if (_f_Parameter1[i1] != null) {
          result+= _f_Parameter1[i1].hashCode();
        }
      }
    }
    return result;
  }
}
