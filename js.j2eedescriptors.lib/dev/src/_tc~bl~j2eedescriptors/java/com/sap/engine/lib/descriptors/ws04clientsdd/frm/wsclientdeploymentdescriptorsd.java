﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Mar 12 17:51:19 EET 2008
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04clientsdd.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/630/soap/java/descriptors/ws-clients-deployment-descriptor}WSClientDeploymentDescriptor
 */
public  class WSClientDeploymentDescriptorSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://www.sap.com/webas/630/soap/java/descriptors/ws-clients-deployment-descriptor";
  }

  public java.lang.String _d_originalLocalName() {
    return "WSClientDeploymentDescriptor";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[2];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "version";
    ATTRIBUTEINFO[0].fieldUri = "";
    ATTRIBUTEINFO[0].fieldJavaName = "Version";
    ATTRIBUTEINFO[0].typeName = "string";
    ATTRIBUTEINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[0].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[0].defaultValue = "6.30";
    ATTRIBUTEINFO[0].required = false;
    ATTRIBUTEINFO[0].setterMethod = "setVersion";
    ATTRIBUTEINFO[0].getterMethod = "getVersion";
    ATTRIBUTEINFO[0].checkMethod = null;
    // Attribute 1
    ATTRIBUTEINFO[1].fieldLocalName = "keep-runtime-mode";
    ATTRIBUTEINFO[1].fieldUri = "";
    ATTRIBUTEINFO[1].fieldJavaName = "KeepRuntimeMode";
    ATTRIBUTEINFO[1].typeName = "boolean";
    ATTRIBUTEINFO[1].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[1].typeJavaName = "boolean";
    ATTRIBUTEINFO[1].defaultValue = "false";
    ATTRIBUTEINFO[1].required = false;
    ATTRIBUTEINFO[1].setterMethod = "setKeepRuntimeMode";
    ATTRIBUTEINFO[1].getterMethod = "isKeepRuntimeMode";
    ATTRIBUTEINFO[1].checkMethod = null;
  }

  // Field information
  private static com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[] FIELDINFO;

  private synchronized static void initFields() {
    // Creating fields
    if (FIELDINFO != null) return;
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[2];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "ComponentScopedRefs";
    FIELDINFO[0].fieldLocalName = "component-scoped-refs";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 2147483647;
    FIELDINFO[0].minOccurs = 0;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors.ws04clientsdd.ComponentScopedRefsDescriptor[]";
    FIELDINFO[0].typeLocalName = "ComponentScopedRefsDescriptor";
    FIELDINFO[0].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-clients-deployment-descriptor";
    FIELDINFO[0].getterMethod = "getComponentScopedRefs";
    FIELDINFO[0].setterMethod = "setComponentScopedRefs";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "ServiceRef";
    FIELDINFO[1].fieldLocalName = "service-ref";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 2147483647;
    FIELDINFO[1].minOccurs = 0;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors.ws04clientsdd.ServiceRefDescriptor[]";
    FIELDINFO[1].typeLocalName = "ServiceRefDescriptor";
    FIELDINFO[1].typeUri = "http://www.sap.com/webas/630/soap/java/descriptors/ws-clients-deployment-descriptor";
    FIELDINFO[1].getterMethod = "getServiceRef";
    FIELDINFO[1].setterMethod = "setServiceRef";
    FIELDINFO[1].checkMethod = null;
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
