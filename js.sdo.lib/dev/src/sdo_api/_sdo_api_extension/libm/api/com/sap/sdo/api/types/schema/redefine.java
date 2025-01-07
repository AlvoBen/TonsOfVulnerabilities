package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "redefine"
        )},
    sdoName = "redefine",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Redefine extends com.sap.sdo.api.types.schema.OpenAttrs {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#annotation",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Annotation> getAnnotation();
    void setAnnotation(java.util.List<com.sap.sdo.api.types.schema.Annotation> pAnnotation);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#simpleType",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.TopLevelSimpleType> getSimpleType();
    void setSimpleType(java.util.List<com.sap.sdo.api.types.schema.TopLevelSimpleType> pSimpleType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#complexType",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.TopLevelComplexType> getComplexType();
    void setComplexType(java.util.List<com.sap.sdo.api.types.schema.TopLevelComplexType> pComplexType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#group",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.NamedGroup> getGroup();
    void setGroup(java.util.List<com.sap.sdo.api.types.schema.NamedGroup> pGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#attributeGroup",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.NamedAttributeGroup> getAttributeGroup();
    void setAttributeGroup(java.util.List<com.sap.sdo.api.types.schema.NamedAttributeGroup> pAttributeGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 5,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getSchemaLocation();
    void setSchemaLocation(String pSchemaLocation);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 6,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#ID"
        )
    )
    String getId();
    void setId(String pId);

}
