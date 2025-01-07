package com.sap.sdo.testcase.anonymous;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "/com/sap/sdo/testcase/schemas/Anonymous.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "+global",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "global"
        )}
)
public interface EGlobal extends com.sap.sdo.testcase.anonymous.GlobalType {

}
