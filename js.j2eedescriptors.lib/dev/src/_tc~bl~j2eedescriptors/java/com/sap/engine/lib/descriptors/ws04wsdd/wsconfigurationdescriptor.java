﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 12:55:14 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04wsdd;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor}WSConfigurationDescriptor
 */
public  class WSConfigurationDescriptor implements java.io.Serializable,java.lang.Cloneable {

  // Attribute field for attribute {}web-support
  private java.lang.Boolean _a_WebSupport;
  /**
   * Set method for attribute {}web-support
   */
  public void setWebSupport(java.lang.Boolean _WebSupport) {
    this._a_WebSupport = _WebSupport;
  }
  /**
   * Get method for attribute {}web-support
   */
  public java.lang.Boolean getWebSupport() {
    return _a_WebSupport;
  }

  // Element field for element {}configuration-name
  private java.lang.String _f_ConfigurationName;
  /**
   * Set method for element {}configuration-name
   */
  public void setConfigurationName(java.lang.String _ConfigurationName) {
    this._f_ConfigurationName = _ConfigurationName;
  }
  /**
   * Get method for element {}configuration-name
   */
  public java.lang.String getConfigurationName() {
    return this._f_ConfigurationName;
  }

  // Element field for element {}impl-link
  private com.sap.engine.lib.descriptors.ws04wsdd.ImplLinkDescriptor _f_ImplLink;
  /**
   * Set method for element {}impl-link
   */
  public void setImplLink(com.sap.engine.lib.descriptors.ws04wsdd.ImplLinkDescriptor _ImplLink) {
    this._f_ImplLink = _ImplLink;
  }
  /**
   * Get method for element {}impl-link
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.ImplLinkDescriptor getImplLink() {
    return this._f_ImplLink;
  }

  // Element field for element {}ejb-name
  private java.lang.String _f_EjbName;
  /**
   * Set method for element {}ejb-name
   */
  public void setEjbName(java.lang.String _EjbName) {
    this._f_EjbName = _EjbName;
  }
  /**
   * Get method for element {}ejb-name
   */
  public java.lang.String getEjbName() {
    return this._f_EjbName;
  }

  // Element field for element {}service-endpoint-name
  private com.sap.engine.lib.descriptors.ws04wsdd.QNameDescriptor _f_ServiceEndpointName;
  /**
   * Set method for element {}service-endpoint-name
   */
  public void setServiceEndpointName(com.sap.engine.lib.descriptors.ws04wsdd.QNameDescriptor _ServiceEndpointName) {
    this._f_ServiceEndpointName = _ServiceEndpointName;
  }
  /**
   * Get method for element {}service-endpoint-name
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.QNameDescriptor getServiceEndpointName() {
    return this._f_ServiceEndpointName;
  }

  // Element field for element {}wsdl-porttype-name
  private com.sap.engine.lib.descriptors.ws04wsdd.QNameDescriptor _f_WsdlPorttypeName;
  /**
   * Set method for element {}wsdl-porttype-name
   */
  public void setWsdlPorttypeName(com.sap.engine.lib.descriptors.ws04wsdd.QNameDescriptor _WsdlPorttypeName) {
    this._f_WsdlPorttypeName = _WsdlPorttypeName;
  }
  /**
   * Get method for element {}wsdl-porttype-name
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.QNameDescriptor getWsdlPorttypeName() {
    return this._f_WsdlPorttypeName;
  }

  // Element field for element {}webservice-definition-ref
  private com.sap.engine.lib.descriptors.ws04wsdd.NameDescriptor _f_WebserviceDefinitionRef;
  /**
   * Set method for element {}webservice-definition-ref
   */
  public void setWebserviceDefinitionRef(com.sap.engine.lib.descriptors.ws04wsdd.NameDescriptor _WebserviceDefinitionRef) {
    this._f_WebserviceDefinitionRef = _WebserviceDefinitionRef;
  }
  /**
   * Get method for element {}webservice-definition-ref
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.NameDescriptor getWebserviceDefinitionRef() {
    return this._f_WebserviceDefinitionRef;
  }

  // Element field for element {}service-endpoint-vi-ref
  private com.sap.engine.lib.descriptors.ws04wsdd.NameDescriptor _f_ServiceEndpointViRef;
  /**
   * Set method for element {}service-endpoint-vi-ref
   */
  public void setServiceEndpointViRef(com.sap.engine.lib.descriptors.ws04wsdd.NameDescriptor _ServiceEndpointViRef) {
    this._f_ServiceEndpointViRef = _ServiceEndpointViRef;
  }
  /**
   * Get method for element {}service-endpoint-vi-ref
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.NameDescriptor getServiceEndpointViRef() {
    return this._f_ServiceEndpointViRef;
  }

  // Element field for element {}transport-binding
  private com.sap.engine.lib.descriptors.ws04wsdd.TrBindingDescriptor _f_TransportBinding;
  /**
   * Set method for element {}transport-binding
   */
  public void setTransportBinding(com.sap.engine.lib.descriptors.ws04wsdd.TrBindingDescriptor _TransportBinding) {
    this._f_TransportBinding = _TransportBinding;
  }
  /**
   * Get method for element {}transport-binding
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.TrBindingDescriptor getTransportBinding() {
    return this._f_TransportBinding;
  }

  // Element field for element {}target-server-url
  private java.lang.String _f_TargetServerUrl;
  /**
   * Set method for element {}target-server-url
   */
  public void setTargetServerUrl(java.lang.String _TargetServerUrl) {
    this._f_TargetServerUrl = _TargetServerUrl;
  }
  /**
   * Get method for element {}target-server-url
   */
  public java.lang.String getTargetServerUrl() {
    return this._f_TargetServerUrl;
  }

