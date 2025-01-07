package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "complexContent"
        )},
    sdoName = "complexContent",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface ComplexContent extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.ComplexRestrictionType getRestriction();
    void setRestriction(com.sap.sdo.api.types.schema.ComplexRestrictionType pRestriction);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.ExtensionType getExtension();
    void setExtension(com.sap.sdo.api.types.schema.ExtensionType pExtension);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    boolean isMixed();
    @Deprecated
    boolean getMixed();
    void setMixed(boolean pMixed);

}
