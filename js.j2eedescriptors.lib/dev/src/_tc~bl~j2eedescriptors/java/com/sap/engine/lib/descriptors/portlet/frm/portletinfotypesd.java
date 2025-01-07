﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 13 17:05:19 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.portlet.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd}portlet-infoType
 */
public  class PortletInfoTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
  }

  public java.lang.String _d_originalLocalName() {
    return "portlet-infoType";
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
    ATTRIBUTEINFO[0].typeName = "string";
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
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[3];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "Title";
    FIELDINFO[0].fieldLocalName = "title";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 1;
    FIELDINFO[0].minOccurs = 1;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors.portlet.TitleType";
    FIELDINFO[0].typeLocalName = "titleType";
    FIELDINFO[0].typeUri = "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
    FIELDINFO[0].getterMethod = "getTitle";
    FIELDINFO[0].setterMethod = "setTitle";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "ShortTitle";
    FIELDINFO[1].fieldLocalName = "short-title";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 1;
    FIELDINFO[1].minOccurs = 0;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors.portlet.ShortTitleType";
    FIELDINFO[1].typeLocalName = "short-titleType";
    FIELDINFO[1].typeUri = "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
    FIELDINFO[1].getterMethod = "getShortTitle";
    FIELDINFO[1].setterMethod = "setShortTitle";
    FIELDINFO[1].checkMethod = null;
    // Field 2
    FIELDINFO[2].defaultValue = null;
    FIELDINFO[2].fieldJavaName = "Keywords";
    FIELDINFO[2].fieldLocalName = "keywords";
    FIELDINFO[2].fieldModel = 1;
    FIELDINFO[2].fieldUri = "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
    FIELDINFO[2].isSoapArray = false;
    FIELDINFO[2].maxOccurs = 1;
    FIELDINFO[2].minOccurs = 0;
    FIELDINFO[2].nillable = false;
    FIELDINFO[2].soapArrayDimensions = 0;
    FIELDINFO[2].soapArrayItemTypeJavaName = null;
    FIELDINFO[2].soapArrayItemTypeLocalName = null;
    FIELDINFO[2].soapArrayItemTypeUri = null;
    FIELDINFO[2].typeJavaName = "com.sap.engine.lib.descriptors.portlet.KeywordsType";
    FIELDINFO[2].typeLocalName = "keywordsType";
    FIELDINFO[2].typeUri = "http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd";
    FIELDINFO[2].getterMethod = "getKeywords";
    FIELDINFO[2].setterMethod = "setKeywords";
    FIELDINFO[2].checkMethod = null;
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
