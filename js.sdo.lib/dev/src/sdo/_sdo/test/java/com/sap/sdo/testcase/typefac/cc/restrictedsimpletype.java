package com.sap.sdo.testcase.typefac.cc;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "extendedSimpleType.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "restricted"
        )},
    uri = "ext.xsd"
)
public interface RestrictedSimpleType extends com.sap.sdo.testcase.typefac.cc.ExtendedSimpleType {

}
