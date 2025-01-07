package report.consumption.es;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"JAX-WS Proxy", "Dynamic API"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.sap.com/es-consumption-test-report"
)
public interface TechnologyType extends com.sap.sdo.api.types.sdo.String {}

