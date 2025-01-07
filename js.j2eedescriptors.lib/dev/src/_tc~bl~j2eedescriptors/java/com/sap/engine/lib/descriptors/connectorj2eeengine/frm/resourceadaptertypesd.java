﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Fri Aug 11 15:00:44 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.connectorj2eeengine.frm;

/**
 * Schema complexType Java representation.
 * Represents type {}resourceadapterType
 */
public  class ResourceadapterTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "";
  }

  public java.lang.String _d_originalLocalName() {
    return "resourceadapterType";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[1];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "apply-updated";
    ATTRIBUTEINFO[0].fieldUri = "";
    ATTRIBUTEINFO[0].fieldJavaName = "ApplyUpdated";
    ATTRIBUTEINFO[0].typeName = "anySimpleType";
    ATTRIBUTEINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[0].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[0].defaultValue = null;
    ATTRIBUTEINFO[0].required = false;
    ATTRIBUTEINFO[0].setterMethod = "setApplyUpdated";
    ATTRIBUTEINFO[0].getterMethod = "getApplyUpdated";
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
    FIELDINFO[0].fieldJavaName = "RaJndiName";
    FIELDINFO[0].fieldLocalName = "ra-jndi-name";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 1;
    FIELDINFO[0].minOccurs = 0;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "java.lang.String";
    FIELDINFO[0].typeLocalName = "string";
    FIELDINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[0].getterMethod = "getRaJndiName";
    FIELDINFO[0].setterMethod = "setRaJndiName";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "OutboundResourceadapter";
    FIELDINFO[1].fieldLocalName = "outbound-resourceadapter";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 1;
    FIELDINFO[1].minOccurs = 0;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors.connectorj2eeengine.OutboundResourceadapterType";
    FIELDINFO[1].typeLocalName = "outbound-resourceadapterType";
    FIELDINFO[1].typeUri = "";
    FIELDINFO[1].getterMethod = "getOutboundResourceadapter";
    FIELDINFO[1].setterMethod = "setOutboundResourceadapter";
    FIELDINFO[1].checkMethod = null;
    // Field 2
    FIELDINFO[2].defaultValue = null;
    FIELDINFO[2].fieldJavaName = "InboundResourceadapter";
    FIELDINFO[2].fieldLocalName = "inbound-resourceadapter";
    FIELDINFO[2].fieldModel = 1;
    FIELDINFO[2].fieldUri = "";
    FIELDINFO[2].isSoapArray = false;
    FIELDINFO[2].maxOccurs = 1;
    FIELDINFO[2].minOccurs = 0;
    FIELDINFO[2].nillable = false;
    FIELDINFO[2].soapArrayDimensions = 0;
    FIELDINFO[2].soapArrayItemTypeJavaName = null;
    FIELDINFO[2].soapArrayItemTypeLocalName = null;
    FIELDINFO[2].soapArrayItemTypeUri = null;
    FIELDINFO[2].typeJavaName = "com.sap.engine.lib.descriptors.connectorj2eeengine.InboundResourceadapterType";
    FIELDINFO[2].typeLocalName = "inbound-resourceadapterType";
    FIELDINFO[2].typeUri = "";
    FIELDINFO[2].getterMethod = "getInboundResourceadapter";
    FIELDINFO[2].setterMethod = "setInboundResourceadapter";
    FIELDINFO[2].checkMethod = null;
    // Field 3
    FIELDINFO[3].defaultValue = null;
    FIELDINFO[3].fieldJavaName = "Adminobjects";
    FIELDINFO[3].fieldLocalName = "adminobjects";
    FIELDINFO[3].fieldModel = 1;
    FIELDINFO[3].fieldUri = "";
    FIELDINFO[3].isSoapArray = false;
    FIELDINFO[3].maxOccurs = 1;
    FIELDINFO[3].minOccurs = 0;
    FIELDINFO[3].nillable = false;
    FIELDINFO[3].soapArrayDimensions = 0;
    FIELDINFO[3].soapArrayItemTypeJavaName = null;
    FIELDINFO[3].soapArrayItemTypeLocalName = null;
    FIELDINFO[3].soapArrayItemTypeUri = null;
    FIELDINFO[3].typeJavaName = "com.sap.engine.lib.descriptors.connectorj2eeengine.AdminobjectsType";
    FIELDINFO[3].typeLocalName = "adminobjectsType";
    FIELDINFO[3].typeUri = "";
    FIELDINFO[3].getterMethod = "getAdminobjects";
    FIELDINFO[3].setterMethod = "setAdminobjects";
    FIELDINFO[3].checkMethod = null;
    // Field 4
    FIELDINFO[4].defaultValue = null;
    FIELDINFO[4].fieldJavaName = "LoaderReferences";
    FIELDINFO[4].fieldLocalName = "loader-references";
    FIELDINFO[4].fieldModel = 1;
    FIELDINFO[4].fieldUri = "";
    FIELDINFO[4].isSoapArray = false;
    FIELDINFO[4].maxOccurs = 1;
    FIELDINFO[4].minOccurs = 0;
    FIELDINFO[4].nillable = false;
    FIELDINFO[4].soapArrayDimensions = 0;
    FIELDINFO[4].soapArrayItemTypeJavaName = null;
    FIELDINFO[4].soapArrayItemTypeLocalName = null;
    FIELDINFO[4].soapArrayItemTypeUri = null;
    FIELDINFO[4].typeJavaName = "com.sap.engine.lib.descriptors.connectorj2eeengine.LoaderReferencesType";
    FIELDINFO[4].typeLocalName = "loader-referencesType";
    FIELDINFO[4].typeUri = "";
    FIELDINFO[4].getterMethod = "getLoaderReferences";
    FIELDINFO[4].setterMethod = "setLoaderReferences";
    FIELDINFO[4].checkMethod = null;
    // Field 5
    FIELDINFO[5].defaultValue = null;
    FIELDINFO[5].fieldJavaName = "WorkmanagerSettings";
    FIELDINFO[5].fieldLocalName = "workmanager-settings";
    FIELDINFO[5].fieldModel = 1;
    FIELDINFO[5].fieldUri = "";
    FIELDINFO[5].isSoapArray = false;
    FIELDINFO[5].maxOccurs = 1;
    FIELDINFO[5].minOccurs = 0;
    FIELDINFO[5].nillable = false;
    FIELDINFO[5].soapArrayDimensions = 0;
    FIELDINFO[5].soapArrayItemTypeJavaName = null;
    FIELDINFO[5].soapArrayItemTypeLocalName = null;
    FIELDINFO[5].soapArrayItemTypeUri = null;
    FIELDINFO[5].typeJavaName = "com.sap.engine.lib.descriptors.connectorj2eeengine.WorkmanagerSettingsType";
    FIELDINFO[5].typeLocalName = "workmanager-settingsType";
    FIELDINFO[5].typeUri = "";
    FIELDINFO[5].getterMethod = "getWorkmanagerSettings";
    FIELDINFO[5].setterMethod = "setWorkmanagerSettings";
    FIELDINFO[5].checkMethod = null;
    // Field 6
    FIELDINFO[6].defaultValue = null;
    FIELDINFO[6].fieldJavaName = "ExtendedProperties";
    FIELDINFO[6].fieldLocalName = "extended-properties";
    FIELDINFO[6].fieldModel = 1;
    FIELDINFO[6].fieldUri = "";
    FIELDINFO[6].isSoapArray = false;
    FIELDINFO[6].maxOccurs = 1;
    FIELDINFO[6].minOccurs = 0;
    FIELDINFO[6].nillable = false;
    FIELDINFO[6].soapArrayDimensions = 0;
    FIELDINFO[6].soapArrayItemTypeJavaName = null;
    FIELDINFO[6].soapArrayItemTypeLocalName = null;
    FIELDINFO[6].soapArrayItemTypeUri = null;
    FIELDINFO[6].typeJavaName = "com.sap.engine.lib.descriptors.connectorj2eeengine.ExtendedPropertiesType";
    FIELDINFO[6].typeLocalName = "extended-propertiesType";
    FIELDINFO[6].typeUri = "";
    FIELDINFO[6].getterMethod = "getExtendedProperties";
    FIELDINFO[6].setterMethod = "setExtendedProperties";
    FIELDINFO[6].checkMethod = null;
    // Field 7
    FIELDINFO[7].defaultValue = null;
    FIELDINFO[7].fieldJavaName = "DestinationName";
    FIELDINFO[7].fieldLocalName = "destination-name";
    FIELDINFO[7].fieldModel = 1;
    FIELDINFO[7].fieldUri = "";
    FIELDINFO[7].isSoapArray = false;
    FIELDINFO[7].maxOccurs = 1;
    FIELDINFO[7].minOccurs = 0;
    FIELDINFO[7].nillable = false;
    FIELDINFO[7].soapArrayDimensions = 0;
    FIELDINFO[7].soapArrayItemTypeJavaName = null;
    FIELDINFO[7].soapArrayItemTypeLocalName = null;
    FIELDINFO[7].soapArrayItemTypeUri = null;
    FIELDINFO[7].typeJavaName = "java.lang.String";
    FIELDINFO[7].typeLocalName = "string";
    FIELDINFO[7].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[7].getterMethod = "getDestinationName";
    FIELDINFO[7].setterMethod = "setDestinationName";
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
