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
            name = "description"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface DescriptionType extends com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ExtensibleDocumentedType {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ImportType> getImport();
    void setImport(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ImportType> pImport);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.IncludeType> getInclude();
    void setInclude(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.IncludeType> pInclude);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.TypesType> getTypes();
    void setTypes(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.TypesType> pTypes);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.InterfaceType> getInterface();
    void setInterface(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.InterfaceType> pInterface);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 5,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingType> getBinding();
    void setBinding(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.BindingType> pBinding);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 6,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ServiceType> getService();
    void setService(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ServiceType> pService);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 7,
        sdoType = "commonj.sdo#URI"
    )
    String getTargetNamespace();
    void setTargetNamespace(String pTargetNamespace);

}
