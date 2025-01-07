package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "any"
        )},
    sdoName = "any",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Any extends com.sap.sdo.api.types.schema.Wildcard {

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "1",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#nonNegativeInteger"
        )
    )
    java.math.BigInteger getMinOccurs();
    void setMinOccurs(java.math.BigInteger pMinOccurs);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "1",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 5,
        sdoType = "http://www.w3.org/2001/XMLSchema#allNNI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    Object getMaxOccurs();
    void setMaxOccurs(Object pMaxOccurs);

}
