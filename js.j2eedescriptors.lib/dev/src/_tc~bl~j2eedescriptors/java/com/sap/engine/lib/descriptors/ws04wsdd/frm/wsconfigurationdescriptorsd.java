﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 12:55:14 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04wsdd.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor}WSConfigurationDescriptor
 */
public  class WSConfigurationDescriptorSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
  }

  public java.lang.String _d_originalLocalName() {
    return "WSConfigurationDescriptor";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[1];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "web-support";
    ATTRIBUTEINFO[0].fieldUri = "";
    ATTRIBUTEINFO[0].fieldJavaName = "WebSupport";
    ATTRIBUTEINFO[0].typeName = "boolean";
    ATTRIBUTEINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[0].typeJavaName = "java.lang.Boolean";
    ATTRIBUTEINFO[0].defaultValue = null;
    ATTRIBUTEINFO[0].required = false;
    ATTRIBUTEINFO[0].setterMethod = "setWebSupport";
    ATTRIBUTEINFO[0].getterMethod = "getWebSupport";
    ATTRIBUTEINFO[0].checkMethod = null;
  }

  // Field information
  private static com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[] FIELDINFO;

  private synchronized static void initFields() {
    // Creating fields
    if (FIELDINFO != null) return;
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[15];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[3] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[4] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[5] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[6] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[7] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[8] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[9] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[10] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[11] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[12] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[13] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[14] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "ConfigurationName";
    FIELDINFO[0].fieldLocalName = "configuration-name";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 1;
    FIELDINFO[0].minOccurs = 1;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "java.lang.String";
    FIELDINFO[0].typeLocalName = "string";
    FIELDINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[0].getterMethod = "getConfigurationName";
    FIELDINFO[0].setterMethod = "setConfigurationName";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "ImplLink";
    FIELDINFO[1].fieldLocalName = "impl-link";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 1;
    FIELDINFO[1].minOccurs = 0;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.ImplLinkDescriptor";
    FIELDINFO[1].typeLocalName = "ImplLinkDescriptor";
    FIELDINFO[1].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[1].getterMethod = "getImplLink";
    FIELDINFO[1].setterMethod = "setImplLink";
    FIELDINFO[1].checkMethod = null;
    // Field 2
    FIELDINFO[2].defaultValue = null;
    FIELDINFO[2].fieldJavaName = "EjbName";
    FIELDINFO[2].fieldLocalName = "ejb-name";
    FIELDINFO[2].fieldModel = 1;
    FIELDINFO[2].fieldUri = "";
    FIELDINFO[2].isSoapArray = false;
    FIELDINFO[2].maxOccurs = 1;
    FIELDINFO[2].minOccurs = 0;
    FIELDINFO[2].nillable = false;
    FIELDINFO[2].soapArrayDimensions = 0;
    FIELDINFO[2].soapArrayItemTypeJavaName = null;
    FIELDINFO[2].soapArrayItemTypeLocalName = null;
    FIELDINFO[2].soapArrayItemTypeUri = null;
    FIELDINFO[2].typeJavaName = "java.lang.String";
    FIELDINFO[2].typeLocalName = "string";
    FIELDINFO[2].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[2].getterMethod = "getEjbName";
    FIELDINFO[2].setterMethod = "setEjbName";
    FIELDINFO[2].checkMethod = null;
    // Field 3
    FIELDINFO[3].defaultValue = null;
    FIELDINFO[3].fieldJavaName = "ServiceEndpointName";
    FIELDINFO[3].fieldLocalName = "service-endpoint-name";
    FIELDINFO[3].fieldModel = 1;
    FIELDINFO[3].fieldUri = "";
    FIELDINFO[3].isSoapArray = false;
    FIELDINFO[3].maxOccurs = 1;
    FIELDINFO[3].minOccurs = 1;
    FIELDINFO[3].nillable = false;
    FIELDINFO[3].soapArrayDimensions = 0;
    FIELDINFO[3].soapArrayItemTypeJavaName = null;
    FIELDINFO[3].soapArrayItemTypeLocalName = null;
    FIELDINFO[3].soapArrayItemTypeUri = null;
    FIELDINFO[3].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.QNameDescriptor";
    FIELDINFO[3].typeLocalName = "QNameDescriptor";
    FIELDINFO[3].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[3].getterMethod = "getServiceEndpointName";
    FIELDINFO[3].setterMethod = "setServiceEndpointName";
    FIELDINFO[3].checkMethod = null;
    // Field 4
    FIELDINFO[4].defaultValue = null;
    FIELDINFO[4].fieldJavaName = "WsdlPorttypeName";
    FIELDINFO[4].fieldLocalName = "wsdl-porttype-name";
    FIELDINFO[4].fieldModel = 1;
    FIELDINFO[4].fieldUri = "";
    FIELDINFO[4].isSoapArray = false;
    FIELDINFO[4].maxOccurs = 1;
    FIELDINFO[4].minOccurs = 1;
    FIELDINFO[4].nillable = false;
    FIELDINFO[4].soapArrayDimensions = 0;
    FIELDINFO[4].soapArrayItemTypeJavaName = null;
    FIELDINFO[4].soapArrayItemTypeLocalName = null;
    FIELDINFO[4].soapArrayItemTypeUri = null;
    FIELDINFO[4].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.QNameDescriptor";
    FIELDINFO[4].typeLocalName = "QNameDescriptor";
    FIELDINFO[4].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[4].getterMethod = "getWsdlPorttypeName";
    FIELDINFO[4].setterMethod = "setWsdlPorttypeName";
    FIELDINFO[4].checkMethod = null;
    // Field 5
    FIELDINFO[5].defaultValue = null;
    FIELDINFO[5].fieldJavaName = "WebserviceDefinitionRef";
    FIELDINFO[5].fieldLocalName = "webservice-definition-ref";
    FIELDINFO[5].fieldModel = 1;
    FIELDINFO[5].fieldUri = "";
    FIELDINFO[5].isSoapArray = false;
    FIELDINFO[5].maxOccurs = 1;
    FIELDINFO[5].minOccurs = 1;
    FIELDINFO[5].nillable = false;
    FIELDINFO[5].soapArrayDimensions = 0;
    FIELDINFO[5].soapArrayItemTypeJavaName = null;
    FIELDINFO[5].soapArrayItemTypeLocalName = null;
    FIELDINFO[5].soapArrayItemTypeUri = null;
    FIELDINFO[5].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.NameDescriptor";
    FIELDINFO[5].typeLocalName = "NameDescriptor";
    FIELDINFO[5].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[5].getterMethod = "getWebserviceDefinitionRef";
    FIELDINFO[5].setterMethod = "setWebserviceDefinitionRef";
    FIELDINFO[5].checkMethod = null;
    // Field 6
    FIELDINFO[6].defaultValue = null;
    FIELDINFO[6].fieldJavaName = "ServiceEndpointViRef";
    FIELDINFO[6].fieldLocalName = "service-endpoint-vi-ref";
    FIELDINFO[6].fieldModel = 1;
    FIELDINFO[6].fieldUri = "";
    FIELDINFO[6].isSoapArray = false;
    FIELDINFO[6].maxOccurs = 1;
    FIELDINFO[6].minOccurs = 1;
    FIELDINFO[6].nillable = false;
    FIELDINFO[6].soapArrayDimensions = 0;
    FIELDINFO[6].soapArrayItemTypeJavaName = null;
    FIELDINFO[6].soapArrayItemTypeLocalName = null;
    FIELDINFO[6].soapArrayItemTypeUri = null;
    FIELDINFO[6].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.NameDescriptor";
    FIELDINFO[6].typeLocalName = "NameDescriptor";
    FIELDINFO[6].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[6].getterMethod = "getServiceEndpointViRef";
    FIELDINFO[6].setterMethod = "setServiceEndpointViRef";
    FIELDINFO[6].checkMethod = null;
    // Field 7
    FIELDINFO[7].defaultValue = null;
    FIELDINFO[7].fieldJavaName = "TransportBinding";
    FIELDINFO[7].fieldLocalName = "transport-binding";
    FIELDINFO[7].fieldModel = 1;
    FIELDINFO[7].fieldUri = "";
    FIELDINFO[7].isSoapArray = false;
    FIELDINFO[7].maxOccurs = 1;
    FIELDINFO[7].minOccurs = 1;
    FIELDINFO[7].nillable = false;
    FIELDINFO[7].soapArrayDimensions = 0;
    FIELDINFO[7].soapArrayItemTypeJavaName = null;
    FIELDINFO[7].soapArrayItemTypeLocalName = null;
    FIELDINFO[7].soapArrayItemTypeUri = null;
    FIELDINFO[7].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.TrBindingDescriptor";
    FIELDINFO[7].typeLocalName = "TrBindingDescriptor";
    FIELDINFO[7].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[7].getterMethod = "getTransportBinding";
    FIELDINFO[7].setterMethod = "setTransportBinding";
    FIELDINFO[7].checkMethod = null;
    // Field 8
    FIELDINFO[8].defaultValue = null;
    FIELDINFO[8].fieldJavaName = "TargetServerUrl";
    FIELDINFO[8].fieldLocalName = "target-server-url";
    FIELDINFO[8].fieldModel = 1;
    FIELDINFO[8].fieldUri = "";
    FIELDINFO[8].isSoapArray = false;
    FIELDINFO[8].maxOccurs = 1;
    FIELDINFO[8].minOccurs = 0;
    FIELDINFO[8].nillable = false;
    FIELDINFO[8].soapArrayDimensions = 0;
    FIELDINFO[8].soapArrayItemTypeJavaName = null;
    FIELDINFO[8].soapArrayItemTypeLocalName = null;
    FIELDINFO[8].soapArrayItemTypeUri = null;
    FIELDINFO[8].typeJavaName = "java.lang.String";
    FIELDINFO[8].typeLocalName = "string";
    FIELDINFO[8].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[8].getterMethod = "getTargetServerUrl";
    FIELDINFO[8].setterMethod = "setTargetServerUrl";
    FIELDINFO[8].checkMethod = null;
    // Field 9
    FIELDINFO[9].defaultValue = null;
    FIELDINFO[9].fieldJavaName = "TransportAddress";
    FIELDINFO[9].fieldLocalName = "transport-address";
    FIELDINFO[9].fieldModel = 1;
    FIELDINFO[9].fieldUri = "";
    FIELDINFO[9].isSoapArray = false;
    FIELDINFO[9].maxOccurs = 1;
    FIELDINFO[9].minOccurs = 1;
    FIELDINFO[9].nillable = false;
    FIELDINFO[9].soapArrayDimensions = 0;
    FIELDINFO[9].soapArrayItemTypeJavaName = null;
    FIELDINFO[9].soapArrayItemTypeLocalName = null;
    FIELDINFO[9].soapArrayItemTypeUri = null;
    FIELDINFO[9].typeJavaName = "java.lang.String";
    FIELDINFO[9].typeLocalName = "string";
    FIELDINFO[9].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[9].getterMethod = "getTransportAddress";
    FIELDINFO[9].setterMethod = "setTransportAddress";
    FIELDINFO[9].checkMethod = null;
    // Field 10
    FIELDINFO[10].defaultValue = null;
    FIELDINFO[10].fieldJavaName = "GlobalFeatures";
    FIELDINFO[10].fieldLocalName = "global-features";
    FIELDINFO[10].fieldModel = 1;
    FIELDINFO[10].fieldUri = "";
    FIELDINFO[10].isSoapArray = false;
    FIELDINFO[10].maxOccurs = 1;
    FIELDINFO[10].minOccurs = 0;
    FIELDINFO[10].nillable = false;
    FIELDINFO[10].soapArrayDimensions = 0;
    FIELDINFO[10].soapArrayItemTypeJavaName = null;
    FIELDINFO[10].soapArrayItemTypeLocalName = null;
    FIELDINFO[10].soapArrayItemTypeUri = null;
    FIELDINFO[10].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.GlobalFeaturesDescriptor";
    FIELDINFO[10].typeLocalName = "GlobalFeaturesDescriptor";
    FIELDINFO[10].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[10].getterMethod = "getGlobalFeatures";
    FIELDINFO[10].setterMethod = "setGlobalFeatures";
    FIELDINFO[10].checkMethod = null;
    // Field 11
    FIELDINFO[11].defaultValue = null;
    FIELDINFO[11].fieldJavaName = "OperationConfiguration";
    FIELDINFO[11].fieldLocalName = "operation-configuration";
    FIELDINFO[11].fieldModel = 1;
    FIELDINFO[11].fieldUri = "";
    FIELDINFO[11].isSoapArray = false;
    FIELDINFO[11].maxOccurs = 2147483647;
    FIELDINFO[11].minOccurs = 0;
    FIELDINFO[11].nillable = false;
    FIELDINFO[11].soapArrayDimensions = 0;
    FIELDINFO[11].soapArrayItemTypeJavaName = null;
    FIELDINFO[11].soapArrayItemTypeLocalName = null;
    FIELDINFO[11].soapArrayItemTypeUri = null;
    FIELDINFO[11].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.OperationConfigurationDescriptor[]";
    FIELDINFO[11].typeLocalName = "OperationConfigurationDescriptor";
    FIELDINFO[11].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[11].getterMethod = "getOperationConfiguration";
    FIELDINFO[11].setterMethod = "setOperationConfiguration";
    FIELDINFO[11].checkMethod = null;
    // Field 12
    FIELDINFO[12].defaultValue = null;
    FIELDINFO[12].fieldJavaName = "OutsideInConfiguration";
    FIELDINFO[12].fieldLocalName = "outside-in-configuration";
    FIELDINFO[12].fieldModel = 1;
    FIELDINFO[12].fieldUri = "";
    FIELDINFO[12].isSoapArray = false;
    FIELDINFO[12].maxOccurs = 1;
    FIELDINFO[12].minOccurs = 0;
    FIELDINFO[12].nillable = false;
    FIELDINFO[12].soapArrayDimensions = 0;
    FIELDINFO[12].soapArrayItemTypeJavaName = null;
    FIELDINFO[12].soapArrayItemTypeLocalName = null;
    FIELDINFO[12].soapArrayItemTypeUri = null;
    FIELDINFO[12].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.OutsideInConfiguration";
    FIELDINFO[12].typeLocalName = "OutsideInConfiguration";
    FIELDINFO[12].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[12].getterMethod = "getOutsideInConfiguration";
    FIELDINFO[12].setterMethod = "setOutsideInConfiguration";
    FIELDINFO[12].checkMethod = null;
    // Field 13
    FIELDINFO[13].defaultValue = null;
    FIELDINFO[13].fieldJavaName = "SecurityRolesDefinition";
    FIELDINFO[13].fieldLocalName = "security-roles-definition";
    FIELDINFO[13].fieldModel = 1;
    FIELDINFO[13].fieldUri = "";
    FIELDINFO[13].isSoapArray = false;
    FIELDINFO[13].maxOccurs = 1;
    FIELDINFO[13].minOccurs = 0;
    FIELDINFO[13].nillable = false;
    FIELDINFO[13].soapArrayDimensions = 0;
    FIELDINFO[13].soapArrayItemTypeJavaName = null;
    FIELDINFO[13].soapArrayItemTypeLocalName = null;
    FIELDINFO[13].soapArrayItemTypeUri = null;
    FIELDINFO[13].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.SecurityRolesDefDescriptor";
    FIELDINFO[13].typeLocalName = "SecurityRolesDefDescriptor";
    FIELDINFO[13].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[13].getterMethod = "getSecurityRolesDefinition";
    FIELDINFO[13].setterMethod = "setSecurityRolesDefinition";
    FIELDINFO[13].checkMethod = null;
    // Field 14
    FIELDINFO[14].defaultValue = null;
    FIELDINFO[14].fieldJavaName = "EntrypointSettings";
    FIELDINFO[14].fieldLocalName = "entrypoint-settings";
    FIELDINFO[14].fieldModel = 1;
    FIELDINFO[14].fieldUri = "";
    FIELDINFO[14].isSoapArray = false;
    FIELDINFO[14].maxOccurs = 1;
    FIELDINFO[14].minOccurs = 0;
    FIELDINFO[14].nillable = false;
    FIELDINFO[14].soapArrayDimensions = 0;
    FIELDINFO[14].soapArrayItemTypeJavaName = null;
    FIELDINFO[14].soapArrayItemTypeLocalName = null;
    FIELDINFO[14].soapArrayItemTypeUri = null;
    FIELDINFO[14].typeJavaName = "com.sap.engine.lib.descriptors.ws04wsdd.EntryPointSettingsDescriptor";
    FIELDINFO[14].typeLocalName = "EntryPointSettingsDescriptor";
    FIELDINFO[14].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
    FIELDINFO[14].getterMethod = "getEntrypointSettings";
    FIELDINFO[14].setterMethod = "setEntrypointSettings";
    FIELDINFO[14].checkMethod = null;
  }


  // Returns model Group Type
  public int _getModelType() {
    return 3;
  }

  private static boolean init = false;
  public synchronized com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[] _getFields() {
    if (init == false) {
      com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[] parent = super._getFields();
      FIELDINFO =  _insertFieldInfo(parent,FIELDINFO);
      init = true;
    }
    return FIELDINFO;
  }

  public int _getNumberOfFields() {
    return (FIELDINFO.length+super._getNumberOfFields());
  }

  public com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] _getAttributes() {
    return ATTRIBUTEINFO;
  }

  public int _getNumberOfAttributes() {
    return ATTRIBUTEINFO.length;
  }

  static {
    initFields();
    initAttribs();
  }
}
