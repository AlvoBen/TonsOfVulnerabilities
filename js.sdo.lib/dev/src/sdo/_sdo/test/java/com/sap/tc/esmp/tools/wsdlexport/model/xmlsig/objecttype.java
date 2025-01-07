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
            name = "Object"
        )},
    elementFormDefault = true,
    sequenced = true,
    mixed = true
)
public interface ObjectType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 0,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "MimeType",
        propertyIndex = 1
    )
    String getMimeType();
    void setMimeType(String pMimeType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Encoding",
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI"
    )
    String getEncoding();
    void setEncoding(String pEncoding);

}
