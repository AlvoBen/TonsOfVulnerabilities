package com.sap.glx.paradigmInterface.postprocessor.ws.wsaddr;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "ws-addr.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RelatesTo"
        )},
    uri = "http://www.w3.org/2005/08/addressing"
)
public interface RelatesToType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI"
    )
    String getValue();
    void setValue(String pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "http://www.w3.org/2005/08/addressing/reply",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        sdoName = "RelationshipType",
        sdoType = "http://www.w3.org/2005/08/addressing#RelationshipTypeOpenEnum"
    )
    String getRelationshipType();
    void setRelationshipType(String pRelationshipType);

}
