package com.example.sca;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.osoa.org/xmlns/sca/0.9",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "implementation.java"
        )},
    sequenced = true
)
public interface JavaImplementation extends com.example.sca.Implementation {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName",
            xmlElement = false
        ),
        sdoName = "class",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getClazz();
    void setClazz(String pClazz);

}
