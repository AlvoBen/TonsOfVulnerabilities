package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "tExtensibilityElement",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    abstractDataObject = true,
    elementFormDefault = true
)
public interface TExtensibilityElement  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "false",
        propertyIndex = 0
    )
    boolean getRequired();
    void setRequired(boolean pRequired);

}
