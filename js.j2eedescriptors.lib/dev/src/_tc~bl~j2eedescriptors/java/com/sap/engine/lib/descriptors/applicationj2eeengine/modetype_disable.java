﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Tue Jul 19 13:29:43 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.applicationj2eeengine;

/**
 * Enumeration Java representation.
 * Represents type {}ModeType_disable
 */
public class ModeType_disable implements java.io.Serializable {

  public static final java.lang.String _disable = "disable";

  public static final ModeType_disable disable = new ModeType_disable(_disable);

  //  Enumeration Content
  protected java.lang.String _value;

  public ModeType_disable(java.lang.String _value) {
    if (_disable.equals(_value)) {
      this._value = _value;
      return;
    }
    throw new IllegalArgumentException("Invalid Enumeration value ["+_value+"] passed.");
  }

  public java.lang.String getValue() {
    return _value;
  }

  public static ModeType_disable fromValue(java.lang.String value) {
    if (_disable.equals(value)) {
      return disable;
    }
    throw new IllegalArgumentException("Invalid Enumeration value ["+value+"] passed.");
  }

  public static ModeType_disable fromString(String value) {
    if ("disable".equals(value)) {
      return disable;
    }
    throw new IllegalArgumentException("Invalid Enumeration value ["+value+"] passed.");
  }

  public java.lang.String toString() {
    if (_disable.equals(_value)) {
      return "disable";
    }
    return java.lang.String.valueOf(_value);
  }

  public boolean equals(java.lang.Object obj) {
    if (obj != null) {
      if (obj instanceof ModeType_disable) {
        if (_value.equals(((ModeType_disable)obj)._value)) {
          return true;
        }
      }
    }
    return false;
  }

  public int hashCode() {
    return this._value.hashCode();
  }

}
