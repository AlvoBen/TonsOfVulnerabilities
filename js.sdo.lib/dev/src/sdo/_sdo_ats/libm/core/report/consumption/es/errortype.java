package report.consumption.es;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"Execution", "Test Data Deserialization"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.sap.com/es-consumption-test-report"
)
public interface ErrorType extends com.sap.sdo.api.types.sdo.String {}

