﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 11:42:02 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04vi;

/**
 * Schema complexType Java representation.
 * Represents type {http://xml.sap.com/2002/10/metamodel/vi}FunctionState
 */
public  class FunctionState extends com.sap.engine.lib.descriptors.ws04vi.MappableItemState implements java.io.Serializable,java.lang.Cloneable {

  // Attribute field for attribute {}nameMappedTo
  private java.lang.String _a_NameMappedTo;
  /**
   * Set method for attribute {}nameMappedTo
   */
  public void setNameMappedTo(java.lang.String _NameMappedTo) {
    this._a_NameMappedTo = _NameMappedTo;
  }
  /**
   * Get method for attribute {}nameMappedTo
   */
  public java.lang.String getNameMappedTo() {
    return _a_NameMappedTo;
  }

  // Attribute field for attribute {}technicalDocumentation
  private java.lang.String _a_TechnicalDocumentation;
  /**
   * Set method for attribute {}technicalDocumentation
   */
  public void setTechnicalDocumentation(java.lang.String _TechnicalDocumentation) {
    this._a_TechnicalDocumentation = _TechnicalDocumentation;
  }
  /**
   * Get method for attribute {}technicalDocumentation
   */
  public java.lang.String getTechnicalDocumentation() {
    return _a_TechnicalDocumentation;
  }

  // Attribute field for attribute {}name
  private java.lang.String _a_Name;
  /**
   * Set method for attribute {}name
   */
  public void setName(java.lang.String _Name) {
    this._a_Name = _Name;
  }
  /**
   * Get method for attribute {}name
   */
  public java.lang.String getName() {
    return _a_Name;
  }

  // Attribute field for attribute {}originalName
  private java.lang.String _a_OriginalName;
  /**
   * Set method for attribute {}originalName
   */
  public void setOriginalName(java.lang.String _OriginalName) {
    this._a_OriginalName = _OriginalName;
  }
  /**
   * Get method for attribute {}originalName
   */
  public java.lang.String getOriginalName() {
    return _a_OriginalName;
  }

  // Attribute field for attribute {}isExposed
  private boolean _a_IsExposed = false;
  /**
   * Set method for attribute {}isExposed
   */
  public void setIsExposed(boolean _IsExposed) {
    this._a_IsExposed = _IsExposed;
  }
  /**
   * Get method for attribute {}isExposed
   */
  public boolean isIsExposed() {
    return _a_IsExposed;
  }

  // Element field for element {http://xml.sap.com/2002/10/metamodel/vi}Function.Faults
  private com.sap.engine.lib.descriptors.ws04vi.FunctionFaults _f_FunctionFaults;
  /**
   * Set method for element {http://xml.sap.com/2002/10/metamodel/vi}Function.Faults
   */
  public void setFunctionFaults(com.sap.engine.lib.descriptors.ws04vi.FunctionFaults _FunctionFaults) {
    this._f_FunctionFaults = _FunctionFaults;
  }
  /**
   * Get method for element {http://xml.sap.com/2002/10/metamodel/vi}Function.Faults
   */
  public com.sap.engine.lib.descriptors.ws04vi.FunctionFaults getFunctionFaults() {
    return this._f_FunctionFaults;
  }

  // Element field for element {http://xml.sap.com/2002/10/metamodel/vi}Function.IncomingParameters
  private com.sap.engine.lib.descriptors.ws04vi.FunctionIncomingParameters _f_FunctionIncomingParameters;
  /**
   * Set method for element {http://xml.sap.com/2002/10/metamodel/vi}Function.IncomingParameters
   */
  public void setFunctionIncomingParameters(com.sap.engine.lib.descriptors.ws04vi.FunctionIncomingParameters _FunctionIncomingParameters) {
    this._f_FunctionIncomingParameters = _FunctionIncomingParameters;
  }
  /**
   * Get method for element {http://xml.sap.com/2002/10/metamodel/vi}Function.IncomingParameters
   */
  public com.sap.engine.lib.descriptors.ws04vi.FunctionIncomingParameters getFunctionIncomingParameters() {
    return this._f_FunctionIncomingParameters;
  }

  // Element field for element {http://xml.sap.com/2002/10/metamodel/vi}Function.OutgoingParameters
  private com.sap.engine.lib.descriptors.ws04vi.FunctionOutgoingParameters _f_FunctionOutgoingParameters;
  /**
   * Set method for element {http://xml.sap.com/2002/10/metamodel/vi}Function.OutgoingParameters
   */
  public void setFunctionOutgoingParameters(com.sap.engine.lib.descriptors.ws04vi.FunctionOutgoingParameters _FunctionOutgoingParameters) {
    this._f_FunctionOutgoingParameters = _FunctionOutgoingParameters;
  }
  /**
   * Get method for element {http://xml.sap.com/2002/10/metamodel/vi}Function.OutgoingParameters
   */
  public com.sap.engine.lib.descriptors.ws04vi.FunctionOutgoingParameters getFunctionOutgoingParameters() {
    return this._f_FunctionOutgoingParameters;
  }

