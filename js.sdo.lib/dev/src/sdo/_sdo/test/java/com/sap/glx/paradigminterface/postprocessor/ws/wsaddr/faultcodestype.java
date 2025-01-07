package com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"tns:InvalidAddressingHeader", "tns:InvalidAddress", "tns:InvalidEPR", "tns:InvalidCardinality", "tns:MissingAddressInEPR", "tns:DuplicateMessageID", "tns:ActionMismatch", "tns:MessageAddressingHeaderRequired", "tns:DestinationUnreachable", "tns:ActionNotSupported", "tns:EndpointUnavailable"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2005/08/addressing"
)
public interface FaultCodesType extends com.sap.sdo.api.types.sdo.Uri {}

