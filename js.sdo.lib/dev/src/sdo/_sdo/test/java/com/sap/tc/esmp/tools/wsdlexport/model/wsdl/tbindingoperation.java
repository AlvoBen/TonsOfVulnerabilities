package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tBindingOperation",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    elementFormDefault = true,
    sequenced = true
)
public interface TBindingOperation extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibleDocumented {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBindingOperationMessage getInput();
    void setInput(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBindingOperationMessage pInput);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBindingOperationMessage getOutput();
    void setOutput(com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBindingOperationMessage pOutput);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBindingOperationFault> getFault();
    void setFault(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBindingOperationFault> pFault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 4
    )
    String getName();
    void setName(String pName);

}
