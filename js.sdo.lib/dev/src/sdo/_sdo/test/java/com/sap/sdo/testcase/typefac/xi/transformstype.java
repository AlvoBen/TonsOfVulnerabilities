package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Transforms"
        )},
    elementFormDefault = true
)
public interface TransformsType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##Transform",
            xmlElement = true
        ),
        containment = true,
        sdoName = "Transform",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.TransformType> getTransform();
    void setTransform(java.util.List<com.sap.sdo.testcase.typefac.xi.TransformType> pTransform);

}
