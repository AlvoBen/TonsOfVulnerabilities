package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "annotation"
        )},
    sdoName = "annotation",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Annotation extends com.sap.sdo.api.types.schema.OpenAttrs {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#appinfo",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Appinfo> getAppinfo();
    void setAppinfo(java.util.List<com.sap.sdo.api.types.schema.Appinfo> pAppinfo);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#documentation",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Documentation> getDocumentation();
    void setDocumentation(java.util.List<com.sap.sdo.api.types.schema.Documentation> pDocumentation);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#ID"
        )
    )
    String getId();
    void setId(String pId);

}
