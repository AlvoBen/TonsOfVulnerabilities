package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "soap.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "tFault",
    uri = "http://schemas.xmlsoap.org/wsdl/soap/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "fault"
        )}
)
public interface TFault extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.soap.TFaultRes {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 5
    )
    String getName();
    void setName(String pName);

}
