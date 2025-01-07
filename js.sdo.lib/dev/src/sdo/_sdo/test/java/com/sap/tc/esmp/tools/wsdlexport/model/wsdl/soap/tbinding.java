package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "soap.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "tBinding",
    uri = "http://schemas.xmlsoap.org/wsdl/soap/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "binding"
        )}
)
public interface TBinding extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibilityElement {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI"
    )
    String getTransport();
    void setTransport(String pTransport);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2,
        sdoType = "http://schemas.xmlsoap.org/wsdl/soap/#tStyleChoice"
    )
    String getStyle();
    void setStyle(String pStyle);

}
