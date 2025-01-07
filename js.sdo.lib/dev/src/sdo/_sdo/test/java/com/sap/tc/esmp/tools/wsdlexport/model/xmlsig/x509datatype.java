﻿package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "X509Data"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface X509DataType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "X509IssuerSerial",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.X509IssuerSerialType> getX509IssuerSerial();
    void setX509IssuerSerial(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.X509IssuerSerialType> pX509IssuerSerial);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "X509SKI",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<byte[]> getX509Ski();
    void setX509Ski(java.util.List<byte[]> pX509Ski);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "X509SubjectName",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getX509SubjectName();
    void setX509SubjectName(java.util.List<String> pX509SubjectName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "X509Certificate",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<byte[]> getX509Certificate();
    void setX509Certificate(java.util.List<byte[]> pX509Certificate);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "X509CRL",
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<byte[]> getX509Crl();
    void setX509Crl(java.util.List<byte[]> pX509Crl);

}
