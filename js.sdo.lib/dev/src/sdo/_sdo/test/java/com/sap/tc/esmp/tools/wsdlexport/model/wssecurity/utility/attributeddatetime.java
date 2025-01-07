package com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.utility;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "oasis-200401-wss-wssecurity-utility-1.0.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Expires"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Created"
        )},
    elementFormDefault = true
)
public interface AttributedDateTime  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0
    )
    String getValue();
    void setValue(String pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 1,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
