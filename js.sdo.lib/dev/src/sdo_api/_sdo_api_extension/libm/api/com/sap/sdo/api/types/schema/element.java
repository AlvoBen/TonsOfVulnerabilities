package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    abstractDataObject = true,
    elementFormDefault = true,
    open = true,
    sdoName = "element",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Element extends com.sap.sdo.api.types.schema.Annotated {

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
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.LocalComplexType getComplexType();
    void setComplexType(com.sap.sdo.api.types.schema.LocalComplexType pComplexType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#unique",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Keybase> getUnique();
    void setUnique(java.util.List<com.sap.sdo.api.types.schema.Keybase> pUnique);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 5,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#key",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Keybase> getKey();
    void setKey(java.util.List<com.sap.sdo.api.types.schema.Keybase> pKey);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 6,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#keyref",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Keyref> getKeyref();
    void setKeyref(java.util.List<com.sap.sdo.api.types.schema.Keyref> pKeyref);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 7,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName"
        )
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 8,
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
        propertyIndex = 9,
        sdoName = "type",
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#QName"
        )
    )
    String getElementType();
    void setElementType(String pElementType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 10,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#QName"
        )
    )
    String getSubstitutionGroup();
    void setSubstitutionGroup(String pSubstitutionGroup);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "1",
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 11,
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
        propertyIndex = 12,
        sdoType = "http://www.w3.org/2001/XMLSchema#allNNI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    Object getMaxOccurs();
    void setMaxOccurs(Object pMaxOccurs);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 13,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getDefault();
    void setDefault(String pDefault);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 14,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getFixed();
    void setFixed(String pFixed);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "false",
        propertyIndex = 15,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    boolean isNillable();
    @Deprecated
    boolean getNillable();
    void setNillable(boolean pNillable);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "false",
        propertyIndex = 16,
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
        propertyIndex = 17,
        sdoType = "http://www.w3.org/2001/XMLSchema#derivationSet",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    java.util.List<String> getFinal();
    void setFinal(java.util.List<String> pFinal);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 18,
        sdoType = "http://www.w3.org/2001/XMLSchema#blockSet",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    java.util.List<String> getBlock();
    void setBlock(java.util.List<String> pBlock);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 19,
        sdoType = "http://www.w3.org/2001/XMLSchema#formChoice",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    String getForm();
    void setForm(String pForm);

}
