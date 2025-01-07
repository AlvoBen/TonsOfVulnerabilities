package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30",
    elementFormDefault = true
)
public interface BranchType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "MessageId",
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/XI/Message/30#GUIDType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getMessageId();
    void setMessageId(String pMessageId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Party",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
  com.sap.sdo.testcase.typefac.xi.PartyType getParty();
    void setParty(com.sap.sdo.testcase.typefac.xi.PartyType pParty);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Service",
        propertyIndex = 2,
        sdoType = "http://sap.com/xi/XI/Message/30#String60Type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getService();
    void setService(String pService);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Info",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getInfo();
    void setInfo(String pInfo);

}
