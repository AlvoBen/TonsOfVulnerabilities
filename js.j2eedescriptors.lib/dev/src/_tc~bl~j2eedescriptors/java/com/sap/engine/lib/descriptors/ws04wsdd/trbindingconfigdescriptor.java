﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 12:55:14 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04wsdd;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor}TrBindingConfigDescriptor
 */
public  class TrBindingConfigDescriptor implements java.io.Serializable,java.lang.Cloneable {

  // Element field for element {}general-configuration
  private com.sap.engine.lib.descriptors.ws04wsdd.PropertiesDescriptor _f_GeneralConfiguration;
  /**
   * Set method for element {}general-configuration
   */
  public void setGeneralConfiguration(com.sap.engine.lib.descriptors.ws04wsdd.PropertiesDescriptor _GeneralConfiguration) {
    this._f_GeneralConfiguration = _GeneralConfiguration;
  }
  /**
   * Get method for element {}general-configuration
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.PropertiesDescriptor getGeneralConfiguration() {
    return this._f_GeneralConfiguration;
  }

  // Element field for element {}input
  private com.sap.engine.lib.descriptors.ws04wsdd.PropertiesDescriptor _f_Input;
  /**
   * Set method for element {}input
   */
  public void setInput(com.sap.engine.lib.descriptors.ws04wsdd.PropertiesDescriptor _Input) {
    this._f_Input = _Input;
  }
  /**
   * Get method for element {}input
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.PropertiesDescriptor getInput() {
    return this._f_Input;
  }

  // Element field for element {}output
  private com.sap.engine.lib.descriptors.ws04wsdd.PropertiesDescriptor _f_Output;
  /**
   * Set method for element {}output
   */
  public void setOutput(com.sap.engine.lib.descriptors.ws04wsdd.PropertiesDescriptor _Output) {
    this._f_Output = _Output;
  }
  /**
   * Get method for element {}output
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.PropertiesDescriptor getOutput() {
    return this._f_Output;
  }

  // Element field for element {}fault
  private java.util.ArrayList _f_Fault = new java.util.ArrayList();
  /**
   * Set method for element {}fault
   */
  public void setFault(com.sap.engine.lib.descriptors.ws04wsdd.FaultConfigDescriptor[] _Fault) {
    this._f_Fault.clear();
    if (_Fault != null) {
      for (int i=0; i<_Fault.length; i++) {
        if (_Fault[i] != null)
          this._f_Fault.add(_Fault[i]);
      }
    }
  }
  /**
   * Get method for element {}fault
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.FaultConfigDescriptor[] getFault() {
    com.sap.engine.lib.descriptors.ws04wsdd.FaultConfigDescriptor[] result = new com.sap.engine.lib.descriptors.ws04wsdd.FaultConfigDescriptor[_f_Fault.size()];
    _f_Fault.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof TrBindingConfigDescriptor)) return false;
    TrBindingConfigDescriptor typed = (TrBindingConfigDescriptor) object;
    if (this._f_GeneralConfiguration != null) {
      if (typed._f_GeneralConfiguration == null) return false;
      if (!this._f_GeneralConfiguration.equals(typed._f_GeneralConfiguration)) return false;
    } else {
      if (typed._f_GeneralConfiguration != null) return false;
    }
    if (this._f_Input != null) {
      if (typed._f_Input == null) return false;
      if (!this._f_Input.equals(typed._f_Input)) return false;
    } else {
      if (typed._f_Input != null) return false;
    }
    if (this._f_Output != null) {
      if (typed._f_Output == null) return false;
      if (!this._f_Output.equals(typed._f_Output)) return false;
    } else {
      if (typed._f_Output != null) return false;
    }
    com.sap.engine.lib.descriptors.ws04wsdd.FaultConfigDescriptor[] _f_Fault1 = this.getFault();
    com.sap.engine.lib.descriptors.ws04wsdd.FaultConfigDescriptor[] _f_Fault2 = typed.getFault();
    if (_f_Fault1 != null) {
      if (_f_Fault2 == null) return false;
      if (_f_Fault1.length != _f_Fault2.length) return false;
      for (int i1 = 0; i1 < _f_Fault1.length ; i1++) {
        if (_f_Fault1[i1] != null) {
          if (_f_Fault2[i1] == null) return false;
          if (!_f_Fault1[i1].equals(_f_Fault2[i1])) return false;
        } else {
          if (_f_Fault2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Fault2 != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._f_GeneralConfiguration != null) {
      result+= this._f_GeneralConfiguration.hashCode();
    }
    if (this._f_Input != null) {
      result+= this._f_Input.hashCode();
    }
    if (this._f_Output != null) {
      result+= this._f_Output.hashCode();
    }
    com.sap.engine.lib.descriptors.ws04wsdd.FaultConfigDescriptor[] _f_Fault1 = this.getFault();
    if (_f_Fault1 != null) {
      for (int i1 = 0; i1 < _f_Fault1.length ; i1++) {
        if (_f_Fault1[i1] != null) {
          result+= _f_Fault1[i1].hashCode();
        }
      }
    }
    return result;
  }
}
