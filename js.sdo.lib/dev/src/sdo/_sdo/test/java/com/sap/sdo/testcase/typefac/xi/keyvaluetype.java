package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "KeyValue"
        )},
    elementFormDefault = true,
    sequenced = true,
    mixed = true
)
public interface KeyValueType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##DSAKeyValue",
            xmlElement = true
        ),
        containment = true,
        sdoName = "DSAKeyValue",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.DsaKeyValueType getDsaKeyValue();
    void setDsaKeyValue(com.sap.sdo.testcase.typefac.xi.DsaKeyValueType pDsaKeyValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##RSAKeyValue",
            xmlElement = true
        ),
        containment = true,
        sdoName = "RSAKeyValue",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
   com.sap.sdo.testcase.typefac.xi.RsaKeyValueType getRsaKeyValue();
    void setRsaKeyValue(com.sap.sdo.testcase.typefac.xi.RsaKeyValueType pRsaKeyValue);

}
