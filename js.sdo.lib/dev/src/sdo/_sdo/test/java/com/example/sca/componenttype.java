package com.example.sca;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.osoa.org/xmlns/sca/0.9",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "componentType"
        )},
    sequenced = true
)
public interface ComponentType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.example.sca.Service> getService();
    void setService(java.util.List<com.example.sca.Service> pService);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.example.sca.Reference> getReference();
    void setReference(java.util.List<com.example.sca.Reference> pReference);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.example.sca.Property> getProperty();
    void setProperty(java.util.List<com.example.sca.Property> pProperty);

}
