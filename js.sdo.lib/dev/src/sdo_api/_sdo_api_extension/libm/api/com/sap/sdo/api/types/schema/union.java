package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "union"
        )},
    sdoName = "union",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Union extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.LocalSimpleType> getSimpleType();
    void setSimpleType(java.util.List<com.sap.sdo.api.types.schema.LocalSimpleType> pSimpleType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        sdoType = "http://www.w3.org/2001/XMLSchema#memberTypes",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#QName"
        )
    )
    java.util.List<String> getMemberTypes();
    void setMemberTypes(java.util.List<String> pMemberTypes);

}
