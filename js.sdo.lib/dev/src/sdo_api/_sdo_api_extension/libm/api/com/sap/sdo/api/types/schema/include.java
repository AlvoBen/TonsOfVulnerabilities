﻿package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "include"
        )},
    sdoName = "include",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Include extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getSchemaLocation();
    void setSchemaLocation(String pSchemaLocation);

}
