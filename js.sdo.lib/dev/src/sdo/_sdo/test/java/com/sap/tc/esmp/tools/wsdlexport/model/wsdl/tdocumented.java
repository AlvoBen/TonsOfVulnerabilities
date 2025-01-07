package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "tDocumented",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    elementFormDefault = true
)
public interface TDocumented  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TDocumentation getDocumentation();
    void setDocumentation(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TDocumentation pDocumentation);

}
