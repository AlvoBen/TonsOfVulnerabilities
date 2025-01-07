﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 12 11:18:44 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ormapping.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/persistence/orm}column
 */
public  class ColumnSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/persistence/orm";
  }

  public java.lang.String _d_originalLocalName() {
    return "column";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[10];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[3] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[4] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[5] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[6] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[7] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[8] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[9] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "name";
    ATTRIBUTEINFO[0].fieldUri = "";
    ATTRIBUTEINFO[0].fieldJavaName = "Name";
    ATTRIBUTEINFO[0].typeName = "string";
    ATTRIBUTEINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[0].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[0].defaultValue = null;
    ATTRIBUTEINFO[0].required = false;
    ATTRIBUTEINFO[0].setterMethod = "setName";
    ATTRIBUTEINFO[0].getterMethod = "getName";
    ATTRIBUTEINFO[0].checkMethod = null;
    // Attribute 1
    ATTRIBUTEINFO[1].fieldLocalName = "unique";
    ATTRIBUTEINFO[1].fieldUri = "";
    ATTRIBUTEINFO[1].fieldJavaName = "Unique";
    ATTRIBUTEINFO[1].typeName = "boolean";
    ATTRIBUTEINFO[1].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[1].typeJavaName = "java.lang.Boolean";
    ATTRIBUTEINFO[1].defaultValue = null;
    ATTRIBUTEINFO[1].required = false;
    ATTRIBUTEINFO[1].setterMethod = "setUnique";
    ATTRIBUTEINFO[1].getterMethod = "getUnique";
    ATTRIBUTEINFO[1].checkMethod = null;
    // Attribute 2
    ATTRIBUTEINFO[2].fieldLocalName = "nullable";
    ATTRIBUTEINFO[2].fieldUri = "";
    ATTRIBUTEINFO[2].fieldJavaName = "Nullable";
    ATTRIBUTEINFO[2].typeName = "boolean";
    ATTRIBUTEINFO[2].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[2].typeJavaName = "java.lang.Boolean";
    ATTRIBUTEINFO[2].defaultValue = null;
    ATTRIBUTEINFO[2].required = false;
    ATTRIBUTEINFO[2].setterMethod = "setNullable";
    ATTRIBUTEINFO[2].getterMethod = "getNullable";
    ATTRIBUTEINFO[2].checkMethod = null;
    // Attribute 3
    ATTRIBUTEINFO[3].fieldLocalName = "insertable";
    ATTRIBUTEINFO[3].fieldUri = "";
    ATTRIBUTEINFO[3].fieldJavaName = "Insertable";
    ATTRIBUTEINFO[3].typeName = "boolean";
    ATTRIBUTEINFO[3].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[3].typeJavaName = "java.lang.Boolean";
    ATTRIBUTEINFO[3].defaultValue = null;
    ATTRIBUTEINFO[3].required = false;
    ATTRIBUTEINFO[3].setterMethod = "setInsertable";
    ATTRIBUTEINFO[3].getterMethod = "getInsertable";
    ATTRIBUTEINFO[3].checkMethod = null;
    // Attribute 4
    ATTRIBUTEINFO[4].fieldLocalName = "updatable";
    ATTRIBUTEINFO[4].fieldUri = "";
    ATTRIBUTEINFO[4].fieldJavaName = "Updatable";
    ATTRIBUTEINFO[4].typeName = "boolean";
    ATTRIBUTEINFO[4].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[4].typeJavaName = "java.lang.Boolean";
    ATTRIBUTEINFO[4].defaultValue = null;
    ATTRIBUTEINFO[4].required = false;
    ATTRIBUTEINFO[4].setterMethod = "setUpdatable";
    ATTRIBUTEINFO[4].getterMethod = "getUpdatable";
    ATTRIBUTEINFO[4].checkMethod = null;
    // Attribute 5
    ATTRIBUTEINFO[5].fieldLocalName = "column-definition";
    ATTRIBUTEINFO[5].fieldUri = "";
    ATTRIBUTEINFO[5].fieldJavaName = "ColumnDefinition";
    ATTRIBUTEINFO[5].typeName = "string";
    ATTRIBUTEINFO[5].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[5].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[5].defaultValue = null;
    ATTRIBUTEINFO[5].required = false;
    ATTRIBUTEINFO[5].setterMethod = "setColumnDefinition";
    ATTRIBUTEINFO[5].getterMethod = "getColumnDefinition";
    ATTRIBUTEINFO[5].checkMethod = null;
    // Attribute 6
    ATTRIBUTEINFO[6].fieldLocalName = "table";
    ATTRIBUTEINFO[6].fieldUri = "";
    ATTRIBUTEINFO[6].fieldJavaName = "Table";
    ATTRIBUTEINFO[6].typeName = "string";
    ATTRIBUTEINFO[6].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[6].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[6].defaultValue = null;
    ATTRIBUTEINFO[6].required = false;
    ATTRIBUTEINFO[6].setterMethod = "setTable";
    ATTRIBUTEINFO[6].getterMethod = "getTable";
    ATTRIBUTEINFO[6].checkMethod = null;
    // Attribute 7
    ATTRIBUTEINFO[7].fieldLocalName = "length";
    ATTRIBUTEINFO[7].fieldUri = "";
    ATTRIBUTEINFO[7].fieldJavaName = "Length";
    ATTRIBUTEINFO[7].typeName = "int";
    ATTRIBUTEINFO[7].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[7].typeJavaName = "java.lang.Integer";
    ATTRIBUTEINFO[7].defaultValue = null;
    ATTRIBUTEINFO[7].required = false;
    ATTRIBUTEINFO[7].setterMethod = "setLength";
    ATTRIBUTEINFO[7].getterMethod = "getLength";
    ATTRIBUTEINFO[7].checkMethod = null;
    // Attribute 8
    ATTRIBUTEINFO[8].fieldLocalName = "precision";
    ATTRIBUTEINFO[8].fieldUri = "";
    ATTRIBUTEINFO[8].fieldJavaName = "Precision";
    ATTRIBUTEINFO[8].typeName = "int";
    ATTRIBUTEINFO[8].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[8].typeJavaName = "java.lang.Integer";
    ATTRIBUTEINFO[8].defaultValue = null;
    ATTRIBUTEINFO[8].required = false;
    ATTRIBUTEINFO[8].setterMethod = "setPrecision";
    ATTRIBUTEINFO[8].getterMethod = "getPrecision";
    ATTRIBUTEINFO[8].checkMethod = null;
    // Attribute 9
    ATTRIBUTEINFO[9].fieldLocalName = "scale";
    ATTRIBUTEINFO[9].fieldUri = "";
    ATTRIBUTEINFO[9].fieldJavaName = "Scale";
    ATTRIBUTEINFO[9].typeName = "int";
    ATTRIBUTEINFO[9].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[9].typeJavaName = "java.lang.Integer";
    ATTRIBUTEINFO[9].defaultValue = null;
    ATTRIBUTEINFO[9].required = false;
    ATTRIBUTEINFO[9].setterMethod = "setScale";
    ATTRIBUTEINFO[9].getterMethod = "getScale";
    ATTRIBUTEINFO[9].checkMethod = null;
  }

  // Field information
  private static com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[] FIELDINFO;

  private synchronized static void initFields() {
    // Creating fields
    if (FIELDINFO != null) return;
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[0];
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
