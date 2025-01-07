﻿package report.service.ecattdistinc.testdata;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "SERVICE_ECATTSDISTINC_testdata.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "abap",
    uri = "http://www.sap.com/abapxml",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "abap"
        )}
)
public interface Abap  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    report.service.ecattdistinc.testdata.Values getValues();
    void setValues(report.service.ecattdistinc.testdata.Values pValues);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1
    )
    java.math.BigDecimal getVersion();
    void setVersion(java.math.BigDecimal pVersion);

}
