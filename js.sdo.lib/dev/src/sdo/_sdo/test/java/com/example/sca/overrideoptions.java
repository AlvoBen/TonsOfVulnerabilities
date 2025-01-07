package com.example.sca;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"no", "may", "must"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.osoa.org/xmlns/sca/0.9"
)
public interface OverrideOptions extends commonj.sdo.types.String {}

