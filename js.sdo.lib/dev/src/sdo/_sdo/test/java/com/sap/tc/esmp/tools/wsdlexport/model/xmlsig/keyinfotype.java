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
            name = "KeyInfo"
        )},
    elementFormDefault = true,
    sequenced = true,
    mixed = true
)
public interface KeyInfoType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "KeyName",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getKeyName();
    void setKeyName(java.util.List<String> pKeyName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "KeyValue",
        propertyIndex = 1,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.KeyValueType> getKeyValue();
    void setKeyValue(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.KeyValueType> pKeyValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "RetrievalMethod",
        propertyIndex = 2,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.RetrievalMethodType> getRetrievalMethod();
    void setRetrievalMethod(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.RetrievalMethodType> pRetrievalMethod);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "X509Data",
        propertyIndex = 3,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.X509DataType> getX509Data();
    void setX509Data(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.X509DataType> pX509Data);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "PGPData",
        propertyIndex = 4,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.PgpDataType> getPgpData();
    void setPgpData(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.PgpDataType> pPgpData);

    @com.sap.sdo.api.SdoPropertyMetaData(
        containment = true,
        sdoName = "SPKIData",
        propertyIndex = 5,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SpkiDataType> getSpkiData();
    void setSpkiData(java.util.List<com.sap.tc.esmp.tools.wsdlexport.model.xmlsig.SpkiDataType> pSpkiData);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "MgmtData",
        propertyIndex = 6,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<String> getMgmtData();
    void setMgmtData(java.util.List<String> pMgmtData);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 7,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
