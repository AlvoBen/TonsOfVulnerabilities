﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Thu May 05 12:55:14 EEST 2005
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors.ws04wsdd.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor}SecurityRoleMapDescriptor
 */
public  class SecurityRoleMapDescriptorSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://www.sap.com/webas/630/soap/java/descriptors/ws-deployment-descriptor";
  }

  public java.lang.String _d_originalLocalName() {
    return "SecurityRoleMapDescriptor";
  }


  // Model group nested serializer.
  public static class Choice1 extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedModelGroup {

    public Choice1() {
    }


    // Model group nested serializer.
  public static class Sequence1 extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedModelGroup {

    public Sequence1() {
    }

    // Returns model Group Type
    public int _getModelType() {
      return 3;
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
      FIELDINFO[0].fieldJavaName = "UserName";
      FIELDINFO[0].fieldLocalName = "user-name";
      FIELDINFO[0].fieldModel = 1;
      FIELDINFO[0].fieldUri = "";
      FIELDINFO[0].isSoapArray = false;
      FIELDINFO[0].maxOccurs = 2147483647;
      FIELDINFO[0].minOccurs = 0;
      FIELDINFO[0].nillable = false;
      FIELDINFO[0].soapArrayDimensions = 0;
      FIELDINFO[0].soapArrayItemTypeJavaName = null;
      FIELDINFO[0].soapArrayItemTypeLocalName = null;
      FIELDINFO[0].soapArrayItemTypeUri = null;
      FIELDINFO[0].typeJavaName = "java.lang.String[]";
      FIELDINFO[0].typeLocalName = "string";
      FIELDINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
      FIELDINFO[0].getterMethod = "getUserName";
      FIELDINFO[0].setterMethod = "setUserName";
      FIELDINFO[0].checkMethod = null;
      // Field 1
      FIELDINFO[1].defaultValue = null;
      FIELDINFO[1].fieldJavaName = "GroupName";
      FIELDINFO[1].fieldLocalName = "group-name";
      FIELDINFO[1].fieldModel = 1;
      FIELDINFO[1].fieldUri = "";
      FIELDINFO[1].isSoapArray = false;
      FIELDINFO[1].maxOccurs = 2147483647;
      FIELDINFO[1].minOccurs = 0;
      FIELDINFO[1].nillable = false;
      FIELDINFO[1].soapArrayDimensions = 0;
      FIELDINFO[1].soapArrayItemTypeJavaName = null;
      FIELDINFO[1].soapArrayItemTypeLocalName = null;
      FIELDINFO[1].soapArrayItemTypeUri = null;
      FIELDINFO[1].typeJavaName = "java.lang.String[]";
      FIELDINFO[1].typeLocalName = "string";
      FIELDINFO[1].typeUri = "http://www.w3.org/2001/XMLSchema";
      FIELDINFO[1].getterMethod = "getGroupName";
      FIELDINFO[1].setterMethod = "setGroupName";
      FIELDINFO[1].checkMethod = null;
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
      FIELDINFO[0].fieldJavaName = "SequenceGroup1";
      FIELDINFO[0].fieldLocalName = null;
      FIELDINFO[0].fieldModel = 3;
      FIELDINFO[0].fieldUri = null;
      FIELDINFO[0].isSoapArray = false;
      FIELDINFO[0].maxOccurs = 1;
      FIELDINFO[0].minOccurs = 1;
      FIELDINFO[0].nillable = false;
      FIELDINFO[0].soapArrayDimensions = 0;
      FIELDINFO[0].soapArrayItemTypeJavaName = null;
      FIELDINFO[0].soapArrayItemTypeLocalName = null;
      FIELDINFO[0].soapArrayItemTypeUri = null;
      FIELDINFO[0].typeJavaName = "Sequence1";
      FIELDINFO[0].typeLocalName = null;
      FIELDINFO[0].typeUri = null;
      FIELDINFO[0].getterMethod = "getSequenceGroup1";
      FIELDINFO[0].setterMethod = "setSequenceGroup1";
      FIELDINFO[0].checkMethod = "isSetSequenceGroup1";
      FIELDINFO[0].objectValue = new Sequence1();
      // Field 1
      FIELDINFO[1].defaultValue = null;
      FIELDINFO[1].fieldJavaName = "ServerRoleName";
      FIELDINFO[1].fieldLocalName = "server-role-name";
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
      FIELDINFO[1].getterMethod = "getServerRoleName";
      FIELDINFO[1].setterMethod = "setServerRoleName";
      FIELDINFO[1].checkMethod = "isSetServerRoleName";
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
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[1];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "ChoiceGroup1";
    FIELDINFO[0].fieldLocalName = null;
    FIELDINFO[0].fieldModel = 2;
    FIELDINFO[0].fieldUri = null;
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 1;
    FIELDINFO[0].minOccurs = 1;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "Choice1";
    FIELDINFO[0].typeLocalName = null;
    FIELDINFO[0].typeUri = null;
    FIELDINFO[0].getterMethod = "getChoiceGroup1";
    FIELDINFO[0].setterMethod = "setChoiceGroup1";
    FIELDINFO[0].checkMethod = null;
    FIELDINFO[0].objectValue = new Choice1();
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
