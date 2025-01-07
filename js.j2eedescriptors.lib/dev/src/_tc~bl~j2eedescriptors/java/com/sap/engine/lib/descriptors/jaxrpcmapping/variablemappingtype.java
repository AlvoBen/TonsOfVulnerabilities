﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Fri Apr 22 10:18:13 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.jaxrpcmapping;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}variable-mappingType
 */
public  class VariableMappingType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Element field for element {http://java.sun.com/xml/ns/j2ee}java-variable-name
  private com.sap.engine.lib.descriptors.j2ee.String _f_JavaVariableName;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}java-variable-name
   */
  public void setJavaVariableName(com.sap.engine.lib.descriptors.j2ee.String _JavaVariableName) {
    this._f_JavaVariableName = _JavaVariableName;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}java-variable-name
   */
  public com.sap.engine.lib.descriptors.j2ee.String getJavaVariableName() {
    return this._f_JavaVariableName;
  }

  // Element field for element {http://java.sun.com/xml/ns/j2ee}data-member
  private com.sap.engine.lib.descriptors.j2ee.EmptyType _f_DataMember;
  /**
   * Set method for element {http://java.sun.com/xml/ns/j2ee}data-member
   */
  public void setDataMember(com.sap.engine.lib.descriptors.j2ee.EmptyType _DataMember) {
    this._f_DataMember = _DataMember;
  }
  /**
   * Get method for element {http://java.sun.com/xml/ns/j2ee}data-member
   */
  public com.sap.engine.lib.descriptors.j2ee.EmptyType getDataMember() {
    return this._f_DataMember;
  }

  // Model group field class 
  public static class Choice1 implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

    public Choice1() {
    }


    // // Active choise field
    private int _c_validField = 0;
    private com.sap.engine.lib.descriptors.j2ee.String _f_XmlAttributeName;
    /**
     * Set method for element {http://java.sun.com/xml/ns/j2ee}xml-attribute-name
     */
    public void setXmlAttributeName(com.sap.engine.lib.descriptors.j2ee.String _XmlAttributeName) {
      if (this._c_validField != 0 && this._c_validField != 1) {
        this.unsetContent();
      }
      this._f_XmlAttributeName = _XmlAttributeName;
      this._c_validField = 1;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/j2ee}xml-attribute-name
     */
    public com.sap.engine.lib.descriptors.j2ee.String getXmlAttributeName() {
      if (this._c_validField != 1) {
        return null;
      }
      return this._f_XmlAttributeName;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/j2ee}xml-attribute-name
     */
    public boolean isSetXmlAttributeName() {
      return (this._c_validField ==1);
    }
    private com.sap.engine.lib.descriptors.j2ee.String _f_XmlElementName;
    /**
     * Set method for element {http://java.sun.com/xml/ns/j2ee}xml-element-name
     */
    public void setXmlElementName(com.sap.engine.lib.descriptors.j2ee.String _XmlElementName) {
      if (this._c_validField != 0 && this._c_validField != 2) {
        this.unsetContent();
      }
      this._f_XmlElementName = _XmlElementName;
      this._c_validField = 2;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/j2ee}xml-element-name
     */
    public com.sap.engine.lib.descriptors.j2ee.String getXmlElementName() {
      if (this._c_validField != 2) {
        return null;
      }
      return this._f_XmlElementName;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/j2ee}xml-element-name
     */
    public boolean isSetXmlElementName() {
      return (this._c_validField ==2);
    }
    private com.sap.engine.lib.descriptors.j2ee.EmptyType _f_XmlWildcard;
    /**
     * Set method for element {http://java.sun.com/xml/ns/j2ee}xml-wildcard
     */
    public void setXmlWildcard(com.sap.engine.lib.descriptors.j2ee.EmptyType _XmlWildcard) {
      if (this._c_validField != 0 && this._c_validField != 3) {
        this.unsetContent();
      }
      this._f_XmlWildcard = _XmlWildcard;
      this._c_validField = 3;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/j2ee}xml-wildcard
     */
    public com.sap.engine.lib.descriptors.j2ee.EmptyType getXmlWildcard() {
      if (this._c_validField != 3) {
        return null;
      }
      return this._f_XmlWildcard;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/j2ee}xml-wildcard
     */
    public boolean isSetXmlWildcard() {
      return (this._c_validField ==3);
    }
    /**
     * Common get method for choice type.
     */
    public java.lang.Object getContent() {
      switch (this._c_validField) {
        case 1: return this._f_XmlAttributeName;
        case 2: return this._f_XmlElementName;
        case 3: return this._f_XmlWildcard;
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
          this._f_XmlAttributeName = null;
          break;
        }
        case  2: {
          this._f_XmlElementName = null;
          break;
        }
        case  3: {
          this._f_XmlWildcard = null;
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
          if (this._f_XmlAttributeName != null) {
            if (typed._f_XmlAttributeName == null) return false;
            if (!this._f_XmlAttributeName.equals(typed._f_XmlAttributeName)) return false;
          } else {
            if (typed._f_XmlAttributeName != null) return false;
          }
          break;
        }
        case 2: {
          if (this._f_XmlElementName != null) {
            if (typed._f_XmlElementName == null) return false;
            if (!this._f_XmlElementName.equals(typed._f_XmlElementName)) return false;
          } else {
            if (typed._f_XmlElementName != null) return false;
          }
          break;
        }
        case 3: {
          if (this._f_XmlWildcard != null) {
            if (typed._f_XmlWildcard == null) return false;
            if (!this._f_XmlWildcard.equals(typed._f_XmlWildcard)) return false;
          } else {
            if (typed._f_XmlWildcard != null) return false;
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
          if (this._f_XmlAttributeName != null) {
            result+= this._f_XmlAttributeName.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 2: {
          if (this._f_XmlElementName != null) {
            result+= this._f_XmlElementName.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 3: {
          if (this._f_XmlWildcard != null) {
            result+= this._f_XmlWildcard.hashCode();
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

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof VariableMappingType)) return false;
    VariableMappingType typed = (VariableMappingType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    if (this._f_JavaVariableName != null) {
      if (typed._f_JavaVariableName == null) return false;
      if (!this._f_JavaVariableName.equals(typed._f_JavaVariableName)) return false;
    } else {
      if (typed._f_JavaVariableName != null) return false;
    }
    if (this._f_DataMember != null) {
      if (typed._f_DataMember == null) return false;
      if (!this._f_DataMember.equals(typed._f_DataMember)) return false;
    } else {
      if (typed._f_DataMember != null) return false;
    }
    if (this._f_ChoiceGroup1 != null) {
      if (typed._f_ChoiceGroup1 == null) return false;
      if (!this._f_ChoiceGroup1.equals(typed._f_ChoiceGroup1)) return false;
    } else {
      if (typed._f_ChoiceGroup1 != null) return false;
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
    if (this._f_JavaVariableName != null) {
      result+= this._f_JavaVariableName.hashCode();
    }
    if (this._f_DataMember != null) {
      result+= this._f_DataMember.hashCode();
    }
    if (this._f_ChoiceGroup1 != null) {
      result+= this._f_ChoiceGroup1.hashCode();
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
