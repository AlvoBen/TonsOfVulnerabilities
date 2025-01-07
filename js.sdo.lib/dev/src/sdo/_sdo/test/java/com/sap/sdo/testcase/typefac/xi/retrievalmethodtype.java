package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RetrievalMethod"
        )},
    elementFormDefault = true
)
public interface RetrievalMethodType  {

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
            xmlElement = false
        ),
        sdoName = "URI",
        propertyIndex = 1,
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
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getType();
    void setType(String pType);

}
