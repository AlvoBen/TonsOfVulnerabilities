﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 11:42:02 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04vi.frm;

/**
 * Schema complexType Java representation.
 * Represents type of namespace {http://xml.sap.com/2002/10/metamodel/vi} anonymous with xpath [/xsd:schema/xsd:complexType[23]/xsd:complexContent/xsd:extension/xsd:sequence/xsd:element[3]/xsd:complexType]
 */
public  class VirtualInterfaceSoapExtensionVISD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://xml.sap.com/2002/10/metamodel/vi";
  }

  public java.lang.String _d_originalLocalName() {
    return "";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[0];
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
    FIELDINFO[0].fieldJavaName = "SoapExtensionVI";
    FIELDINFO[0].fieldLocalName = "SoapExtensionVI";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 1;
    FIELDINFO[0].minOccurs = 1;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors.ws04vi.SoapExtensionVIState";
    FIELDINFO[0].typeLocalName = "SoapExtensionVIState";
    FIELDINFO[0].typeUri = "http://xml.sap.com/2002/10/metamodel/vi";
    FIELDINFO[0].getterMethod = "getSoapExtensionVI";
    FIELDINFO[0].setterMethod = "setSoapExtensionVI";
    FIELDINFO[0].checkMethod = null;
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
