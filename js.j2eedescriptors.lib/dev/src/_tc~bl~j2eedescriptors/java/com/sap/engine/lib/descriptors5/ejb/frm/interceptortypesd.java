﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Oct 19 11:20:17 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ejb.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}interceptorType
 */
public  class InterceptorTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/javaee";
  }

  public java.lang.String _d_originalLocalName() {
    return "interceptorType";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[1];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "id";
    ATTRIBUTEINFO[0].fieldUri = "";
    ATTRIBUTEINFO[0].fieldJavaName = "Id";
    ATTRIBUTEINFO[0].typeName = "ID";
    ATTRIBUTEINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[0].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[0].defaultValue = null;
    ATTRIBUTEINFO[0].required = false;
    ATTRIBUTEINFO[0].setterMethod = "setId";
    ATTRIBUTEINFO[0].getterMethod = "getId";
    ATTRIBUTEINFO[0].checkMethod = null;
  }

  // Field information
  private static com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[] FIELDINFO;

  private synchronized static void initFields() {
    // Creating fields
    if (FIELDINFO != null) return;
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[16];
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
    FIELDINFO[15] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "Description";
    FIELDINFO[0].fieldLocalName = "description";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 2147483647;
    FIELDINFO[0].minOccurs = 0;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.DescriptionType[]";
    FIELDINFO[0].typeLocalName = "descriptionType";
    FIELDINFO[0].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[0].getterMethod = "getDescription";
    FIELDINFO[0].setterMethod = "setDescription";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "InterceptorClass";
    FIELDINFO[1].fieldLocalName = "interceptor-class";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 1;
    FIELDINFO[1].minOccurs = 1;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType";
    FIELDINFO[1].typeLocalName = "fully-qualified-classType";
    FIELDINFO[1].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[1].getterMethod = "getInterceptorClass";
    FIELDINFO[1].setterMethod = "setInterceptorClass";
    FIELDINFO[1].checkMethod = null;
    // Field 2
    FIELDINFO[2].defaultValue = null;
    FIELDINFO[2].fieldJavaName = "AroundInvoke";
    FIELDINFO[2].fieldLocalName = "around-invoke";
    FIELDINFO[2].fieldModel = 1;
    FIELDINFO[2].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[2].isSoapArray = false;
    FIELDINFO[2].maxOccurs = 2147483647;
    FIELDINFO[2].minOccurs = 0;
    FIELDINFO[2].nillable = false;
    FIELDINFO[2].soapArrayDimensions = 0;
    FIELDINFO[2].soapArrayItemTypeJavaName = null;
    FIELDINFO[2].soapArrayItemTypeLocalName = null;
    FIELDINFO[2].soapArrayItemTypeUri = null;
    FIELDINFO[2].typeJavaName = "com.sap.engine.lib.descriptors5.ejb.AroundInvokeType[]";
    FIELDINFO[2].typeLocalName = "around-invokeType";
    FIELDINFO[2].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[2].getterMethod = "getAroundInvoke";
    FIELDINFO[2].setterMethod = "setAroundInvoke";
    FIELDINFO[2].checkMethod = null;
    // Field 3
    FIELDINFO[3].defaultValue = null;
    FIELDINFO[3].fieldJavaName = "EnvEntry";
    FIELDINFO[3].fieldLocalName = "env-entry";
    FIELDINFO[3].fieldModel = 1;
    FIELDINFO[3].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[3].isSoapArray = false;
    FIELDINFO[3].maxOccurs = 2147483647;
    FIELDINFO[3].minOccurs = 0;
    FIELDINFO[3].nillable = false;
    FIELDINFO[3].soapArrayDimensions = 0;
    FIELDINFO[3].soapArrayItemTypeJavaName = null;
    FIELDINFO[3].soapArrayItemTypeLocalName = null;
    FIELDINFO[3].soapArrayItemTypeUri = null;
    FIELDINFO[3].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.EnvEntryType[]";
    FIELDINFO[3].typeLocalName = "env-entryType";
    FIELDINFO[3].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[3].getterMethod = "getEnvEntry";
    FIELDINFO[3].setterMethod = "setEnvEntry";
    FIELDINFO[3].checkMethod = null;
    // Field 4
    FIELDINFO[4].defaultValue = null;
    FIELDINFO[4].fieldJavaName = "EjbRef";
    FIELDINFO[4].fieldLocalName = "ejb-ref";
    FIELDINFO[4].fieldModel = 1;
    FIELDINFO[4].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[4].isSoapArray = false;
    FIELDINFO[4].maxOccurs = 2147483647;
    FIELDINFO[4].minOccurs = 0;
    FIELDINFO[4].nillable = false;
    FIELDINFO[4].soapArrayDimensions = 0;
    FIELDINFO[4].soapArrayItemTypeJavaName = null;
    FIELDINFO[4].soapArrayItemTypeLocalName = null;
    FIELDINFO[4].soapArrayItemTypeUri = null;
    FIELDINFO[4].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.EjbRefType[]";
    FIELDINFO[4].typeLocalName = "ejb-refType";
    FIELDINFO[4].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[4].getterMethod = "getEjbRef";
    FIELDINFO[4].setterMethod = "setEjbRef";
    FIELDINFO[4].checkMethod = null;
    // Field 5
    FIELDINFO[5].defaultValue = null;
    FIELDINFO[5].fieldJavaName = "EjbLocalRef";
    FIELDINFO[5].fieldLocalName = "ejb-local-ref";
    FIELDINFO[5].fieldModel = 1;
    FIELDINFO[5].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[5].isSoapArray = false;
    FIELDINFO[5].maxOccurs = 2147483647;
    FIELDINFO[5].minOccurs = 0;
    FIELDINFO[5].nillable = false;
    FIELDINFO[5].soapArrayDimensions = 0;
    FIELDINFO[5].soapArrayItemTypeJavaName = null;
    FIELDINFO[5].soapArrayItemTypeLocalName = null;
    FIELDINFO[5].soapArrayItemTypeUri = null;
    FIELDINFO[5].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.EjbLocalRefType[]";
    FIELDINFO[5].typeLocalName = "ejb-local-refType";
    FIELDINFO[5].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[5].getterMethod = "getEjbLocalRef";
    FIELDINFO[5].setterMethod = "setEjbLocalRef";
    FIELDINFO[5].checkMethod = null;
    // Field 6
    FIELDINFO[6].defaultValue = null;
    FIELDINFO[6].fieldJavaName = "ServiceRef";
    FIELDINFO[6].fieldLocalName = "service-ref";
    FIELDINFO[6].fieldModel = 1;
    FIELDINFO[6].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[6].isSoapArray = false;
    FIELDINFO[6].maxOccurs = 2147483647;
    FIELDINFO[6].minOccurs = 0;
    FIELDINFO[6].nillable = false;
    FIELDINFO[6].soapArrayDimensions = 0;
    FIELDINFO[6].soapArrayItemTypeJavaName = null;
    FIELDINFO[6].soapArrayItemTypeLocalName = null;
    FIELDINFO[6].soapArrayItemTypeUri = null;
    FIELDINFO[6].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.ServiceRefType[]";
    FIELDINFO[6].typeLocalName = "service-refType";
    FIELDINFO[6].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[6].getterMethod = "getServiceRef";
    FIELDINFO[6].setterMethod = "setServiceRef";
    FIELDINFO[6].checkMethod = null;
    // Field 7
    FIELDINFO[7].defaultValue = null;
    FIELDINFO[7].fieldJavaName = "ResourceRef";
    FIELDINFO[7].fieldLocalName = "resource-ref";
    FIELDINFO[7].fieldModel = 1;
    FIELDINFO[7].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[7].isSoapArray = false;
    FIELDINFO[7].maxOccurs = 2147483647;
    FIELDINFO[7].minOccurs = 0;
    FIELDINFO[7].nillable = false;
    FIELDINFO[7].soapArrayDimensions = 0;
    FIELDINFO[7].soapArrayItemTypeJavaName = null;
    FIELDINFO[7].soapArrayItemTypeLocalName = null;
    FIELDINFO[7].soapArrayItemTypeUri = null;
    FIELDINFO[7].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.ResourceRefType[]";
    FIELDINFO[7].typeLocalName = "resource-refType";
    FIELDINFO[7].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[7].getterMethod = "getResourceRef";
    FIELDINFO[7].setterMethod = "setResourceRef";
    FIELDINFO[7].checkMethod = null;
    // Field 8
    FIELDINFO[8].defaultValue = null;
    FIELDINFO[8].fieldJavaName = "ResourceEnvRef";
    FIELDINFO[8].fieldLocalName = "resource-env-ref";
    FIELDINFO[8].fieldModel = 1;
    FIELDINFO[8].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[8].isSoapArray = false;
    FIELDINFO[8].maxOccurs = 2147483647;
    FIELDINFO[8].minOccurs = 0;
    FIELDINFO[8].nillable = false;
    FIELDINFO[8].soapArrayDimensions = 0;
    FIELDINFO[8].soapArrayItemTypeJavaName = null;
    FIELDINFO[8].soapArrayItemTypeLocalName = null;
    FIELDINFO[8].soapArrayItemTypeUri = null;
    FIELDINFO[8].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.ResourceEnvRefType[]";
    FIELDINFO[8].typeLocalName = "resource-env-refType";
    FIELDINFO[8].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[8].getterMethod = "getResourceEnvRef";
    FIELDINFO[8].setterMethod = "setResourceEnvRef";
    FIELDINFO[8].checkMethod = null;
    // Field 9
    FIELDINFO[9].defaultValue = null;
    FIELDINFO[9].fieldJavaName = "MessageDestinationRef";
    FIELDINFO[9].fieldLocalName = "message-destination-ref";
    FIELDINFO[9].fieldModel = 1;
    FIELDINFO[9].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[9].isSoapArray = false;
    FIELDINFO[9].maxOccurs = 2147483647;
    FIELDINFO[9].minOccurs = 0;
    FIELDINFO[9].nillable = false;
    FIELDINFO[9].soapArrayDimensions = 0;
    FIELDINFO[9].soapArrayItemTypeJavaName = null;
    FIELDINFO[9].soapArrayItemTypeLocalName = null;
    FIELDINFO[9].soapArrayItemTypeUri = null;
    FIELDINFO[9].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.MessageDestinationRefType[]";
    FIELDINFO[9].typeLocalName = "message-destination-refType";
    FIELDINFO[9].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[9].getterMethod = "getMessageDestinationRef";
    FIELDINFO[9].setterMethod = "setMessageDestinationRef";
    FIELDINFO[9].checkMethod = null;
    // Field 10
    FIELDINFO[10].defaultValue = null;
    FIELDINFO[10].fieldJavaName = "PersistenceContextRef";
    FIELDINFO[10].fieldLocalName = "persistence-context-ref";
    FIELDINFO[10].fieldModel = 1;
    FIELDINFO[10].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[10].isSoapArray = false;
    FIELDINFO[10].maxOccurs = 2147483647;
    FIELDINFO[10].minOccurs = 0;
    FIELDINFO[10].nillable = false;
    FIELDINFO[10].soapArrayDimensions = 0;
    FIELDINFO[10].soapArrayItemTypeJavaName = null;
    FIELDINFO[10].soapArrayItemTypeLocalName = null;
    FIELDINFO[10].soapArrayItemTypeUri = null;
    FIELDINFO[10].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.PersistenceContextRefType[]";
    FIELDINFO[10].typeLocalName = "persistence-context-refType";
    FIELDINFO[10].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[10].getterMethod = "getPersistenceContextRef";
    FIELDINFO[10].setterMethod = "setPersistenceContextRef";
    FIELDINFO[10].checkMethod = null;
    // Field 11
    FIELDINFO[11].defaultValue = null;
    FIELDINFO[11].fieldJavaName = "PersistenceUnitRef";
    FIELDINFO[11].fieldLocalName = "persistence-unit-ref";
    FIELDINFO[11].fieldModel = 1;
    FIELDINFO[11].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[11].isSoapArray = false;
    FIELDINFO[11].maxOccurs = 2147483647;
    FIELDINFO[11].minOccurs = 0;
    FIELDINFO[11].nillable = false;
    FIELDINFO[11].soapArrayDimensions = 0;
    FIELDINFO[11].soapArrayItemTypeJavaName = null;
    FIELDINFO[11].soapArrayItemTypeLocalName = null;
    FIELDINFO[11].soapArrayItemTypeUri = null;
    FIELDINFO[11].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.PersistenceUnitRefType[]";
    FIELDINFO[11].typeLocalName = "persistence-unit-refType";
    FIELDINFO[11].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[11].getterMethod = "getPersistenceUnitRef";
    FIELDINFO[11].setterMethod = "setPersistenceUnitRef";
    FIELDINFO[11].checkMethod = null;
    // Field 12
    FIELDINFO[12].defaultValue = null;
    FIELDINFO[12].fieldJavaName = "PostConstruct";
    FIELDINFO[12].fieldLocalName = "post-construct";
    FIELDINFO[12].fieldModel = 1;
    FIELDINFO[12].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[12].isSoapArray = false;
    FIELDINFO[12].maxOccurs = 2147483647;
    FIELDINFO[12].minOccurs = 0;
    FIELDINFO[12].nillable = false;
    FIELDINFO[12].soapArrayDimensions = 0;
    FIELDINFO[12].soapArrayItemTypeJavaName = null;
    FIELDINFO[12].soapArrayItemTypeLocalName = null;
    FIELDINFO[12].soapArrayItemTypeUri = null;
    FIELDINFO[12].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.LifecycleCallbackType[]";
    FIELDINFO[12].typeLocalName = "lifecycle-callbackType";
    FIELDINFO[12].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[12].getterMethod = "getPostConstruct";
    FIELDINFO[12].setterMethod = "setPostConstruct";
    FIELDINFO[12].checkMethod = null;
    // Field 13
    FIELDINFO[13].defaultValue = null;
    FIELDINFO[13].fieldJavaName = "PreDestroy";
    FIELDINFO[13].fieldLocalName = "pre-destroy";
    FIELDINFO[13].fieldModel = 1;
    FIELDINFO[13].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[13].isSoapArray = false;
    FIELDINFO[13].maxOccurs = 2147483647;
    FIELDINFO[13].minOccurs = 0;
    FIELDINFO[13].nillable = false;
    FIELDINFO[13].soapArrayDimensions = 0;
    FIELDINFO[13].soapArrayItemTypeJavaName = null;
    FIELDINFO[13].soapArrayItemTypeLocalName = null;
    FIELDINFO[13].soapArrayItemTypeUri = null;
    FIELDINFO[13].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.LifecycleCallbackType[]";
    FIELDINFO[13].typeLocalName = "lifecycle-callbackType";
    FIELDINFO[13].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[13].getterMethod = "getPreDestroy";
    FIELDINFO[13].setterMethod = "setPreDestroy";
    FIELDINFO[13].checkMethod = null;
    // Field 14
    FIELDINFO[14].defaultValue = null;
    FIELDINFO[14].fieldJavaName = "PostActivate";
    FIELDINFO[14].fieldLocalName = "post-activate";
    FIELDINFO[14].fieldModel = 1;
    FIELDINFO[14].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[14].isSoapArray = false;
    FIELDINFO[14].maxOccurs = 2147483647;
    FIELDINFO[14].minOccurs = 0;
    FIELDINFO[14].nillable = false;
    FIELDINFO[14].soapArrayDimensions = 0;
    FIELDINFO[14].soapArrayItemTypeJavaName = null;
    FIELDINFO[14].soapArrayItemTypeLocalName = null;
    FIELDINFO[14].soapArrayItemTypeUri = null;
    FIELDINFO[14].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.LifecycleCallbackType[]";
    FIELDINFO[14].typeLocalName = "lifecycle-callbackType";
    FIELDINFO[14].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[14].getterMethod = "getPostActivate";
    FIELDINFO[14].setterMethod = "setPostActivate";
    FIELDINFO[14].checkMethod = null;
    // Field 15
    FIELDINFO[15].defaultValue = null;
    FIELDINFO[15].fieldJavaName = "PrePassivate";
    FIELDINFO[15].fieldLocalName = "pre-passivate";
    FIELDINFO[15].fieldModel = 1;
    FIELDINFO[15].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[15].isSoapArray = false;
    FIELDINFO[15].maxOccurs = 2147483647;
    FIELDINFO[15].minOccurs = 0;
    FIELDINFO[15].nillable = false;
    FIELDINFO[15].soapArrayDimensions = 0;
    FIELDINFO[15].soapArrayItemTypeJavaName = null;
    FIELDINFO[15].soapArrayItemTypeLocalName = null;
    FIELDINFO[15].soapArrayItemTypeUri = null;
    FIELDINFO[15].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.LifecycleCallbackType[]";
    FIELDINFO[15].typeLocalName = "lifecycle-callbackType";
    FIELDINFO[15].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[15].getterMethod = "getPrePassivate";
    FIELDINFO[15].setterMethod = "setPrePassivate";
    FIELDINFO[15].checkMethod = null;
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
