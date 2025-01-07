package report.consumption.es;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "es-consumption-test-report.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "error-details",
    uri = "http://www.sap.com/es-consumption-test-report",
    elementFormDefault = true
)
public interface ErrorDetails  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0
    )
    String getValue();
    void setValue(String pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        sdoType = "http://www.sap.com/es-consumption-test-report#ErrorType"
    )
    String getKind();
    void setKind(String pKind);

}
