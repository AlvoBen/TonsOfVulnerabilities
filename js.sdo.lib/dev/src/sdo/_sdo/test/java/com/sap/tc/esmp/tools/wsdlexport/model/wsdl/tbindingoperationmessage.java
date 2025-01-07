package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tBindingOperationMessage",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    elementFormDefault = true,
    sequenced = true
)
public interface TBindingOperationMessage extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibleDocumented {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1
    )
    String getName();
    void setName(String pName);

}
