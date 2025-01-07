package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    abstractDataObject = true,
    elementFormDefault = true,
    open = true,
    sdoName = "complexType",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface ComplexType extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#simpleContent",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.SimpleContent getSimpleContent();
    void setSimpleContent(com.sap.sdo.api.types.schema.SimpleContent pSimpleContent);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#complexContent",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.ComplexContent getComplexContent();
    void setComplexContent(com.sap.sdo.api.types.schema.ComplexContent pComplexContent);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.GroupRef getGroup();
    void setGroup(com.sap.sdo.api.types.schema.GroupRef pGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 5,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#all",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.All getAll();
    void setAll(com.sap.sdo.api.types.schema.All pAll);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 6,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#choice",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.ExplicitGroup getChoice();
    void setChoice(com.sap.sdo.api.types.schema.ExplicitGroup pChoice);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 7,
        sdoName = "sequence",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#sequence",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.ExplicitGroup getComplexTypeSequence();
    void setComplexTypeSequence(com.sap.sdo.api.types.schema.ExplicitGroup pComplexTypeSequence);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 8,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Attribute> getAttribute();
    void setAttribute(java.util.List<com.sap.sdo.api.types.schema.Attribute> pAttribute);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 9,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.AttributeGroupRef> getAttributeGroup();
    void setAttributeGroup(java.util.List<com.sap.sdo.api.types.schema.AttributeGroupRef> pAttributeGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 10,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#anyAttribute",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.Wildcard getAnyAttribute();
    void setAnyAttribute(com.sap.sdo.api.types.schema.Wildcard pAnyAttribute);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 11,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName"
        )
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "false",
        propertyIndex = 12,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    boolean isMixed();
    @Deprecated
    boolean getMixed();
    void setMixed(boolean pMixed);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "false",
        propertyIndex = 13,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    boolean isAbstract();
    @Deprecated
    boolean getAbstract();
    void setAbstract(boolean pAbstract);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 14,
        sdoType = "http://www.w3.org/2001/XMLSchema#derivationSet",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    java.util.List<String> getFinal();
    void setFinal(java.util.List<String> pFinal);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 15,
        sdoType = "http://www.w3.org/2001/XMLSchema#derivationSet",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    java.util.List<String> getBlock();
    void setBlock(java.util.List<String> pBlock);

}
