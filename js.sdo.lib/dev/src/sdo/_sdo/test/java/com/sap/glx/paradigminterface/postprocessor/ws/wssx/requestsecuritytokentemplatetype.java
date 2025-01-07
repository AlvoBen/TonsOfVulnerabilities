package com.sap.glx.paradigmInterface.postprocessor.ws.wssx;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "ws-securitypolicy-1.2.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    sequenced = true,
    uri = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702"
)
public interface RequestSecurityTokenTemplateType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoName = "TrustVersion",
        sdoType = "commonj.sdo#URI"
    )
    String getTrustVersion();
    void setTrustVersion(String pTrustVersion);

}
