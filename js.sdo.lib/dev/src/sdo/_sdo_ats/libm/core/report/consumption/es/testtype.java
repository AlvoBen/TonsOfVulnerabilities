package report.consumption.es;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "es-consumption-test-report.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.sap.com/es-consumption-test-report",
    elementFormDefault = true
)
public interface TestType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "dynamic-metadata-load",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    report.consumption.es.DynamicMetadataLoad getDynamicMetadataLoad();
    void setDynamicMetadataLoad(report.consumption.es.DynamicMetadataLoad pDynamicMetadataLoad);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "test-run",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<report.consumption.es.TestRun> getTestRun();
    void setTestRun(java.util.List<report.consumption.es.TestRun> pTestRun);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2
    )
    String getService();
    void setService(String pService);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3,
        sdoType = "http://www.sap.com/es-consumption-test-report#StatusType"
    )
    String getStatus();
    void setStatus(String pStatus);

}
