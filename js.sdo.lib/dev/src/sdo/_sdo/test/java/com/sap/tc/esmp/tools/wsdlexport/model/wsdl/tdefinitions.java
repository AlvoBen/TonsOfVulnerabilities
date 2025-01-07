package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tDefinitions",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "definitions"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface TDefinitions extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibleDocumented {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TImport> getImport();
    void setImport(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TImport> pImport);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TTypes> getTypes();
    void setTypes(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TTypes> pTypes);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TMessage> getMessage();
    void setMessage(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TMessage> pMessage);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TPortType> getPortType();
    void setPortType(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TPortType> pPortType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 5,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBinding> getBinding();
    void setBinding(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TBinding> pBinding);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 6,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TService> getService();
    void setService(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TService> pService);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 7,
        sdoType = "commonj.sdo#URI"
    )
    String getTargetNamespace();
    void setTargetNamespace(String pTargetNamespace);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 8
    )
    String getName();
    void setName(String pName);

}
