package com.sap.glx.paradigmInterface.postprocessor.ws.wssx;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "ws-securitypolicy-1.2.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "EncryptedElements"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RequiredElements"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignedElements"
        )},
    sequenced = true,
    uri = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702"
)
public interface SerElementsType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoName = "XPath"
    )
    java.util.List<String> getXPath();
    void setXPath(java.util.List<String> pXPath);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        sdoName = "XPathVersion",
        sdoType = "commonj.sdo#URI"
    )
    String getXPathVersion();
    void setXPathVersion(String pXPathVersion);

}
