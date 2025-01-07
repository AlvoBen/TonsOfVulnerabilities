package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"ApplicationMessage", "ApplicationResponse", "SystemAck", "ApplicationAck", "SystemError", "ApplicationError"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30"
)
public interface MessageClassType extends com.sap.sdo.api.types.sdo.String {}

