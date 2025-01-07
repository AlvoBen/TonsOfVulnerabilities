package com.example.sca;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.osoa.org/xmlns/sca/0.9",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "interface"
        )}
)
public interface Interface  {

}
