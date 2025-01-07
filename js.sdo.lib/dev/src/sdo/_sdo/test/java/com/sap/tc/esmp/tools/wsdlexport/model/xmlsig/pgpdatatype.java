package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "PGPDataType",
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "PGPData"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface PgpDataType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "PGPKeyID",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getPgpKeyId();
    void setPgpKeyId(byte[] pPgpKeyId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "PGPKeyPacket",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getPgpKeyPacket();
    void setPgpKeyPacket(byte[] pPgpKeyPacket);

}
