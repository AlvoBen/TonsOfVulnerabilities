﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Oct 19 11:20:17 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ejb.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}security-identityType
 */
public  class SecurityIdentityTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/javaee";
  }

  public java.lang.String _d_originalLocalName() {
    return "security-identityType";
  }


  // Model group nested serializer.
  public static class Choice1 extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedModelGroup {

    public Choice1() {
    }

    // Returns model Group Type
    public int _getModelType() {
      return 2;
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
      FIELDINFO[0].fieldJavaName = "UseCallerIdentity";
      FIELDINFO[0].fieldLocalName = "use-caller-identity";
      FIELDINFO[0].fieldModel = 1;
      FIELDINFO[0].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[0].isSoapArray = false;
      FIELDINFO[0].maxOccurs = 1;
      FIELDINFO[0].minOccurs = 1;
      FIELDINFO[0].nillable = false;
      FIELDINFO[0].soapArrayDimensions = 0;
      FIELDINFO[0].soapArrayItemTypeJavaName = null;
      FIELDINFO[0].soapArrayItemTypeLocalName = null;
      FIELDINFO[0].soapArrayItemTypeUri = null;
      FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.EmptyType";
      FIELDINFO[0].typeLocalName = "emptyType";
      FIELDINFO[0].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[0].getterMethod = "getUseCallerIdentity";
      FIELDINFO[0].setterMethod = "setUseCallerIdentity";
      FIELDINFO[0].checkMethod = "isSetUseCallerIdentity";
      // Field 1
      FIELDINFO[1].defaultValue = null;
      FIELDINFO[1].fieldJavaName = "RunAs";
      FIELDINFO[1].fieldLocalName = "run-as";
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
      FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.RunAsType";
      FIELDINFO[1].typeLocalName = "run-asType";
      FIELDINFO[1].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[1].getterMethod = "getRunAs";
      FIELDINFO[1].setterMethod = "setRunAs";
      FIELDINFO[1].checkMethod = "isSetRunAs";
    }



    public com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[] _getFields() {
      return FIELDINFO;
    }

    public int _getNumberOfFields() {
      return (FIELDINFO.length+super._getNumberOfFields());
    }

    static {
      initFields();
    }
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
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[2];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
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
    FIELDINFO[1].fieldJavaName = "ChoiceGroup1";
    FIELDINFO[1].fieldLocalName = null;
    FIELDINFO[1].fieldModel = 2;
    FIELDINFO[1].fieldUri = null;
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 1;
    FIELDINFO[1].minOccurs = 1;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "Choice1";
    FIELDINFO[1].typeLocalName = null;
    FIELDINFO[1].typeUri = null;
    FIELDINFO[1].getterMethod = "getChoiceGroup1";
    FIELDINFO[1].setterMethod = "setChoiceGroup1";
    FIELDINFO[1].checkMethod = null;
    FIELDINFO[1].objectValue = new Choice1();
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
