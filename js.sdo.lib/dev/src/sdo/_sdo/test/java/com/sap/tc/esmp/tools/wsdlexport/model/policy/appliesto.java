package com.sap.tc.esmp.tools.wsdlexport.model.policy;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "ws-policy.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://schemas.xmlsoap.org/ws/2004/09/policy",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "AppliesTo"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface AppliesTo  {

}
