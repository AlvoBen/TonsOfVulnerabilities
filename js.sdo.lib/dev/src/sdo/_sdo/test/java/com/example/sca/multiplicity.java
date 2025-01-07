package com.example.sca;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"0..1", "1..1", "0..n", "1..n"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.osoa.org/xmlns/sca/0.9"
)
public interface Multiplicity extends commonj.sdo.types.String {}

