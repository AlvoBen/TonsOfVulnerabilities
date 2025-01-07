package com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "oasis-200401-wss-wssecurity-secext-1.0.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SecurityTokenReference"
        )},
    elementFormDefault = true
)
public interface SecurityTokenReferenceType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 0,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Usage",
        propertyIndex = 1,
        sdoType = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd#tUsage"
    )
    java.util.List<String> getUsage();
    void setUsage(java.util.List<String> pUsage);

}
