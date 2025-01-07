package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "BinarySecurityToken"
        )},
    elementFormDefault = true
)
public interface BinarySecurityTokenType extends com.sap.sdo.testcase.typefac.xi.EncodedString {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        sdoName = "ValueType",
        propertyIndex = 3,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getValueType();
    void setValueType(String pValueType);

}
