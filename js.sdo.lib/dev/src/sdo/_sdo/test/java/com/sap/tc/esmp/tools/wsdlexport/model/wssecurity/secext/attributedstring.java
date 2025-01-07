package com.sap.tc.esmp.tools.wsdlexport.model.wssecurity.secext;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "oasis-200401-wss-wssecurity-secext-1.0.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
    elementFormDefault = true
)
public interface AttributedString  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0
    )
    String getValue();
    void setValue(String pValue);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "Id",
        propertyIndex = 1,
        sdoType = "commonj.sdo#ID"
    )
    String getId();
    void setId(String pId);

}
