package com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "ws-addr.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "ProblemAction"
        )},
    uri = "http://www.w3.org/2005/08/addressing"
)
public interface ProblemActionType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoName = "Action"
    )
    com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.AttributedUriType getAction();
    void setAction(com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.AttributedUriType pAction);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        sdoName = "SoapAction",
        sdoType = "commonj.sdo#URI"
    )
    String getSoapAction();
    void setSoapAction(String pSoapAction);

}