  // Element field for element {http://xml.sap.com/2002/10/metamodel/vi}Function.SoapExtensionFunction
  private com.sap.engine.lib.descriptors.ws04vi.FunctionSoapExtensionFunction _f_FunctionSoapExtensionFunction;
  /**
   * Set method for element {http://xml.sap.com/2002/10/metamodel/vi}Function.SoapExtensionFunction
   */
  public void setFunctionSoapExtensionFunction(com.sap.engine.lib.descriptors.ws04vi.FunctionSoapExtensionFunction _FunctionSoapExtensionFunction) {
    this._f_FunctionSoapExtensionFunction = _FunctionSoapExtensionFunction;
  }
  /**
   * Get method for element {http://xml.sap.com/2002/10/metamodel/vi}Function.SoapExtensionFunction
   */
  public com.sap.engine.lib.descriptors.ws04vi.FunctionSoapExtensionFunction getFunctionSoapExtensionFunction() {
    return this._f_FunctionSoapExtensionFunction;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (super.equals(object) == false) return false;
    if (object == null) return false;
    if (!(object instanceof FunctionState)) return false;
    FunctionState typed = (FunctionState) object;
    if (this._a_NameMappedTo != null) {
      if (typed._a_NameMappedTo == null) return false;
      if (!this._a_NameMappedTo.equals(typed._a_NameMappedTo)) return false;
    } else {
      if (typed._a_NameMappedTo != null) return false;
    }
    if (this._a_TechnicalDocumentation != null) {
      if (typed._a_TechnicalDocumentation == null) return false;
      if (!this._a_TechnicalDocumentation.equals(typed._a_TechnicalDocumentation)) return false;
    } else {
      if (typed._a_TechnicalDocumentation != null) return false;
    }
    if (this._a_Name != null) {
      if (typed._a_Name == null) return false;
      if (!this._a_Name.equals(typed._a_Name)) return false;
    } else {
      if (typed._a_Name != null) return false;
    }
    if (this._a_OriginalName != null) {
      if (typed._a_OriginalName == null) return false;
      if (!this._a_OriginalName.equals(typed._a_OriginalName)) return false;
    } else {
      if (typed._a_OriginalName != null) return false;
    }
    if (this._a_IsExposed != typed._a_IsExposed) return false;
    if (this._f_FunctionFaults != null) {
      if (typed._f_FunctionFaults == null) return false;
      if (!this._f_FunctionFaults.equals(typed._f_FunctionFaults)) return false;
    } else {
      if (typed._f_FunctionFaults != null) return false;
    }
    if (this._f_FunctionIncomingParameters != null) {
      if (typed._f_FunctionIncomingParameters == null) return false;
      if (!this._f_FunctionIncomingParameters.equals(typed._f_FunctionIncomingParameters)) return false;
    } else {
      if (typed._f_FunctionIncomingParameters != null) return false;
    }
    if (this._f_FunctionOutgoingParameters != null) {
      if (typed._f_FunctionOutgoingParameters == null) return false;
      if (!this._f_FunctionOutgoingParameters.equals(typed._f_FunctionOutgoingParameters)) return false;
    } else {
      if (typed._f_FunctionOutgoingParameters != null) return false;
    }
    if (this._f_FunctionSoapExtensionFunction != null) {
      if (typed._f_FunctionSoapExtensionFunction == null) return false;
      if (!this._f_FunctionSoapExtensionFunction.equals(typed._f_FunctionSoapExtensionFunction)) return false;
    } else {
      if (typed._f_FunctionSoapExtensionFunction != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = super.hashCode();
    if (this._a_NameMappedTo != null) {
      result+= this._a_NameMappedTo.hashCode();
    }
    if (this._a_TechnicalDocumentation != null) {
      result+= this._a_TechnicalDocumentation.hashCode();
    }
    if (this._a_Name != null) {
      result+= this._a_Name.hashCode();
    }
    if (this._a_OriginalName != null) {
      result+= this._a_OriginalName.hashCode();
    }
    if (this._a_IsExposed) result += 1;
    if (this._f_FunctionFaults != null) {
      result+= this._f_FunctionFaults.hashCode();
    }
    if (this._f_FunctionIncomingParameters != null) {
      result+= this._f_FunctionIncomingParameters.hashCode();
    }
    if (this._f_FunctionOutgoingParameters != null) {
      result+= this._f_FunctionOutgoingParameters.hashCode();
    }
    if (this._f_FunctionSoapExtensionFunction != null) {
      result+= this._f_FunctionSoapExtensionFunction.hashCode();
    }
    return result;
  }
}
