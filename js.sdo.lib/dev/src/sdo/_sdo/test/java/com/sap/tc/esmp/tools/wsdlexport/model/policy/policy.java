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
            name = "Policy"
        )},
    elementFormDefault = true
)
public interface Policy extends com.sap.tc.esmp.tools.wsdlexport.model.policy.OperatorContentType {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Name",
        propertyIndex = 4,
        sdoType = "commonj.sdo#URI"
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 5,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
