﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Mon Jun 26 13:55:46 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webservices.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}ejb-local-refType
 */
public  class EjbLocalRefTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/javaee";
  }

  public java.lang.String _d_originalLocalName() {
    return "ejb-local-refType";
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
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[8];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[3] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[4] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[5] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[6] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[7] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
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
    FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors5.webservices.DescriptionType[]";
    FIELDINFO[0].typeLocalName = "descriptionType";
    FIELDINFO[0].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[0].getterMethod = "getDescription";
    FIELDINFO[0].setterMethod = "setDescription";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "EjbRefName";
    FIELDINFO[1].fieldLocalName = "ejb-ref-name";
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
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors5.webservices.EjbRefNameType";
    FIELDINFO[1].typeLocalName = "ejb-ref-nameType";
    FIELDINFO[1].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[1].getterMethod = "getEjbRefName";
    FIELDINFO[1].setterMethod = "setEjbRefName";
    FIELDINFO[1].checkMethod = null;
    // Field 2
    FIELDINFO[2].defaultValue = null;
    FIELDINFO[2].fieldJavaName = "EjbRefType";
    FIELDINFO[2].fieldLocalName = "ejb-ref-type";
    FIELDINFO[2].fieldModel = 1;
    FIELDINFO[2].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[2].isSoapArray = false;
    FIELDINFO[2].maxOccurs = 1;
    FIELDINFO[2].minOccurs = 0;
    FIELDINFO[2].nillable = false;
    FIELDINFO[2].soapArrayDimensions = 0;
    FIELDINFO[2].soapArrayItemTypeJavaName = null;
    FIELDINFO[2].soapArrayItemTypeLocalName = null;
    FIELDINFO[2].soapArrayItemTypeUri = null;
    FIELDINFO[2].typeJavaName = "com.sap.engine.lib.descriptors5.webservices.EjbRefTypeType";
    FIELDINFO[2].typeLocalName = "ejb-ref-typeType";
    FIELDINFO[2].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[2].getterMethod = "getEjbRefType";
    FIELDINFO[2].setterMethod = "setEjbRefType";
    FIELDINFO[2].checkMethod = null;
    // Field 3
    FIELDINFO[3].defaultValue = null;
    FIELDINFO[3].fieldJavaName = "LocalHome";
    FIELDINFO[3].fieldLocalName = "local-home";
    FIELDINFO[3].fieldModel = 1;
    FIELDINFO[3].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[3].isSoapArray = false;
    FIELDINFO[3].maxOccurs = 1;
    FIELDINFO[3].minOccurs = 0;
    FIELDINFO[3].nillable = false;
    FIELDINFO[3].soapArrayDimensions = 0;
    FIELDINFO[3].soapArrayItemTypeJavaName = null;
    FIELDINFO[3].soapArrayItemTypeLocalName = null;
    FIELDINFO[3].soapArrayItemTypeUri = null;
    FIELDINFO[3].typeJavaName = "com.sap.engine.lib.descriptors5.webservices.LocalHomeType";
    FIELDINFO[3].typeLocalName = "local-homeType";
    FIELDINFO[3].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[3].getterMethod = "getLocalHome";
    FIELDINFO[3].setterMethod = "setLocalHome";
    FIELDINFO[3].checkMethod = null;
    // Field 4
    FIELDINFO[4].defaultValue = null;
    FIELDINFO[4].fieldJavaName = "Local";
    FIELDINFO[4].fieldLocalName = "local";
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
    FIELDINFO[4].typeJavaName = "com.sap.engine.lib.descriptors5.webservices.LocalType";
    FIELDINFO[4].typeLocalName = "localType";
    FIELDINFO[4].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[4].getterMethod = "getLocal";
    FIELDINFO[4].setterMethod = "setLocal";
    FIELDINFO[4].checkMethod = null;
    // Field 5
    FIELDINFO[5].defaultValue = null;
    FIELDINFO[5].fieldJavaName = "EjbLink";
    FIELDINFO[5].fieldLocalName = "ejb-link";
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
    FIELDINFO[5].typeJavaName = "com.sap.engine.lib.descriptors5.webservices.EjbLinkType";
    FIELDINFO[5].typeLocalName = "ejb-linkType";
    FIELDINFO[5].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[5].getterMethod = "getEjbLink";
    FIELDINFO[5].setterMethod = "setEjbLink";
    FIELDINFO[5].checkMethod = null;
    // Field 6
    FIELDINFO[6].defaultValue = null;
    FIELDINFO[6].fieldJavaName = "MappedName";
    FIELDINFO[6].fieldLocalName = "mapped-name";
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
    FIELDINFO[6].typeJavaName = "com.sap.engine.lib.descriptors5.webservices.XsdStringType";
    FIELDINFO[6].typeLocalName = "xsdStringType";
    FIELDINFO[6].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[6].getterMethod = "getMappedName";
    FIELDINFO[6].setterMethod = "setMappedName";
    FIELDINFO[6].checkMethod = null;
    // Field 7
    FIELDINFO[7].defaultValue = null;
    FIELDINFO[7].fieldJavaName = "InjectionTarget";
    FIELDINFO[7].fieldLocalName = "injection-target";
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
    FIELDINFO[7].typeJavaName = "com.sap.engine.lib.descriptors5.webservices.InjectionTargetType[]";
    FIELDINFO[7].typeLocalName = "injection-targetType";
    FIELDINFO[7].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[7].getterMethod = "getInjectionTarget";
    FIELDINFO[7].setterMethod = "setInjectionTarget";
    FIELDINFO[7].checkMethod = null;
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
