package com.sap.sdo.testcase.typefac.xi;
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30",
    elementFormDefault = true
)
public interface EngineType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/XI/Message/30#String256Type"
    )
    String getValue();
    void setValue(String pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        sdoName = "type",
        propertyIndex = 1,
        sdoType = "http://sap.com/xi/XI/Message/30#String2Type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getEngineTypeType();
    void setEngineTypeType(String pEngineTypeType);

}
