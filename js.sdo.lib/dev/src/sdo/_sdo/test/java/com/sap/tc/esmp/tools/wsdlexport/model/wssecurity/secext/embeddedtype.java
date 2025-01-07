package com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "oasis-200401-wss-wssecurity-secext-1.0.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Embedded"
        )},
    elementFormDefault = true
)
public interface EmbeddedType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "ValueType",
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI"
    )
    String getValueType();
    void setValueType(String pValueType);

}
