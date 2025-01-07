package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl11soap12.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tFault",
    uri = "http://schemas.xmlsoap.org/wsdl/soap12/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "fault"
        )}
)
public interface TFault extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap12.TFaultRes {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 5
    )
    String getName();
    void setName(String pName);

}
