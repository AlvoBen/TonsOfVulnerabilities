package com.sap.tc.esmp.tools.wsdlexport.model.xmlsig;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "xmldsig-core-schema.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "SPKIDataType",
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SPKIData"
        )},
    elementFormDefault = true
)
public interface SpkiDataType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "SPKISexp",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<byte[]> getSpkiSexp();
    void setSpkiSexp(java.util.List<byte[]> pSpkiSexp);

}
