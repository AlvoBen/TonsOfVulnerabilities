package report.service.ecattdistinc.testdata;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "SERVICE_ECATTSDISTINC_testdata.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "values",
    uri = "http://www.sap.com/abapxml"
)
public interface Values  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "DATA",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    report.service.ecattdistinc.testdata.Data getData();
    void setData(report.service.ecattdistinc.testdata.Data pData);

}
