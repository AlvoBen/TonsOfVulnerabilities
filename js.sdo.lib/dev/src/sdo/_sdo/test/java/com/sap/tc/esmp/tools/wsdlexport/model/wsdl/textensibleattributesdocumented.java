package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tExtensibleAttributesDocumented",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    abstractDataObject = true,
    elementFormDefault = true,
    sequenced = true // this line added because WSI allows element extension on all components
)
public interface TExtensibleAttributesDocumented extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TDocumented {

}
