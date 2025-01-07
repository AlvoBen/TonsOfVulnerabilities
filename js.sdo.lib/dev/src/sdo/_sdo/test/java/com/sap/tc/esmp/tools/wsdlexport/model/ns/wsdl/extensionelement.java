package com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl20.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/ns/wsdl",
    abstractDataObject = true,
    elementFormDefault = true
)
public interface ExtensionElement  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "false",
        propertyIndex = 0
    )
    boolean getRequired();
    void setRequired(boolean pRequired);

}
