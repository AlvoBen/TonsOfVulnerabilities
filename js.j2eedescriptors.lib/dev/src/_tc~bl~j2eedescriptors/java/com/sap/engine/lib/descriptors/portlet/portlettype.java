﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 13 17:05:09 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.portlet;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portletType
 */
public  class PortletType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}description
  private java.util.ArrayList _f_Description = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}description
   */
  public void setDescription(com.sap.engine.lib.descriptors.portlet.DescriptionType[] _Description) {
    this._f_Description.clear();
    if (_Description != null) {
      for (int i=0; i<_Description.length; i++) {
        if (_Description[i] != null)
          this._f_Description.add(_Description[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}description
   */
  public com.sap.engine.lib.descriptors.portlet.DescriptionType[] getDescription() {
    com.sap.engine.lib.descriptors.portlet.DescriptionType[] result = new com.sap.engine.lib.descriptors.portlet.DescriptionType[_f_Description.size()];
    _f_Description.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-name
  private com.sap.engine.lib.descriptors.portlet.PortletNameType _f_PortletName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-name
   */
  public void setPortletName(com.sap.engine.lib.descriptors.portlet.PortletNameType _PortletName) {
    this._f_PortletName = _PortletName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-name
   */
  public com.sap.engine.lib.descriptors.portlet.PortletNameType getPortletName() {
    return this._f_PortletName;
  }

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}display-name
  private java.util.ArrayList _f_DisplayName = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}display-name
   */
  public void setDisplayName(com.sap.engine.lib.descriptors.portlet.DisplayNameType[] _DisplayName) {
    this._f_DisplayName.clear();
    if (_DisplayName != null) {
      for (int i=0; i<_DisplayName.length; i++) {
        if (_DisplayName[i] != null)
          this._f_DisplayName.add(_DisplayName[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}display-name
   */
  public com.sap.engine.lib.descriptors.portlet.DisplayNameType[] getDisplayName() {
    com.sap.engine.lib.descriptors.portlet.DisplayNameType[] result = new com.sap.engine.lib.descriptors.portlet.DisplayNameType[_f_DisplayName.size()];
    _f_DisplayName.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-class
  private java.lang.String _f_PortletClass;
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-class
   */
  public void setPortletClass(java.lang.String _PortletClass) {
    this._f_PortletClass = _PortletClass;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-class
   */
  public java.lang.String getPortletClass() {
    return this._f_PortletClass;
  }

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}init-param
  private java.util.ArrayList _f_InitParam = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}init-param
   */
  public void setInitParam(com.sap.engine.lib.descriptors.portlet.InitParamType[] _InitParam) {
    this._f_InitParam.clear();
    if (_InitParam != null) {
      for (int i=0; i<_InitParam.length; i++) {
        if (_InitParam[i] != null)
          this._f_InitParam.add(_InitParam[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}init-param
   */
  public com.sap.engine.lib.descriptors.portlet.InitParamType[] getInitParam() {
    com.sap.engine.lib.descriptors.portlet.InitParamType[] result = new com.sap.engine.lib.descriptors.portlet.InitParamType[_f_InitParam.size()];
    _f_InitParam.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}expiration-cache
  private com.sap.engine.lib.descriptors.portlet.ExpirationCacheType _f_ExpirationCache;
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}expiration-cache
   */
  public void setExpirationCache(com.sap.engine.lib.descriptors.portlet.ExpirationCacheType _ExpirationCache) {
    this._f_ExpirationCache = _ExpirationCache;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}expiration-cache
   */
  public com.sap.engine.lib.descriptors.portlet.ExpirationCacheType getExpirationCache() {
    return this._f_ExpirationCache;
  }

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}supports
  private java.util.ArrayList _f_Supports = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}supports
   */
  public void setSupports(com.sap.engine.lib.descriptors.portlet.SupportsType[] _Supports) {
    this._f_Supports.clear();
    if (_Supports != null) {
      for (int i=0; i<_Supports.length; i++) {
        if (_Supports[i] != null)
          this._f_Supports.add(_Supports[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}supports
   */
  public com.sap.engine.lib.descriptors.portlet.SupportsType[] getSupports() {
    com.sap.engine.lib.descriptors.portlet.SupportsType[] result = new com.sap.engine.lib.descriptors.portlet.SupportsType[_f_Supports.size()];
    _f_Supports.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}supported-locale
  private java.util.ArrayList _f_SupportedLocale = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}supported-locale
   */
  public void setSupportedLocale(com.sap.engine.lib.descriptors.portlet.SupportedLocaleType[] _SupportedLocale) {
    this._f_SupportedLocale.clear();
    if (_SupportedLocale != null) {
      for (int i=0; i<_SupportedLocale.length; i++) {
        if (_SupportedLocale[i] != null)
          this._f_SupportedLocale.add(_SupportedLocale[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}supported-locale
   */
  public com.sap.engine.lib.descriptors.portlet.SupportedLocaleType[] getSupportedLocale() {
    com.sap.engine.lib.descriptors.portlet.SupportedLocaleType[] result = new com.sap.engine.lib.descriptors.portlet.SupportedLocaleType[_f_SupportedLocale.size()];
    _f_SupportedLocale.toArray(result);
    return result;
  }

  // Model group field class 
  public static class Choice1 implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

    public Choice1() {
    }


    // // Active choise field
    private int _c_validField = 0;

    // Model group field class
  public static class Sequence1 implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

    public Sequence1() {
    }


    // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}resource-bundle
    private com.sap.engine.lib.descriptors.portlet.ResourceBundleType _f_ResourceBundle;
    /**
     * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}resource-bundle
     */
    public void setResourceBundle(com.sap.engine.lib.descriptors.portlet.ResourceBundleType _ResourceBundle) {
      this._f_ResourceBundle = _ResourceBundle;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}resource-bundle
     */
    public com.sap.engine.lib.descriptors.portlet.ResourceBundleType getResourceBundle() {
      return this._f_ResourceBundle;
    }

    // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-info
    private com.sap.engine.lib.descriptors.portlet.PortletInfoType _f_PortletInfo;
    /**
     * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-info
     */
    public void setPortletInfo(com.sap.engine.lib.descriptors.portlet.PortletInfoType _PortletInfo) {
      this._f_PortletInfo = _PortletInfo;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-info
     */
    public com.sap.engine.lib.descriptors.portlet.PortletInfoType getPortletInfo() {
      return this._f_PortletInfo;
    }

    /**
     * Equals method implementation.
     */
    public boolean equals(Object object) {
      if (object == null) return false;
      if (!(object instanceof Sequence1)) return false;
      Sequence1 typed = (Sequence1) object;
      if (this._f_ResourceBundle != null) {
        if (typed._f_ResourceBundle == null) return false;
        if (!this._f_ResourceBundle.equals(typed._f_ResourceBundle)) return false;
      } else {
        if (typed._f_ResourceBundle != null) return false;
      }
      if (this._f_PortletInfo != null) {
        if (typed._f_PortletInfo == null) return false;
        if (!this._f_PortletInfo.equals(typed._f_PortletInfo)) return false;
      } else {
        if (typed._f_PortletInfo != null) return false;
      }
      return true;
    }

    /**
     * Hashcode method implementation.
     */
    public int hashCode() {
      int result = 0;
      if (this._f_ResourceBundle != null) {
        result+= this._f_ResourceBundle.hashCode();
      }
      if (this._f_PortletInfo != null) {
        result+= this._f_PortletInfo.hashCode();
      }
      return result;
    }

    public java.lang.String get__ID() {
      return java.lang.String.valueOf(super.hashCode());
    }
  }

    private Sequence1 _f_SequenceGroup1;
    public void setSequenceGroup1(Sequence1 _SequenceGroup1) {
      if (this._c_validField != 0 && this._c_validField != 1) {
        this.unsetContent();
      }
      this._f_SequenceGroup1 = _SequenceGroup1;
      this._c_validField = 1;
    }
    public Sequence1 getSequenceGroup1() {
      if (this._c_validField != 1) {
        return null;
      }
      return this._f_SequenceGroup1;
    }
    public boolean isSetSequenceGroup1() {
      return (this._c_validField ==1);
    }
    private com.sap.engine.lib.descriptors.portlet.PortletInfoType _f_PortletInfo;
    /**
     * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-info
     */
    public void setPortletInfo(com.sap.engine.lib.descriptors.portlet.PortletInfoType _PortletInfo) {
      if (this._c_validField != 0 && this._c_validField != 2) {
        this.unsetContent();
      }
      this._f_PortletInfo = _PortletInfo;
      this._c_validField = 2;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-info
     */
    public com.sap.engine.lib.descriptors.portlet.PortletInfoType getPortletInfo() {
      if (this._c_validField != 2) {
        return null;
      }
      return this._f_PortletInfo;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-info
     */
    public boolean isSetPortletInfo() {
      return (this._c_validField ==2);
    }
    /**
     * Common get method for choice type.
     */
    public java.lang.Object getContent() {
      switch (this._c_validField) {
        case 1: return this._f_SequenceGroup1;
        case 2: return this._f_PortletInfo;
      }
      return null;
    }
    /**
     * Returns true if this choice has content set.
     */
    public boolean isSetContent() {
      return (this._c_validField == 0);
    }
    /**
     * Clears choice content.
     */
    public void unsetContent() {
      switch (this._c_validField) {
        case  1: {
          this._f_SequenceGroup1 = null;
          break;
        }
        case  2: {
          this._f_PortletInfo = null;
          break;
        }
      }
      this._c_validField = 0;
    }

    /**
     * Equals method implementation.
     */
    public boolean equals(Object object) {
      if (object == null) return false;
      if (!(object instanceof Choice1)) return false;
      Choice1 typed = (Choice1) object;
      if (this._c_validField != typed._c_validField) return false;
      switch (this._c_validField) {
        case 1: {
          if (this._f_SequenceGroup1 != null) {
            if (typed._f_SequenceGroup1 == null) return false;
            if (!this._f_SequenceGroup1.equals(typed._f_SequenceGroup1)) return false;
          } else {
            if (typed._f_SequenceGroup1 != null) return false;
          }
          break;
        }
        case 2: {
          if (this._f_PortletInfo != null) {
            if (typed._f_PortletInfo == null) return false;
            if (!this._f_PortletInfo.equals(typed._f_PortletInfo)) return false;
          } else {
            if (typed._f_PortletInfo != null) return false;
          }
          break;
        }
      }
      return true;
    }

    /**
     * Hashcode method implementation.
     */
    public int hashCode() {
      int result = 0;
      switch (this._c_validField) {
        case 1: {
          if (this._f_SequenceGroup1 != null) {
            result+= this._f_SequenceGroup1.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 2: {
          if (this._f_PortletInfo != null) {
            result+= this._f_PortletInfo.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
      }
      return result;
    }

    public java.lang.String get__ID() {
      return java.lang.String.valueOf(super.hashCode());
    }
  }

  private Choice1 _f_ChoiceGroup1;
  public void setChoiceGroup1(Choice1 _ChoiceGroup1) {
    this._f_ChoiceGroup1 = _ChoiceGroup1;
  }
  public Choice1 getChoiceGroup1() {
    return this._f_ChoiceGroup1;
  }

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-preferences
  private com.sap.engine.lib.descriptors.portlet.PortletPreferencesType _f_PortletPreferences;
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-preferences
   */
  public void setPortletPreferences(com.sap.engine.lib.descriptors.portlet.PortletPreferencesType _PortletPreferences) {
    this._f_PortletPreferences = _PortletPreferences;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-preferences
   */
  public com.sap.engine.lib.descriptors.portlet.PortletPreferencesType getPortletPreferences() {
    return this._f_PortletPreferences;
  }

  // Element field for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}security-role-ref
  private java.util.ArrayList _f_SecurityRoleRef = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}security-role-ref
   */
  public void setSecurityRoleRef(com.sap.engine.lib.descriptors.portlet.SecurityRoleRefType[] _SecurityRoleRef) {
    this._f_SecurityRoleRef.clear();
    if (_SecurityRoleRef != null) {
      for (int i=0; i<_SecurityRoleRef.length; i++) {
        if (_SecurityRoleRef[i] != null)
          this._f_SecurityRoleRef.add(_SecurityRoleRef[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}security-role-ref
   */
  public com.sap.engine.lib.descriptors.portlet.SecurityRoleRefType[] getSecurityRoleRef() {
    com.sap.engine.lib.descriptors.portlet.SecurityRoleRefType[] result = new com.sap.engine.lib.descriptors.portlet.SecurityRoleRefType[_f_SecurityRoleRef.size()];
    _f_SecurityRoleRef.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof PortletType)) return false;
    PortletType typed = (PortletType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors.portlet.DescriptionType[] _f_Description1 = this.getDescription();
    com.sap.engine.lib.descriptors.portlet.DescriptionType[] _f_Description2 = typed.getDescription();
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
    if (this._f_PortletName != null) {
      if (typed._f_PortletName == null) return false;
      if (!this._f_PortletName.equals(typed._f_PortletName)) return false;
    } else {
      if (typed._f_PortletName != null) return false;
    }
    com.sap.engine.lib.descriptors.portlet.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    com.sap.engine.lib.descriptors.portlet.DisplayNameType[] _f_DisplayName2 = typed.getDisplayName();
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
    if (this._f_PortletClass != null) {
      if (typed._f_PortletClass == null) return false;
      if (!this._f_PortletClass.equals(typed._f_PortletClass)) return false;
    } else {
      if (typed._f_PortletClass != null) return false;
    }
    com.sap.engine.lib.descriptors.portlet.InitParamType[] _f_InitParam1 = this.getInitParam();
    com.sap.engine.lib.descriptors.portlet.InitParamType[] _f_InitParam2 = typed.getInitParam();
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
    if (this._f_ExpirationCache != null) {
      if (typed._f_ExpirationCache == null) return false;
      if (!this._f_ExpirationCache.equals(typed._f_ExpirationCache)) return false;
    } else {
      if (typed._f_ExpirationCache != null) return false;
    }
    com.sap.engine.lib.descriptors.portlet.SupportsType[] _f_Supports1 = this.getSupports();
    com.sap.engine.lib.descriptors.portlet.SupportsType[] _f_Supports2 = typed.getSupports();
    if (_f_Supports1 != null) {
      if (_f_Supports2 == null) return false;
      if (_f_Supports1.length != _f_Supports2.length) return false;
      for (int i1 = 0; i1 < _f_Supports1.length ; i1++) {
        if (_f_Supports1[i1] != null) {
          if (_f_Supports2[i1] == null) return false;
          if (!_f_Supports1[i1].equals(_f_Supports2[i1])) return false;
        } else {
          if (_f_Supports2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Supports2 != null) return false;
    }
    com.sap.engine.lib.descriptors.portlet.SupportedLocaleType[] _f_SupportedLocale1 = this.getSupportedLocale();
    com.sap.engine.lib.descriptors.portlet.SupportedLocaleType[] _f_SupportedLocale2 = typed.getSupportedLocale();
    if (_f_SupportedLocale1 != null) {
      if (_f_SupportedLocale2 == null) return false;
      if (_f_SupportedLocale1.length != _f_SupportedLocale2.length) return false;
      for (int i1 = 0; i1 < _f_SupportedLocale1.length ; i1++) {
        if (_f_SupportedLocale1[i1] != null) {
          if (_f_SupportedLocale2[i1] == null) return false;
          if (!_f_SupportedLocale1[i1].equals(_f_SupportedLocale2[i1])) return false;
        } else {
          if (_f_SupportedLocale2[i1] != null) return false;
        }
      }
    } else {
      if (_f_SupportedLocale2 != null) return false;
    }
    if (this._f_ChoiceGroup1 != null) {
      if (typed._f_ChoiceGroup1 == null) return false;
      if (!this._f_ChoiceGroup1.equals(typed._f_ChoiceGroup1)) return false;
    } else {
      if (typed._f_ChoiceGroup1 != null) return false;
    }
    if (this._f_PortletPreferences != null) {
      if (typed._f_PortletPreferences == null) return false;
      if (!this._f_PortletPreferences.equals(typed._f_PortletPreferences)) return false;
    } else {
      if (typed._f_PortletPreferences != null) return false;
    }
    com.sap.engine.lib.descriptors.portlet.SecurityRoleRefType[] _f_SecurityRoleRef1 = this.getSecurityRoleRef();
    com.sap.engine.lib.descriptors.portlet.SecurityRoleRefType[] _f_SecurityRoleRef2 = typed.getSecurityRoleRef();
    if (_f_SecurityRoleRef1 != null) {
      if (_f_SecurityRoleRef2 == null) return false;
      if (_f_SecurityRoleRef1.length != _f_SecurityRoleRef2.length) return false;
      for (int i1 = 0; i1 < _f_SecurityRoleRef1.length ; i1++) {
        if (_f_SecurityRoleRef1[i1] != null) {
          if (_f_SecurityRoleRef2[i1] == null) return false;
          if (!_f_SecurityRoleRef1[i1].equals(_f_SecurityRoleRef2[i1])) return false;
        } else {
          if (_f_SecurityRoleRef2[i1] != null) return false;
        }
      }
    } else {
      if (_f_SecurityRoleRef2 != null) return false;
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
    com.sap.engine.lib.descriptors.portlet.DescriptionType[] _f_Description1 = this.getDescription();
    if (_f_Description1 != null) {
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          result+= _f_Description1[i1].hashCode();
        }
      }
    }
    if (this._f_PortletName != null) {
      result+= this._f_PortletName.hashCode();
    }
    com.sap.engine.lib.descriptors.portlet.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    if (_f_DisplayName1 != null) {
      for (int i1 = 0; i1 < _f_DisplayName1.length ; i1++) {
        if (_f_DisplayName1[i1] != null) {
          result+= _f_DisplayName1[i1].hashCode();
        }
      }
    }
    if (this._f_PortletClass != null) {
      result+= this._f_PortletClass.hashCode();
    }
    com.sap.engine.lib.descriptors.portlet.InitParamType[] _f_InitParam1 = this.getInitParam();
    if (_f_InitParam1 != null) {
      for (int i1 = 0; i1 < _f_InitParam1.length ; i1++) {
        if (_f_InitParam1[i1] != null) {
          result+= _f_InitParam1[i1].hashCode();
        }
      }
    }
    if (this._f_ExpirationCache != null) {
      result+= this._f_ExpirationCache.hashCode();
    }
    com.sap.engine.lib.descriptors.portlet.SupportsType[] _f_Supports1 = this.getSupports();
    if (_f_Supports1 != null) {
      for (int i1 = 0; i1 < _f_Supports1.length ; i1++) {
        if (_f_Supports1[i1] != null) {
          result+= _f_Supports1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors.portlet.SupportedLocaleType[] _f_SupportedLocale1 = this.getSupportedLocale();
    if (_f_SupportedLocale1 != null) {
      for (int i1 = 0; i1 < _f_SupportedLocale1.length ; i1++) {
        if (_f_SupportedLocale1[i1] != null) {
          result+= _f_SupportedLocale1[i1].hashCode();
        }
      }
    }
    if (this._f_ChoiceGroup1 != null) {
      result+= this._f_ChoiceGroup1.hashCode();
    }
    if (this._f_PortletPreferences != null) {
      result+= this._f_PortletPreferences.hashCode();
    }
    com.sap.engine.lib.descriptors.portlet.SecurityRoleRefType[] _f_SecurityRoleRef1 = this.getSecurityRoleRef();
    if (_f_SecurityRoleRef1 != null) {
      for (int i1 = 0; i1 < _f_SecurityRoleRef1.length ; i1++) {
        if (_f_SecurityRoleRef1[i1] != null) {
          result+= _f_SecurityRoleRef1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
