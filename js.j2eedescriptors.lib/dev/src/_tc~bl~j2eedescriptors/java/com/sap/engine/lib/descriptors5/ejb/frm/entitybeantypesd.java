﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Oct 19 11:20:17 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ejb.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}entity-beanType
 */
public  class EntityBeanTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/javaee";
  }

  public java.lang.String _d_originalLocalName() {
    return "entity-beanType";
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
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[31];
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
    FIELDINFO[16] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[17] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[18] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[19] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[20] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[21] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[22] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[23] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[24] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[25] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[26] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[27] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[28] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[29] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[30] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
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
    FIELDINFO[1].fieldJavaName = "DisplayName";
    FIELDINFO[1].fieldLocalName = "display-name";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 2147483647;
    FIELDINFO[1].minOccurs = 0;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.DisplayNameType[]";
    FIELDINFO[1].typeLocalName = "display-nameType";
    FIELDINFO[1].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[1].getterMethod = "getDisplayName";
    FIELDINFO[1].setterMethod = "setDisplayName";
    FIELDINFO[1].checkMethod = null;
    // Field 2
    FIELDINFO[2].defaultValue = null;
    FIELDINFO[2].fieldJavaName = "Icon";
    FIELDINFO[2].fieldLocalName = "icon";
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
    FIELDINFO[2].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.IconType[]";
    FIELDINFO[2].typeLocalName = "iconType";
    FIELDINFO[2].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[2].getterMethod = "getIcon";
    FIELDINFO[2].setterMethod = "setIcon";
    FIELDINFO[2].checkMethod = null;
    // Field 3
    FIELDINFO[3].defaultValue = null;
    FIELDINFO[3].fieldJavaName = "EjbName";
    FIELDINFO[3].fieldLocalName = "ejb-name";
    FIELDINFO[3].fieldModel = 1;
    FIELDINFO[3].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[3].isSoapArray = false;
    FIELDINFO[3].maxOccurs = 1;
    FIELDINFO[3].minOccurs = 1;
    FIELDINFO[3].nillable = false;
    FIELDINFO[3].soapArrayDimensions = 0;
    FIELDINFO[3].soapArrayItemTypeJavaName = null;
    FIELDINFO[3].soapArrayItemTypeLocalName = null;
    FIELDINFO[3].soapArrayItemTypeUri = null;
    FIELDINFO[3].typeJavaName = "com.sap.engine.lib.descriptors5.ejb.EjbNameType";
    FIELDINFO[3].typeLocalName = "ejb-nameType";
    FIELDINFO[3].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[3].getterMethod = "getEjbName";
    FIELDINFO[3].setterMethod = "setEjbName";
    FIELDINFO[3].checkMethod = null;
    // Field 4
    FIELDINFO[4].defaultValue = null;
    FIELDINFO[4].fieldJavaName = "MappedName";
    FIELDINFO[4].fieldLocalName = "mapped-name";
    FIELDINFO[4].fieldModel = 1;
    FIELDINFO[4].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[4].isSoapArray = false;
    FIELDINFO[4].maxOccurs = 1;
    FIELDINFO[4].minOccurs = 0;
    FIELDINFO[4].nillable = false;
    FIELDINFO[4].soapArrayDimensions = 0;
    FIELDINFO[4].soapArrayItemTypeJavaName = null;
    FIELDINFO[4].soapArrayItemTypeLocalName = null;
    FIELDINFO[4].soapArrayItemTypeUri = null;
    FIELDINFO[4].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.XsdStringType";
    FIELDINFO[4].typeLocalName = "xsdStringType";
    FIELDINFO[4].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[4].getterMethod = "getMappedName";
    FIELDINFO[4].setterMethod = "setMappedName";
    FIELDINFO[4].checkMethod = null;
    // Field 5
    FIELDINFO[5].defaultValue = null;
    FIELDINFO[5].fieldJavaName = "Home";
    FIELDINFO[5].fieldLocalName = "home";
    FIELDINFO[5].fieldModel = 1;
    FIELDINFO[5].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[5].isSoapArray = false;
    FIELDINFO[5].maxOccurs = 1;
    FIELDINFO[5].minOccurs = 0;
    FIELDINFO[5].nillable = false;
    FIELDINFO[5].soapArrayDimensions = 0;
    FIELDINFO[5].soapArrayItemTypeJavaName = null;
    FIELDINFO[5].soapArrayItemTypeLocalName = null;
    FIELDINFO[5].soapArrayItemTypeUri = null;
    FIELDINFO[5].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.HomeType";
    FIELDINFO[5].typeLocalName = "homeType";
    FIELDINFO[5].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[5].getterMethod = "getHome";
    FIELDINFO[5].setterMethod = "setHome";
    FIELDINFO[5].checkMethod = null;
    // Field 6
    FIELDINFO[6].defaultValue = null;
    FIELDINFO[6].fieldJavaName = "Remote";
    FIELDINFO[6].fieldLocalName = "remote";
    FIELDINFO[6].fieldModel = 1;
    FIELDINFO[6].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[6].isSoapArray = false;
    FIELDINFO[6].maxOccurs = 1;
    FIELDINFO[6].minOccurs = 0;
    FIELDINFO[6].nillable = false;
    FIELDINFO[6].soapArrayDimensions = 0;
    FIELDINFO[6].soapArrayItemTypeJavaName = null;
    FIELDINFO[6].soapArrayItemTypeLocalName = null;
    FIELDINFO[6].soapArrayItemTypeUri = null;
    FIELDINFO[6].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.RemoteType";
    FIELDINFO[6].typeLocalName = "remoteType";
    FIELDINFO[6].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[6].getterMethod = "getRemote";
    FIELDINFO[6].setterMethod = "setRemote";
    FIELDINFO[6].checkMethod = null;
    // Field 7
    FIELDINFO[7].defaultValue = null;
    FIELDINFO[7].fieldJavaName = "LocalHome";
    FIELDINFO[7].fieldLocalName = "local-home";
    FIELDINFO[7].fieldModel = 1;
    FIELDINFO[7].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[7].isSoapArray = false;
    FIELDINFO[7].maxOccurs = 1;
    FIELDINFO[7].minOccurs = 0;
    FIELDINFO[7].nillable = false;
    FIELDINFO[7].soapArrayDimensions = 0;
    FIELDINFO[7].soapArrayItemTypeJavaName = null;
    FIELDINFO[7].soapArrayItemTypeLocalName = null;
    FIELDINFO[7].soapArrayItemTypeUri = null;
    FIELDINFO[7].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.LocalHomeType";
    FIELDINFO[7].typeLocalName = "local-homeType";
    FIELDINFO[7].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[7].getterMethod = "getLocalHome";
    FIELDINFO[7].setterMethod = "setLocalHome";
    FIELDINFO[7].checkMethod = null;
    // Field 8
    FIELDINFO[8].defaultValue = null;
    FIELDINFO[8].fieldJavaName = "Local";
    FIELDINFO[8].fieldLocalName = "local";
    FIELDINFO[8].fieldModel = 1;
    FIELDINFO[8].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[8].isSoapArray = false;
    FIELDINFO[8].maxOccurs = 1;
    FIELDINFO[8].minOccurs = 0;
    FIELDINFO[8].nillable = false;
    FIELDINFO[8].soapArrayDimensions = 0;
    FIELDINFO[8].soapArrayItemTypeJavaName = null;
    FIELDINFO[8].soapArrayItemTypeLocalName = null;
    FIELDINFO[8].soapArrayItemTypeUri = null;
    FIELDINFO[8].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.LocalType";
    FIELDINFO[8].typeLocalName = "localType";
    FIELDINFO[8].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[8].getterMethod = "getLocal";
    FIELDINFO[8].setterMethod = "setLocal";
    FIELDINFO[8].checkMethod = null;
    // Field 9
    FIELDINFO[9].defaultValue = null;
    FIELDINFO[9].fieldJavaName = "EjbClass";
    FIELDINFO[9].fieldLocalName = "ejb-class";
    FIELDINFO[9].fieldModel = 1;
    FIELDINFO[9].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[9].isSoapArray = false;
    FIELDINFO[9].maxOccurs = 1;
    FIELDINFO[9].minOccurs = 1;
    FIELDINFO[9].nillable = false;
    FIELDINFO[9].soapArrayDimensions = 0;
    FIELDINFO[9].soapArrayItemTypeJavaName = null;
    FIELDINFO[9].soapArrayItemTypeLocalName = null;
    FIELDINFO[9].soapArrayItemTypeUri = null;
    FIELDINFO[9].typeJavaName = "com.sap.engine.lib.descriptors5.ejb.EjbClassType";
    FIELDINFO[9].typeLocalName = "ejb-classType";
    FIELDINFO[9].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[9].getterMethod = "getEjbClass";
    FIELDINFO[9].setterMethod = "setEjbClass";
    FIELDINFO[9].checkMethod = null;
    // Field 10
    FIELDINFO[10].defaultValue = null;
    FIELDINFO[10].fieldJavaName = "PersistenceType";
    FIELDINFO[10].fieldLocalName = "persistence-type";
    FIELDINFO[10].fieldModel = 1;
    FIELDINFO[10].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[10].isSoapArray = false;
    FIELDINFO[10].maxOccurs = 1;
    FIELDINFO[10].minOccurs = 1;
    FIELDINFO[10].nillable = false;
    FIELDINFO[10].soapArrayDimensions = 0;
    FIELDINFO[10].soapArrayItemTypeJavaName = null;
    FIELDINFO[10].soapArrayItemTypeLocalName = null;
    FIELDINFO[10].soapArrayItemTypeUri = null;
    FIELDINFO[10].typeJavaName = "com.sap.engine.lib.descriptors5.ejb.PersistenceTypeType";
    FIELDINFO[10].typeLocalName = "persistence-typeType";
    FIELDINFO[10].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[10].getterMethod = "getPersistenceType";
    FIELDINFO[10].setterMethod = "setPersistenceType";
    FIELDINFO[10].checkMethod = null;
    // Field 11
    FIELDINFO[11].defaultValue = null;
    FIELDINFO[11].fieldJavaName = "PrimKeyClass";
    FIELDINFO[11].fieldLocalName = "prim-key-class";
    FIELDINFO[11].fieldModel = 1;
    FIELDINFO[11].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[11].isSoapArray = false;
    FIELDINFO[11].maxOccurs = 1;
    FIELDINFO[11].minOccurs = 1;
    FIELDINFO[11].nillable = false;
    FIELDINFO[11].soapArrayDimensions = 0;
    FIELDINFO[11].soapArrayItemTypeJavaName = null;
    FIELDINFO[11].soapArrayItemTypeLocalName = null;
    FIELDINFO[11].soapArrayItemTypeUri = null;
    FIELDINFO[11].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType";
    FIELDINFO[11].typeLocalName = "fully-qualified-classType";
    FIELDINFO[11].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[11].getterMethod = "getPrimKeyClass";
    FIELDINFO[11].setterMethod = "setPrimKeyClass";
    FIELDINFO[11].checkMethod = null;
    // Field 12
    FIELDINFO[12].defaultValue = null;
    FIELDINFO[12].fieldJavaName = "Reentrant";
    FIELDINFO[12].fieldLocalName = "reentrant";
    FIELDINFO[12].fieldModel = 1;
    FIELDINFO[12].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[12].isSoapArray = false;
    FIELDINFO[12].maxOccurs = 1;
    FIELDINFO[12].minOccurs = 1;
    FIELDINFO[12].nillable = false;
    FIELDINFO[12].soapArrayDimensions = 0;
    FIELDINFO[12].soapArrayItemTypeJavaName = null;
    FIELDINFO[12].soapArrayItemTypeLocalName = null;
    FIELDINFO[12].soapArrayItemTypeUri = null;
    FIELDINFO[12].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.TrueFalseType";
    FIELDINFO[12].typeLocalName = "true-falseType";
    FIELDINFO[12].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[12].getterMethod = "getReentrant";
    FIELDINFO[12].setterMethod = "setReentrant";
    FIELDINFO[12].checkMethod = null;
    // Field 13
    FIELDINFO[13].defaultValue = null;
    FIELDINFO[13].fieldJavaName = "CmpVersion";
    FIELDINFO[13].fieldLocalName = "cmp-version";
    FIELDINFO[13].fieldModel = 1;
    FIELDINFO[13].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[13].isSoapArray = false;
    FIELDINFO[13].maxOccurs = 1;
    FIELDINFO[13].minOccurs = 0;
    FIELDINFO[13].nillable = false;
    FIELDINFO[13].soapArrayDimensions = 0;
    FIELDINFO[13].soapArrayItemTypeJavaName = null;
    FIELDINFO[13].soapArrayItemTypeLocalName = null;
    FIELDINFO[13].soapArrayItemTypeUri = null;
    FIELDINFO[13].typeJavaName = "com.sap.engine.lib.descriptors5.ejb.CmpVersionType";
    FIELDINFO[13].typeLocalName = "cmp-versionType";
    FIELDINFO[13].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[13].getterMethod = "getCmpVersion";
    FIELDINFO[13].setterMethod = "setCmpVersion";
    FIELDINFO[13].checkMethod = null;
    // Field 14
    FIELDINFO[14].defaultValue = null;
    FIELDINFO[14].fieldJavaName = "AbstractSchemaName";
    FIELDINFO[14].fieldLocalName = "abstract-schema-name";
    FIELDINFO[14].fieldModel = 1;
    FIELDINFO[14].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[14].isSoapArray = false;
    FIELDINFO[14].maxOccurs = 1;
    FIELDINFO[14].minOccurs = 0;
    FIELDINFO[14].nillable = false;
    FIELDINFO[14].soapArrayDimensions = 0;
    FIELDINFO[14].soapArrayItemTypeJavaName = null;
    FIELDINFO[14].soapArrayItemTypeLocalName = null;
    FIELDINFO[14].soapArrayItemTypeUri = null;
    FIELDINFO[14].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.JavaIdentifierType";
    FIELDINFO[14].typeLocalName = "java-identifierType";
    FIELDINFO[14].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[14].getterMethod = "getAbstractSchemaName";
    FIELDINFO[14].setterMethod = "setAbstractSchemaName";
    FIELDINFO[14].checkMethod = null;
    // Field 15
    FIELDINFO[15].defaultValue = null;
    FIELDINFO[15].fieldJavaName = "CmpField";
    FIELDINFO[15].fieldLocalName = "cmp-field";
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
    FIELDINFO[15].typeJavaName = "com.sap.engine.lib.descriptors5.ejb.CmpFieldType[]";
    FIELDINFO[15].typeLocalName = "cmp-fieldType";
    FIELDINFO[15].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[15].getterMethod = "getCmpField";
    FIELDINFO[15].setterMethod = "setCmpField";
    FIELDINFO[15].checkMethod = null;
    // Field 16
    FIELDINFO[16].defaultValue = null;
    FIELDINFO[16].fieldJavaName = "PrimkeyField";
    FIELDINFO[16].fieldLocalName = "primkey-field";
    FIELDINFO[16].fieldModel = 1;
    FIELDINFO[16].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[16].isSoapArray = false;
    FIELDINFO[16].maxOccurs = 1;
    FIELDINFO[16].minOccurs = 0;
    FIELDINFO[16].nillable = false;
    FIELDINFO[16].soapArrayDimensions = 0;
    FIELDINFO[16].soapArrayItemTypeJavaName = null;
    FIELDINFO[16].soapArrayItemTypeLocalName = null;
    FIELDINFO[16].soapArrayItemTypeUri = null;
    FIELDINFO[16].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.String";
    FIELDINFO[16].typeLocalName = "string";
    FIELDINFO[16].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[16].getterMethod = "getPrimkeyField";
    FIELDINFO[16].setterMethod = "setPrimkeyField";
    FIELDINFO[16].checkMethod = null;
    // Field 17
    FIELDINFO[17].defaultValue = null;
    FIELDINFO[17].fieldJavaName = "EnvEntry";
    FIELDINFO[17].fieldLocalName = "env-entry";
    FIELDINFO[17].fieldModel = 1;
    FIELDINFO[17].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[17].isSoapArray = false;
    FIELDINFO[17].maxOccurs = 2147483647;
    FIELDINFO[17].minOccurs = 0;
    FIELDINFO[17].nillable = false;
    FIELDINFO[17].soapArrayDimensions = 0;
    FIELDINFO[17].soapArrayItemTypeJavaName = null;
    FIELDINFO[17].soapArrayItemTypeLocalName = null;
    FIELDINFO[17].soapArrayItemTypeUri = null;
    FIELDINFO[17].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.EnvEntryType[]";
    FIELDINFO[17].typeLocalName = "env-entryType";
    FIELDINFO[17].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[17].getterMethod = "getEnvEntry";
    FIELDINFO[17].setterMethod = "setEnvEntry";
    FIELDINFO[17].checkMethod = null;
    // Field 18
    FIELDINFO[18].defaultValue = null;
    FIELDINFO[18].fieldJavaName = "EjbRef";
    FIELDINFO[18].fieldLocalName = "ejb-ref";
    FIELDINFO[18].fieldModel = 1;
    FIELDINFO[18].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[18].isSoapArray = false;
    FIELDINFO[18].maxOccurs = 2147483647;
    FIELDINFO[18].minOccurs = 0;
    FIELDINFO[18].nillable = false;
    FIELDINFO[18].soapArrayDimensions = 0;
    FIELDINFO[18].soapArrayItemTypeJavaName = null;
    FIELDINFO[18].soapArrayItemTypeLocalName = null;
    FIELDINFO[18].soapArrayItemTypeUri = null;
    FIELDINFO[18].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.EjbRefType[]";
    FIELDINFO[18].typeLocalName = "ejb-refType";
    FIELDINFO[18].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[18].getterMethod = "getEjbRef";
    FIELDINFO[18].setterMethod = "setEjbRef";
    FIELDINFO[18].checkMethod = null;
    // Field 19
    FIELDINFO[19].defaultValue = null;
    FIELDINFO[19].fieldJavaName = "EjbLocalRef";
    FIELDINFO[19].fieldLocalName = "ejb-local-ref";
    FIELDINFO[19].fieldModel = 1;
    FIELDINFO[19].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[19].isSoapArray = false;
    FIELDINFO[19].maxOccurs = 2147483647;
    FIELDINFO[19].minOccurs = 0;
    FIELDINFO[19].nillable = false;
    FIELDINFO[19].soapArrayDimensions = 0;
    FIELDINFO[19].soapArrayItemTypeJavaName = null;
    FIELDINFO[19].soapArrayItemTypeLocalName = null;
    FIELDINFO[19].soapArrayItemTypeUri = null;
    FIELDINFO[19].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.EjbLocalRefType[]";
    FIELDINFO[19].typeLocalName = "ejb-local-refType";
    FIELDINFO[19].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[19].getterMethod = "getEjbLocalRef";
    FIELDINFO[19].setterMethod = "setEjbLocalRef";
    FIELDINFO[19].checkMethod = null;
    // Field 20
    FIELDINFO[20].defaultValue = null;
    FIELDINFO[20].fieldJavaName = "ServiceRef";
    FIELDINFO[20].fieldLocalName = "service-ref";
    FIELDINFO[20].fieldModel = 1;
    FIELDINFO[20].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[20].isSoapArray = false;
    FIELDINFO[20].maxOccurs = 2147483647;
    FIELDINFO[20].minOccurs = 0;
    FIELDINFO[20].nillable = false;
    FIELDINFO[20].soapArrayDimensions = 0;
    FIELDINFO[20].soapArrayItemTypeJavaName = null;
    FIELDINFO[20].soapArrayItemTypeLocalName = null;
    FIELDINFO[20].soapArrayItemTypeUri = null;
    FIELDINFO[20].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.ServiceRefType[]";
    FIELDINFO[20].typeLocalName = "service-refType";
    FIELDINFO[20].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[20].getterMethod = "getServiceRef";
    FIELDINFO[20].setterMethod = "setServiceRef";
    FIELDINFO[20].checkMethod = null;
    // Field 21
    FIELDINFO[21].defaultValue = null;
    FIELDINFO[21].fieldJavaName = "ResourceRef";
    FIELDINFO[21].fieldLocalName = "resource-ref";
    FIELDINFO[21].fieldModel = 1;
    FIELDINFO[21].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[21].isSoapArray = false;
    FIELDINFO[21].maxOccurs = 2147483647;
    FIELDINFO[21].minOccurs = 0;
    FIELDINFO[21].nillable = false;
    FIELDINFO[21].soapArrayDimensions = 0;
    FIELDINFO[21].soapArrayItemTypeJavaName = null;
    FIELDINFO[21].soapArrayItemTypeLocalName = null;
    FIELDINFO[21].soapArrayItemTypeUri = null;
    FIELDINFO[21].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.ResourceRefType[]";
    FIELDINFO[21].typeLocalName = "resource-refType";
    FIELDINFO[21].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[21].getterMethod = "getResourceRef";
    FIELDINFO[21].setterMethod = "setResourceRef";
    FIELDINFO[21].checkMethod = null;
    // Field 22
    FIELDINFO[22].defaultValue = null;
    FIELDINFO[22].fieldJavaName = "ResourceEnvRef";
    FIELDINFO[22].fieldLocalName = "resource-env-ref";
    FIELDINFO[22].fieldModel = 1;
    FIELDINFO[22].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[22].isSoapArray = false;
    FIELDINFO[22].maxOccurs = 2147483647;
    FIELDINFO[22].minOccurs = 0;
    FIELDINFO[22].nillable = false;
    FIELDINFO[22].soapArrayDimensions = 0;
    FIELDINFO[22].soapArrayItemTypeJavaName = null;
    FIELDINFO[22].soapArrayItemTypeLocalName = null;
    FIELDINFO[22].soapArrayItemTypeUri = null;
    FIELDINFO[22].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.ResourceEnvRefType[]";
    FIELDINFO[22].typeLocalName = "resource-env-refType";
    FIELDINFO[22].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[22].getterMethod = "getResourceEnvRef";
    FIELDINFO[22].setterMethod = "setResourceEnvRef";
    FIELDINFO[22].checkMethod = null;
    // Field 23
    FIELDINFO[23].defaultValue = null;
    FIELDINFO[23].fieldJavaName = "MessageDestinationRef";
    FIELDINFO[23].fieldLocalName = "message-destination-ref";
    FIELDINFO[23].fieldModel = 1;
    FIELDINFO[23].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[23].isSoapArray = false;
    FIELDINFO[23].maxOccurs = 2147483647;
    FIELDINFO[23].minOccurs = 0;
    FIELDINFO[23].nillable = false;
    FIELDINFO[23].soapArrayDimensions = 0;
    FIELDINFO[23].soapArrayItemTypeJavaName = null;
    FIELDINFO[23].soapArrayItemTypeLocalName = null;
    FIELDINFO[23].soapArrayItemTypeUri = null;
    FIELDINFO[23].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.MessageDestinationRefType[]";
    FIELDINFO[23].typeLocalName = "message-destination-refType";
    FIELDINFO[23].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[23].getterMethod = "getMessageDestinationRef";
    FIELDINFO[23].setterMethod = "setMessageDestinationRef";
    FIELDINFO[23].checkMethod = null;
    // Field 24
    FIELDINFO[24].defaultValue = null;
    FIELDINFO[24].fieldJavaName = "PersistenceContextRef";
    FIELDINFO[24].fieldLocalName = "persistence-context-ref";
    FIELDINFO[24].fieldModel = 1;
    FIELDINFO[24].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[24].isSoapArray = false;
    FIELDINFO[24].maxOccurs = 2147483647;
    FIELDINFO[24].minOccurs = 0;
    FIELDINFO[24].nillable = false;
    FIELDINFO[24].soapArrayDimensions = 0;
    FIELDINFO[24].soapArrayItemTypeJavaName = null;
    FIELDINFO[24].soapArrayItemTypeLocalName = null;
    FIELDINFO[24].soapArrayItemTypeUri = null;
    FIELDINFO[24].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.PersistenceContextRefType[]";
    FIELDINFO[24].typeLocalName = "persistence-context-refType";
    FIELDINFO[24].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[24].getterMethod = "getPersistenceContextRef";
    FIELDINFO[24].setterMethod = "setPersistenceContextRef";
    FIELDINFO[24].checkMethod = null;
    // Field 25
    FIELDINFO[25].defaultValue = null;
    FIELDINFO[25].fieldJavaName = "PersistenceUnitRef";
    FIELDINFO[25].fieldLocalName = "persistence-unit-ref";
    FIELDINFO[25].fieldModel = 1;
    FIELDINFO[25].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[25].isSoapArray = false;
    FIELDINFO[25].maxOccurs = 2147483647;
    FIELDINFO[25].minOccurs = 0;
    FIELDINFO[25].nillable = false;
    FIELDINFO[25].soapArrayDimensions = 0;
    FIELDINFO[25].soapArrayItemTypeJavaName = null;
    FIELDINFO[25].soapArrayItemTypeLocalName = null;
    FIELDINFO[25].soapArrayItemTypeUri = null;
    FIELDINFO[25].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.PersistenceUnitRefType[]";
    FIELDINFO[25].typeLocalName = "persistence-unit-refType";
    FIELDINFO[25].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[25].getterMethod = "getPersistenceUnitRef";
    FIELDINFO[25].setterMethod = "setPersistenceUnitRef";
    FIELDINFO[25].checkMethod = null;
    // Field 26
    FIELDINFO[26].defaultValue = null;
    FIELDINFO[26].fieldJavaName = "PostConstruct";
    FIELDINFO[26].fieldLocalName = "post-construct";
    FIELDINFO[26].fieldModel = 1;
    FIELDINFO[26].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[26].isSoapArray = false;
    FIELDINFO[26].maxOccurs = 2147483647;
    FIELDINFO[26].minOccurs = 0;
    FIELDINFO[26].nillable = false;
    FIELDINFO[26].soapArrayDimensions = 0;
    FIELDINFO[26].soapArrayItemTypeJavaName = null;
    FIELDINFO[26].soapArrayItemTypeLocalName = null;
    FIELDINFO[26].soapArrayItemTypeUri = null;
    FIELDINFO[26].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.LifecycleCallbackType[]";
    FIELDINFO[26].typeLocalName = "lifecycle-callbackType";
    FIELDINFO[26].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[26].getterMethod = "getPostConstruct";
    FIELDINFO[26].setterMethod = "setPostConstruct";
    FIELDINFO[26].checkMethod = null;
    // Field 27
    FIELDINFO[27].defaultValue = null;
    FIELDINFO[27].fieldJavaName = "PreDestroy";
    FIELDINFO[27].fieldLocalName = "pre-destroy";
    FIELDINFO[27].fieldModel = 1;
    FIELDINFO[27].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[27].isSoapArray = false;
    FIELDINFO[27].maxOccurs = 2147483647;
    FIELDINFO[27].minOccurs = 0;
    FIELDINFO[27].nillable = false;
    FIELDINFO[27].soapArrayDimensions = 0;
    FIELDINFO[27].soapArrayItemTypeJavaName = null;
    FIELDINFO[27].soapArrayItemTypeLocalName = null;
    FIELDINFO[27].soapArrayItemTypeUri = null;
    FIELDINFO[27].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.LifecycleCallbackType[]";
    FIELDINFO[27].typeLocalName = "lifecycle-callbackType";
    FIELDINFO[27].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[27].getterMethod = "getPreDestroy";
    FIELDINFO[27].setterMethod = "setPreDestroy";
    FIELDINFO[27].checkMethod = null;
    // Field 28
    FIELDINFO[28].defaultValue = null;
    FIELDINFO[28].fieldJavaName = "SecurityRoleRef";
    FIELDINFO[28].fieldLocalName = "security-role-ref";
    FIELDINFO[28].fieldModel = 1;
    FIELDINFO[28].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[28].isSoapArray = false;
    FIELDINFO[28].maxOccurs = 2147483647;
    FIELDINFO[28].minOccurs = 0;
    FIELDINFO[28].nillable = false;
    FIELDINFO[28].soapArrayDimensions = 0;
    FIELDINFO[28].soapArrayItemTypeJavaName = null;
    FIELDINFO[28].soapArrayItemTypeLocalName = null;
    FIELDINFO[28].soapArrayItemTypeUri = null;
    FIELDINFO[28].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.SecurityRoleRefType[]";
    FIELDINFO[28].typeLocalName = "security-role-refType";
    FIELDINFO[28].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[28].getterMethod = "getSecurityRoleRef";
    FIELDINFO[28].setterMethod = "setSecurityRoleRef";
    FIELDINFO[28].checkMethod = null;
    // Field 29
    FIELDINFO[29].defaultValue = null;
    FIELDINFO[29].fieldJavaName = "SecurityIdentity";
    FIELDINFO[29].fieldLocalName = "security-identity";
    FIELDINFO[29].fieldModel = 1;
    FIELDINFO[29].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[29].isSoapArray = false;
    FIELDINFO[29].maxOccurs = 1;
    FIELDINFO[29].minOccurs = 0;
    FIELDINFO[29].nillable = false;
    FIELDINFO[29].soapArrayDimensions = 0;
    FIELDINFO[29].soapArrayItemTypeJavaName = null;
    FIELDINFO[29].soapArrayItemTypeLocalName = null;
    FIELDINFO[29].soapArrayItemTypeUri = null;
    FIELDINFO[29].typeJavaName = "com.sap.engine.lib.descriptors5.ejb.SecurityIdentityType";
    FIELDINFO[29].typeLocalName = "security-identityType";
    FIELDINFO[29].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[29].getterMethod = "getSecurityIdentity";
    FIELDINFO[29].setterMethod = "setSecurityIdentity";
    FIELDINFO[29].checkMethod = null;
    // Field 30
    FIELDINFO[30].defaultValue = null;
    FIELDINFO[30].fieldJavaName = "Query";
    FIELDINFO[30].fieldLocalName = "query";
    FIELDINFO[30].fieldModel = 1;
    FIELDINFO[30].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[30].isSoapArray = false;
    FIELDINFO[30].maxOccurs = 2147483647;
    FIELDINFO[30].minOccurs = 0;
    FIELDINFO[30].nillable = false;
    FIELDINFO[30].soapArrayDimensions = 0;
    FIELDINFO[30].soapArrayItemTypeJavaName = null;
    FIELDINFO[30].soapArrayItemTypeLocalName = null;
    FIELDINFO[30].soapArrayItemTypeUri = null;
    FIELDINFO[30].typeJavaName = "com.sap.engine.lib.descriptors5.ejb.QueryType[]";
    FIELDINFO[30].typeLocalName = "queryType";
    FIELDINFO[30].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[30].getterMethod = "getQuery";
    FIELDINFO[30].setterMethod = "setQuery";
    FIELDINFO[30].checkMethod = null;
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
