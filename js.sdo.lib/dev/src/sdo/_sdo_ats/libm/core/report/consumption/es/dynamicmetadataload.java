package report.consumption.es;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "es-consumption-test-report.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "dynamic-metadata-load",
    uri = "http://www.sap.com/es-consumption-test-report",
    elementFormDefault = true
)
public interface DynamicMetadataLoad  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "error-details",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getErrorDetails();
    void setErrorDetails(String pErrorDetails);

}
