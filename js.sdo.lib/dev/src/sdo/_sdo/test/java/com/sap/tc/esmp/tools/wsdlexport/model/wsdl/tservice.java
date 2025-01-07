package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tService",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    elementFormDefault = true,
    sequenced = true
)
public interface TService extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibleDocumented {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TPort> getPort();
    void setPort(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TPort> pPort);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2
    )
    String getName();
    void setName(String pName);

}
