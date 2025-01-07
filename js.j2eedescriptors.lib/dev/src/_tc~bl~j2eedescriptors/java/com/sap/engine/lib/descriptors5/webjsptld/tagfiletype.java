﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 09 16:24:16 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webjsptld;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}tagFileType
 */
public  class TagFileType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/javaee}name
  private com.sap.engine.lib.descriptors5.webjsptld.TldCanonicalNameType _f_Name;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}name
   */
  public void setName(com.sap.engine.lib.descriptors5.webjsptld.TldCanonicalNameType _Name) {
    this._f_Name = _Name;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}name
   */
  public com.sap.engine.lib.descriptors5.webjsptld.TldCanonicalNameType getName() {
    return this._f_Name;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}path
  private com.sap.engine.lib.descriptors5.javaee.PathType _f_Path;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}path
   */
  public void setPath(com.sap.engine.lib.descriptors5.javaee.PathType _Path) {
    this._f_Path = _Path;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}path
   */
  public com.sap.engine.lib.descriptors5.javaee.PathType getPath() {
    return this._f_Path;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}example
  private com.sap.engine.lib.descriptors5.javaee.XsdStringType _f_Example;
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}example
   */
  public void setExample(com.sap.engine.lib.descriptors5.javaee.XsdStringType _Example) {
    this._f_Example = _Example;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}example
   */
  public com.sap.engine.lib.descriptors5.javaee.XsdStringType getExample() {
    return this._f_Example;
  }

  // Element field for element {http://java.sun.com/xml/ns/javaee}tag-extension
  private java.util.ArrayList _f_TagExtension = new java.util.ArrayList();
  /**
   * Set method for element {http://java.sun.com/xml/ns/javaee}tag-extension
   */
  public void setTagExtension(com.sap.engine.lib.descriptors5.webjsptld.TldExtensionType[] _TagExtension) {
    this._f_TagExtension.clear();
    if (_TagExtension != null) {
      for (int i=0; i<_TagExtension.length; i++) {
        if (_TagExtension[i] != null)
          this._f_TagExtension.add(_TagExtension[i]);
      }
    }
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/javaee}tag-extension
   */
  public com.sap.engine.lib.descriptors5.webjsptld.TldExtensionType[] getTagExtension() {
    com.sap.engine.lib.descriptors5.webjsptld.TldExtensionType[] result = new com.sap.engine.lib.descriptors5.webjsptld.TldExtensionType[_f_TagExtension.size()];
    _f_TagExtension.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof TagFileType)) return false;
    TagFileType typed = (TagFileType) object;
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
    if (this._f_Name != null) {
      if (typed._f_Name == null) return false;
      if (!this._f_Name.equals(typed._f_Name)) return false;
    } else {
      if (typed._f_Name != null) return false;
    }
    if (this._f_Path != null) {
      if (typed._f_Path == null) return false;
      if (!this._f_Path.equals(typed._f_Path)) return false;
    } else {
      if (typed._f_Path != null) return false;
    }
    if (this._f_Example != null) {
      if (typed._f_Example == null) return false;
      if (!this._f_Example.equals(typed._f_Example)) return false;
    } else {
      if (typed._f_Example != null) return false;
    }
    com.sap.engine.lib.descriptors5.webjsptld.TldExtensionType[] _f_TagExtension1 = this.getTagExtension();
    com.sap.engine.lib.descriptors5.webjsptld.TldExtensionType[] _f_TagExtension2 = typed.getTagExtension();
    if (_f_TagExtension1 != null) {
      if (_f_TagExtension2 == null) return false;
      if (_f_TagExtension1.length != _f_TagExtension2.length) return false;
      for (int i1 = 0; i1 < _f_TagExtension1.length ; i1++) {
        if (_f_TagExtension1[i1] != null) {
          if (_f_TagExtension2[i1] == null) return false;
          if (!_f_TagExtension1[i1].equals(_f_TagExtension2[i1])) return false;
        } else {
          if (_f_TagExtension2[i1] != null) return false;
        }
      }
    } else {
      if (_f_TagExtension2 != null) return false;
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
    if (this._f_Name != null) {
      result+= this._f_Name.hashCode();
    }
    if (this._f_Path != null) {
      result+= this._f_Path.hashCode();
    }
    if (this._f_Example != null) {
      result+= this._f_Example.hashCode();
    }
    com.sap.engine.lib.descriptors5.webjsptld.TldExtensionType[] _f_TagExtension1 = this.getTagExtension();
    if (_f_TagExtension1 != null) {
      for (int i1 = 0; i1 < _f_TagExtension1.length ; i1++) {
        if (_f_TagExtension1[i1] != null) {
          result+= _f_TagExtension1[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
