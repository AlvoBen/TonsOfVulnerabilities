﻿
package com.sap.engine.services.webservices.jaxr.impl.uddi_v2.types;


/**
 * Schema complex type representation (generated by SAP Schema to Java generator).
 * Represents schema complex type {urn:uddi-org:api_v2}bindingDetail
 */

public  class BindingDetail extends com.sap.engine.services.webservices.jaxrpc.encoding.GeneratedComplexType {


  public java.lang.String _d_originalUri() {
    return "urn:uddi-org:api_v2";
  }

  public java.lang.String _d_originalLocalName() {
    return "bindingDetail";
  }

  private static com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[] ATTRIBUTEINFO;

  private synchronized static void initAttribs() {
    // Creating attribute fields
    if (ATTRIBUTEINFO != null) return;
    ATTRIBUTEINFO = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo[3];
    ATTRIBUTEINFO[0] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[1] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    ATTRIBUTEINFO[2] = new com.sap.engine.services.webservices.jaxrpc.encoding.AttributeInfo();
    // Attribute 0
    ATTRIBUTEINFO[0].fieldLocalName = "generic";
    ATTRIBUTEINFO[0].fieldUri = "";
    ATTRIBUTEINFO[0].fieldJavaName = "Generic";
    ATTRIBUTEINFO[0].typeName = "string";
    ATTRIBUTEINFO[0].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[0].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[0].defaultValue = null;
    ATTRIBUTEINFO[0].required = true;
    ATTRIBUTEINFO[0].setterMethod = "setGeneric";
    ATTRIBUTEINFO[0].getterMethod = "getGeneric";
    ATTRIBUTEINFO[0].checkMethod = "hasGeneric";
    // Attribute 1
    ATTRIBUTEINFO[1].fieldLocalName = "operator";
    ATTRIBUTEINFO[1].fieldUri = "";
    ATTRIBUTEINFO[1].fieldJavaName = "Operator";
    ATTRIBUTEINFO[1].typeName = "string";
    ATTRIBUTEINFO[1].typeUri = "http://www.w3.org/2001/XMLSchema";
    ATTRIBUTEINFO[1].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[1].defaultValue = null;
    ATTRIBUTEINFO[1].required = true;
    ATTRIBUTEINFO[1].setterMethod = "setOperator";
    ATTRIBUTEINFO[1].getterMethod = "getOperator";
    ATTRIBUTEINFO[1].checkMethod = "hasOperator";
    // Attribute 2
    ATTRIBUTEINFO[2].fieldLocalName = "truncated";
    ATTRIBUTEINFO[2].fieldUri = "";
    ATTRIBUTEINFO[2].fieldJavaName = "Truncated";
    ATTRIBUTEINFO[2].typeName = "truncated";
    ATTRIBUTEINFO[2].typeUri = "urn:uddi-org:api_v2";
    ATTRIBUTEINFO[2].typeJavaName = "java.lang.String";
    ATTRIBUTEINFO[2].defaultValue = null;
    ATTRIBUTEINFO[2].required = false;
    ATTRIBUTEINFO[2].setterMethod = "setTruncated";
    ATTRIBUTEINFO[2].getterMethod = "getTruncated";
    ATTRIBUTEINFO[2].checkMethod = "hasTruncated";
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
    FIELDINFO[0].fieldJavaName = "BindingTemplate";
    FIELDINFO[0].fieldLocalName = "bindingTemplate";
    FIELDINFO[0].fieldModel = 1;
    FIELDINFO[0].fieldUri = "urn:uddi-org:api_v2";
    FIELDINFO[0].isSoapArray = false;
    FIELDINFO[0].maxOccurs = 2147483647;
    FIELDINFO[0].minOccurs = 0;
    FIELDINFO[0].nillable = false;
    FIELDINFO[0].soapArrayDimensions = 0;
    FIELDINFO[0].soapArrayItemTypeJavaName = null;
    FIELDINFO[0].soapArrayItemTypeLocalName = null;
    FIELDINFO[0].soapArrayItemTypeUri = null;
    FIELDINFO[0].typeJavaName = "com.sap.engine.services.webservices.jaxr.impl.uddi_v2.types.BindingTemplate";
    FIELDINFO[0].typeLocalName = "bindingTemplate";
    FIELDINFO[0].typeUri = "urn:uddi-org:api_v2";
    FIELDINFO[0].getterMethod = "getBindingTemplate";
    FIELDINFO[0].setterMethod = "setBindingTemplate";
    FIELDINFO[0].checkMethod = "hasBindingTemplate";
  }


  // Returns model Group Type
  public int _getModelType() {
    return 3;
  }

  // Attribute field
  private java.lang.String _a_Generic;
  private boolean _a_hasGeneric;
  // set method
  public void setGeneric(java.lang.String _Generic) {
    this._a_Generic = _Generic;
    this._a_hasGeneric = true;
  }
  // clear method
  public void clearGeneric(java.lang.String _Generic) {
    this._a_hasGeneric = false;
  }
  // get method
  public java.lang.String getGeneric() {
    return _a_Generic;
  }
  // has method
  public boolean hasGeneric() {
    return _a_hasGeneric;
  }

  // Attribute field
  private java.lang.String _a_Operator;
  private boolean _a_hasOperator;
  // set method
  public void setOperator(java.lang.String _Operator) {
    this._a_Operator = _Operator;
    this._a_hasOperator = true;
  }
  // clear method
  public void clearOperator(java.lang.String _Operator) {
    this._a_hasOperator = false;
  }
  // get method
  public java.lang.String getOperator() {
    return _a_Operator;
  }
  // has method
  public boolean hasOperator() {
    return _a_hasOperator;
  }

  // Attribute field
  private java.lang.String _a_Truncated;
  private boolean _a_hasTruncated;
  // set method
  public void setTruncated(java.lang.String _Truncated) {
    this._a_Truncated = _Truncated;
    this._a_hasTruncated = true;
  }
  // clear method
  public void clearTruncated(java.lang.String _Truncated) {
    this._a_hasTruncated = false;
  }
  // get method
  public java.lang.String getTruncated() {
    return _a_Truncated;
  }
  // has method
  public boolean hasTruncated() {
    return _a_hasTruncated;
  }

  // Element field
  private com.sap.engine.services.webservices.jaxr.impl.uddi_v2.types.BindingTemplate[] _f_BindingTemplate = new com.sap.engine.services.webservices.jaxr.impl.uddi_v2.types.BindingTemplate[0];
  public void setBindingTemplate(com.sap.engine.services.webservices.jaxr.impl.uddi_v2.types.BindingTemplate[] _BindingTemplate) {
    this._f_BindingTemplate = _BindingTemplate;
  }
  public com.sap.engine.services.webservices.jaxr.impl.uddi_v2.types.BindingTemplate[] getBindingTemplate() {
    return _f_BindingTemplate;
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
