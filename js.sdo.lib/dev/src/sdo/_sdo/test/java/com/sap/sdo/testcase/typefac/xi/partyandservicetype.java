package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30",
    elementFormDefault = true
)
public interface PartyAndServiceType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Party",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
   com.sap.sdo.testcase.typefac.xi.PartyType getParty();
    void setParty(com.sap.sdo.testcase.typefac.xi.PartyType pParty);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Service",
        propertyIndex = 1,
        sdoType = "http://sap.com/xi/XI/Message/30#String60Type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getService();
    void setService(String pService);

}
