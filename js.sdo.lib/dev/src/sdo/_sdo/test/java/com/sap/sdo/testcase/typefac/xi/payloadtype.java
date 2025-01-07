package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/xi/XI/Message/30",
    elementFormDefault = true
)
public interface PayloadType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Name",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Description",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getDescription();
    void setDescription(String pDescription);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        sdoName = "Type",
        propertyIndex = 2,
        sdoType = "http://sap.com/xi/XI/Message/30#PayloadTypeType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getPayloadType();
    void setPayloadType(String pType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/1999/xlink#type",
            xmlElement = false
        ),
        sdoName = "type",
        propertyIndex = 3,
        sdoType = "http://www.w3.org/1999/xlink#@type",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getPayloadTypeType();
    void setPayloadTypeType(String pPayloadTypeType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/1999/xlink#href",
            xmlElement = false
        ),
        propertyIndex = 4,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getHref();
    void setHref(String pHref);

}
