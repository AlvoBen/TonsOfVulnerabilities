package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    abstractDataObject = true,
    elementFormDefault = true,
    open = true,
    sdoName = "simpleType",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface SimpleType extends com.sap.sdo.api.types.schema.Annotated {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 2,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#restriction",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.Restriction getRestriction();
    void setRestriction(com.sap.sdo.api.types.schema.Restriction pRestriction);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 3,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#list",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.List getList();
    void setList(com.sap.sdo.api.types.schema.List pList);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 4,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#union",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.Union getUnion();
    void setUnion(com.sap.sdo.api.types.schema.Union pUnion);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 5,
        sdoType = "http://www.w3.org/2001/XMLSchema#simpleDerivationSet",
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        )
    )
    java.util.List<String> getFinal();
    void setFinal(java.util.List<String> pFinal);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 6,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#NCName"
        )
    )
    String getName();
    void setName(String pName);

}
