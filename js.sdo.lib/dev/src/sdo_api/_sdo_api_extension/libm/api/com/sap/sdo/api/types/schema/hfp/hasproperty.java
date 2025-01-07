package com.sap.sdo.api.types.schema.hfp;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "hasProperty",
    uri = "http://www.w3.org/2001/XMLSchema-hasFacetAndProperty",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "hasProperty"
        )}
)
public interface HasProperty  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 0,
        sdoType = "http://www.w3.org/2001/XMLSchema-hasFacetAndProperty#hasPropertyName",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#normalizedString",
            xmlElement = false
        ),
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getValue();
    void setValue(String pValue);

}
