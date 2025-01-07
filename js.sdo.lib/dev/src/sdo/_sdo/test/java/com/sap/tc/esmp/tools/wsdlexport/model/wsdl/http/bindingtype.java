package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.http;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "http.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "bindingType",
    uri = "http://schemas.xmlsoap.org/wsdl/http/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "binding"
        )}
)
public interface BindingType extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibilityElement {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1
    )
    String getVerb();
    void setVerb(String pVerb);

}
