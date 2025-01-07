package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl11soap12.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tBody",
    uri = "http://schemas.xmlsoap.org/wsdl/soap12/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "body"
        )}
)
public interface TBody extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TExtensibilityElementOpenAttrs {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        sdoType = "http://schemas.xmlsoap.org/wsdl/soap12/#tParts"
    )
    java.util.List<String> getParts();
    void setParts(java.util.List<String> pParts);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI"
    )
    String getEncodingStyle();
    void setEncodingStyle(String pEncodingStyle);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3,
        sdoType = "http://schemas.xmlsoap.org/wsdl/soap12/#useChoice"
    )
    String getUse();
    void setUse(String pUse);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 4,
        sdoType = "commonj.sdo#URI"
    )
    String getNamespace();
    void setNamespace(String pNamespace);

}
