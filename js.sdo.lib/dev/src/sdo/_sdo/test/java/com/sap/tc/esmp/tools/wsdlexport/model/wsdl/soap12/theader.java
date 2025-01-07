package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl11soap12.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tHeader",
    uri = "http://schemas.xmlsoap.org/wsdl/soap12/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "header"
        )}
)
public interface THeader extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TExtensibilityElementOpenAttrs {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.THeaderFault> getHeaderfault();
    void setHeaderfault(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.THeaderFault> pHeaderfault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI"
    )
    String getMessage();
    void setMessage(String pMessage);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3
    )
    String getPart();
    void setPart(String pPart);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 4,
        sdoType = "http://schemas.xmlsoap.org/wsdl/soap12/#useChoice"
    )
    String getUse();
    void setUse(String pUse);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 5,
        sdoType = "commonj.sdo#URI"
    )
    String getEncodingStyle();
    void setEncodingStyle(String pEncodingStyle);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 6,
        sdoType = "commonj.sdo#URI"
    )
    String getNamespace();
    void setNamespace(String pNamespace);

}
