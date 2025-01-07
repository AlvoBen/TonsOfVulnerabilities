﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jun 18 09:31:26 EEST 2007
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.jmsconnector;

/**
 * Schema complexType Java representation.
 * Represents type {}external-destination-typeType
 */
public  class ExternalDestinationTypeType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Element field for element {}link-connection-factory
  private java.lang.String _f_LinkConnectionFactory;
  /**
   * Set method for element {}link-connection-factory
   */
  public void setLinkConnectionFactory(java.lang.String _LinkConnectionFactory) {
    this._f_LinkConnectionFactory = _LinkConnectionFactory;
  }
  /**
   * Get method for element {}link-connection-factory
   */
  public java.lang.String getLinkConnectionFactory() {
    return this._f_LinkConnectionFactory;
  }

  // Element field for element {}link-destination
  private java.lang.String _f_LinkDestination;
  /**
   * Set method for element {}link-destination
   */
  public void setLinkDestination(java.lang.String _LinkDestination) {
    this._f_LinkDestination = _LinkDestination;
  }
  /**
   * Get method for element {}link-destination
   */
  public java.lang.String getLinkDestination() {
    return this._f_LinkDestination;
  }

  // Element field for element {}user-name
  private java.lang.String _f_UserName;
  /**
   * Set method for element {}user-name
   */
  public void setUserName(java.lang.String _UserName) {
    this._f_UserName = _UserName;
  }
  /**
   * Get method for element {}user-name
   */
  public java.lang.String getUserName() {
    return this._f_UserName;
  }

  // Element field for element {}password
  private java.lang.String _f_Password;
  /**
   * Set method for element {}password
   */
  public void setPassword(java.lang.String _Password) {
    this._f_Password = _Password;
  }
  /**
   * Get method for element {}password
   */
  public java.lang.String getPassword() {
    return this._f_Password;
  }

  // Element field for element {}property
  private java.util.ArrayList _f_Property = new java.util.ArrayList();
  /**
   * Set method for element {}property
   */
  public void setProperty(com.sap.engine.lib.descriptors.jmsconnector.PropertyType[] _Property) {
    this._f_Property.clear();
    if (_Property != null) {
      for (int i=0; i<_Property.length; i++) {
        if (_Property[i] != null)
          this._f_Property.add(_Property[i]);
      }
    }
  }
  /**
   * Get method for element {}property
   */
  public com.sap.engine.lib.descriptors.jmsconnector.PropertyType[] getProperty() {
    com.sap.engine.lib.descriptors.jmsconnector.PropertyType[] result = new com.sap.engine.lib.descriptors.jmsconnector.PropertyType[_f_Property.size()];
    _f_Property.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ExternalDestinationTypeType)) return false;
    ExternalDestinationTypeType typed = (ExternalDestinationTypeType) object;
    if (this._f_LinkConnectionFactory != null) {
      if (typed._f_LinkConnectionFactory == null) return false;
      if (!this._f_LinkConnectionFactory.equals(typed._f_LinkConnectionFactory)) return false;
    } else {
      if (typed._f_LinkConnectionFactory != null) return false;
    }
    if (this._f_LinkDestination != null) {
      if (typed._f_LinkDestination == null) return false;
      if (!this._f_LinkDestination.equals(typed._f_LinkDestination)) return false;
    } else {
      if (typed._f_LinkDestination != null) return false;
    }
    if (this._f_UserName != null) {
      if (typed._f_UserName == null) return false;
      if (!this._f_UserName.equals(typed._f_UserName)) return false;
    } else {
      if (typed._f_UserName != null) return false;
    }
    if (this._f_Password != null) {
      if (typed._f_Password == null) return false;
      if (!this._f_Password.equals(typed._f_Password)) return false;
    } else {
      if (typed._f_Password != null) return false;
    }
    com.sap.engine.lib.descriptors.jmsconnector.PropertyType[] _f_Property1 = this.getProperty();
    com.sap.engine.lib.descriptors.jmsconnector.PropertyType[] _f_Property2 = typed.getProperty();
    if (_f_Property1 != null) {
      if (_f_Property2 == null) return false;
      if (_f_Property1.length != _f_Property2.length) return false;
      for (int i1 = 0; i1 < _f_Property1.length ; i1++) {
        if (_f_Property1[i1] != null) {
          if (_f_Property2[i1] == null) return false;
          if (!_f_Property1[i1].equals(_f_Property2[i1])) return false;
        } else {
          if (_f_Property2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Property2 != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._f_LinkConnectionFactory != null) {
      result+= this._f_LinkConnectionFactory.hashCode();
    }
    if (this._f_LinkDestination != null) {
      result+= this._f_LinkDestination.hashCode();
    }
    if (this._f_UserName != null) {
      result+= this._f_UserName.hashCode();
    }
    if (this._f_Password != null) {
      result+= this._f_Password.hashCode();
    }
    com.sap.engine.lib.descriptors.jmsconnector.PropertyType[] _f_Property1 = this.getProperty();
    if (_f_Property1 != null) {
      for (int i1 = 0; i1 < _f_Property1.length ; i1++) {
        if (_f_Property1[i1] != null) {
          result+= _f_Property1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
