package com.example.sca;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "SCABinding",
    uri = "http://www.osoa.org/xmlns/sca/0.9",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "binding.sca"
        )},
    sequenced = true
)
public interface ScaBinding extends com.example.sca.Binding {

}
