package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://schemas.xmlsoap.org/soap/envelope/"
)
public interface Fault  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#QName",
            xmlElement = true
        ),
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getFaultcode();
    void setFaultcode(String pFaultcode);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getFaultstring();
    void setFaultstring(String pFaultstring);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getFaultactor();
    void setFaultactor(String pFaultactor);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.Detail getDetail();
    void setDetail(com.sap.sdo.testcase.typefac.xi.Detail pDetail);

}
