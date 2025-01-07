package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "RetrievalMethod"
        )},
    elementFormDefault = true
)
public interface RetrievalMethodType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Transforms",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.TransformsType getTransforms();
    void setTransforms(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.TransformsType pTransforms);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "URI",
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI"
    )
    String getUri();
    void setUri(String pUri);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Type",
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI"
    )
    String getType();
    void setType(String pType);

}
