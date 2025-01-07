package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    sdoName = "+HopList",
    uri = "http://sap.com/xi/XI/Message/30",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "HopList"
        )},
    elementFormDefault = true
)
public interface EHopList  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        sdoName = "Hop",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.testcase.typefac.xi.HopType> getHop();
    void setHop(java.util.List<com.sap.sdo.testcase.typefac.xi.HopType> pHop);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://schemas.xmlsoap.org/soap/envelope/#mustUnderstand",
            xmlElement = false
        ),
        defaultValue = "false",
        propertyIndex = 1,
        sdoType = "http://schemas.xmlsoap.org/soap/envelope/#@mustUnderstand"
    )
    boolean isMustUnderstand();
    void setMustUnderstand(boolean pMustUnderstand);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd#Id",
            xmlElement = false
        ),
        sdoName = "Id",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getId();
    void setId(String pId);

}
