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
            name = "interface"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface InterfaceType extends com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ExtensibleDocumentedType {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.InterfaceOperationType> getOperation();
    void setOperation(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.InterfaceOperationType> pOperation);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.InterfaceFaultType> getFault();
    void setFault(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.InterfaceFaultType> pFault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 4,
        sdoType = "http://www.w3.org/ns/wsdl#extends"
    )
    java.util.List<String> getExtends();
    void setExtends(java.util.List<String> pExtends);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 5,
        sdoType = "http://www.w3.org/ns/wsdl#styleDefault"
    )
    java.util.List<String> getStyleDefault();
    void setStyleDefault(java.util.List<String> pStyleDefault);

}
