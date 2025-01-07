package com.example.sca;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.osoa.org/xmlns/sca/0.9",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "binding"
        )}
)
public interface Binding  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getUri();
    void setUri(String pUri);

}
