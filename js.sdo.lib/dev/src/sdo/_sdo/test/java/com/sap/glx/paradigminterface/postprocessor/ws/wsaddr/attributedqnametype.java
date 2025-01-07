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
            name = "ProblemHeaderQName"
        )},
    uri = "http://www.w3.org/2005/08/addressing"
)
public interface AttributedQNameType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI"
    )
    String getValue();
    void setValue(String pValue);

}
