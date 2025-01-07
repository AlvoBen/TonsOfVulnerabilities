package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SignedInfo"
        )},
    elementFormDefault = true
)
public interface SignedInfoType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "CanonicalizationMethod",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.CanonicalizationMethodType getCanonicalizationMethod();
    void setCanonicalizationMethod(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.CanonicalizationMethodType pCanonicalizationMethod);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "SignatureMethod",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignatureMethodType getSignatureMethod();
    void setSignatureMethod(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SignatureMethodType pSignatureMethod);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "Reference",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.ReferenceType> getReference();
    void setReference(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.ReferenceType> pReference);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 3,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
