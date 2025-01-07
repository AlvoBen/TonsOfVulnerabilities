package com.sap.demo.test12.p491158754BC80170AB1311DDB9BE003005F636A2;

@com.sap.sdo.api.SdoTypeMetaData(
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "$demo.sap.com/test12/491158754BC80170AB1311DDB9BE003005F636A2:response"
        )},
    sdoName = "#response",
    uri = "demo.sap.com/test12/491158754BC80170AB1311DDB9BE003005F636A2"
)
public interface I_Response  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        sdoName = "$result",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true,
            xsdName = "result"
        )
    )
    String getEResult();
    void setEResult(String pEResult);

}
