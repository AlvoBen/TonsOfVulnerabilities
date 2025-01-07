package report.consumption.es;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "es-consumption-test-report.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "report",
    uri = "http://www.sap.com/es-consumption-test-report",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "report"
        )},
    elementFormDefault = true
)
public interface Report  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<report.consumption.es.TestType> getTest();
    void setTest(java.util.List<report.consumption.es.TestType> pTest);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "0",
        propertyIndex = 1
    )
    int getCw();
    void setCw(int pCw);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "0",
        propertyIndex = 2
    )
    int getYear();
    void setYear(int pYear);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3,
        sdoType = "http://www.sap.com/es-consumption-test-report#TechnologyType"
    )
    String getTechnology();
    void setTechnology(String pTechnology);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 4,
        sdoType = "http://www.sap.com/es-consumption-test-report#ConsumerNameType"
    )
    String getConsumer();
    void setConsumer(String pConsumer);

}
