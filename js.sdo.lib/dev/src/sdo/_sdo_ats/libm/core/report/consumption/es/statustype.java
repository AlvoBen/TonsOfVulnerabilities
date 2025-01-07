package report.consumption.es;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"Error", "Ok"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.sap.com/es-consumption-test-report"
)
public interface StatusType extends com.sap.sdo.api.types.sdo.String {}

