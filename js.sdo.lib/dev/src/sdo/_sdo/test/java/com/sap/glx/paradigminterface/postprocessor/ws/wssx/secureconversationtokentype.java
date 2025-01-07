package com.sap.glx.paradigmInterface.postprocessor.ws.wssx;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "ws-securitypolicy-1.2.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SecureConversationToken"
        )},
    sequenced = true,
    uri = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702"
)
public interface SecureConversationTokenType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoName = "Issuer"
    )
    com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.EndpointReferenceType getIssuer();
    void setIssuer(com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.EndpointReferenceType pIssuer);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        sdoName = "IssuerName",
        sdoType = "commonj.sdo#URI"
    )
    String getIssuerName();
    void setIssuerName(String pIssuerName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        sdoName = "IncludeToken",
        sdoType = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702#IncludeTokenOpenType"
    )
    String getIncludeToken();
    void setIncludeToken(String pIncludeToken);

}
