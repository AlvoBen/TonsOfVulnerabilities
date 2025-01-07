﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 11:42:02 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04vi.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://xml.sap.com/2002/10/metamodel/vi}ParameterState
 */
public  class ParameterStateSD extends com.sap.engine.lib.descriptors.ws04vi.frm.MappableItemStateSD {

  public java.lang.String _d_originalUri() {
    return "http://xml.sap.com/2002/10/metamodel/vi";
  }

  public java.lang.String _d_originalLocalName() {
    return "ParameterState";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[4];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[3] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "nameMappedTo";
    ATTRIBUTEINFO[0].fieldUri = "";
    ATTRIBUTEINFO[0].fieldJavaName = "NameMappedTo";
    ATTRIBUTEINFO[0].typeName = "string";
    ATTRIBUTEINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[0].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[0].defaultValue = null;
    ATTRIBUTEINFO[0].required = false;
    ATTRIBUTEINFO[0].setterMethod = "setNameMappedTo";
    ATTRIBUTEINFO[0].getterMethod = "getNameMappedTo";
    ATTRIBUTEINFO[0].checkMethod = null;
    // Attribute 1
    ATTRIBUTEINFO[1].fieldLocalName = "isOptional";
    ATTRIBUTEINFO[1].fieldUri = "";
    ATTRIBUTEINFO[1].fieldJavaName = "IsOptional";
    ATTRIBUTEINFO[1].typeName = "boolean";
    ATTRIBUTEINFO[1].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[1].typeJavaName = "boolean";
    ATTRIBUTEINFO[1].defaultValue = "false";
    ATTRIBUTEINFO[1].required = false;
    ATTRIBUTEINFO[1].setterMethod = "setIsOptional";
    ATTRIBUTEINFO[1].getterMethod = "isIsOptional";
    ATTRIBUTEINFO[1].checkMethod = null;
    // Attribute 2
    ATTRIBUTEINFO[2].fieldLocalName = "name";
    ATTRIBUTEINFO[2].fieldUri = "";
    ATTRIBUTEINFO[2].fieldJavaName = "Name";
    ATTRIBUTEINFO[2].typeName = "string";
    ATTRIBUTEINFO[2].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[2].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[2].defaultValue = null;
    ATTRIBUTEINFO[2].required = true;
    ATTRIBUTEINFO[2].setterMethod = "setName";
    ATTRIBUTEINFO[2].getterMethod = "getName";
    ATTRIBUTEINFO[2].checkMethod = null;
    // Attribute 3
    ATTRIBUTEINFO[3].fieldLocalName = "isExposed";
    ATTRIBUTEINFO[3].fieldUri = "";
    ATTRIBUTEINFO[3].fieldJavaName = "IsExposed";
    ATTRIBUTEINFO[3].typeName = "boolean";
    ATTRIBUTEINFO[3].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[3].typeJavaName = "boolean";
    ATTRIBUTEINFO[3].defaultValue = "true";
    ATTRIBUTEINFO[3].required = false;
    ATTRIBUTEINFO[3].setterMethod = "setIsExposed";
    ATTRIBUTEINFO[3].getterMethod = "isIsExposed";
    ATTRIBUTEINFO[3].checkMethod = null;
  }

  // Field information
  private static com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[] FIELDINFO;

  private synchronized static void initFields() {
    // Creating fields
    if (FIELDINFO != null) return;
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[4];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[3] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "ParameterDefaultValue";
    FIELDINFO[0].fieldLocalName = "Parameter.DefaultValue";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 1;
    FIELDINFO[0].minOccurs = 0;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors.ws04vi.ParameterDefaultValue";
    FIELDINFO[0].typeLocalName = "/xsd:schema/xsd:complexType[33]/xsd:complexContent/xsd:extension/xsd:sequence/xsd:element[1]/xsd:complexType";
    FIELDINFO[0].typeUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[0].getterMethod = "getParameterDefaultValue";
    FIELDINFO[0].setterMethod = "setParameterDefaultValue";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "ParameterFieldReferences";
    FIELDINFO[1].fieldLocalName = "Parameter.FieldReferences";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 1;
    FIELDINFO[1].minOccurs = 0;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors.ws04vi.ParameterFieldReferences";
    FIELDINFO[1].typeLocalName = "/xsd:schema/xsd:complexType[33]/xsd:complexContent/xsd:extension/xsd:sequence/xsd:element[2]/xsd:complexType";
    FIELDINFO[1].typeUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[1].getterMethod = "getParameterFieldReferences";
    FIELDINFO[1].setterMethod = "setParameterFieldReferences";
    FIELDINFO[1].checkMethod = null;
    // Field 2
    FIELDINFO[2].defaultValue = null;
    FIELDINFO[2].fieldJavaName = "ParameterMappedTypeReference";
    FIELDINFO[2].fieldLocalName = "Parameter.MappedTypeReference";
    FIELDINFO[2].fieldModel = 1;
    FIELDINFO[2].fieldUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[2].isSoapArray = false;
    FIELDINFO[2].maxOccurs = 1;
    FIELDINFO[2].minOccurs = 1;
    FIELDINFO[2].nillable = false;
    FIELDINFO[2].soapArrayDimensions = 0;
    FIELDINFO[2].soapArrayItemTypeJavaName = null;
    FIELDINFO[2].soapArrayItemTypeLocalName = null;
    FIELDINFO[2].soapArrayItemTypeUri = null;
    FIELDINFO[2].typeJavaName = "com.sap.engine.lib.descriptors.ws04vi.ParameterMappedTypeReference";
    FIELDINFO[2].typeLocalName = "/xsd:schema/xsd:complexType[33]/xsd:complexContent/xsd:extension/xsd:sequence/xsd:element[3]/xsd:complexType";
    FIELDINFO[2].typeUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[2].getterMethod = "getParameterMappedTypeReference";
    FIELDINFO[2].setterMethod = "setParameterMappedTypeReference";
    FIELDINFO[2].checkMethod = null;
    // Field 3
    FIELDINFO[3].defaultValue = null;
    FIELDINFO[3].fieldJavaName = "ParameterSoapExtensionParameter";
    FIELDINFO[3].fieldLocalName = "Parameter.SoapExtensionParameter";
    FIELDINFO[3].fieldModel = 1;
    FIELDINFO[3].fieldUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[3].isSoapArray = false;
    FIELDINFO[3].maxOccurs = 1;
    FIELDINFO[3].minOccurs = 0;
    FIELDINFO[3].nillable = false;
    FIELDINFO[3].soapArrayDimensions = 0;
    FIELDINFO[3].soapArrayItemTypeJavaName = null;
    FIELDINFO[3].soapArrayItemTypeLocalName = null;
    FIELDINFO[3].soapArrayItemTypeUri = null;
    FIELDINFO[3].typeJavaName = "com.sap.engine.lib.descriptors.ws04vi.ParameterSoapExtensionParameter";
    FIELDINFO[3].typeLocalName = "/xsd:schema/xsd:complexType[33]/xsd:complexContent/xsd:extension/xsd:sequence/xsd:element[4]/xsd:complexType";
    FIELDINFO[3].typeUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[3].getterMethod = "getParameterSoapExtensionParameter";
    FIELDINFO[3].setterMethod = "setParameterSoapExtensionParameter";
    FIELDINFO[3].checkMethod = null;
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
