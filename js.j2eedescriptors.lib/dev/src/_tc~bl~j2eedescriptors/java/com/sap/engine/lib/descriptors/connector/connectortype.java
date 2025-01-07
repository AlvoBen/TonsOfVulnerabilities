﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:03:16 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.connector;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}connectorType
 */
public  class ConnectorType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Attribute field for attribute {}version
  private java.math.BigDecimal _a_Version;
  /**
   * Set method for attribute {}version
   */
  public void setVersion(java.math.BigDecimal _Version) {
    this._a_Version = _Version;
  }
  /**
   * Get method for attribute {}version
   */
  public java.math.BigDecimal getVersion() {
    return _a_Version;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}description
  private java.util.ArrayList _f_Description = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}description
   */
  public void setDescription(com.sap.engine.lib.descriptors.j2ee.DescriptionType[] _Description) {
    this._f_Description.clear();
    if (_Description != null) {
      for (int i=0; i<_Description.length; i++) {
        if (_Description[i] != null)
          this._f_Description.add(_Description[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}description
   */
  public com.sap.engine.lib.descriptors.j2ee.DescriptionType[] getDescription() {
    com.sap.engine.lib.descriptors.j2ee.DescriptionType[] result = new com.sap.engine.lib.descriptors.j2ee.DescriptionType[_f_Description.size()];
    _f_Description.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}display-name
  private java.util.ArrayList _f_DisplayName = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}display-name
   */
  public void setDisplayName(com.sap.engine.lib.descriptors.j2ee.DisplayNameType[] _DisplayName) {
    this._f_DisplayName.clear();
    if (_DisplayName != null) {
      for (int i=0; i<_DisplayName.length; i++) {
        if (_DisplayName[i] != null)
          this._f_DisplayName.add(_DisplayName[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}display-name
   */
  public com.sap.engine.lib.descriptors.j2ee.DisplayNameType[] getDisplayName() {
    com.sap.engine.lib.descriptors.j2ee.DisplayNameType[] result = new com.sap.engine.lib.descriptors.j2ee.DisplayNameType[_f_DisplayName.size()];
    _f_DisplayName.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}icon
  private java.util.ArrayList _f_Icon = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}icon
   */
  public void setIcon(com.sap.engine.lib.descriptors.j2ee.IconType[] _Icon) {
    this._f_Icon.clear();
    if (_Icon != null) {
      for (int i=0; i<_Icon.length; i++) {
        if (_Icon[i] != null)
          this._f_Icon.add(_Icon[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}icon
   */
  public com.sap.engine.lib.descriptors.j2ee.IconType[] getIcon() {
    com.sap.engine.lib.descriptors.j2ee.IconType[] result = new com.sap.engine.lib.descriptors.j2ee.IconType[_f_Icon.size()];
    _f_Icon.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}vendor-name
  private com.sap.engine.lib.descriptors.j2ee.XsdStringType _f_VendorName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}vendor-name
   */
  public void setVendorName(com.sap.engine.lib.descriptors.j2ee.XsdStringType _VendorName) {
    this._f_VendorName = _VendorName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}vendor-name
   */
  public com.sap.engine.lib.descriptors.j2ee.XsdStringType getVendorName() {
    return this._f_VendorName;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}eis-type
  private com.sap.engine.lib.descriptors.j2ee.XsdStringType _f_EisType;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}eis-type
   */
  public void setEisType(com.sap.engine.lib.descriptors.j2ee.XsdStringType _EisType) {
    this._f_EisType = _EisType;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}eis-type
   */
  public com.sap.engine.lib.descriptors.j2ee.XsdStringType getEisType() {
    return this._f_EisType;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}resourceadapter-version
  private com.sap.engine.lib.descriptors.j2ee.XsdStringType _f_ResourceadapterVersion;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}resourceadapter-version
   */
  public void setResourceadapterVersion(com.sap.engine.lib.descriptors.j2ee.XsdStringType _ResourceadapterVersion) {
    this._f_ResourceadapterVersion = _ResourceadapterVersion;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}resourceadapter-version
   */
  public com.sap.engine.lib.descriptors.j2ee.XsdStringType getResourceadapterVersion() {
    return this._f_ResourceadapterVersion;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}license
  private com.sap.engine.lib.descriptors.connector.LicenseType _f_License;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}license
   */
  public void setLicense(com.sap.engine.lib.descriptors.connector.LicenseType _License) {
    this._f_License = _License;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}license
   */
  public com.sap.engine.lib.descriptors.connector.LicenseType getLicense() {
    return this._f_License;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}resourceadapter
  private com.sap.engine.lib.descriptors.connector.ResourceadapterType _f_Resourceadapter;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}resourceadapter
   */
  public void setResourceadapter(com.sap.engine.lib.descriptors.connector.ResourceadapterType _Resourceadapter) {
    this._f_Resourceadapter = _Resourceadapter;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}resourceadapter
   */
  public com.sap.engine.lib.descriptors.connector.ResourceadapterType getResourceadapter() {
    return this._f_Resourceadapter;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ConnectorType)) return false;
    ConnectorType typed = (ConnectorType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    if (this._a_Version != null) {
      if (typed._a_Version == null) return false;
      if (!this._a_Version.equals(typed._a_Version)) return false;
    } else {
      if (typed._a_Version != null) return false;
    }
    com.sap.engine.lib.descriptors.j2ee.DescriptionType[] _f_Description1 = this.getDescription();
    com.sap.engine.lib.descriptors.j2ee.DescriptionType[] _f_Description2 = typed.getDescription();
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
    com.sap.engine.lib.descriptors.j2ee.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    com.sap.engine.lib.descriptors.j2ee.DisplayNameType[] _f_DisplayName2 = typed.getDisplayName();
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
    com.sap.engine.lib.descriptors.j2ee.IconType[] _f_Icon1 = this.getIcon();
    com.sap.engine.lib.descriptors.j2ee.IconType[] _f_Icon2 = typed.getIcon();
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
    if (this._f_VendorName != null) {
      if (typed._f_VendorName == null) return false;
      if (!this._f_VendorName.equals(typed._f_VendorName)) return false;
    } else {
      if (typed._f_VendorName != null) return false;
    }
    if (this._f_EisType != null) {
      if (typed._f_EisType == null) return false;
      if (!this._f_EisType.equals(typed._f_EisType)) return false;
    } else {
      if (typed._f_EisType != null) return false;
    }
    if (this._f_ResourceadapterVersion != null) {
      if (typed._f_ResourceadapterVersion == null) return false;
      if (!this._f_ResourceadapterVersion.equals(typed._f_ResourceadapterVersion)) return false;
    } else {
      if (typed._f_ResourceadapterVersion != null) return false;
    }
    if (this._f_License != null) {
      if (typed._f_License == null) return false;
      if (!this._f_License.equals(typed._f_License)) return false;
    } else {
      if (typed._f_License != null) return false;
    }
    if (this._f_Resourceadapter != null) {
      if (typed._f_Resourceadapter == null) return false;
      if (!this._f_Resourceadapter.equals(typed._f_Resourceadapter)) return false;
    } else {
      if (typed._f_Resourceadapter != null) return false;
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
    if (this._a_Version != null) {
      result+= this._a_Version.hashCode();
    }
    com.sap.engine.lib.descriptors.j2ee.DescriptionType[] _f_Description1 = this.getDescription();
    if (_f_Description1 != null) {
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          result+= _f_Description1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors.j2ee.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    if (_f_DisplayName1 != null) {
      for (int i1 = 0; i1 < _f_DisplayName1.length ; i1++) {
        if (_f_DisplayName1[i1] != null) {
          result+= _f_DisplayName1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors.j2ee.IconType[] _f_Icon1 = this.getIcon();
    if (_f_Icon1 != null) {
      for (int i1 = 0; i1 < _f_Icon1.length ; i1++) {
        if (_f_Icon1[i1] != null) {
          result+= _f_Icon1[i1].hashCode();
        }
      }
    }
    if (this._f_VendorName != null) {
      result+= this._f_VendorName.hashCode();
    }
    if (this._f_EisType != null) {
      result+= this._f_EisType.hashCode();
    }
    if (this._f_ResourceadapterVersion != null) {
      result+= this._f_ResourceadapterVersion.hashCode();
    }
    if (this._f_License != null) {
      result+= this._f_License.hashCode();
    }
    if (this._f_Resourceadapter != null) {
      result+= this._f_Resourceadapter.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
