﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 12 11:18:44 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ormapping;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/persistence/orm}named-native-query
 */
public  class NamedNativeQuery implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Attribute field for attribute {}result-class
  private java.lang.String _a_ResultClass;
  /**
   * Set method for attribute {}result-class
   */
  public void setResultClass(java.lang.String _ResultClass) {
    this._a_ResultClass = _ResultClass;
  }
  /**
   * Get method for attribute {}result-class
   */
  public java.lang.String getResultClass() {
    return _a_ResultClass;
  }

  // Attribute field for attribute {}result-set-mapping
  private java.lang.String _a_ResultSetMapping;
  /**
   * Set method for attribute {}result-set-mapping
   */
  public void setResultSetMapping(java.lang.String _ResultSetMapping) {
    this._a_ResultSetMapping = _ResultSetMapping;
  }
  /**
   * Get method for attribute {}result-set-mapping
   */
  public java.lang.String getResultSetMapping() {
    return _a_ResultSetMapping;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}query
  private java.lang.String _f_Query;
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}query
   */
  public void setQuery(java.lang.String _Query) {
    this._f_Query = _Query;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}query
   */
  public java.lang.String getQuery() {
    return this._f_Query;
  }

  // Element field for element {http://java.sun.com/xml/ns/persistence/orm}hint
  private java.util.ArrayList _f_Hint = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/persistence/orm}hint
   */
  public void setHint(com.sap.engine.lib.descriptors5.ormapping.QueryHint[] _Hint) {
    this._f_Hint.clear();
    if (_Hint != null) {
      for (int i=0; i<_Hint.length; i++) {
        if (_Hint[i] != null)
          this._f_Hint.add(_Hint[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/persistence/orm}hint
   */
  public com.sap.engine.lib.descriptors5.ormapping.QueryHint[] getHint() {
    com.sap.engine.lib.descriptors5.ormapping.QueryHint[] result = new com.sap.engine.lib.descriptors5.ormapping.QueryHint[_f_Hint.size()];
    _f_Hint.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof NamedNativeQuery)) return false;
    NamedNativeQuery typed = (NamedNativeQuery) object;
    if (this._a_Name != null) {
      if (typed._a_Name == null) return false;
      if (!this._a_Name.equals(typed._a_Name)) return false;
    } else {
      if (typed._a_Name != null) return false;
    }
    if (this._a_ResultClass != null) {
      if (typed._a_ResultClass == null) return false;
      if (!this._a_ResultClass.equals(typed._a_ResultClass)) return false;
    } else {
      if (typed._a_ResultClass != null) return false;
    }
    if (this._a_ResultSetMapping != null) {
      if (typed._a_ResultSetMapping == null) return false;
      if (!this._a_ResultSetMapping.equals(typed._a_ResultSetMapping)) return false;
    } else {
      if (typed._a_ResultSetMapping != null) return false;
    }
    if (this._f_Query != null) {
      if (typed._f_Query == null) return false;
      if (!this._f_Query.equals(typed._f_Query)) return false;
    } else {
      if (typed._f_Query != null) return false;
    }
    com.sap.engine.lib.descriptors5.ormapping.QueryHint[] _f_Hint1 = this.getHint();
    com.sap.engine.lib.descriptors5.ormapping.QueryHint[] _f_Hint2 = typed.getHint();
    if (_f_Hint1 != null) {
      if (_f_Hint2 == null) return false;
      if (_f_Hint1.length != _f_Hint2.length) return false;
      for (int i1 = 0; i1 < _f_Hint1.length ; i1++) {
        if (_f_Hint1[i1] != null) {
          if (_f_Hint2[i1] == null) return false;
          if (!_f_Hint1[i1].equals(_f_Hint2[i1])) return false;
        } else {
          if (_f_Hint2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Hint2 != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_Name != null) {
      result+= this._a_Name.hashCode();
    }
    if (this._a_ResultClass != null) {
      result+= this._a_ResultClass.hashCode();
    }
    if (this._a_ResultSetMapping != null) {
      result+= this._a_ResultSetMapping.hashCode();
    }
    if (this._f_Query != null) {
      result+= this._f_Query.hashCode();
    }
    com.sap.engine.lib.descriptors5.ormapping.QueryHint[] _f_Hint1 = this.getHint();
    if (_f_Hint1 != null) {
      for (int i1 = 0; i1 < _f_Hint1.length ; i1++) {
        if (_f_Hint1[i1] != null) {
          result+= _f_Hint1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