  // Element field for element {}transport-address
  private java.lang.String _f_TransportAddress;
  /**
   * Set method for element {}transport-address
   */
  public void setTransportAddress(java.lang.String _TransportAddress) {
    this._f_TransportAddress = _TransportAddress;
  }
  /**
   * Get method for element {}transport-address
   */
  public java.lang.String getTransportAddress() {
    return this._f_TransportAddress;
  }

  // Element field for element {}global-features
  private com.sap.engine.lib.descriptors.ws04wsdd.GlobalFeaturesDescriptor _f_GlobalFeatures;
  /**
   * Set method for element {}global-features
   */
  public void setGlobalFeatures(com.sap.engine.lib.descriptors.ws04wsdd.GlobalFeaturesDescriptor _GlobalFeatures) {
    this._f_GlobalFeatures = _GlobalFeatures;
  }
  /**
   * Get method for element {}global-features
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.GlobalFeaturesDescriptor getGlobalFeatures() {
    return this._f_GlobalFeatures;
  }

  // Element field for element {}operation-configuration
  private java.util.ArrayList _f_OperationConfiguration = new java.util.ArrayList();
  /**
   * Set method for element {}operation-configuration
   */
  public void setOperationConfiguration(com.sap.engine.lib.descriptors.ws04wsdd.OperationConfigurationDescriptor[] _OperationConfiguration) {
    this._f_OperationConfiguration.clear();
    if (_OperationConfiguration != null) {
      for (int i=0; i<_OperationConfiguration.length; i++) {
        if (_OperationConfiguration[i] != null)
          this._f_OperationConfiguration.add(_OperationConfiguration[i]);
      }
    }
  }
  /**
   * Get method for element {}operation-configuration
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.OperationConfigurationDescriptor[] getOperationConfiguration() {
    com.sap.engine.lib.descriptors.ws04wsdd.OperationConfigurationDescriptor[] result = new com.sap.engine.lib.descriptors.ws04wsdd.OperationConfigurationDescriptor[_f_OperationConfiguration.size()];
    _f_OperationConfiguration.toArray(result);
    return result;
  }

  // Element field for element {}outside-in-configuration
  private com.sap.engine.lib.descriptors.ws04wsdd.OutsideInConfiguration _f_OutsideInConfiguration;
  /**
   * Set method for element {}outside-in-configuration
   */
  public void setOutsideInConfiguration(com.sap.engine.lib.descriptors.ws04wsdd.OutsideInConfiguration _OutsideInConfiguration) {
    this._f_OutsideInConfiguration = _OutsideInConfiguration;
  }
  /**
   * Get method for element {}outside-in-configuration
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.OutsideInConfiguration getOutsideInConfiguration() {
    return this._f_OutsideInConfiguration;
  }

  // Element field for element {}security-roles-definition
  private com.sap.engine.lib.descriptors.ws04wsdd.SecurityRolesDefDescriptor _f_SecurityRolesDefinition;
  /**
   * Set method for element {}security-roles-definition
   */
  public void setSecurityRolesDefinition(com.sap.engine.lib.descriptors.ws04wsdd.SecurityRolesDefDescriptor _SecurityRolesDefinition) {
    this._f_SecurityRolesDefinition = _SecurityRolesDefinition;
  }
  /**
   * Get method for element {}security-roles-definition
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.SecurityRolesDefDescriptor getSecurityRolesDefinition() {
    return this._f_SecurityRolesDefinition;
  }

  // Element field for element {}entrypoint-settings
  private com.sap.engine.lib.descriptors.ws04wsdd.EntryPointSettingsDescriptor _f_EntrypointSettings;
  /**
   * Set method for element {}entrypoint-settings
   */
  public void setEntrypointSettings(com.sap.engine.lib.descriptors.ws04wsdd.EntryPointSettingsDescriptor _EntrypointSettings) {
    this._f_EntrypointSettings = _EntrypointSettings;
  }
  /**
   * Get method for element {}entrypoint-settings
   */
  public com.sap.engine.lib.descriptors.ws04wsdd.EntryPointSettingsDescriptor getEntrypointSettings() {
    return this._f_EntrypointSettings;
  }

