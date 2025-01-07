package com.sap.sdo.testcase.typefac;

@com.sap.sdo.api.SdoTypeMetaData(
    uri = "com.sap.sdo.testcase3"
)
public interface FacetTestType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 0,
        sdoType = "com.sap.sdo.testcase3#DecimalInclusiveFacetType"
    )
    java.math.BigDecimal getDecimalInclusiveProp();
    void setDecimalInclusiveProp(java.math.BigDecimal pDecimalInclusiveProp);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 1,
        sdoType = "com.sap.sdo.testcase3#DecimalExclusiveFacetType"
    )
    java.math.BigDecimal getDecimalExclusiveProp();
    void setDecimalExclusiveProp(java.math.BigDecimal pDecimalExclusiveProp);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 2,
        sdoType = "com.sap.sdo.testcase3#StringFacetType"
    )
    String getStringProp();
    void setStringProp(String pStringProp);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = true
        ),
        propertyIndex = 3,
        sdoType = "com.sap.sdo.testcase3#LengthFacetType"
    )
    String getLengthProp();
    void setLengthProp(String pLengthProp);

}
