package com.sap.sdo.api.types.schema;

@com.sap.sdo.api.SdoTypeMetaData(
    elementFormDefault = true,
    open = true,
    sdoName = "annotated",
    uri = "http://www.w3.org/2001/XMLSchema"
)
public interface Annotated extends com.sap.sdo.api.types.schema.OpenAttrs {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 0,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            ref = "http://www.w3.org/2001/XMLSchema#annotation",
            xmlElement = true
        )
    )
    com.sap.sdo.api.types.schema.Annotation getAnnotation();
    void setAnnotation(com.sap.sdo.api.types.schema.Annotation pAnnotation);

    @com.sap.sdo.api.SdoPropertyMetaData(
        nullable = com.sap.sdo.api.Bool.FALSE,
        propertyIndex = 1,
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false,
            xsdType = "http://www.w3.org/2001/XMLSchema#ID"
        )
    )
    String getId();
    void setId(String pId);

}
