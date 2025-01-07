package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "SPKIDataType",
    uri = "http://www.w3.org/2000/09/xmldsig#",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "SPKIData"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface SpkiDataType  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xsdType = "http://www.w3.org/2001/XMLSchema#base64Binary",
            xmlElement = true
        ),
        sdoName = "SPKISexp",
        propertyIndex = 0,
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    java.util.List<byte[]> getSpkiSexp();
    void setSpkiSexp(java.util.List<byte[]> pSpkiSexp);

}
