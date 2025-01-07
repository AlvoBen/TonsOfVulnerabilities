package com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl20.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/ns/wsdl",
    elementFormDefault = true
)
public interface DocumentedType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.DocumentationType> getDocumentation();
    void setDocumentation(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.DocumentationType> pDocumentation);

}
