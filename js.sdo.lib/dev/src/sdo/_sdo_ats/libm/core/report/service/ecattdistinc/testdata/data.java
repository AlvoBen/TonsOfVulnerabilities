package report.service.ecattdistinc.testdata;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "SERVICE_ECATTSDISTINC_testdata.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "DATA",
    uri = "http://www.sap.com/abapxml"
)
public interface Data  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<report.service.ecattdistinc.testdata.ItemType> getItem();
    void setItem(java.util.List<report.service.ecattdistinc.testdata.ItemType> pItem);

}
