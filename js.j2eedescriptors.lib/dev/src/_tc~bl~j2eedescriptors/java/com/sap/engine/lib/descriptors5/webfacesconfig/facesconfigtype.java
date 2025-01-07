﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Jun 14 11:13:11 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webfacesconfig;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}faces-configType
 */
public  class FacesConfigType implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

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
  private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigVersionType _a_Version;
  /**
   * Set method for attribute {}version
   */
  public void setVersion(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigVersionType _Version) {
    this._a_Version = _Version;
  }
  /**
   * Get method for attribute {}version
   */
  public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigVersionType getVersion() {
    return _a_Version;
  }

  // Model group field class 
  public static class Choice1 implements java.io.Serializable,com.sap.engine.services.webservices.jaxrpc.encoding.IdenticObject {

    public Choice1() {
    }


    // // Active choise field
    private int _c_validField = 0;
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigApplicationType _f_Application;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}application
     */
    public void setApplication(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigApplicationType _Application) {
      if (this._c_validField != 0 && this._c_validField != 1) {
        this.unsetContent();
      }
      this._f_Application = _Application;
      this._c_validField = 1;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}application
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigApplicationType getApplication() {
      if (this._c_validField != 1) {
        return null;
      }
      return this._f_Application;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}application
     */
    public boolean isSetApplication() {
      return (this._c_validField ==1);
    }
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigFactoryType _f_Factory;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}factory
     */
    public void setFactory(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigFactoryType _Factory) {
      if (this._c_validField != 0 && this._c_validField != 2) {
        this.unsetContent();
      }
      this._f_Factory = _Factory;
      this._c_validField = 2;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}factory
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigFactoryType getFactory() {
      if (this._c_validField != 2) {
        return null;
      }
      return this._f_Factory;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}factory
     */
    public boolean isSetFactory() {
      return (this._c_validField ==2);
    }
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigComponentType _f_Component;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}component
     */
    public void setComponent(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigComponentType _Component) {
      if (this._c_validField != 0 && this._c_validField != 3) {
        this.unsetContent();
      }
      this._f_Component = _Component;
      this._c_validField = 3;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}component
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigComponentType getComponent() {
      if (this._c_validField != 3) {
        return null;
      }
      return this._f_Component;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}component
     */
    public boolean isSetComponent() {
      return (this._c_validField ==3);
    }
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigConverterType _f_Converter;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}converter
     */
    public void setConverter(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigConverterType _Converter) {
      if (this._c_validField != 0 && this._c_validField != 4) {
        this.unsetContent();
      }
      this._f_Converter = _Converter;
      this._c_validField = 4;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}converter
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigConverterType getConverter() {
      if (this._c_validField != 4) {
        return null;
      }
      return this._f_Converter;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}converter
     */
    public boolean isSetConverter() {
      return (this._c_validField ==4);
    }
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigManagedBeanType _f_ManagedBean;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}managed-bean
     */
    public void setManagedBean(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigManagedBeanType _ManagedBean) {
      if (this._c_validField != 0 && this._c_validField != 5) {
        this.unsetContent();
      }
      this._f_ManagedBean = _ManagedBean;
      this._c_validField = 5;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}managed-bean
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigManagedBeanType getManagedBean() {
      if (this._c_validField != 5) {
        return null;
      }
      return this._f_ManagedBean;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}managed-bean
     */
    public boolean isSetManagedBean() {
      return (this._c_validField ==5);
    }
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigNavigationRuleType _f_NavigationRule;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}navigation-rule
     */
    public void setNavigationRule(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigNavigationRuleType _NavigationRule) {
      if (this._c_validField != 0 && this._c_validField != 6) {
        this.unsetContent();
      }
      this._f_NavigationRule = _NavigationRule;
      this._c_validField = 6;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}navigation-rule
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigNavigationRuleType getNavigationRule() {
      if (this._c_validField != 6) {
        return null;
      }
      return this._f_NavigationRule;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}navigation-rule
     */
    public boolean isSetNavigationRule() {
      return (this._c_validField ==6);
    }
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigReferencedBeanType _f_ReferencedBean;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}referenced-bean
     */
    public void setReferencedBean(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigReferencedBeanType _ReferencedBean) {
      if (this._c_validField != 0 && this._c_validField != 7) {
        this.unsetContent();
      }
      this._f_ReferencedBean = _ReferencedBean;
      this._c_validField = 7;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}referenced-bean
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigReferencedBeanType getReferencedBean() {
      if (this._c_validField != 7) {
        return null;
      }
      return this._f_ReferencedBean;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}referenced-bean
     */
    public boolean isSetReferencedBean() {
      return (this._c_validField ==7);
    }
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigRenderKitType _f_RenderKit;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}render-kit
     */
    public void setRenderKit(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigRenderKitType _RenderKit) {
      if (this._c_validField != 0 && this._c_validField != 8) {
        this.unsetContent();
      }
      this._f_RenderKit = _RenderKit;
      this._c_validField = 8;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}render-kit
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigRenderKitType getRenderKit() {
      if (this._c_validField != 8) {
        return null;
      }
      return this._f_RenderKit;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}render-kit
     */
    public boolean isSetRenderKit() {
      return (this._c_validField ==8);
    }
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigLifecycleType _f_Lifecycle;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}lifecycle
     */
    public void setLifecycle(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigLifecycleType _Lifecycle) {
      if (this._c_validField != 0 && this._c_validField != 9) {
        this.unsetContent();
      }
      this._f_Lifecycle = _Lifecycle;
      this._c_validField = 9;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}lifecycle
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigLifecycleType getLifecycle() {
      if (this._c_validField != 9) {
        return null;
      }
      return this._f_Lifecycle;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}lifecycle
     */
    public boolean isSetLifecycle() {
      return (this._c_validField ==9);
    }
    private com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigValidatorType _f_Validator;
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}validator
     */
    public void setValidator(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigValidatorType _Validator) {
      if (this._c_validField != 0 && this._c_validField != 10) {
        this.unsetContent();
      }
      this._f_Validator = _Validator;
      this._c_validField = 10;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}validator
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigValidatorType getValidator() {
      if (this._c_validField != 10) {
        return null;
      }
      return this._f_Validator;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}validator
     */
    public boolean isSetValidator() {
      return (this._c_validField ==10);
    }
    private java.util.ArrayList _f_FacesConfigExtension = new java.util.ArrayList();
    /**
     * Set method for element {http://java.sun.com/xml/ns/javaee}faces-config-extension
     */
    public void setFacesConfigExtension(com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigExtensionType[] _FacesConfigExtension) {
      if (this._c_validField != 0 && this._c_validField != 11) {
        this.unsetContent();
      }
      this._f_FacesConfigExtension.clear();
      if (_FacesConfigExtension != null) {
        for (int i=0; i<_FacesConfigExtension.length; i++) {
          if (_FacesConfigExtension[i] != null)
            this._f_FacesConfigExtension.add(_FacesConfigExtension[i]);
        }
      }
      this._c_validField = 11;
    }
    /**
     * Get method for element {http://java.sun.com/xml/ns/javaee}faces-config-extension
     */
    public com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigExtensionType[] getFacesConfigExtension() {
      if (this._c_validField != 11) {
        return null;
      }
      com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigExtensionType[] result = new com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigExtensionType[_f_FacesConfigExtension.size()];
      _f_FacesConfigExtension.toArray(result);
      return result;
    }
    /**
     * Check method for element {http://java.sun.com/xml/ns/javaee}faces-config-extension
     */
    public boolean isSetFacesConfigExtension() {
      return (this._c_validField ==11);
    }
    /**
     * Common get method for choice type.
     */
    public java.lang.Object getContent() {
      switch (this._c_validField) {
        case 1: return this.getApplication();
        case 2: return this.getFactory();
        case 3: return this.getComponent();
        case 4: return this.getConverter();
        case 5: return this.getManagedBean();
        case 6: return this.getNavigationRule();
        case 7: return this.getReferencedBean();
        case 8: return this.getRenderKit();
        case 9: return this.getLifecycle();
        case 10: return this.getValidator();
        case 11: return this.getFacesConfigExtension();
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
          this._f_Application = null;
          break;
        }
        case  2: {
          this._f_Factory = null;
          break;
        }
        case  3: {
          this._f_Component = null;
          break;
        }
        case  4: {
          this._f_Converter = null;
          break;
        }
        case  5: {
          this._f_ManagedBean = null;
          break;
        }
        case  6: {
          this._f_NavigationRule = null;
          break;
        }
        case  7: {
          this._f_ReferencedBean = null;
          break;
        }
        case  8: {
          this._f_RenderKit = null;
          break;
        }
        case  9: {
          this._f_Lifecycle = null;
          break;
        }
        case  10: {
          this._f_Validator = null;
          break;
        }
        case  11: {
          this._f_FacesConfigExtension = null;
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
          if (this._f_Application != null) {
            if (typed._f_Application == null) return false;
            if (!this._f_Application.equals(typed._f_Application)) return false;
          } else {
            if (typed._f_Application != null) return false;
          }
          break;
        }
        case 2: {
          if (this._f_Factory != null) {
            if (typed._f_Factory == null) return false;
            if (!this._f_Factory.equals(typed._f_Factory)) return false;
          } else {
            if (typed._f_Factory != null) return false;
          }
          break;
        }
        case 3: {
          if (this._f_Component != null) {
            if (typed._f_Component == null) return false;
            if (!this._f_Component.equals(typed._f_Component)) return false;
          } else {
            if (typed._f_Component != null) return false;
          }
          break;
        }
        case 4: {
          if (this._f_Converter != null) {
            if (typed._f_Converter == null) return false;
            if (!this._f_Converter.equals(typed._f_Converter)) return false;
          } else {
            if (typed._f_Converter != null) return false;
          }
          break;
        }
        case 5: {
          if (this._f_ManagedBean != null) {
            if (typed._f_ManagedBean == null) return false;
            if (!this._f_ManagedBean.equals(typed._f_ManagedBean)) return false;
          } else {
            if (typed._f_ManagedBean != null) return false;
          }
          break;
        }
        case 6: {
          if (this._f_NavigationRule != null) {
            if (typed._f_NavigationRule == null) return false;
            if (!this._f_NavigationRule.equals(typed._f_NavigationRule)) return false;
          } else {
            if (typed._f_NavigationRule != null) return false;
          }
          break;
        }
        case 7: {
          if (this._f_ReferencedBean != null) {
            if (typed._f_ReferencedBean == null) return false;
            if (!this._f_ReferencedBean.equals(typed._f_ReferencedBean)) return false;
          } else {
            if (typed._f_ReferencedBean != null) return false;
          }
          break;
        }
        case 8: {
          if (this._f_RenderKit != null) {
            if (typed._f_RenderKit == null) return false;
            if (!this._f_RenderKit.equals(typed._f_RenderKit)) return false;
          } else {
            if (typed._f_RenderKit != null) return false;
          }
          break;
        }
        case 9: {
          if (this._f_Lifecycle != null) {
            if (typed._f_Lifecycle == null) return false;
            if (!this._f_Lifecycle.equals(typed._f_Lifecycle)) return false;
          } else {
            if (typed._f_Lifecycle != null) return false;
          }
          break;
        }
        case 10: {
          if (this._f_Validator != null) {
            if (typed._f_Validator == null) return false;
            if (!this._f_Validator.equals(typed._f_Validator)) return false;
          } else {
            if (typed._f_Validator != null) return false;
          }
          break;
        }
        case 11: {
          com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigExtensionType[] _f_FacesConfigExtension1 = this.getFacesConfigExtension();
          com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigExtensionType[] _f_FacesConfigExtension2 = typed.getFacesConfigExtension();
          if (_f_FacesConfigExtension1 != null) {
            if (_f_FacesConfigExtension2 == null) return false;
            if (_f_FacesConfigExtension1.length != _f_FacesConfigExtension2.length) return false;
            for (int i1 = 0; i1 < _f_FacesConfigExtension1.length ; i1++) {
              if (_f_FacesConfigExtension1[i1] != null) {
                if (_f_FacesConfigExtension2[i1] == null) return false;
                if (!_f_FacesConfigExtension1[i1].equals(_f_FacesConfigExtension2[i1])) return false;
              } else {
                if (_f_FacesConfigExtension2[i1] != null) return false;
              }
            }
          } else {
            if (_f_FacesConfigExtension2 != null) return false;
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
          if (this._f_Application != null) {
            result+= this._f_Application.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 2: {
          if (this._f_Factory != null) {
            result+= this._f_Factory.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 3: {
          if (this._f_Component != null) {
            result+= this._f_Component.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 4: {
          if (this._f_Converter != null) {
            result+= this._f_Converter.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 5: {
          if (this._f_ManagedBean != null) {
            result+= this._f_ManagedBean.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 6: {
          if (this._f_NavigationRule != null) {
            result+= this._f_NavigationRule.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 7: {
          if (this._f_ReferencedBean != null) {
            result+= this._f_ReferencedBean.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 8: {
          if (this._f_RenderKit != null) {
            result+= this._f_RenderKit.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 9: {
          if (this._f_Lifecycle != null) {
            result+= this._f_Lifecycle.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 10: {
          if (this._f_Validator != null) {
            result+= this._f_Validator.hashCode();
          }
          result = result * this._c_validField;
          break;
        }
        case 11: {
          com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigExtensionType[] _f_FacesConfigExtension1 = this.getFacesConfigExtension();
          if (_f_FacesConfigExtension1 != null) {
            for (int i1 = 0; i1 < _f_FacesConfigExtension1.length ; i1++) {
              if (_f_FacesConfigExtension1[i1] != null) {
                result+= _f_FacesConfigExtension1[i1].hashCode();
              }
            }
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
    if (!(object instanceof FacesConfigType)) return false;
    FacesConfigType typed = (FacesConfigType) object;
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
    if (this._a_Version != null) {
      result+= this._a_Version.hashCode();
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
