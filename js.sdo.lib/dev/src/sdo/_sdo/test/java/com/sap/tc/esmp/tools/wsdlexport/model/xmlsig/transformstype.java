package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Transforms"
        )},
    elementFormDefault = true
)
public interface TransformsType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Transform",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.TransformType> getTransform();
    void setTransform(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.TransformType> pTransform);

}
