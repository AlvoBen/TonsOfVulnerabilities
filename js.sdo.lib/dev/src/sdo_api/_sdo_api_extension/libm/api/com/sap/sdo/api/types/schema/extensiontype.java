package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    sdoName = "extensionType",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface ExtensionType extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.GroupRef getGroup();
    void setGroup(com.sap.sdo.api.types.schema.GroupRef pGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
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
        propertyIndex = 4,
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
        propertyIndex = 5,
        sdoName = "sequence",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#sequence",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.ExplicitGroup getExtensionTypeSequence();
    void setExtensionTypeSequence(com.sap.sdo.api.types.schema.ExplicitGroup pExtensionTypeSequence);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 6,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Attribute> getAttribute();
    void setAttribute(java.util.List<com.sap.sdo.api.types.schema.Attribute> pAttribute);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 7,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.AttributeGroupRef> getAttributeGroup();
    void setAttributeGroup(java.util.List<com.sap.sdo.api.types.schema.AttributeGroupRef> pAttributeGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 8,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#anyAttribute",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.Wildcard getAnyAttribute();
    void setAnyAttribute(com.sap.sdo.api.types.schema.Wildcard pAnyAttribute);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 9,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#QName"
        )
    )
    String getBase();
    void setBase(String pBase);

}
