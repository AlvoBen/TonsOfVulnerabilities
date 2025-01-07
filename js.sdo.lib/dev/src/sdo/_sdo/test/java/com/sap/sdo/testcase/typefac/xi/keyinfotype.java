package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "KeyInfo"
        )},
    elementFormDefault = true,
    sequenced = true,
    mixed = true
)
public interface KeyInfoType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##KeyName",
            xmlElement = true
        ),
        sdoName = "KeyName",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getKeyName();
    void setKeyName(java.util.List<String> pKeyName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##KeyValue",
            xmlElement = true
        ),
        containment = true,
        sdoName = "KeyValue",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.KeyValueType> getKeyValue();
    void setKeyValue(java.util.List<com.sap.sdo.testcase.typefac.xi.KeyValueType> pKeyValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##RetrievalMethod",
            xmlElement = true
        ),
        containment = true,
        sdoName = "RetrievalMethod",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.RetrievalMethodType> getRetrievalMethod();
    void setRetrievalMethod(java.util.List<com.sap.sdo.testcase.typefac.xi.RetrievalMethodType> pRetrievalMethod);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##X509Data",
            xmlElement = true
        ),
        containment = true,
        sdoName = "X509Data",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.X509DataType> getX509Data();
    void setX509Data(java.util.List<com.sap.sdo.testcase.typefac.xi.X509DataType> pX509Data);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##PGPData",
            xmlElement = true
        ),
        containment = true,
        sdoName = "PGPData",
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.PgpDataType> getPgpData();
    void setPgpData(java.util.List<com.sap.sdo.testcase.typefac.xi.PgpDataType> pPgpData);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##SPKIData",
            xmlElement = true
        ),
        containment = true,
        sdoName = "SPKIData",
        propertyIndex = 5,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.SpkiDataType> getSpkiData();
    void setSpkiData(java.util.List<com.sap.sdo.testcase.typefac.xi.SpkiDataType> pSpkiData);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2000/09/xmldsig##MgmtData",
            xmlElement = true
        ),
        sdoName = "MgmtData",
        propertyIndex = 6,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getMgmtData();
    void setMgmtData(java.util.List<String> pMgmtData);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#ID",
            xmlElement = false
        ),
        sdoName = "Id",
        propertyIndex = 7,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getId();
    void setId(String pId);

}
