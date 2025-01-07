package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "KeyValue"
        )},
    elementFormDefault = true,
    sequenced = true,
    mixed = true
)
public interface KeyValueType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "DSAKeyValue",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.DsaKeyValueType getDsaKeyValue();
    void setDsaKeyValue(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.DsaKeyValueType pDsaKeyValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "RSAKeyValue",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.RsaKeyValueType getRsaKeyValue();
    void setRsaKeyValue(com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.RsaKeyValueType pRsaKeyValue);

}
