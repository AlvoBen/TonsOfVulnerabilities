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
            name = "EndpointReference"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "FaultTo"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "From"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "ReplyTo"
        )},
    sequenced = true,
    uri = "http://www.w3.org/2005/08/addressing"
)
public interface EndpointReferenceType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoName = "Address"
    )
    com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.AttributedUriType getAddress();
    void setAddress(com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.AttributedUriType pAddress);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        sdoName = "ReferenceParameters"
    )
    com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.ReferenceParametersType getReferenceParameters();
    void setReferenceParameters(com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.ReferenceParametersType pReferenceParameters);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        sdoName = "Metadata"
    )
    com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.MetadataType getMetadata();
    void setMetadata(com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr.MetadataType pMetadata);

}
