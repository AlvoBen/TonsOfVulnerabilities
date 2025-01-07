package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "+Manifest",
    uri = "http://sap.com/xi/XI/Message/30",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Manifest"
        )},
    elementFormDefault = true
)
public interface EManifest  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Payload",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.PayloadType> getPayload();
    void setPayload(java.util.List<com.sap.sdo.testcase.typefac.xi.PayloadType> pPayload);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd#Id",
            xmlElement = false
        ),
        sdoName = "Id",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getId();
    void setId(String pId);

}
