﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:07:06 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ejb;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/j2ee}enterprise-beansType
 */
public  class EnterpriseBeansType implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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

  // Model group field class 
  public static class Choice1 implements java.io.Serializable,java.lang.Cloneable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

    public Choice1() {
    }


    // // Active choise field
    private int _c_validField = 0;
    private com.sap.engine.lib.descriptors.ejb.SessionBeanType _f_Session;
    /**
     * Set method for element {http://java.sun.com/xml/ns/j2ee}session
     */
    public void setSession(com.sap.engine.lib.descriptors.ejb.SessionBeanType _Session) {
      if (this._c_validField != 0 && this._c_validField != 1) {
        this.unsetContent();
      }
      this._f_Session = _Session;
      this._c_validField = 1;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/j2ee}session
     */
    public com.sap.engine.lib.descriptors.ejb.SessionBeanType getSession() {
      if (this._c_validField != 1) {
        return null;
      }
      return this._f_Session;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/j2ee}session
     */
    public boolean isSetSession() {
      return (this._c_validField ==1);
    }
    private com.sap.engine.lib.descriptors.ejb.EntityBeanType _f_Entity;
    /**
     * Set method for element {http://java.sun.com/xml/ns/j2ee}entity
     */
    public void setEntity(com.sap.engine.lib.descriptors.ejb.EntityBeanType _Entity) {
      if (this._c_validField != 0 && this._c_validField != 2) {
        this.unsetContent();
      }
      this._f_Entity = _Entity;
      this._c_validField = 2;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/j2ee}entity
     */
    public com.sap.engine.lib.descriptors.ejb.EntityBeanType getEntity() {
      if (this._c_validField != 2) {
        return null;
      }
      return this._f_Entity;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/j2ee}entity
     */
    public boolean isSetEntity() {
      return (this._c_validField ==2);
    }
    private com.sap.engine.lib.descriptors.ejb.MessageDrivenBeanType _f_MessageDriven;
    /**
     * Set method for element {http://java.sun.com/xml/ns/j2ee}message-driven
     */
    public void setMessageDriven(com.sap.engine.lib.descriptors.ejb.MessageDrivenBeanType _MessageDriven) {
      if (this._c_validField != 0 && this._c_validField != 3) {
        this.unsetContent();
      }
      this._f_MessageDriven = _MessageDriven;
      this._c_validField = 3;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/j2ee}message-driven
     */
    public com.sap.engine.lib.descriptors.ejb.MessageDrivenBeanType getMessageDriven() {
      if (this._c_validField != 3) {
        return null;
      }
      return this._f_MessageDriven;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/j2ee}message-driven
     */
    public boolean isSetMessageDriven() {
      return (this._c_validField ==3);
    }
    /**
     * Common get method for choice type.
     */
    public java.lang.Object getContent() {
      switch (this._c_validField) {
        case 1: return this._f_Session;
        case 2: return this._f_Entity;
        case 3: return this._f_MessageDriven;
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
          this._f_Session = null;
          break;
        }
        case  2: {
          this._f_Entity = null;
          break;
        }
        case  3: {
          this._f_MessageDriven = null;
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
          if (this._f_Session != null) {
            if (typed._f_Session == null) return false;
            if (!this._f_Session.equals(typed._f_Session)) return false;
          } else {
            if (typed._f_Session != null) return false;
          }
          break;
        }
        case 2: {
          if (this._f_Entity != null) {
            if (typed._f_Entity == null) return false;
            if (!this._f_Entity.equals(typed._f_Entity)) return false;
          } else {
            if (typed._f_Entity != null) return false;
          }
          break;
        }
        case 3: {
          if (this._f_MessageDriven != null) {
            if (typed._f_MessageDriven == null) return false;
            if (!this._f_MessageDriven.equals(typed._f_MessageDriven)) return false;
          } else {
            if (typed._f_MessageDriven != null) return false;
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
          if (this._f_Session != null) {
            result+= this._f_Session.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 2: {
          if (this._f_Entity != null) {
            result+= this._f_Entity.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 3: {
          if (this._f_MessageDriven != null) {
            result+= this._f_MessageDriven.hashCode();
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

  private java.util.ArrayList _f_ChoiceGroup1 = new java.util.ArrayList();
  public void setChoiceGroup1(Choice1[] _ChoiceGroup1) {
    this._f_ChoiceGroup1.clear();
    if (_ChoiceGroup1 != null) {
      for (int i=0; i<_ChoiceGroup1.length; i++) {
        if (_ChoiceGroup1[i] != null)
          this._f_ChoiceGroup1.add(_ChoiceGroup1[i]);
      }
    }
  }
  public Choice1[] getChoiceGroup1() {
    Choice1[] result = new Choice1[_f_ChoiceGroup1.size()];
    _f_ChoiceGroup1.toArray(result);
    return result;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof EnterpriseBeansType)) return false;
    EnterpriseBeansType typed = (EnterpriseBeansType) object;
    if (this._a_Id != null) {
      if (typed._a_Id == null) return false;
      if (!this._a_Id.equals(typed._a_Id)) return false;
    } else {
      if (typed._a_Id != null) return false;
    }
    Choice1[] _f_ChoiceGroup11 = this.getChoiceGroup1();
    Choice1[] _f_ChoiceGroup12 = typed.getChoiceGroup1();
    if (_f_ChoiceGroup11 != null) {
      if (_f_ChoiceGroup12 == null) return false;
      if (_f_ChoiceGroup11.length != _f_ChoiceGroup12.length) return false;
      for (int i1 = 0; i1 < _f_ChoiceGroup11.length ; i1++) {
        if (_f_ChoiceGroup11[i1] != null) {
          if (_f_ChoiceGroup12[i1] == null) return false;
          if (!_f_ChoiceGroup11[i1].equals(_f_ChoiceGroup12[i1])) return false;
        } else {
          if (_f_ChoiceGroup12[i1] != null) return false;
        }
      }
    } else {
      if (_f_ChoiceGroup12 != null) return false;
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
    Choice1[] _f_ChoiceGroup11 = this.getChoiceGroup1();
    if (_f_ChoiceGroup11 != null) {
      for (int i1 = 0; i1 < _f_ChoiceGroup11.length ; i1++) {
        if (_f_ChoiceGroup11[i1] != null) {
          result+= _f_ChoiceGroup11[i1].hashCode();
        }
      }
    }
    return result;
  }

  public java.lang.String get__ID() {
    return java.lang.String.valueOf(super.hashCode());
  }
}
