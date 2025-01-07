package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignedInfo"
        )},
    elementFormDefault = true
)
public interface SignedInfoType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##CanonicalizationMethod",
            xmlElement = true
        ),
        containment = true,
        sdoName = "CanonicalizationMethod",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
   com.sap.sdo.testcase.typefac.xi.CanonicalizationMethodType getCanonicalizationMethod();
    void setCanonicalizationMethod(com.sap.sdo.testcase.typefac.xi.CanonicalizationMethodType pCanonicalizationMethod);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##SignatureMethod",
            xmlElement = true
        ),
        containment = true,
        sdoName = "SignatureMethod",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.SignatureMethodType getSignatureMethod();
    void setSignatureMethod(com.sap.sdo.testcase.typefac.xi.SignatureMethodType pSignatureMethod);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##Reference",
            xmlElement = true
        ),
        containment = true,
        sdoName = "Reference",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.ReferenceType2> getReference();
    void setReference(java.util.List<com.sap.sdo.testcase.typefac.xi.ReferenceType2> pReference);

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

}
