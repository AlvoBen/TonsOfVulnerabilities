package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "DSAKeyValueType",
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "DSAKeyValue"
        )},
    elementFormDefault = true
)
public interface DsaKeyValueType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "P",
        propertyIndex = 0,
        sdoType = "http://www.w3.org/2000/09/xmldsig##CryptoBinary",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getP();
    void setP(byte[] pP);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Q",
        propertyIndex = 1,
        sdoType = "http://www.w3.org/2000/09/xmldsig##CryptoBinary",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getQ();
    void setQ(byte[] pQ);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "G",
        propertyIndex = 2,
        sdoType = "http://www.w3.org/2000/09/xmldsig##CryptoBinary",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getG();
    void setG(byte[] pG);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Y",
        propertyIndex = 3,
        sdoType = "http://www.w3.org/2000/09/xmldsig##CryptoBinary",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getY();
    void setY(byte[] pY);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "J",
        propertyIndex = 4,
        sdoType = "http://www.w3.org/2000/09/xmldsig##CryptoBinary",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getJ();
    void setJ(byte[] pJ);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Seed",
        propertyIndex = 5,
        sdoType = "http://www.w3.org/2000/09/xmldsig##CryptoBinary",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getSeed();
    void setSeed(byte[] pSeed);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "PgenCounter",
        propertyIndex = 6,
        sdoType = "http://www.w3.org/2000/09/xmldsig##CryptoBinary",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getPgenCounter();
    void setPgenCounter(byte[] pPgenCounter);

}
