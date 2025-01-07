package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignatureProperties"
        )},
    elementFormDefault = true
)
public interface SignaturePropertiesType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##SignatureProperty",
            xmlElement = true
        ),
        containment = true,
        sdoName = "SignatureProperty",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.SignaturePropertyType> getSignatureProperty();
    void setSignatureProperty(java.util.List<com.sap.sdo.testcase.typefac.xi.SignaturePropertyType> pSignatureProperty);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#ID",
            xmlElement = false
        ),
        sdoName = "Id",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getId();
    void setId(String pId);

}
