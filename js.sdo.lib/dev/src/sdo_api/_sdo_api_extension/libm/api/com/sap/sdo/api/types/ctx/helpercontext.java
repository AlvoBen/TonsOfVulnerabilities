package com.sap.sdo.api.types.ctx;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "Context.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://sap.com/sdo/api/types/ctx",
    elementFormDefault = true
)
public interface HelperContext  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.api.types.ctx.Namespace> getNamespace();
    void setNamespace(java.util.List<com.sap.sdo.api.types.ctx.Namespace> pNamespace);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.sdo.api.types.ctx.HelperContext> getHelperContext();
    void setHelperContext(java.util.List<com.sap.sdo.api.types.ctx.HelperContext> pHelperContext);

    @com.sap.sdo.api.SdoPropertyMetaData(
        defaultValue = "com.sap.sdo.api.types.ctx.default",
        propertyIndex = 2
    )
    String getId();
    void setId(String pId);

}
