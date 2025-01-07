package com.sap.sdo.api.types.schema.hfp;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "hasFacet",
    uri = "http://www.w3.org/2001/XMLSchema-hasFacetAndProperty",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "hasFacet"
        )}
)
public interface HasFacet  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 0,
        sdoType = "http://www.w3.org/2001/XMLSchema-hasFacetAndProperty#hasFacetName",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getName();
    void setName(String pName);

}
