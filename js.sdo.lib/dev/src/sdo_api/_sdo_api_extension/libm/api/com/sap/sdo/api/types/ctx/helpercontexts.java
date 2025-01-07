package com.sap.sdo.api.types.ctx;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "Context.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "helperContexts",
    uri = "http://sap.com/sdo/api/types/ctx",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "helperContexts"
        )},
    elementFormDefault = true
)
public interface HelperContexts extends com.sap.sdo.api.types.ctx.HelperContext {

}
