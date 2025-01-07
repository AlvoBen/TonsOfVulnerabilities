﻿/*
 * Generated by SAP SchemaToJava Generator NW05 on Wed Jun 14 11:13:11 EEST 2006
 * Copyright (c) 2002 by SAP Labs Sofia AG.
 * url: http://www.saplabs.bg
 * All rights reserved.
 */
package com.sap.engine.lib.descriptors5.webfacesconfig.frm;

/**
 * Schema complexType Java representation.
 * Represents type {http://java.sun.com/xml/ns/javaee}faces-config-applicationType
 */
public  class FacesConfigApplicationTypeSD extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {

  public java.lang.String _d_originalUri() {
    return "http://java.sun.com/xml/ns/javaee";
  }

  public java.lang.String _d_originalLocalName() {
    return "faces-config-applicationType";
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
      FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[12];
      FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[3] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[4] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[5] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[6] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[7] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[8] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[9] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[10] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      FIELDINFO[11] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
      // Field 0
      FIELDINFO[0].defaultValue = null;
      FIELDINFO[0].fieldJavaName = "ActionListener";
      FIELDINFO[0].fieldLocalName = "action-listener";
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
      FIELDINFO[0].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType";
      FIELDINFO[0].typeLocalName = "fully-qualified-classType";
      FIELDINFO[0].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[0].getterMethod = "getActionListener";
      FIELDINFO[0].setterMethod = "setActionListener";
      FIELDINFO[0].checkMethod = "isSetActionListener";
      // Field 1
      FIELDINFO[1].defaultValue = null;
      FIELDINFO[1].fieldJavaName = "DefaultRenderKitId";
      FIELDINFO[1].fieldLocalName = "default-render-kit-id";
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
      FIELDINFO[1].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.String";
      FIELDINFO[1].typeLocalName = "string";
      FIELDINFO[1].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[1].getterMethod = "getDefaultRenderKitId";
      FIELDINFO[1].setterMethod = "setDefaultRenderKitId";
      FIELDINFO[1].checkMethod = "isSetDefaultRenderKitId";
      // Field 2
      FIELDINFO[2].defaultValue = null;
      FIELDINFO[2].fieldJavaName = "MessageBundle";
      FIELDINFO[2].fieldLocalName = "message-bundle";
      FIELDINFO[2].fieldModel = 1;
      FIELDINFO[2].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[2].isSoapArray = false;
      FIELDINFO[2].maxOccurs = 1;
      FIELDINFO[2].minOccurs = 1;
      FIELDINFO[2].nillable = false;
      FIELDINFO[2].soapArrayDimensions = 0;
      FIELDINFO[2].soapArrayItemTypeJavaName = null;
      FIELDINFO[2].soapArrayItemTypeLocalName = null;
      FIELDINFO[2].soapArrayItemTypeUri = null;
      FIELDINFO[2].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.String";
      FIELDINFO[2].typeLocalName = "string";
      FIELDINFO[2].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[2].getterMethod = "getMessageBundle";
      FIELDINFO[2].setterMethod = "setMessageBundle";
      FIELDINFO[2].checkMethod = "isSetMessageBundle";
      // Field 3
      FIELDINFO[3].defaultValue = null;
      FIELDINFO[3].fieldJavaName = "NavigationHandler";
      FIELDINFO[3].fieldLocalName = "navigation-handler";
      FIELDINFO[3].fieldModel = 1;
      FIELDINFO[3].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[3].isSoapArray = false;
      FIELDINFO[3].maxOccurs = 1;
      FIELDINFO[3].minOccurs = 1;
      FIELDINFO[3].nillable = false;
      FIELDINFO[3].soapArrayDimensions = 0;
      FIELDINFO[3].soapArrayItemTypeJavaName = null;
      FIELDINFO[3].soapArrayItemTypeLocalName = null;
      FIELDINFO[3].soapArrayItemTypeUri = null;
      FIELDINFO[3].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType";
      FIELDINFO[3].typeLocalName = "fully-qualified-classType";
      FIELDINFO[3].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[3].getterMethod = "getNavigationHandler";
      FIELDINFO[3].setterMethod = "setNavigationHandler";
      FIELDINFO[3].checkMethod = "isSetNavigationHandler";
      // Field 4
      FIELDINFO[4].defaultValue = null;
      FIELDINFO[4].fieldJavaName = "ViewHandler";
      FIELDINFO[4].fieldLocalName = "view-handler";
      FIELDINFO[4].fieldModel = 1;
      FIELDINFO[4].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[4].isSoapArray = false;
      FIELDINFO[4].maxOccurs = 1;
      FIELDINFO[4].minOccurs = 1;
      FIELDINFO[4].nillable = false;
      FIELDINFO[4].soapArrayDimensions = 0;
      FIELDINFO[4].soapArrayItemTypeJavaName = null;
      FIELDINFO[4].soapArrayItemTypeLocalName = null;
      FIELDINFO[4].soapArrayItemTypeUri = null;
      FIELDINFO[4].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType";
      FIELDINFO[4].typeLocalName = "fully-qualified-classType";
      FIELDINFO[4].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[4].getterMethod = "getViewHandler";
      FIELDINFO[4].setterMethod = "setViewHandler";
      FIELDINFO[4].checkMethod = "isSetViewHandler";
      // Field 5
      FIELDINFO[5].defaultValue = null;
      FIELDINFO[5].fieldJavaName = "StateManager";
      FIELDINFO[5].fieldLocalName = "state-manager";
      FIELDINFO[5].fieldModel = 1;
      FIELDINFO[5].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[5].isSoapArray = false;
      FIELDINFO[5].maxOccurs = 1;
      FIELDINFO[5].minOccurs = 1;
      FIELDINFO[5].nillable = false;
      FIELDINFO[5].soapArrayDimensions = 0;
      FIELDINFO[5].soapArrayItemTypeJavaName = null;
      FIELDINFO[5].soapArrayItemTypeLocalName = null;
      FIELDINFO[5].soapArrayItemTypeUri = null;
      FIELDINFO[5].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType";
      FIELDINFO[5].typeLocalName = "fully-qualified-classType";
      FIELDINFO[5].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[5].getterMethod = "getStateManager";
      FIELDINFO[5].setterMethod = "setStateManager";
      FIELDINFO[5].checkMethod = "isSetStateManager";
      // Field 6
      FIELDINFO[6].defaultValue = null;
      FIELDINFO[6].fieldJavaName = "ElResolver";
      FIELDINFO[6].fieldLocalName = "el-resolver";
      FIELDINFO[6].fieldModel = 1;
      FIELDINFO[6].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[6].isSoapArray = false;
      FIELDINFO[6].maxOccurs = 1;
      FIELDINFO[6].minOccurs = 1;
      FIELDINFO[6].nillable = false;
      FIELDINFO[6].soapArrayDimensions = 0;
      FIELDINFO[6].soapArrayItemTypeJavaName = null;
      FIELDINFO[6].soapArrayItemTypeLocalName = null;
      FIELDINFO[6].soapArrayItemTypeUri = null;
      FIELDINFO[6].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType";
      FIELDINFO[6].typeLocalName = "fully-qualified-classType";
      FIELDINFO[6].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[6].getterMethod = "getElResolver";
      FIELDINFO[6].setterMethod = "setElResolver";
      FIELDINFO[6].checkMethod = "isSetElResolver";
      // Field 7
      FIELDINFO[7].defaultValue = null;
      FIELDINFO[7].fieldJavaName = "PropertyResolver";
      FIELDINFO[7].fieldLocalName = "property-resolver";
      FIELDINFO[7].fieldModel = 1;
      FIELDINFO[7].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[7].isSoapArray = false;
      FIELDINFO[7].maxOccurs = 1;
      FIELDINFO[7].minOccurs = 1;
      FIELDINFO[7].nillable = false;
      FIELDINFO[7].soapArrayDimensions = 0;
      FIELDINFO[7].soapArrayItemTypeJavaName = null;
      FIELDINFO[7].soapArrayItemTypeLocalName = null;
      FIELDINFO[7].soapArrayItemTypeUri = null;
      FIELDINFO[7].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType";
      FIELDINFO[7].typeLocalName = "fully-qualified-classType";
      FIELDINFO[7].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[7].getterMethod = "getPropertyResolver";
      FIELDINFO[7].setterMethod = "setPropertyResolver";
      FIELDINFO[7].checkMethod = "isSetPropertyResolver";
      // Field 8
      FIELDINFO[8].defaultValue = null;
      FIELDINFO[8].fieldJavaName = "VariableResolver";
      FIELDINFO[8].fieldLocalName = "variable-resolver";
      FIELDINFO[8].fieldModel = 1;
      FIELDINFO[8].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[8].isSoapArray = false;
      FIELDINFO[8].maxOccurs = 1;
      FIELDINFO[8].minOccurs = 1;
      FIELDINFO[8].nillable = false;
      FIELDINFO[8].soapArrayDimensions = 0;
      FIELDINFO[8].soapArrayItemTypeJavaName = null;
      FIELDINFO[8].soapArrayItemTypeLocalName = null;
      FIELDINFO[8].soapArrayItemTypeUri = null;
      FIELDINFO[8].typeJavaName = "com.sap.engine.lib.descriptors5.javaee.FullyQualifiedClassType";
      FIELDINFO[8].typeLocalName = "fully-qualified-classType";
      FIELDINFO[8].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[8].getterMethod = "getVariableResolver";
      FIELDINFO[8].setterMethod = "setVariableResolver";
      FIELDINFO[8].checkMethod = "isSetVariableResolver";
      // Field 9
      FIELDINFO[9].defaultValue = null;
      FIELDINFO[9].fieldJavaName = "LocaleConfig";
      FIELDINFO[9].fieldLocalName = "locale-config";
      FIELDINFO[9].fieldModel = 1;
      FIELDINFO[9].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[9].isSoapArray = false;
      FIELDINFO[9].maxOccurs = 1;
      FIELDINFO[9].minOccurs = 1;
      FIELDINFO[9].nillable = false;
      FIELDINFO[9].soapArrayDimensions = 0;
      FIELDINFO[9].soapArrayItemTypeJavaName = null;
      FIELDINFO[9].soapArrayItemTypeLocalName = null;
      FIELDINFO[9].soapArrayItemTypeUri = null;
      FIELDINFO[9].typeJavaName = "com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigLocaleConfigType";
      FIELDINFO[9].typeLocalName = "faces-config-locale-configType";
      FIELDINFO[9].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[9].getterMethod = "getLocaleConfig";
      FIELDINFO[9].setterMethod = "setLocaleConfig";
      FIELDINFO[9].checkMethod = "isSetLocaleConfig";
      // Field 10
      FIELDINFO[10].defaultValue = null;
      FIELDINFO[10].fieldJavaName = "ResourceBundle";
      FIELDINFO[10].fieldLocalName = "resource-bundle";
      FIELDINFO[10].fieldModel = 1;
      FIELDINFO[10].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[10].isSoapArray = false;
      FIELDINFO[10].maxOccurs = 1;
      FIELDINFO[10].minOccurs = 1;
      FIELDINFO[10].nillable = false;
      FIELDINFO[10].soapArrayDimensions = 0;
      FIELDINFO[10].soapArrayItemTypeJavaName = null;
      FIELDINFO[10].soapArrayItemTypeLocalName = null;
      FIELDINFO[10].soapArrayItemTypeUri = null;
      FIELDINFO[10].typeJavaName = "com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigApplicationResourceBundleType";
      FIELDINFO[10].typeLocalName = "faces-config-application-resource-bundleType";
      FIELDINFO[10].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[10].getterMethod = "getResourceBundle";
      FIELDINFO[10].setterMethod = "setResourceBundle";
      FIELDINFO[10].checkMethod = "isSetResourceBundle";
      // Field 11
      FIELDINFO[11].defaultValue = null;
      FIELDINFO[11].fieldJavaName = "ApplicationExtension";
      FIELDINFO[11].fieldLocalName = "application-extension";
      FIELDINFO[11].fieldModel = 1;
      FIELDINFO[11].fieldUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[11].isSoapArray = false;
      FIELDINFO[11].maxOccurs = 2147483647;
      FIELDINFO[11].minOccurs = 0;
      FIELDINFO[11].nillable = false;
      FIELDINFO[11].soapArrayDimensions = 0;
      FIELDINFO[11].soapArrayItemTypeJavaName = null;
      FIELDINFO[11].soapArrayItemTypeLocalName = null;
      FIELDINFO[11].soapArrayItemTypeUri = null;
      FIELDINFO[11].typeJavaName = "com.sap.engine.lib.descriptors5.webfacesconfig.FacesConfigApplicationExtensionType[]";
      FIELDINFO[11].typeLocalName = "faces-config-application-extensionType";
      FIELDINFO[11].typeUri = "http://java.sun.com/xml/ns/javaee";
      FIELDINFO[11].getterMethod = "getApplicationExtension";
      FIELDINFO[11].setterMethod = "setApplicationExtension";
      FIELDINFO[11].checkMethod = "isSetApplicationExtension";
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
    FIELDINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo[1];
    FIELDINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.FieldInfo();
    // Field 0
    FIELDINFO[0].defaultValue = null;
    FIELDINFO[0].fieldJavaName = "ChoiceGroup1";
    FIELDINFO[0].fieldLocalName = null;
    FIELDINFO[0].fieldModel = 2;
    FIELDINFO[0].fieldUri = null;
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 2147483647;
    FIELDINFO[0].minOccurs = 0;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "Choice1[]";
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
