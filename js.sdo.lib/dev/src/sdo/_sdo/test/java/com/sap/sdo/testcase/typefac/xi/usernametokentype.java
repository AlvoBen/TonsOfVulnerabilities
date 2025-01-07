package com.sap.sdo.testcase.typefac.xi;


@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "UsernameToken"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface UsernameTokenType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Username",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.sdo.testcase.typefac.xi.AttributedString getUsername();
    void setUsername(com.sap.sdo.testcase.typefac.xi.AttributedString pUsername);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd#Id",
            xmlElement = false
        ),
        sdoName = "Id",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getId();
    void setId(String pId);

}
