package com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl20.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/ns/wsdl",
    elementFormDefault = true
)
public interface BindingFaultType extends com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ExtensibleDocumentedType {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI"
    )
    String getRef();
    void setRef(String pRef);

}
