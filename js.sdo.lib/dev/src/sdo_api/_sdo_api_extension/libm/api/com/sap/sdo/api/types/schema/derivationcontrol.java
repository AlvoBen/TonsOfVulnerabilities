package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"substitution", "extension", "restriction", "list", "union"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "derivationControl",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface DerivationControl extends com.sap.sdo.api.types.sdo.String {}

