package com.sap.sdo.api.types.ctx;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "Context.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://sap.com/sdo/api/types/ctx",
    elementFormDefault = true,
    sequenced = true
)
public interface Namespace  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<commonj.sdo.Property> getProperty();
    void setProperty(java.util.List<commonj.sdo.Property> pProperty);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "type",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<commonj.sdo.Type> getNamespaceType();
    void setNamespaceType(java.util.List<commonj.sdo.Type> pNamespaceType);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getUri();
    void setUri(String pUri);

}
