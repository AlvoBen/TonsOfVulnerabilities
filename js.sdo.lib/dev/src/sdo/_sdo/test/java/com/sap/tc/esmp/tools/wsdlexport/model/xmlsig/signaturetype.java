package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Signature"
        )},
    elementFormDefault = true
)
public interface SignatureType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "SignedInfo",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignedInfoType getSignedInfo();
    void setSignedInfo(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignedInfoType pSignedInfo);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "SignatureValue",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignatureValueType getSignatureValue();
    void setSignatureValue(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignatureValueType pSignatureValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "KeyInfo",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.KeyInfoType getKeyInfo();
    void setKeyInfo(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.KeyInfoType pKeyInfo);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Object",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.ObjectType> getObject();
    void setObject(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.ObjectType> pObject);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 4,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
