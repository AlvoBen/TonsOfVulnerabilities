package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    abstractDataObject = true,
    elementFormDefault = true,
    open = true,
    sdoName = "group",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Group extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.LocalElement> getElement();
    void setElement(java.util.List<com.sap.sdo.api.types.schema.LocalElement> pElement);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.GroupRef> getGroup();
    void setGroup(java.util.List<com.sap.sdo.api.types.schema.GroupRef> pGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#all",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.All> getAll();
    void setAll(java.util.List<com.sap.sdo.api.types.schema.All> pAll);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 5,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#choice",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.ExplicitGroup> getChoice();
    void setChoice(java.util.List<com.sap.sdo.api.types.schema.ExplicitGroup> pChoice);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 6,
        sdoName = "sequence",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#sequence",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.ExplicitGroup> getGroupSequence();
    void setGroupSequence(java.util.List<com.sap.sdo.api.types.schema.ExplicitGroup> pGroupSequence);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 7,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#any",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Any> getAny();
    void setAny(java.util.List<com.sap.sdo.api.types.schema.Any> pAny);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 8,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName"
        )
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 9,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#QName"
        )
    )
    String getRef();
    void setRef(String pRef);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "1",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 10,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#nonNegativeInteger"
        )
    )
    java.math.BigInteger getMinOccurs();
    void setMinOccurs(java.math.BigInteger pMinOccurs);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "1",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 11,
        sdoType = "http://www.w3.org/2001/XMLSchema#allNNI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    Object getMaxOccurs();
    void setMaxOccurs(Object pMaxOccurs);

}
