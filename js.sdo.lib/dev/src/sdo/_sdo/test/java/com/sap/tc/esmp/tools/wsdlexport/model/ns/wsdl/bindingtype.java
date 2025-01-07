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
            name = "binding"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface BindingType extends com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ExtensibleDocumentedType {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationType> getOperation();
    void setOperation(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationType> pOperation);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingFaultType> getFault();
    void setFault(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingFaultType> pFault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "type",
        propertyIndex = 4,
        sdoType = "commonj.sdo#URI"
    )
    String getBindingTypeType();
    void setBindingTypeType(String pBindingTypeType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 5,
        sdoType = "commonj.sdo#URI"
    )
    String getInterface();
    void setInterface(String pInterface);

}
