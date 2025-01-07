package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "schema"
        )},
    sdoName = "schema",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Schema extends com.sap.sdo.api.types.schema.OpenAttrs {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#include",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Include> getInclude();
    void setInclude(java.util.List<com.sap.sdo.api.types.schema.Include> pInclude);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#import",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Import> getImport();
    void setImport(java.util.List<com.sap.sdo.api.types.schema.Import> pImport);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#redefine",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Redefine> getRedefine();
    void setRedefine(java.util.List<com.sap.sdo.api.types.schema.Redefine> pRedefine);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
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
        propertyIndex = 4,
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
        propertyIndex = 5,
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
        propertyIndex = 6,
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
        propertyIndex = 7,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#attributeGroup",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.NamedAttributeGroup> getAttributeGroup();
    void setAttributeGroup(java.util.List<com.sap.sdo.api.types.schema.NamedAttributeGroup> pAttributeGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 8,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#element",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.TopLevelElement> getElement();
    void setElement(java.util.List<com.sap.sdo.api.types.schema.TopLevelElement> pElement);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 9,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#attribute",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.TopLevelAttribute> getAttribute();
    void setAttribute(java.util.List<com.sap.sdo.api.types.schema.TopLevelAttribute> pAttribute);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 10,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#notation",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Notation> getNotation();
    void setNotation(java.util.List<com.sap.sdo.api.types.schema.Notation> pNotation);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 11,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getTargetNamespace();
    void setTargetNamespace(String pTargetNamespace);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 12,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#token"
        )
    )
    String getVersion();
    void setVersion(String pVersion);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 13,
        sdoType = "http://www.w3.org/2001/XMLSchema#fullDerivationSet",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    java.util.List<String> getFinalDefault();
    void setFinalDefault(java.util.List<String> pFinalDefault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 14,
        sdoType = "http://www.w3.org/2001/XMLSchema#blockSet",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    java.util.List<String> getBlockDefault();
    void setBlockDefault(java.util.List<String> pBlockDefault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "unqualified",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 15,
        sdoType = "http://www.w3.org/2001/XMLSchema#formChoice",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getAttributeFormDefault();
    void setAttributeFormDefault(String pAttributeFormDefault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "unqualified",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 16,
        sdoType = "http://www.w3.org/2001/XMLSchema#formChoice",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getElementFormDefault();
    void setElementFormDefault(String pElementFormDefault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 17,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#ID"
        )
    )
    String getId();
    void setId(String pId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 18,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/XML/1998/namespace#lang",
            xmlElement = false
        )
    )
    String getLang();
    void setLang(String pLang);

}
