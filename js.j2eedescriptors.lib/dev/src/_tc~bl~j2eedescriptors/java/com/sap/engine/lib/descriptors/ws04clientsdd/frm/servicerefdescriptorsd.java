﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 13:01:22 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04clientsdd.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/630/soap/java/descriptors/ws-clients-deployment-descriptor}ServiceRefDescriptor
 */
public  class ServiceRefDescriptorSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://www.sap.com/webas/630/soap/java/descriptors/ws-clients-deployment-descriptor";
  }

  public java.lang.String _d_originalLocalName() {
    return "ServiceRefDescriptor";
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
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[7];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[3] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[4] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[5] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    FIELDINFO[6] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "ServiceRefName";
    FIELDINFO[0].fieldLocalName = "service-ref-name";
    FIELDINFO[0].fieldModel = 1;
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
    FIELDINFO[0].getterMethod = "getServiceRefName";
    FIELDINFO[0].setterMethod = "setServiceRefName";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "ServiceInterface";
    FIELDINFO[1].fieldLocalName = "service-interface";
    FIELDINFO[1].fieldModel = 1;
    FIELDINFO[1].fieldUri = "";
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 1;
    FIELDINFO[1].minOccurs = 1;
    FIELDINFO[1].nillable = false;
    FIELDINFO[1].soapArrayDimensions = 0;
    FIELDINFO[1].soapArrayItemTypeJavaName = null;
    FIELDINFO[1].soapArrayItemTypeLocalName = null;
    FIELDINFO[1].soapArrayItemTypeUri = null;
    FIELDINFO[1].typeJavaName = "java.lang.String";
    FIELDINFO[1].typeLocalName = "string";
    FIELDINFO[1].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[1].getterMethod = "getServiceInterface";
    FIELDINFO[1].setterMethod = "setServiceInterface";
    FIELDINFO[1].checkMethod = null;
    // Field 2
    FIELDINFO[2].defaultValue = null;
    FIELDINFO[2].fieldJavaName = "PackageName";
    FIELDINFO[2].fieldLocalName = "package-name";
    FIELDINFO[2].fieldModel = 1;
    FIELDINFO[2].fieldUri = "";
    FIELDINFO[2].isSoapArray = false;
    FIELDINFO[2].maxOccurs = 1;
    FIELDINFO[2].minOccurs = 1;
    FIELDINFO[2].nillable = false;
    FIELDINFO[2].soapArrayDimensions = 0;
    FIELDINFO[2].soapArrayItemTypeJavaName = null;
    FIELDINFO[2].soapArrayItemTypeLocalName = null;
    FIELDINFO[2].soapArrayItemTypeUri = null;
    FIELDINFO[2].typeJavaName = "java.lang.String";
    FIELDINFO[2].typeLocalName = "string";
    FIELDINFO[2].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[2].getterMethod = "getPackageName";
    FIELDINFO[2].setterMethod = "setPackageName";
    FIELDINFO[2].checkMethod = null;
    // Field 3
    FIELDINFO[3].defaultValue = null;
    FIELDINFO[3].fieldJavaName = "WsdlFile";
    FIELDINFO[3].fieldLocalName = "wsdl-file";
    FIELDINFO[3].fieldModel = 1;
    FIELDINFO[3].fieldUri = "";
    FIELDINFO[3].isSoapArray = false;
    FIELDINFO[3].maxOccurs = 1;
    FIELDINFO[3].minOccurs = 1;
    FIELDINFO[3].nillable = false;
    FIELDINFO[3].soapArrayDimensions = 0;
    FIELDINFO[3].soapArrayItemTypeJavaName = null;
    FIELDINFO[3].soapArrayItemTypeLocalName = null;
    FIELDINFO[3].soapArrayItemTypeUri = null;
    FIELDINFO[3].typeJavaName = "java.lang.String";
    FIELDINFO[3].typeLocalName = "string";
    FIELDINFO[3].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[3].getterMethod = "getWsdlFile";
    FIELDINFO[3].setterMethod = "setWsdlFile";
    FIELDINFO[3].checkMethod = null;
    // Field 4
    FIELDINFO[4].defaultValue = null;
    FIELDINFO[4].fieldJavaName = "LogicalPortsFile";
    FIELDINFO[4].fieldLocalName = "logical-ports-file";
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
    FIELDINFO[4].typeJavaName = "java.lang.String";
    FIELDINFO[4].typeLocalName = "string";
    FIELDINFO[4].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[4].getterMethod = "getLogicalPortsFile";
    FIELDINFO[4].setterMethod = "setLogicalPortsFile";
    FIELDINFO[4].checkMethod = null;
    // Field 5
    FIELDINFO[5].defaultValue = null;
    FIELDINFO[5].fieldJavaName = "UriMappingFile";
    FIELDINFO[5].fieldLocalName = "uri-mapping-file";
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
    FIELDINFO[5].typeJavaName = "java.lang.String";
    FIELDINFO[5].typeLocalName = "string";
    FIELDINFO[5].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[5].getterMethod = "getUriMappingFile";
    FIELDINFO[5].setterMethod = "setUriMappingFile";
    FIELDINFO[5].checkMethod = null;
    // Field 6
    FIELDINFO[6].defaultValue = null;
    FIELDINFO[6].fieldJavaName = "PackageMappingFile";
    FIELDINFO[6].fieldLocalName = "package-mapping-file";
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
    FIELDINFO[6].typeJavaName = "java.lang.String";
    FIELDINFO[6].typeLocalName = "string";
    FIELDINFO[6].typeUri = "http://www.w3.org/2001/XMLSchema";
    FIELDINFO[6].getterMethod = "getPackageMappingFile";
    FIELDINFO[6].setterMethod = "setPackageMappingFile";
    FIELDINFO[6].checkMethod = null;
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
