﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Apr 12 11:18:44 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.ormapping.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/persistence/orm}embeddable-attributes
 */
public  class EmbeddableAttributesSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/persistence/orm";
  }

  public java.lang.String _d_originalLocalName() {
    return "embeddable-attributes";
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
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[2];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "Basic";
    FIELDINFO[0].fieldLocalName = "basic";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "http://java.sun.com/xml/ns/persistence/orm";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 2147483647;
    FIELDINFO[0].minOccurs = 0;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors5.ormapping.Basic[]";
    FIELDINFO[0].typeLocalName = "basic";
    FIELDINFO[0].typeUri = "http://java.sun.com/xml/ns/persistence/orm";
    FIELDINFO[0].getterMethod = "getBasic";
    FIELDINFO[0].setterMethod = "setBasic";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "Transient";
    FIELDINFO[1].fieldLocalName = "transient";
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
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors5.ormapping.Transient[]";
    FIELDINFO[1].typeLocalName = "transient";
    FIELDINFO[1].typeUri = "http://java.sun.com/xml/ns/persistence/orm";
    FIELDINFO[1].getterMethod = "getTransient";
    FIELDINFO[1].setterMethod = "setTransient";
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
