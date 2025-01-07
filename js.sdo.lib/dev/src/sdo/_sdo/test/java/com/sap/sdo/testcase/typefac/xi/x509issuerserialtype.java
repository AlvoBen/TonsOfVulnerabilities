package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    elementFormDefault = true
)
public interface X509IssuerSerialType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "X509IssuerName",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getX509IssuerName();
    void setX509IssuerName(String pX509IssuerName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "X509SerialNumber",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.math.BigInteger getX509SerialNumber();
    void setX509SerialNumber(java.math.BigInteger pX509SerialNumber);

}
