package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl11soap12.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tOperation",
    uri = "http://schemas.xmlsoap.org/wsdl/soap12/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "operation"
        )}
)
public interface TOperation extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TExtensibilityElementOpenAttrs {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI"
    )
    String getSoapAction();
    void setSoapAction(String pSoapAction);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "false",
        propertyIndex = 2
    )
    boolean getSoapActionRequired();
    void setSoapActionRequired(boolean pSoapActionRequired);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3,
        sdoType = "http://schemas.xmlsoap.org/wsdl/soap12/#tStyleChoice"
    )
    String getStyle();
    void setStyle(String pStyle);

}
