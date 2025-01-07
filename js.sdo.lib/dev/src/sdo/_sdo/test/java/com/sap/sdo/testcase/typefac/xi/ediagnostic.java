package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "+Diagnostic",
    uri = "http://sap.com/xi/XI/Message/30",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Diagnostic"
        )},
    elementFormDefault = true
)
public interface EDiagnostic  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "TraceLevel",
        propertyIndex = 0,
        sdoType = "http://sap.com/xi/XI/Message/30#TraceLevelType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getTraceLevel();
    void setTraceLevel(String pTraceLevel);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Logging",
        propertyIndex = 1,
        sdoType = "http://sap.com/xi/XI/Message/30#LoggingType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getLogging();
    void setLogging(String pLogging);

}
