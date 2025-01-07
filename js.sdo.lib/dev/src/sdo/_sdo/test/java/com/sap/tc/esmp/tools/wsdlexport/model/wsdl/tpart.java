package com.sap.tc.esmp.tools.wsdlexport.model.wsdl;

@com.sap.sdo.api.SchemaInfo(
    schemaLocation = "wsdl.xsd"
)
@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "tPart",
    uri = "http://schemas.xmlsoap.org/wsdl/",
    elementFormDefault = true
)
public interface TPart extends com.sap.tc.esmp.tools.wsdlexport.model.wsdl.TExtensibleAttributesDocumented {

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 1
    )
    String getName();
    void setName(String pName);

    @com.sap.sdo.api.SdoPropertyMetaData(
        propertyIndex = 2,
        sdoType = "commonj.sdo#URI"
    )
    String getElement();
    void setElement(String pElement);

    @com.sap.sdo.api.SdoPropertyMetaData(
        sdoName = "type",
        propertyIndex = 3,
        sdoType = "commonj.sdo#URI"
    )
    String getTPartType();
    void setTPartType(String pTPartType);

}
