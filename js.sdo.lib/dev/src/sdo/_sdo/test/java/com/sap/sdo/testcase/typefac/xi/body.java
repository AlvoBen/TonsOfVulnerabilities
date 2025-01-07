package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://schemas.xmlsoap.org/soap/envelope/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Body"
        )},
    sequenced = true
)
public interface Body  {

    @com.sap.sdo.api.SdoPropertyMetaData( 
        xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData( 
            xmlElement = true, 
            ref = "http://sap.com/xi/XI/Message/30#Manifest" 
        ), 
        containment = true, 
        sdoName = "Manifest", 
        propertyIndex = 0 
    ) 
    com.sap.sdo.testcase.typefac.xi.EManifest getManifest(); 
    void setManifest(com.sap.sdo.testcase.typefac.xi.EManifest pManifest); 
    
}
