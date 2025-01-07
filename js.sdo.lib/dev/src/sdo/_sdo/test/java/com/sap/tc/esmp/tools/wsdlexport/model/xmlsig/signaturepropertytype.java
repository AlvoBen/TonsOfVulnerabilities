package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignatureProperty"
        )},
    elementFormDefault = true,
    sequenced = true,
    mixed = true
)
public interface SignaturePropertyType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Target",
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI"
    )
    String getTarget();
    void setTarget(String pTarget);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 1,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
