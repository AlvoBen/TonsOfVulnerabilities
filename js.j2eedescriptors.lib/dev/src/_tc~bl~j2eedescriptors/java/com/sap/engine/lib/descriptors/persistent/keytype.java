﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:07:41 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.persistent;

import java.io.Serializable;

/**
 * Enumeration Java representation.
 * Represents type of namespace {} anonymous with xpath [/xs:schema/xs:complexType[8]/xs:attribute/xs:simpleType]
 */
public class KeyType implements Serializable {

  public static final java.lang.String _noKey = "NoKey";
  public static final java.lang.String _primaryKey = "PrimaryKey";
  public static final java.lang.String _uniqueKey = "UniqueKey";

  public static final KeyType noKey = new KeyType(_noKey);
  public static final KeyType primaryKey = new KeyType(_primaryKey);
  public static final KeyType uniqueKey = new KeyType(_uniqueKey);

  //  Enumeration Content
  protected java.lang.String _value;

  public KeyType(java.lang.String _value) {
    if (_noKey.equals(_value)) {
      this._value = _value;
      return;
    }
    if (_primaryKey.equals(_value)) {
      this._value = _value;
      return;
    }
    if (_uniqueKey.equals(_value)) {
      this._value = _value;
      return;
    }
    throw new IllegalArgumentException("Invalid Enumeration value ["+_value+"] passed.");
  }

  public java.lang.String getValue() {
    return _value;
  }

  public static KeyType fromValue(java.lang.String value) {
    if (_noKey.equals(value)) {
      return noKey;
    }
    if (_primaryKey.equals(value)) {
      return primaryKey;
    }
    if (_uniqueKey.equals(value)) {
      return uniqueKey;
    }
    throw new IllegalArgumentException("Invalid Enumeration value ["+value+"] passed.");
  }

  public static KeyType fromString(String value) {
    if ("NoKey".equals(value)) {
      return noKey;
    }
    if ("PrimaryKey".equals(value)) {
      return primaryKey;
    }
    if ("UniqueKey".equals(value)) {
      return uniqueKey;
    }
    throw new IllegalArgumentException("Invalid Enumeration value ["+value+"] passed.");
  }

  public java.lang.String toString() {
    if (_noKey.equals(_value)) {
      return "NoKey";
    }
    if (_primaryKey.equals(_value)) {
      return "PrimaryKey";
    }
    if (_uniqueKey.equals(_value)) {
      return "UniqueKey";
    }
    return java.lang.String.valueOf(_value);
  }

  public boolean equals(java.lang.Object obj) {
    if (obj != null) {
      if (obj instanceof KeyType) {
        if (_value.equals(((KeyType)obj)._value)) {
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
