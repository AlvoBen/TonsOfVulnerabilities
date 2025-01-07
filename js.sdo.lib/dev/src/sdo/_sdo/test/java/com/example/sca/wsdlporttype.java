package com.example.sca;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    sdoName = "WSDLPortType",
    uri = "http://www.osoa.org/xmlns/sca/0.9",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "interface.wsdl"
        )},
    sequenced = true
)
public interface WsdlPortType extends com.example.sca.Interface {

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 0,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getInterface();
    void setInterface(String pInterface);

    @com.sap.sdo.api.SdoPropertyMetaData(
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
            xmlElement = false
        ),
        propertyIndex = 1,
        sdoType = "commonj.sdo#URI",
        nullable = com.sap.sdo.api.Bool.FALSE
    )
    String getCallbackInterface();
    void setCallbackInterface(String pCallbackInterface);

}
