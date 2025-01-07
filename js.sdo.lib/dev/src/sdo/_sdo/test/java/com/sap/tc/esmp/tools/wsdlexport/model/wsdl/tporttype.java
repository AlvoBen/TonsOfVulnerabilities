package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tPortType",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    elementFormDefault = true
)
public interface TPortType extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibleAttributesDocumented {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TOperation> getOperation();
    void setOperation(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TOperation> pOperation);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2
    )
    String getName();
    void setName(String pName);

}
