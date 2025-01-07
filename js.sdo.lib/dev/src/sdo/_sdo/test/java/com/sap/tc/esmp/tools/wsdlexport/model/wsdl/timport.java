package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tImport",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    elementFormDefault = true
)
public interface TImport extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibleAttributesDocumented {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI"
    )
    String getNamespace();
    void setNamespace(String pNamespace);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI"
    )
    String getLocation();
    void setLocation(String pLocation);

}
