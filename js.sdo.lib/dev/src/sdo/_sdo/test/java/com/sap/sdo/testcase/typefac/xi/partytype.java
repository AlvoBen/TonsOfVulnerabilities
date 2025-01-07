package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30",
    elementFormDefault = true
)
public interface PartyType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/XI/Message/30#String60Type"
    )
    String getValue();
    void setValue(String pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 1,
        sdoType = "http://sap.com/xi/XI/Message/30#String120Type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getAgency();
    void setAgency(String pAgency);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 2,
        sdoType = "http://sap.com/xi/XI/Message/30#String120Type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getScheme();
    void setScheme(String pScheme);

}
