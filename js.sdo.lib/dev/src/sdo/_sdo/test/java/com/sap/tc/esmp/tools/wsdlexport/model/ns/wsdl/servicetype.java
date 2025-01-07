package com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl20.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/ns/wsdl",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "service"
        )},
    elementFormDefault = true
)
public interface ServiceType extends com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ExtensibleDocumentedType {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.EndpointType> getEndpoint();
    void setEndpoint(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.EndpointType> pEndpoint);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3,
        sdoType = "commonj.sdo#URI"
    )
    String getInterface();
    void setInterface(String pInterface);

}
