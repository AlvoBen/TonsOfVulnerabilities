package com.sap.demo.test12.p491158754BC80170AB1311DDB9BE003005F636A2;

@com.sap.sdo.api.SdoTypeMetaData(
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "$demo.sap.com/test12/491158754BC80170AB1311DDB9BE003005F636A2:request"
        )},
    sdoName = "#request",
    uri = "demo.sap.com/test12/491158754BC80170AB1311DDB9BE003005F636A2"
)
public interface I_Request  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoName = "$header",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true,
            xsdName = "header"
        )
    )
    com.sap.xi.appl.se.global.I_BasicBusinessDocumentMessageHeader getEHeader();
    void setEHeader(com.sap.xi.appl.se.global.I_BasicBusinessDocumentMessageHeader pEHeader);

}
