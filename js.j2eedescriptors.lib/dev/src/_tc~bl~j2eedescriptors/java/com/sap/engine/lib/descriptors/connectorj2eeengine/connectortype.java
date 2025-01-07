﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jan 30 18:27:10 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.connectorj2eeengine;

/**
 * Schema complexType Java representation.
 * Represents type {}connectorType
 */
public  class ConnectorType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

  // Element field for element {}description
  private java.lang.String _f_Description;
  /**
   * Set method for element {}description
   */
  public void setDescription(java.lang.String _Description) {
    this._f_Description = _Description;
  }
  /**
   * Get method for element {}description
   */
  public java.lang.String getDescription() {
    return this._f_Description;
  }

  // Element field for element {}resourceadapter
  private com.sap.engine.lib.descriptors.connectorj2eeengine.ResourceadapterType _f_Resourceadapter;
  /**
   * Set method for element {}resourceadapter
   */
  public void setResourceadapter(com.sap.engine.lib.descriptors.connectorj2eeengine.ResourceadapterType _Resourceadapter) {
    this._f_Resourceadapter = _Resourceadapter;
  }
  /**
   * Get method for element {}resourceadapter
   */
  public com.sap.engine.lib.descriptors.connectorj2eeengine.ResourceadapterType getResourceadapter() {
    return this._f_Resourceadapter;
  }

  // Model group field class 
  public static class Choice1 implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

    public Choice1() {
    }


    // // Active choise field
    private int _c_validField = 0;
    private com.sap.engine.lib.descriptors.j2ee.EmptyType _f_Template;
    /**
     * Set method for element {}template
     */
    public void setTemplate(com.sap.engine.lib.descriptors.j2ee.EmptyType _Template) {
      if (this._c_validField != 0 && this._c_validField != 1) {
        this.unsetContent();
      }
      this._f_Template = _Template;
      this._c_validField = 1;
    }
    /**
     * Get method for element {}template
     */
    public com.sap.engine.lib.descriptors.j2ee.EmptyType getTemplate() {
      if (this._c_validField != 1) {
        return null;
      }
      return this._f_Template;
    }
    /**
     * Check method for element {}template
     */
    public boolean isSetTemplate() {
      return (this._c_validField ==1);
    }
    private com.sap.engine.lib.descriptors.connectorj2eeengine.ClonningType _f_Clonning;
    /**
     * Set method for element {}clonning
     */
    public void setClonning(com.sap.engine.lib.descriptors.connectorj2eeengine.ClonningType _Clonning) {
      if (this._c_validField != 0 && this._c_validField != 2) {
        this.unsetContent();
      }
      this._f_Clonning = _Clonning;
      this._c_validField = 2;
    }
    /**
     * Get method for element {}clonning
     */
    public com.sap.engine.lib.descriptors.connectorj2eeengine.ClonningType getClonning() {
      if (this._c_validField != 2) {
        return null;
      }
      return this._f_Clonning;
    }
    /**
     * Check method for element {}clonning
     */
    public boolean isSetClonning() {
      return (this._c_validField ==2);
    }
    /**
     * Common get method for choice type.
     */
    public java.lang.Object getContent() {
      switch (this._c_validField) {
        case 1: return this.getTemplate();
        case 2: return this.getClonning();
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
          this._f_Template = null;
          break;
        }
        case  2: {
          this._f_Clonning = null;
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
          if (this._f_Template != null) {
            if (typed._f_Template == null) return false;
            if (!this._f_Template.equals(typed._f_Template)) return false;
          } else {
            if (typed._f_Template != null) return false;
          }
          break;
        }
        case 2: {
          if (this._f_Clonning != null) {
            if (typed._f_Clonning == null) return false;
            if (!this._f_Clonning.equals(typed._f_Clonning)) return false;
          } else {
            if (typed._f_Clonning != null) return false;
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
          if (this._f_Template != null) {
            result+= this._f_Template.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 2: {
          if (this._f_Clonning != null) {
            result+= this._f_Clonning.hashCode();
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
    if (!(object instanceof ConnectorType)) return false;
    ConnectorType typed = (ConnectorType) object;
    if (this._f_Description != null) {
      if (typed._f_Description == null) return false;
      if (!this._f_Description.equals(typed._f_Description)) return false;
    } else {
      if (typed._f_Description != null) return false;
    }
    if (this._f_Resourceadapter != null) {
      if (typed._f_Resourceadapter == null) return false;
      if (!this._f_Resourceadapter.equals(typed._f_Resourceadapter)) return false;
    } else {
      if (typed._f_Resourceadapter != null) return false;
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
    if (this._f_Description != null) {
      result+= this._f_Description.hashCode();
    }
    if (this._f_Resourceadapter != null) {
      result+= this._f_Resourceadapter.hashCode();
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
