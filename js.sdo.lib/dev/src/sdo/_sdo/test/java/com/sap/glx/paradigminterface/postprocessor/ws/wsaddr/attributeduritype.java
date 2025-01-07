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
            name = "Action"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "MessageID"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "ProblemIRI"
        ),
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "To"
        )},
    sdoName = "AttributedURIType",
    uri = "http://www.w3.org/2005/08/addressing"
)
public interface AttributedUriType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI"
    )
    String getValue();
    void setValue(String pValue);

}
