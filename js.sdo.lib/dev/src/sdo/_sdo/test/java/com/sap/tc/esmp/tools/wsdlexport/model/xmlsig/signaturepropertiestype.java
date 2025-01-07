package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignatureProperties"
        )},
    elementFormDefault = true
)
public interface SignaturePropertiesType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "SignatureProperty",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignaturePropertyType> getSignatureProperty();
    void setSignatureProperty(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignaturePropertyType> pSignatureProperty);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 1,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
