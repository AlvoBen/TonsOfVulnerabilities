package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Reference"
        )},
    elementFormDefault = true
)
public interface ReferenceType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Transforms",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.TransformsType getTransforms();
    void setTransforms(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.TransformsType pTransforms);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "DigestMethod",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.DigestMethodType getDigestMethod();
    void setDigestMethod(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.DigestMethodType pDigestMethod);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "DigestValue",
        propertyIndex = 2,
        sdoType = "http://www.w3.org/2000/09/xmldsig##DigestValueType",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    byte[] getDigestValue();
    void setDigestValue(byte[] pDigestValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 3,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "URI",
        propertyIndex = 4,
        sdoType = "commonj.sdo#URI"
    )
    String getUri();
    void setUri(String pUri);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Type",
        propertyIndex = 5,
        sdoType = "commonj.sdo#URI"
    )
    String getType();
    void setType(String pType);

}
