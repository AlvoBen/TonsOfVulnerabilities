﻿package com.example.sca;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.osoa.org/xmlns/sca/0.9",
    sequenced = true
)
public interface ModuleComponent  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.example.sca.PropertyValues getProperties();
    void setProperties(com.example.sca.PropertyValues pProperties);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.example.sca.ReferenceValues getReferences();
    void setReferences(com.example.sca.ReferenceValues pReferences);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName",
            xmlElement = false
        ),
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName",
            xmlElement = false
        ),
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getModule();
    void setModule(String pModule);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 4,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getUri();
    void setUri(String pUri);

}
