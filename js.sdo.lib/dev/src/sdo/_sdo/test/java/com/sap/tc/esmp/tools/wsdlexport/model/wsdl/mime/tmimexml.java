package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "mime.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "tMimeXml",
    uri = "http://schemas.xmlsoap.org/wsdl/mime/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "mimeXml"
        )}
)
public interface TMimeXml extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibilityElement {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1
    )
    String getPart();
    void setPart(String pPart);

}
