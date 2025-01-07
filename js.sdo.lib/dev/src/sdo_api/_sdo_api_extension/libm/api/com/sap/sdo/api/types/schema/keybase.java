package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "key"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "unique"
        )},
    sdoName = "keybase",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Keybase extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#selector",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.Selector getSelector();
    void setSelector(com.sap.sdo.api.types.schema.Selector pSelector);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#field",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Field> getField();
    void setField(java.util.List<com.sap.sdo.api.types.schema.Field> pField);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName"
        )
    )
    String getName();
    void setName(String pName);

}
