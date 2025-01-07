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
            name = "UsernameToken"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface UsernameTokenType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Username",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.AttributedString getUsername();
    void setUsername(com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext.AttributedString pUsername);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 1,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
