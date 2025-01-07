package com.example.sca;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.osoa.org/xmlns/sca/0.9",
    sequenced = true
)
public interface EntryPoint  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.example.sca.Interface getInterface();
    void setInterface(com.example.sca.Interface pInterface);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.example.sca.Binding> getBinding();
    void setBinding(java.util.List<com.example.sca.Binding> pBinding);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getReference();
    void setReference(java.util.List<String> pReference);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName",
            xmlElement = false
        ),
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        defaultValue = "1..1",
        propertyIndex = 4,
        sdoType = "http://www.osoa.org/xmlns/sca/0.9#Multiplicity",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getMultiplicity();
    void setMultiplicity(String pMultiplicity);

}
