package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "anyAttribute"
        )},
    sdoName = "wildcard",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Wildcard extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "##any",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        sdoType = "http://www.w3.org/2001/XMLSchema#namespaceList",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    java.util.List<String> getNamespace();
    void setNamespace(java.util.List<String> pNamespace);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "strict",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        sdoType = "http://www.w3.org/2001/XMLSchema#processContents",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getProcessContents();
    void setProcessContents(String pProcessContents);

}
