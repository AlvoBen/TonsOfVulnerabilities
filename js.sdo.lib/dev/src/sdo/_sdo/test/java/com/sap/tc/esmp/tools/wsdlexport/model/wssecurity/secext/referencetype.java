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
            name = "Reference"
        )},
    elementFormDefault = true
)
public interface ReferenceType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "URI",
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI"
    )
    String getUri();
    void setUri(String pUri);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "ValueType",
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI"
    )
    String getValueType();
    void setValueType(String pValueType);

}
