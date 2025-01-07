package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30",
    elementFormDefault = true
)
public interface ErrorCodeType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/XI/Message/30#String120Type"
    )
    String getValue();
    void setValue(String pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 1,
        sdoType = "http://sap.com/xi/XI/Message/30#String20Type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getArea();
    void setArea(String pArea);

}
