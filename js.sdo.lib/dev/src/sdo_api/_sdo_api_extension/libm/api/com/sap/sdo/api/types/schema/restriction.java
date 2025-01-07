package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "restriction"
        )},
    sdoName = "restriction",
    sequenced = true,
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Restriction extends com.sap.sdo.api.types.schema.Annotated {

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
            ref = "http://www.w3.org/2001/XMLSchema#minExclusive",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Facet> getMinExclusive();
    void setMinExclusive(java.util.List<com.sap.sdo.api.types.schema.Facet> pMinExclusive);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#minInclusive",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Facet> getMinInclusive();
    void setMinInclusive(java.util.List<com.sap.sdo.api.types.schema.Facet> pMinInclusive);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 5,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#maxExclusive",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Facet> getMaxExclusive();
    void setMaxExclusive(java.util.List<com.sap.sdo.api.types.schema.Facet> pMaxExclusive);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 6,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#maxInclusive",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Facet> getMaxInclusive();
    void setMaxInclusive(java.util.List<com.sap.sdo.api.types.schema.Facet> pMaxInclusive);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 7,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#totalDigits",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.TotalDigits> getTotalDigits();
    void setTotalDigits(java.util.List<com.sap.sdo.api.types.schema.TotalDigits> pTotalDigits);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 8,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#fractionDigits",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.NumFacet> getFractionDigits();
    void setFractionDigits(java.util.List<com.sap.sdo.api.types.schema.NumFacet> pFractionDigits);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 9,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#length",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.NumFacet> getLength();
    void setLength(java.util.List<com.sap.sdo.api.types.schema.NumFacet> pLength);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 10,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#minLength",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.NumFacet> getMinLength();
    void setMinLength(java.util.List<com.sap.sdo.api.types.schema.NumFacet> pMinLength);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 11,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#maxLength",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.NumFacet> getMaxLength();
    void setMaxLength(java.util.List<com.sap.sdo.api.types.schema.NumFacet> pMaxLength);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 12,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#enumeration",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.NoFixedFacet> getEnumeration();
    void setEnumeration(java.util.List<com.sap.sdo.api.types.schema.NoFixedFacet> pEnumeration);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 13,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#whiteSpace",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.WhiteSpace> getWhiteSpace();
    void setWhiteSpace(java.util.List<com.sap.sdo.api.types.schema.WhiteSpace> pWhiteSpace);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 14,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#pattern",
            xmlElement = true
        )
    )
    java.util.List<com.sap.sdo.api.types.schema.Pattern> getPattern();
    void setPattern(java.util.List<com.sap.sdo.api.types.schema.Pattern> pPattern);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 15,
        sdoType = "commonj.sdo#URI",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#QName"
        )
    )
    String getBase();
    void setBase(String pBase);

}
