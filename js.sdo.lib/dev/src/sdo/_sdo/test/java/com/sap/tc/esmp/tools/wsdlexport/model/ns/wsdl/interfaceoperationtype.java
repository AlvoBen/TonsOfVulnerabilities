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
public interface InterfaceOperationType extends com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ExtensibleDocumentedType {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.MessageRefType> getInput();
    void setInput(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.MessageRefType> pInput);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.MessageRefType> getOutput();
    void setOutput(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.MessageRefType> pOutput);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.MessageRefFaultType> getInfault();
    void setInfault(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.MessageRefFaultType> pInfault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.MessageRefFaultType> getOutfault();
    void setOutfault(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.MessageRefFaultType> pOutfault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 5
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 6,
        sdoType = "commonj.sdo#URI"
    )
    String getPattern();
    void setPattern(String pPattern);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "false",
        propertyIndex = 7
    )
    boolean getSafe();
    void setSafe(boolean pSafe);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 8,
        sdoType = "commonj.sdo#URI"
    )
    String getStyle();
    void setStyle(String pStyle);

}
