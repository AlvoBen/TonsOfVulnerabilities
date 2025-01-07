package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "maxExclusive"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "maxInclusive"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "minExclusive"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "minInclusive"
        )},
    sdoName = "facet",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Facet extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    Object getValue();
    void setValue(Object pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "false",
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    boolean isFixed();
    @Deprecated
    boolean getFixed();
    void setFixed(boolean pFixed);

}
