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
            name = "EncryptedParts"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignedParts"
        )},
    sequenced = true,
    uri = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702"
)
public interface SePartsType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoName = "Body"
    )
    com.sap.glx.paradigmInterface.postprocessor.ws.wssx.EmptyType getBody();
    void setBody(com.sap.glx.paradigmInterface.postprocessor.ws.wssx.EmptyType pBody);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        sdoName = "Header"
    )
    java.util.List<com.sap.glx.paradigmInterface.postprocessor.ws.wssx.HeaderType> getHeader();
    void setHeader(java.util.List<com.sap.glx.paradigmInterface.postprocessor.ws.wssx.HeaderType> pHeader);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        sdoName = "Attachments"
    )
    com.sap.glx.paradigmInterface.postprocessor.ws.wssx.EmptyType getAttachments();
    void setAttachments(com.sap.glx.paradigmInterface.postprocessor.ws.wssx.EmptyType pAttachments);

}
