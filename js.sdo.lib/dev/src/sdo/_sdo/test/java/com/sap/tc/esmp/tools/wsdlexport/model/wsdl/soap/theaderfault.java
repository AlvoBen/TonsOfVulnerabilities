package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "soap.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "tHeaderFault",
    uri = "http://schemas.xmlsoap.org/wsdl/soap/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "headerfault"
        )}
)
public interface THeaderFault  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI"
    )
    String getMessage();
    void setMessage(String pMessage);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1
    )
    String getPart();
    void setPart(String pPart);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2,
        sdoType = "http://schemas.xmlsoap.org/wsdl/soap/#useChoice"
    )
    String getUse();
    void setUse(String pUse);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3,
        sdoType = "http://schemas.xmlsoap.org/wsdl/soap/#encodingStyle"
    )
    java.util.List<String> getEncodingStyle();
    void setEncodingStyle(java.util.List<String> pEncodingStyle);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 4,
        sdoType = "commonj.sdo#URI"
    )
    String getNamespace();
    void setNamespace(String pNamespace);

}
