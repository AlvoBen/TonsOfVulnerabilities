package report.consumption.es;

@com.sap.sdo.api.SdoFacets(
    enumeration = {"WS Tools", "SOA MW", "GP", "VC", "Galaxy", "SDO", "CAF", "AWS Model"}
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.sap.com/es-consumption-test-report"
)
public interface ConsumerNameType extends com.sap.sdo.api.types.sdo.String {}

