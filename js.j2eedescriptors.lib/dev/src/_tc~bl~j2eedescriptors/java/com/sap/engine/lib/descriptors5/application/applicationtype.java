﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed May 03 14:56:29 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.application;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}applicationType
 */
public  class ApplicationType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Attribute field for attribute {}version
  private java.lang.String _a_Version;
  /**
   * Set method for attribute {}version
   */
  public void setVersion(java.lang.String _Version) {
    this._a_Version = _Version;
  }
  /**
   * Get method for attribute {}version
   */
  public java.lang.String getVersion() {
    return _a_Version;
  }

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}description
  private java.util.ArrayList _f_Description = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}description
   */
  public void setDescription(com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _Description) {
    this._f_Description.clear();
    if (_Description != null) {
      for (int i=0; i<_Description.length; i++) {
        if (_Description[i] != null)
          this._f_Description.add(_Description[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}description
   */
  public com.sap.engine.lib.descriptors5.javaee.DescriptionType[] getDescription() {
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] result = new com.sap.engine.lib.descriptors5.javaee.DescriptionType[_f_Description.size()];
    _f_Description.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}display-name
  private java.util.ArrayList _f_DisplayName = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}display-name
   */
  public void setDisplayName(com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] _DisplayName) {
    this._f_DisplayName.clear();
    if (_DisplayName != null) {
      for (int i=0; i<_DisplayName.length; i++) {
        if (_DisplayName[i] != null)
          this._f_DisplayName.add(_DisplayName[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}display-name
   */
  public com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] getDisplayName() {
    com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] result = new com.sap.engine.lib.descriptors5.javaee.DisplayNameType[_f_DisplayName.size()];
    _f_DisplayName.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}icon
  private java.util.ArrayList _f_Icon = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}icon
   */
  public void setIcon(com.sap.engine.lib.descriptors5.javaee.IconType[] _Icon) {
    this._f_Icon.clear();
    if (_Icon != null) {
      for (int i=0; i<_Icon.length; i++) {
        if (_Icon[i] != null)
          this._f_Icon.add(_Icon[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}icon
   */
  public com.sap.engine.lib.descriptors5.javaee.IconType[] getIcon() {
    com.sap.engine.lib.descriptors5.javaee.IconType[] result = new com.sap.engine.lib.descriptors5.javaee.IconType[_f_Icon.size()];
    _f_Icon.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}module
  private java.util.ArrayList _f_Module = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}module
   */
  public void setModule(com.sap.engine.lib.descriptors5.application.ModuleType[] _Module) {
    this._f_Module.clear();
    if (_Module != null) {
      for (int i=0; i<_Module.length; i++) {
        if (_Module[i] != null)
          this._f_Module.add(_Module[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}module
   */
  public com.sap.engine.lib.descriptors5.application.ModuleType[] getModule() {
    com.sap.engine.lib.descriptors5.application.ModuleType[] result = new com.sap.engine.lib.descriptors5.application.ModuleType[_f_Module.size()];
    _f_Module.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}security-role
  private java.util.ArrayList _f_SecurityRole = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}security-role
   */
  public void setSecurityRole(com.sap.engine.lib.descriptors5.javaee.SecurityRoleType[] _SecurityRole) {
    this._f_SecurityRole.clear();
    if (_SecurityRole != null) {
      for (int i=0; i<_SecurityRole.length; i++) {
        if (_SecurityRole[i] != null)
          this._f_SecurityRole.add(_SecurityRole[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}security-role
   */
  public com.sap.engine.lib.descriptors5.javaee.SecurityRoleType[] getSecurityRole() {
    com.sap.engine.lib.descriptors5.javaee.SecurityRoleType[] result = new com.sap.engine.lib.descriptors5.javaee.SecurityRoleType[_f_SecurityRole.size()];
    _f_SecurityRole.toArray(result);
    return result;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}library-directory
  private com.sap.engine.lib.descriptors5.javaee.PathType _f_LibraryDirectory;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}library-directory
   */
  public void setLibraryDirectory(com.sap.engine.lib.descriptors5.javaee.PathType _LibraryDirectory) {
    this._f_LibraryDirectory = _LibraryDirectory;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}library-directory
   */
  public com.sap.engine.lib.descriptors5.javaee.PathType getLibraryDirectory() {
    return this._f_LibraryDirectory;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof ApplicationType)) return false;
    ApplicationType typed = (ApplicationType) object;
    if (this._a_Version != null) {
      if (typed._a_Version == null) return false;
      if (!this._a_Version.equals(typed._a_Version)) return false;
    } else {
      if (typed._a_Version != null) return false;
    }
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _f_Description1 = this.getDescription();
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _f_Description2 = typed.getDescription();
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
    com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] _f_DisplayName2 = typed.getDisplayName();
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
    com.sap.engine.lib.descriptors5.javaee.IconType[] _f_Icon1 = this.getIcon();
    com.sap.engine.lib.descriptors5.javaee.IconType[] _f_Icon2 = typed.getIcon();
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
    com.sap.engine.lib.descriptors5.application.ModuleType[] _f_Module1 = this.getModule();
    com.sap.engine.lib.descriptors5.application.ModuleType[] _f_Module2 = typed.getModule();
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
    com.sap.engine.lib.descriptors5.javaee.SecurityRoleType[] _f_SecurityRole1 = this.getSecurityRole();
    com.sap.engine.lib.descriptors5.javaee.SecurityRoleType[] _f_SecurityRole2 = typed.getSecurityRole();
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
    if (this._f_LibraryDirectory != null) {
      if (typed._f_LibraryDirectory == null) return false;
      if (!this._f_LibraryDirectory.equals(typed._f_LibraryDirectory)) return false;
    } else {
      if (typed._f_LibraryDirectory != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_Version != null) {
      result+= this._a_Version.hashCode();
    }
    if (this._a_Id != null) {
      result+= this._a_Id.hashCode();
    }
    com.sap.engine.lib.descriptors5.javaee.DescriptionType[] _f_Description1 = this.getDescription();
    if (_f_Description1 != null) {
      for (int i1 = 0; i1 < _f_Description1.length ; i1++) {
        if (_f_Description1[i1] != null) {
          result+= _f_Description1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.javaee.DisplayNameType[] _f_DisplayName1 = this.getDisplayName();
    if (_f_DisplayName1 != null) {
      for (int i1 = 0; i1 < _f_DisplayName1.length ; i1++) {
        if (_f_DisplayName1[i1] != null) {
          result+= _f_DisplayName1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.javaee.IconType[] _f_Icon1 = this.getIcon();
    if (_f_Icon1 != null) {
      for (int i1 = 0; i1 < _f_Icon1.length ; i1++) {
        if (_f_Icon1[i1] != null) {
          result+= _f_Icon1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.application.ModuleType[] _f_Module1 = this.getModule();
    if (_f_Module1 != null) {
      for (int i1 = 0; i1 < _f_Module1.length ; i1++) {
        if (_f_Module1[i1] != null) {
          result+= _f_Module1[i1].hashCode();
        }
      }
    }
    com.sap.engine.lib.descriptors5.javaee.SecurityRoleType[] _f_SecurityRole1 = this.getSecurityRole();
    if (_f_SecurityRole1 != null) {
      for (int i1 = 0; i1 < _f_SecurityRole1.length ; i1++) {
        if (_f_SecurityRole1[i1] != null) {
          result+= _f_SecurityRole1[i1].hashCode();
        }
      }
    }
    if (this._f_LibraryDirectory != null) {
      result+= this._f_LibraryDirectory.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
