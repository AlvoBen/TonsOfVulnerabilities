﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed May 04 15:11:32 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.application;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}applicationType
 */
public  class ApplicationType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/j2ee}module
  private java.util.ArrayList _f_Module = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}module
   */
  public void setModule(com.sap.engine.lib.descriptors.application.ModuleType[] _Module) {
    this._f_Module.clear();
    if (_Module != null) {
      for (int i=0; i<_Module.length; i++) {
        if (_Module[i] != null)
          this._f_Module.add(_Module[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}module
   */
  public com.sap.engine.lib.descriptors.application.ModuleType[] getModule() {
    com.sap.engine.lib.descriptors.application.ModuleType[] result = new com.sap.engine.lib.descriptors.application.ModuleType[_f_Module.size()];
    _f_Module.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}security-role
  private java.util.ArrayList _f_SecurityRole = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}security-role
   */
  public void setSecurityRole(com.sap.engine.lib.descriptors.j2ee.SecurityRoleType[] _SecurityRole) {
    this._f_SecurityRole.clear();
    if (_SecurityRole != null) {
      for (int i=0; i<_SecurityRole.length; i++) {
        if (_SecurityRole[i] != null)
          this._f_SecurityRole.add(_SecurityRole[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}security-role
   */
  public com.sap.engine.lib.descriptors.j2ee.SecurityRoleType[] getSecurityRole() {
    com.sap.engine.lib.descriptors.j2ee.SecurityRoleType[] result = new com.sap.engine.lib.descriptors.j2ee.SecurityRoleType[_f_SecurityRole.size()];
    _f_SecurityRole.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ApplicationType)) return false;
    ApplicationType typed = (ApplicationType) object;
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
    com.sap.engine.lib.descriptors.application.ModuleType[] _f_Module1 = this.getModule();
    com.sap.engine.lib.descriptors.application.ModuleType[] _f_Module2 = typed.getModule();
    if (_f_Module1 != null) {
      if (_f_Module2 == null) return false;
      if (_f_Module1.length != _f_Module2.length) return false;
      for (int i1 = 0; i1 < _f_Module1.length ; i1++) {
        if (_f_Module1[i1] != null) {
          if (_f_Module2[i1] == null) return false;
          if (!_f_Module1[i1].equals(_f_Module2[i1])) return false;
        } else {
          if (_f_Module2[i1] != null) return false;
        }
      }
    } else {
      if (_f_Module2 != null) return false;
    }
    com.sap.engine.lib.descriptors.j2ee.SecurityRoleType[] _f_SecurityRole1 = this.getSecurityRole();
    com.sap.engine.lib.descriptors.j2ee.SecurityRoleType[] _f_SecurityRole2 = typed.getSecurityRole();
    if (_f_SecurityRole1 != null) {
      if (_f_SecurityRole2 == null) return false;
      if (_f_SecurityRole1.length != _f_SecurityRole2.length) return false;
      for (int i1 = 0; i1 < _f_SecurityRole1.length ; i1++) {
        if (_f_SecurityRole1[i1] != null) {
          if (_f_SecurityRole2[i1] == null) return false;
          if (!_f_SecurityRole1[i1].equals(_f_SecurityRole2[i1])) return false;
        } else {
          if (_f_SecurityRole2[i1] != null) return false;
        }
      }
    } else {
      if (_f_SecurityRole2 != null) return false;
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
    com.sap.engine.lib.descriptors.application.ModuleType[] _f_Module1 = this.getModule();
    if (_f_Module1 != null) {
      for (int i1 = 0; i1 < _f_Module1.length ; i1++) {
        if (_f_Module1[i1] != null) {
          result+= _f_Module1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors.j2ee.SecurityRoleType[] _f_SecurityRole1 = this.getSecurityRole();
    if (_f_SecurityRole1 != null) {
      for (int i1 = 0; i1 < _f_SecurityRole1.length ; i1++) {
        if (_f_SecurityRole1[i1] != null) {
          result+= _f_SecurityRole1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
