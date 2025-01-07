package report.payload.metadata;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "payload_metadata.xsd"
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
    report.payload.metadata.Data getData();
    void setData(report.payload.metadata.Data pData);

}
