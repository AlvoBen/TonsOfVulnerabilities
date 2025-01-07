﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Tue Jul 19 13:29:43 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.applicationj2eeengine.frm;

/**
 * Schema complexType Java representation.
 * Represents type {}reference-targetType
 */
public  class ReferenceTargetTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "";
  }

  public java.lang.String _d_originalLocalName() {
    return "reference-targetType";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[2];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "target-type";
    ATTRIBUTEINFO[0].fieldUri = "";
    ATTRIBUTEINFO[0].fieldJavaName = "TargetType";
    ATTRIBUTEINFO[0].typeName = "/xs:schema/xs:complexType[8]/xs:simpleContent/xs:extension/xs:attribute[1]/xs:simpleType";
    ATTRIBUTEINFO[0].typeUri = "";
    ATTRIBUTEINFO[0].typeJavaName = "com.sap.engine.lib.descriptors.applicationj2eeengine.TargetType";
    ATTRIBUTEINFO[0].defaultValue = null;
    ATTRIBUTEINFO[0].required = true;
    ATTRIBUTEINFO[0].setterMethod = "setTargetType";
    ATTRIBUTEINFO[0].getterMethod = "getTargetType";
    ATTRIBUTEINFO[0].checkMethod = null;
    // Attribute 1
    ATTRIBUTEINFO[1].fieldLocalName = "provider-name";
    ATTRIBUTEINFO[1].fieldUri = "";
    ATTRIBUTEINFO[1].fieldJavaName = "ProviderName";
    ATTRIBUTEINFO[1].typeName = "string";
    ATTRIBUTEINFO[1].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[1].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[1].defaultValue = null;
    ATTRIBUTEINFO[1].required = false;
    ATTRIBUTEINFO[1].setterMethod = "setProviderName";
    ATTRIBUTEINFO[1].getterMethod = "getProviderName";
    ATTRIBUTEINFO[1].checkMethod = null;
  }

  // Field information
  private static com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[] FIELDINFO;

  private synchronized static void initFields() {
    // Creating fields
    if (FIELDINFO != null) return;
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[1];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "_value";
    FIELDINFO[0].fieldLocalName = "";
    FIELDINFO[0].fieldModel = 0;
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
    FIELDINFO[0].getterMethod = "get_value";
    FIELDINFO[0].setterMethod = "set_value";
    FIELDINFO[0].checkMethod = null;
  }


  // Returns model Group Type
  public int _getModelType() {
    return 0;
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
