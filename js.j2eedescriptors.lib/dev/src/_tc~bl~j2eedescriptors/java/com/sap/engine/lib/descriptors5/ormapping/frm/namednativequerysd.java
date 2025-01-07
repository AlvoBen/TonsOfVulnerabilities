﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 12 11:18:44 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ormapping.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/persistence/orm}named-native-query
 */
public  class NamedNativeQuerySD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/persistence/orm";
  }

  public java.lang.String _d_originalLocalName() {
    return "named-native-query";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[3];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "name";
    ATTRIBUTEINFO[0].fieldUri = "";
    ATTRIBUTEINFO[0].fieldJavaName = "Name";
    ATTRIBUTEINFO[0].typeName = "string";
    ATTRIBUTEINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[0].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[0].defaultValue = null;
    ATTRIBUTEINFO[0].required = true;
    ATTRIBUTEINFO[0].setterMethod = "setName";
    ATTRIBUTEINFO[0].getterMethod = "getName";
    ATTRIBUTEINFO[0].checkMethod = null;
    // Attribute 1
    ATTRIBUTEINFO[1].fieldLocalName = "result-class";
    ATTRIBUTEINFO[1].fieldUri = "";
    ATTRIBUTEINFO[1].fieldJavaName = "ResultClass";
    ATTRIBUTEINFO[1].typeName = "string";
    ATTRIBUTEINFO[1].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[1].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[1].defaultValue = null;
    ATTRIBUTEINFO[1].required = false;
    ATTRIBUTEINFO[1].setterMethod = "setResultClass";
    ATTRIBUTEINFO[1].getterMethod = "getResultClass";
    ATTRIBUTEINFO[1].checkMethod = null;
    // Attribute 2
    ATTRIBUTEINFO[2].fieldLocalName = "result-set-mapping";
    ATTRIBUTEINFO[2].fieldUri = "";
    ATTRIBUTEINFO[2].fieldJavaName = "ResultSetMapping";
    ATTRIBUTEINFO[2].typeName = "string";
    ATTRIBUTEINFO[2].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[2].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[2].defaultValue = null;
    ATTRIBUTEINFO[2].required = false;
    ATTRIBUTEINFO[2].setterMethod = "setResultSetMapping";
    ATTRIBUTEINFO[2].getterMethod = "getResultSetMapping";
    ATTRIBUTEINFO[2].checkMethod = null;
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
    FIELDINFO[0].fieldJavaName = "Query";
    FIELDINFO[0].fieldLocalName = "query";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "http://java.sun.com/xml/ns/persistence/orm";
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
    FIELDINFO[0].getterMethod = "getQuery";
    FIELDINFO[0].setterMethod = "setQuery";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "Hint";
    FIELDINFO[1].fieldLocalName = "hint";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "http://java.sun.com/xml/ns/persistence/orm";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 2147483647;
    FIELDINFO[1].minOccurs = 0;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors5.ormapping.QueryHint[]";
    FIELDINFO[1].typeLocalName = "query-hint";
    FIELDINFO[1].typeUri = "http://java.sun.com/xml/ns/persistence/orm";
    FIELDINFO[1].getterMethod = "getHint";
    FIELDINFO[1].setterMethod = "setHint";
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
