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
            name = "Timestamp"
        )},
    elementFormDefault = true
)
public interface TimestampType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Created",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.utility.AttributedDateTime getCreated();
    void setCreated(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.utility.AttributedDateTime pCreated);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Expires",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.utility.AttributedDateTime getExpires();
    void setExpires(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.utility.AttributedDateTime pExpires);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 2,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
