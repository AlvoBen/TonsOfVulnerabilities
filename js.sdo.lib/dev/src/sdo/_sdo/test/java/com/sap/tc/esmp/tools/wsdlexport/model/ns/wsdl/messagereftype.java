package com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl20.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://www.w3.org/ns/wsdl",
    elementFormDefault = true
)
public interface MessageRefType extends com.sap.tc.esmp.tools.wsdlexport.model.ns.wsdl.ExtensibleDocumentedType {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1
    )
    String getMessageLabel();
    void setMessageLabel(String pMessageLabel);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2,
        sdoType = "http://www.w3.org/ns/wsdl#ElementReferenceType"
    )
    String getElement();
    void setElement(String pElement);

}
