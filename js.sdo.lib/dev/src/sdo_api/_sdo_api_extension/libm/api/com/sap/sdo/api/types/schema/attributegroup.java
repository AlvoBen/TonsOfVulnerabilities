package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    abstractDataObject = true,
    elementFormDefault = true,
    open = true,
    sdoName = "attributeGroup",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface AttributeGroup extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Attribute> getAttribute();
    void setAttribute(java.util.List<com.sap.sdo.api.types.schema.Attribute> pAttribute);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.AttributeGroupRef> getAttributeGroup();
    void setAttributeGroup(java.util.List<com.sap.sdo.api.types.schema.AttributeGroupRef> pAttributeGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#anyAttribute",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.Wildcard getAnyAttribute();
    void setAnyAttribute(com.sap.sdo.api.types.schema.Wildcard pAnyAttribute);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 5,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName"
        )
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 6,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#QName"
        )
    )
    String getRef();
    void setRef(String pRef);

}
