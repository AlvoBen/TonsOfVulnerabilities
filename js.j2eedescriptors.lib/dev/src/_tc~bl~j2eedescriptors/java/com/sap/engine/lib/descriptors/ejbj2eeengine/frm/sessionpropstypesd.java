﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu Mar 31 10:03:54 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ejbj2eeengine.frm;

/**
 * Schema complexType Java representation.
 * Represents type {}session-propsType
 */
public  class SessionPropsTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "";
  }

  public java.lang.String _d_originalLocalName() {
    return "session-propsType";
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
      FIELDINFO[0].fieldJavaName = "Passivation";
      FIELDINFO[0].fieldLocalName = "passivation";
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
      FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors.ejbj2eeengine.PassivationType";
      FIELDINFO[0].typeLocalName = "passivationType";
      FIELDINFO[0].typeUri = "";
      FIELDINFO[0].getterMethod = "getPassivation";
      FIELDINFO[0].setterMethod = "setPassivation";
      FIELDINFO[0].checkMethod = "isSetPassivation";
      // Field 1
      FIELDINFO[1].defaultValue = null;
      FIELDINFO[1].fieldJavaName = "KeepsOpenResources";
      FIELDINFO[1].fieldLocalName = "keeps-open-resources";
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
      FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors.ejbj2eeengine.KeepsOpenResources";
      FIELDINFO[1].typeLocalName = "/xs:schema/xs:element[18]/xs:complexType";
      FIELDINFO[1].typeUri = "";
      FIELDINFO[1].getterMethod = "getKeepsOpenResources";
      FIELDINFO[1].setterMethod = "setKeepsOpenResources";
      FIELDINFO[1].checkMethod = "isSetKeepsOpenResources";
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
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[0];
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
    FIELDINFO[0].fieldJavaName = "SessionTimeout";
    FIELDINFO[0].fieldLocalName = "session-timeout";
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
    FIELDINFO[0].getterMethod = "getSessionTimeout";
    FIELDINFO[0].setterMethod = "setSessionTimeout";
    FIELDINFO[0].checkMethod = null;
    // Field 1
    FIELDINFO[1].defaultValue = null;
    FIELDINFO[1].fieldJavaName = "ChoiceGroup1";
    FIELDINFO[1].fieldLocalName = null;
    FIELDINFO[1].fieldModel = 2;
    FIELDINFO[1].fieldUri = null;
    FIELDINFO[1].isSoapArray = false;
    FIELDINFO[1].maxOccurs = 1;
    FIELDINFO[1].minOccurs = 0;
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
    // Field 2
    FIELDINFO[2].defaultValue = null;
    FIELDINFO[2].fieldJavaName = "Property";
    FIELDINFO[2].fieldLocalName = "property";
    FIELDINFO[2].fieldModel = 1;
    FIELDINFO[2].fieldUri = "";
    FIELDINFO[2].isSoapArray = false;
    FIELDINFO[2].maxOccurs = 2147483647;
    FIELDINFO[2].minOccurs = 0;
    FIELDINFO[2].nillable = false;
    FIELDINFO[2].soapArrayDimensions = 0;
    FIELDINFO[2].soapArrayItemTypeJavaName = null;
    FIELDINFO[2].soapArrayItemTypeLocalName = null;
    FIELDINFO[2].soapArrayItemTypeUri = null;
    FIELDINFO[2].typeJavaName = "com.sap.engine.lib.descriptors.ejbj2eeengine.PropertyType[]";
    FIELDINFO[2].typeLocalName = "propertyType";
    FIELDINFO[2].typeUri = "";
    FIELDINFO[2].getterMethod = "getProperty";
    FIELDINFO[2].setterMethod = "setProperty";
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
