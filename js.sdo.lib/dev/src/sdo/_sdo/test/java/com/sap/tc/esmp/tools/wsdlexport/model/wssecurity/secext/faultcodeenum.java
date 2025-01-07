package com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"wsse:UnsupportedSecurityToken", "wsse:UnsupportedAlgorithm", "wsse:InvalidSecurity", "wsse:InvalidSecurityToken", "wsse:FailedAuthentication", "wsse:FailedCheck", "wsse:SecurityTokenUnavailable"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
)
public interface FaultcodeEnum extends commonj.sdo.types.URI {}

