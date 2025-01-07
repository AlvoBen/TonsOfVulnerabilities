package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    mixed = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "appinfo"
        )},
    sdoName = "appinfo",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Appinfo  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getSource();
    void setSource(String pSource);

}
