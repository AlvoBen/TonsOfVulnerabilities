package com.sap.sdo.api.types.ctx;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "Context.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/sdo/api/types/ctx",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = false,
            name = "facets"
        )},
    elementFormDefault = true
)
public interface Facets  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getEnumeration();
    void setEnumeration(java.util.List<String> pEnumeration);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getPattern();
    void setPattern(java.util.List<String> pPattern);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2
    )
    Integer getLength();
    void setLength(Integer pLength);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 3
    )
    Integer getMaxLength();
    void setMaxLength(Integer pMaxLength);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 4
    )
    Integer getMinLength();
    void setMinLength(Integer pMinLength);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 5,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getMinInclusive();
    void setMinInclusive(String pMinInclusive);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 6,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getMaxInclusive();
    void setMaxInclusive(String pMaxInclusive);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 7,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getMinExclusive();
    void setMinExclusive(String pMinExclusive);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 8,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getMaxExclusive();
    void setMaxExclusive(String pMaxExclusive);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 9
    )
    Integer getTotalDigits();
    void setTotalDigits(Integer pTotalDigits);

    Integer getFractionDigits();
    void setFractionDigits(Integer pFractionDigits);

}
