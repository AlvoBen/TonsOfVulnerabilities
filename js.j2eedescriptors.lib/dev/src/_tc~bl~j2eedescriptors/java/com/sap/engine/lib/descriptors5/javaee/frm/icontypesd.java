﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 09 16:24:16 EET 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.javaee.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}iconType
 */
public  class IconTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/javaee";
  }

  public java.lang.String _d_originalLocalName() {
    return "iconType";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[2];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "lang";
    ATTRIBUTEINFO[0].fieldUri = "http://www.w3.org/XML/1998/namespace";
    ATTRIBUTEINFO[0].fieldJavaName = "Lang";
    ATTRIBUTEINFO[0].typeName = "/xs:schema/xs:attribute[1]/xs:simpleType";
    ATTRIBUTEINFO[0].typeUri = "http://www.w3.org/XML/1998/namespace";
    ATTRIBUTEINFO[0].typeJavaName = "java.lang.Object";
    ATTRIBUTEINFO[0].defaultValue = null;
    ATTRIBUTEINFO[0].required = false;
    ATTRIBUTEINFO[0].setterMethod = "setLang";
    ATTRIBUTEINFO[0].getterMethod = "getLang";
    ATTRIBUTEINFO[0].checkMethod = null;
    // Attribute 1
    ATTRIBUTEINFO[1].fieldLocalName = "id";
    ATTRIBUTEINFO[1].fieldUri = "";
    ATTRIBUTEINFO[1].fieldJavaName = "Id";
    ATTRIBUTEINFO[1].typeName = "ID";
    ATTRIBUTEINFO[1].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[1].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[1].defaultValue = null;
    ATTRIBUTEINFO[1].required = false;
    ATTRIBUTEINFO[1].setterMethod = "setId";
    ATTRIBUTEINFO[1].getterMethod = "getId";
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
    FIELDINFO[0].fieldJavaName = "SmallIcon";
    FIELDINFO[0].fieldLocalName = "small-icon";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 1;
    FIELDINFO[0].minOccurs = 0;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.PathType";
    FIELDINFO[0].typeLocalName = "pathType";
    FIELDINFO[0].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[0].getterMethod = "getSmallIcon";
    FIELDINFO[0].setterMethod = "setSmallIcon";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "LargeIcon";
    FIELDINFO[1].fieldLocalName = "large-icon";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 1;
    FIELDINFO[1].minOccurs = 0;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.PathType";
    FIELDINFO[1].typeLocalName = "pathType";
    FIELDINFO[1].typeUri = "http://java.sun.com/xml/ns/javaee";
    FIELDINFO[1].getterMethod = "getLargeIcon";
    FIELDINFO[1].setterMethod = "setLargeIcon";
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
