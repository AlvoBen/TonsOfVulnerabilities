package com.sap.sdo.testcase.typefac.xi;

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
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##SignedInfo",
            xmlElement = true
        ),
        containment = true,
        sdoName = "SignedInfo",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
   com.sap.sdo.testcase.typefac.xi.SignedInfoType getSignedInfo();
    void setSignedInfo(com.sap.sdo.testcase.typefac.xi.SignedInfoType pSignedInfo);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##SignatureValue",
            xmlElement = true
        ),
        containment = true,
        sdoName = "SignatureValue",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.SignatureValueType getSignatureValue();
    void setSignatureValue(com.sap.sdo.testcase.typefac.xi.SignatureValueType pSignatureValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##KeyInfo",
            xmlElement = true
        ),
        containment = true,
        sdoName = "KeyInfo",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
   com.sap.sdo.testcase.typefac.xi.KeyInfoType getKeyInfo();
    void setKeyInfo(com.sap.sdo.testcase.typefac.xi.KeyInfoType pKeyInfo);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##Object",
            xmlElement = true
        ),
        containment = true,
        sdoName = "Object",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.ObjectType> getObject();
    void setObject(java.util.List<com.sap.sdo.testcase.typefac.xi.ObjectType> pObject);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#ID",
            xmlElement = false
        ),
        sdoName = "Id",
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getId();
    void setId(String pId);

}
