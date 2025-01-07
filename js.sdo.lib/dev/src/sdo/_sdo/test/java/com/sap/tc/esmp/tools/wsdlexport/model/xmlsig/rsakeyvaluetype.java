package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "RSAKeyValueType",
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RSAKeyValue"
        )},
    elementFormDefault = true
)
public interface RsaKeyValueType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Modulus",
        propertyIndex = 0,
        sdoType = "http://www.w3.org/2000/09/xmldsig##CryptoBinary",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getModulus();
    void setModulus(byte[] pModulus);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Exponent",
        propertyIndex = 1,
        sdoType = "http://www.w3.org/2000/09/xmldsig##CryptoBinary",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getExponent();
    void setExponent(byte[] pExponent);

}
