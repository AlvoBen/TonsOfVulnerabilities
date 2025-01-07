package com.sap.glx.paradigmInterface.postprocessor.ws.wssx;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/Never", "http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/Once", "http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/AlwaysToRecipient", "http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/AlwaysToInitiator", "http://docs.oasis-open.org/ws-sx/ws-trust/200702/ws-securitypolicy/IncludeToken/Always"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://docs.oasis-open.org/ws-sx/ws-securitypolicy/200702"
)
public interface IncludeTokenType extends com.sap.sdo.api.types.sdo.Uri {}

