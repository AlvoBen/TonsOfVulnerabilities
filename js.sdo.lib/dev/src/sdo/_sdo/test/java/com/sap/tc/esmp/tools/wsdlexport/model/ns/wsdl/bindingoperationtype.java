package com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl20.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/ns/wsdl",
    elementFormDefault = true,
    sequenced = true
)
public interface BindingOperationType extends com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ExtensibleDocumentedType {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationMessageType> getInput();
    void setInput(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationMessageType> pInput);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationMessageType> getOutput();
    void setOutput(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationMessageType> pOutput);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationFaultType> getInfault();
    void setInfault(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationFaultType> pInfault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationFaultType> getOutfault();
    void setOutfault(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingOperationFaultType> pOutfault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 5,
        sdoType = "commonj.sdo#URI"
    )
    String getRef();
    void setRef(String pRef);

}
