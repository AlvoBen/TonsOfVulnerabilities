package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignatureMethod"
        )},
    elementFormDefault = true,
    sequenced = true,
    mixed = true
)
public interface SignatureMethodType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "HMACOutputLength",
        propertyIndex = 0,
        sdoType = "http://www.w3.org/2000/09/xmldsig##HMACOutputLengthType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.math.BigInteger getHmacOutputLength();
    void setHmacOutputLength(java.math.BigInteger pHmacOutputLength);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        sdoName = "Algorithm",
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getAlgorithm();
    void setAlgorithm(String pAlgorithm);

}
