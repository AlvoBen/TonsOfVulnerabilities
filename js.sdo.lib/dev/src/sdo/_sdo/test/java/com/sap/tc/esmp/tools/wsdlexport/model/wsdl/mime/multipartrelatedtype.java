package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "mime.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "multipartRelatedType",
    uri = "http://schemas.xmlsoap.org/wsdl/mime/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "multipartRelated"
        )}
)
public interface MultipartRelatedType extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibilityElement {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime.TPart> getPart();
    void setPart(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime.TPart> pPart);

}
