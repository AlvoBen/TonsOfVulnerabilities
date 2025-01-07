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
            name = "RetryAfter"
        )},
    uri = "http://www.w3.org/2005/08/addressing"
)
public interface AttributedUnsignedLongType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0
    )
    java.math.BigInteger getValue();
    void setValue(java.math.BigInteger pValue);

}
