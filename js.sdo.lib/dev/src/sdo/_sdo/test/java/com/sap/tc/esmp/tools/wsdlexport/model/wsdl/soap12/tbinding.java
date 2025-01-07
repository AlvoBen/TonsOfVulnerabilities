package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl11soap12.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tBinding",
    uri = "http://schemas.xmlsoap.org/wsdl/soap12/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "binding"
        )}
)
public interface TBinding extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TExtensibilityElementOpenAttrs {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI"
    )
    String getTransport();
    void setTransport(String pTransport);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2,
        sdoType = "http://schemas.xmlsoap.org/wsdl/soap12/#tStyleChoice"
    )
    String getStyle();
    void setStyle(String pStyle);

}
