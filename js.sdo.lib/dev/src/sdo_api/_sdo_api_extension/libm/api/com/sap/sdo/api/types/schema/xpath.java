package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoFacets(
    pattern = {"(\\.//)?(((child::)?((\\i\\c*:)?(\\i\\c*|\\*)))|\\.)(/(((child::)?((\\i\\c*:)?(\\i\\c*|\\*)))|\\.))*(\\|(\\.//)?(((child::)?((\\i\\c*:)?(\\i\\c*|\\*)))|\\.)(/(((child::)?((\\i\\c*:)?(\\i\\c*|\\*)))|\\.))*)*"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "xpath",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Xpath extends com.sap.sdo.api.types.sdo.String {}

