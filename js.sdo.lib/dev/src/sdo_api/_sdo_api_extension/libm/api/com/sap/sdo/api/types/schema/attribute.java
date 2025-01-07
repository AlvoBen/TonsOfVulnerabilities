package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    sdoName = "attribute",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Attribute extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.LocalSimpleType getSimpleType();
    void setSimpleType(com.sap.sdo.api.types.schema.LocalSimpleType pSimpleType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName"
        )
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#QName"
        )
    )
    String getRef();
    void setRef(String pRef);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 5,
        sdoName = "type",
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#QName"
        )
    )
    String getAttributeType();
    void setAttributeType(String pAttributeType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "optional",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 6,
        sdoType = "http://www.w3.org/2001/XMLSchema#use",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getUse();
    void setUse(String pUse);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 7,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getDefault();
    void setDefault(String pDefault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 8,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getFixed();
    void setFixed(String pFixed);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 9,
        sdoType = "http://www.w3.org/2001/XMLSchema#formChoice",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getForm();
    void setForm(String pForm);

}
