package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Transform"
        )},
    elementFormDefault = true,
    sequenced = true,
    mixed = true
)
public interface TransformType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "XPath",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getXPath();
    void setXPath(java.util.List<String> pXPath);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Algorithm",
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI"
    )
    String getAlgorithm();
    void setAlgorithm(String pAlgorithm);

}
