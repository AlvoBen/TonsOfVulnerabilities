package com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl20.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/ns/wsdl",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "types"
        )},
    elementFormDefault = true,
    sequenced = true
)
public interface TypesType extends com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ExtensibleDocumentedType {

}
