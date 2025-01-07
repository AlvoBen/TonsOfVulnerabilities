package report.consumption.es;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "es-consumption-test-report.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "test-run",
    uri = "http://www.sap.com/es-consumption-test-report",
    elementFormDefault = true
)
public interface TestRun  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "error-details",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    report.consumption.es.ErrorDetails getErrorDetails();
    void setErrorDetails(report.consumption.es.ErrorDetails pErrorDetails);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        sdoType = "http://www.sap.com/es-consumption-test-report#StatusType"
    )
    String getStatus();
    void setStatus(String pStatus);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "test-data-file",
        propertyIndex = 2
    )
    String getTestDataFile();
    void setTestDataFile(String pTestDataFile);

}
