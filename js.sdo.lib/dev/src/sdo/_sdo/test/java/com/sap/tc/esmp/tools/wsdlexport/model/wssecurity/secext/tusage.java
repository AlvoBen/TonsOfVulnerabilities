package com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "tUsage",
    uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            containment = false,
            name = "Usage"
        )}
)
public interface TUsage extends java.util.List<commonj.sdo.types.URI> {}