  /**
   * Equals method implementation.
   */
  public boolean equals(Object object) {
    if (object == null) return false;
    if (!(object instanceof WSConfigurationDescriptor)) return false;
    WSConfigurationDescriptor typed = (WSConfigurationDescriptor) object;
    if (this._a_WebSupport != null) {
      if (typed._a_WebSupport == null) return false;
      if (!this._a_WebSupport.equals(typed._a_WebSupport)) return false;
    } else {
      if (typed._a_WebSupport != null) return false;
    }
    if (this._f_ConfigurationName != null) {
      if (typed._f_ConfigurationName == null) return false;
      if (!this._f_ConfigurationName.equals(typed._f_ConfigurationName)) return false;
    } else {
      if (typed._f_ConfigurationName != null) return false;
    }
    if (this._f_ImplLink != null) {
      if (typed._f_ImplLink == null) return false;
      if (!this._f_ImplLink.equals(typed._f_ImplLink)) return false;
    } else {
      if (typed._f_ImplLink != null) return false;
    }
    if (this._f_EjbName != null) {
      if (typed._f_EjbName == null) return false;
      if (!this._f_EjbName.equals(typed._f_EjbName)) return false;
    } else {
      if (typed._f_EjbName != null) return false;
    }
    if (this._f_ServiceEndpointName != null) {
      if (typed._f_ServiceEndpointName == null) return false;
      if (!this._f_ServiceEndpointName.equals(typed._f_ServiceEndpointName)) return false;
    } else {
      if (typed._f_ServiceEndpointName != null) return false;
    }
    if (this._f_WsdlPorttypeName != null) {
      if (typed._f_WsdlPorttypeName == null) return false;
      if (!this._f_WsdlPorttypeName.equals(typed._f_WsdlPorttypeName)) return false;
    } else {
      if (typed._f_WsdlPorttypeName != null) return false;
    }
    if (this._f_WebserviceDefinitionRef != null) {
      if (typed._f_WebserviceDefinitionRef == null) return false;
      if (!this._f_WebserviceDefinitionRef.equals(typed._f_WebserviceDefinitionRef)) return false;
    } else {
      if (typed._f_WebserviceDefinitionRef != null) return false;
    }
    if (this._f_ServiceEndpointViRef != null) {
      if (typed._f_ServiceEndpointViRef == null) return false;
      if (!this._f_ServiceEndpointViRef.equals(typed._f_ServiceEndpointViRef)) return false;
    } else {
      if (typed._f_ServiceEndpointViRef != null) return false;
    }
    if (this._f_TransportBinding != null) {
      if (typed._f_TransportBinding == null) return false;
      if (!this._f_TransportBinding.equals(typed._f_TransportBinding)) return false;
    } else {
      if (typed._f_TransportBinding != null) return false;
    }
    if (this._f_TargetServerUrl != null) {
      if (typed._f_TargetServerUrl == null) return false;
      if (!this._f_TargetServerUrl.equals(typed._f_TargetServerUrl)) return false;
    } else {
      if (typed._f_TargetServerUrl != null) return false;
    }
    if (this._f_TransportAddress != null) {
      if (typed._f_TransportAddress == null) return false;
      if (!this._f_TransportAddress.equals(typed._f_TransportAddress)) return false;
    } else {
      if (typed._f_TransportAddress != null) return false;
    }
    if (this._f_GlobalFeatures != null) {
      if (typed._f_GlobalFeatures == null) return false;
      if (!this._f_GlobalFeatures.equals(typed._f_GlobalFeatures)) return false;
    } else {
      if (typed._f_GlobalFeatures != null) return false;
    }
    com.sap.engine.lib.descriptors.ws04wsdd.OperationConfigurationDescriptor[] _f_OperationConfiguration1 = this.getOperationConfiguration();
    com.sap.engine.lib.descriptors.ws04wsdd.OperationConfigurationDescriptor[] _f_OperationConfiguration2 = typed.getOperationConfiguration();
    if (_f_OperationConfiguration1 != null) {
      if (_f_OperationConfiguration2 == null) return false;
      if (_f_OperationConfiguration1.length != _f_OperationConfiguration2.length) return false;
      for (int i1 = 0; i1 < _f_OperationConfiguration1.length ; i1++) {
        if (_f_OperationConfiguration1[i1] != null) {
          if (_f_OperationConfiguration2[i1] == null) return false;
          if (!_f_OperationConfiguration1[i1].equals(_f_OperationConfiguration2[i1])) return false;
        } else {
          if (_f_OperationConfiguration2[i1] != null) return false;
        }
      }
    } else {
      if (_f_OperationConfiguration2 != null) return false;
    }
    if (this._f_OutsideInConfiguration != null) {
      if (typed._f_OutsideInConfiguration == null) return false;
      if (!this._f_OutsideInConfiguration.equals(typed._f_OutsideInConfiguration)) return false;
    } else {
      if (typed._f_OutsideInConfiguration != null) return false;
    }
    if (this._f_SecurityRolesDefinition != null) {
      if (typed._f_SecurityRolesDefinition == null) return false;
      if (!this._f_SecurityRolesDefinition.equals(typed._f_SecurityRolesDefinition)) return false;
    } else {
      if (typed._f_SecurityRolesDefinition != null) return false;
    }
    if (this._f_EntrypointSettings != null) {
      if (typed._f_EntrypointSettings == null) return false;
      if (!this._f_EntrypointSettings.equals(typed._f_EntrypointSettings)) return false;
    } else {
      if (typed._f_EntrypointSettings != null) return false;
    }
    return true;
  }

