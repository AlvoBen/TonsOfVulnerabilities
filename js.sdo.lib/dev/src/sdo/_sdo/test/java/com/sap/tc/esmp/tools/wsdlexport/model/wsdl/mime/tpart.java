package com.sap.tc.esmp.tools.wsdlexport.model.wsdl.mime;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "mime.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tPart",
    uri = "http://schemas.xmlsoap.org/wsdl/mime/",
    sequenced = true
)
public interface TPart  {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 0
    )
    String getName();
    void setName(String pName);

}
