package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Reference"
        )},
    elementFormDefault = true
)
public interface ReferenceType2  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##Transforms",
            xmlElement = true
        ),
        containment = true,
        sdoName = "Transforms",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
   com.sap.sdo.testcase.typefac.xi.TransformsType getTransforms();
    void setTransforms(com.sap.sdo.testcase.typefac.xi.TransformsType pTransforms);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##DigestMethod",
            xmlElement = true
        ),
        containment = true,
        sdoName = "DigestMethod",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.DigestMethodType getDigestMethod();
    void setDigestMethod(com.sap.sdo.testcase.typefac.xi.DigestMethodType pDigestMethod);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##DigestValue",
            xmlElement = true
        ),
        sdoName = "DigestValue",
        propertyIndex = 2,
        sdoType = "http://www.w3.org/2000/09/xmldsig##DigestValueType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getDigestValue();
    void setDigestValue(byte[] pDigestValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#ID",
            xmlElement = false
        ),
        sdoName = "Id",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getId();
    void setId(String pId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        sdoName = "URI",
        propertyIndex = 4,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getUri();
    void setUri(String pUri);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        sdoName = "Type",
        propertyIndex = 5,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getType();
    void setType(String pType);

}