  /**
   * Hashcode method implementation.
   */
  public int hashCode() {
    int result = 0;
    if (this._a_WebSupport != null) {
      result+= this._a_WebSupport.hashCode();
    }
    if (this._f_ConfigurationName != null) {
      result+= this._f_ConfigurationName.hashCode();
    }
    if (this._f_ImplLink != null) {
      result+= this._f_ImplLink.hashCode();
    }
    if (this._f_EjbName != null) {
      result+= this._f_EjbName.hashCode();
    }
    if (this._f_ServiceEndpointName != null) {
      result+= this._f_ServiceEndpointName.hashCode();
    }
    if (this._f_WsdlPorttypeName != null) {
      result+= this._f_WsdlPorttypeName.hashCode();
    }
    if (this._f_WebserviceDefinitionRef != null) {
      result+= this._f_WebserviceDefinitionRef.hashCode();
    }
    if (this._f_ServiceEndpointViRef != null) {
      result+= this._f_ServiceEndpointViRef.hashCode();
    }
    if (this._f_TransportBinding != null) {
      result+= this._f_TransportBinding.hashCode();
    }
    if (this._f_TargetServerUrl != null) {
      result+= this._f_TargetServerUrl.hashCode();
    }
    if (this._f_TransportAddress != null) {
      result+= this._f_TransportAddress.hashCode();
    }
    if (this._f_GlobalFeatures != null) {
      result+= this._f_GlobalFeatures.hashCode();
    }
    com.sap.engine.lib.descriptors.ws04wsdd.OperationConfigurationDescriptor[] _f_OperationConfiguration1 = this.getOperationConfiguration();
    if (_f_OperationConfiguration1 != null) {
      for (int i1 = 0; i1 < _f_OperationConfiguration1.length ; i1++) {
        if (_f_OperationConfiguration1[i1] != null) {
          result+= _f_OperationConfiguration1[i1].hashCode();
        }
      }
    }
    if (this._f_OutsideInConfiguration != null) {
      result+= this._f_OutsideInConfiguration.hashCode();
    }
    if (this._f_SecurityRolesDefinition != null) {
      result+= this._f_SecurityRolesDefinition.hashCode();
    }
    if (this._f_EntrypointSettings != null) {
      result+= this._f_EntrypointSettings.hashCode();
    }
    return result;
  }
}
