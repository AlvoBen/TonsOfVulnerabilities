package com.sap.sdo.testcase.typefac.xi;

@com.sap.sdo.api.SdoTypeMetaData(
    open = true,
    uri = "http://schemas.xmlsoap.org/soap/envelope/",
    openContentProperties = {
        @com.sap.sdo.api.OpenContentProperty(
            many = true,
            name = "Header"
        )},
    sequenced = true
)
public interface Header  {

    @com.sap.sdo.api.SdoPropertyMetaData(
            xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData(
                ref = "http://sap.com/xi/XI/Message/30#Main",
                xmlElement = true
            ),
            containment = true,
            sdoName = "Main",
            propertyIndex = 0
        )
	com.sap.sdo.testcase.typefac.xi.EMain getMain();
	void setMain(com.sap.sdo.testcase.typefac.xi.EMain pMain);

	   @com.sap.sdo.api.SdoPropertyMetaData( 
           xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData( 
               xmlElement = true, 
               ref = "http://sap.com/xi/XI/Message/30#ReliableMessaging" 
           ), 
           containment = true, 
           sdoName = "ReliableMessaging", 
           propertyIndex = 1 
       ) 
       com.sap.sdo.testcase.typefac.xi.EReliableMessaging getReliableMessaging(); 
       void setReliableMessaging(com.sap.sdo.testcase.typefac.xi.EReliableMessaging pReliableMessaging);

       
   @com.sap.sdo.api.SdoPropertyMetaData( 
           xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData( 
               xmlElement = true, 
               ref = "http://sap.com/xi/XI/Message/30#HopList" 
           ), 
           containment = true, 
           sdoName = "HopList", 
           propertyIndex = 2 
       ) 
       com.sap.sdo.testcase.typefac.xi.EHopList getHopList(); 
       void setHopList(com.sap.sdo.testcase.typefac.xi.EHopList pHopList); 
       
   @com.sap.sdo.api.SdoPropertyMetaData( 
           xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData( 
               xmlElement = true, 
               ref = "http://sap.com/xi/XI/Message/30#Ack" 
           ), 
           containment = true, 
           sdoName = "Ack", 
           propertyIndex = 3 
       ) 
       com.sap.sdo.testcase.typefac.xi.EAck getAck(); 
       void setAck(com.sap.sdo.testcase.typefac.xi.EAck pAck); 
       
   @com.sap.sdo.api.SdoPropertyMetaData( 
           xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData( 
               xmlElement = true, 
               ref = "http://sap.com/xi/XI/Message/30#Error" 
           ), 
           containment = true, 
           sdoName = "Error", 
           propertyIndex = 4 
       ) 
       com.sap.sdo.testcase.typefac.xi.EError getError(); 
       void setError(com.sap.sdo.testcase.typefac.xi.EError pError); 
       
   @com.sap.sdo.api.SdoPropertyMetaData( 
           xmlInfo =         @com.sap.sdo.api.XmlPropertyMetaData( 
               xmlElement = true, 
               ref = "http://sap.com/xi/XI/Message/30#Diagnostic" 
           ), 
           containment = true, 
           sdoName = "Diagnostic", 
           propertyIndex = 5 
       ) 
       com.sap.sdo.testcase.typefac.xi.EDiagnostic getDiagnostic(); 
       void setDiagnostic(com.sap.sdo.testcase.typefac.xi.EDiagnostic pDiagnostic); 
}
