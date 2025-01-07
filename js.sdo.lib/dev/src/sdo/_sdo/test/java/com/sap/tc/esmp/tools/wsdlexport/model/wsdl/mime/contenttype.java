package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "mime.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "contentType",
    uri = "http://schemas.xmlsoap.org/wsdl/mime/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "content"
        )}
)
public interface ContentType extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibilityElement {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "type",
        propertyIndex = 1
    )
    String getContentTypeType();
    void setContentTypeType(String pContentTypeType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2
    )
    String getPart();
    void setPart(String pPart);

}
