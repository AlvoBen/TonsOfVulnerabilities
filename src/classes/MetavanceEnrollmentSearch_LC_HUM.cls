/*******************************************************************************************************************************
Apex Class Name : MetavanceEnrollmentSearch_LC_HUM
Version         : 1.0
Created On      : 10/13/2020
Function        : 
Test Class      : MetavanceEnrollmentSearch_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                  	10/13/2020               	Original Version
*    2.0           Vardhman Jain					                04/04/2023                  US:4404840 Enchanced CIM changes
*******************************************************************************************************************************************************************/

public class MetavanceEnrollmentSearch_LC_HUM 
 {
      
    public static Map<String,Object> mapCallback= new Map<String,Object>();
    public static Map<String,Object> contstate;
    
    /*
    * Method Name   :    searchEESService
    * Description   :    Method to search Member in EES System                        
    * Return Type   :    Continuation
    * Parameters    :    NA
    */
    @AuraEnabled(continuation=true)
    public static Continuation searchEESService(String metvanceSearchModal)
    {	
        SearchSSNEnrollment objSSN=(SearchSSNEnrollment)JSON.deserialize(metvanceSearchModal, SearchSSNEnrollment.class);
        HUM_Webservice_Callout__c objWebserviceCalloutEES = HUM_Webservice_Callout__c.getInstance('EESLegacySearchService');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutEES.Timeout__c)/1000;
        AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture oEESResponse=NULL;
        Continuation oConEES;
        EESLegacyMemberSearch_LCH_HUM oEesLegacy = new EESLegacyMemberSearch_LCH_HUM();
        if(objWebserviceCalloutEES != null)
        {	
            if(objWebserviceCalloutEES.Active__c)
            {
                oConEES = new Continuation(TIMEOUT_INT_SECS);
                oConEES.continuationMethod = 'EESServiceProcessResponse';
                oEESResponse = oEesLegacy.submitAsync(oConEES, objSSN.sSSN);
                mapCallback.put('LegacyMemberSearchServiceClass',oEESResponse);
                mapCallback.put('SSN',objSSN.sSSn);
                contstate = mapCallBack;
                oConEES.state=contstate;
            }
        }
        return oConEES;
    }
    
    /*Inner class which is used to store the input search data that comes from UI */
   
    public virtual class SearchSSNEnrollment{
        public string sSSN;
        public string sEndDate;
        public string sEffectiveDate;
		public string fName;
        public string lName;
        public string DOB;
        public string sState;
        public boolean isDemographic;
            
        public SearchSSNEnrollment()
        {}
    }  
     
    /*
    * Method Name   :    EESServiceProcessResponse
    * Description   :    Callback Method that will be invoked for EES Service                      
    * Return Type   :    Object
    * Parameters    :    NA
    */    
    @AuraEnabled
    public static Object EESServiceProcessResponse(Object state)
    {	
        try
        {	
            Map<String,Object> mResponse=(Map<String,Object>)state;
            String SSN= String.valueOf(mResponse.get('SSN'));
            AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture oEESResponse=(AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture)mResponse.get('LegacyMemberSearchServiceClass');
            List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService= new List<MemberSearchEnrollmentHelper_LC_HUM>();
            EESLegacyMemberSearch_LCH_HUM oEesLegacy = new EESLegacyMemberSearch_LCH_HUM();
            lstLegacyService.addAll(oEesLegacy.parseEEESResponse(oEESResponse.getValue(), SSN));
            
            return lstLegacyService;
        }
        catch(CalloutException e)
        {
            throw new HUMCustomException('CalloutException '+e.getMessage());
        }
        catch(Exception ex)
        {
            throw new HUMCustomException(ex.getMessage());	
        }
    } 
	
	    /*
    * Method Name   :    searchEESXmlService
    * Description   :    Method to search Member in EES System                        
    * Return Type   :    Continuation
    * Parameters    :    NA
    */
    @AuraEnabled(continuation=true cacheable=true)
    public static Continuation searchEESXmlService(String metvanceSearchModal)
    {	
        system.debug('in searchEESXmlService new ssn'+metvanceSearchModal);
        SearchSSNEnrollment objSSN=(SearchSSNEnrollment)JSON.deserialize(metvanceSearchModal, SearchSSNEnrollment.class);
        HUM_Webservice_Callout__c objWebserviceCalloutEES = HUM_Webservice_Callout__c.getInstance('EESLegacySearchService');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutEES.Timeout__c)/1000;
        AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture oEESResponse=NULL;
        Continuation oConEES;
        EESLegacyMemberSearch_LCH_HUM oEesLegacy = new EESLegacyMemberSearch_LCH_HUM();
        if(objWebserviceCalloutEES != null && objWebserviceCalloutEES.Active__c)
        {	
                oConEES = new Continuation(TIMEOUT_INT_SECS);
                oConEES.continuationMethod = objSSN.isDemographic?'EESServiceProcessXmlDemographicResponse':'EESServiceProcessXmlResponse';
                oEESResponse = oEesLegacy.submitAsync(oConEES, objSSN.sSSN);
                mapCallback.put('LegacyMemberSearchServiceClass',oEESResponse);
                mapCallback.put('SSN',objSSN.sSSN);
                contstate = mapCallBack;
                oConEES.state=contstate; 
        }
        return oConEES;
    }

    @AuraEnabled(cacheable=true)
    public static Object EESServiceProcessXmlResponse(Object state)
    {	
        try
        {	
            Map<String,Object> mResponse=(Map<String,Object>)state;
            String SSN= String.valueOf(mResponse.get('SSN'));
            AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture oEESResponse=(AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture)mResponse.get('LegacyMemberSearchServiceClass');
            List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService= new List<MemberSearchEnrollmentHelper_LC_HUM>();
            EESLegacyMemberSearch_LCH_HUM oEesLegacy = new EESLegacyMemberSearch_LCH_HUM();
            lstLegacyService.addAll(oEesLegacy.parseEEESXMLResponse(oEESResponse.getValue(), SSN));
            return lstLegacyService;
        }
        catch(CalloutException e)
        {
            throw new HUMCustomException('CalloutException '+e.getMessage());
        }
        catch(Exception ex)
        {
            throw new HUMCustomException(ex.getMessage());	
        }
    }
   
    @AuraEnabled(continuation=true cacheable=true)
    public static Continuation searchEESService_Other(String metvanceSearchModal)
    {
            
            SearchSSNEnrollment objSSN=(SearchSSNEnrollment)JSON.deserialize(metvanceSearchModal, SearchSSNEnrollment.class);
            HUM_Webservice_Callout__c objWebserviceCalloutEES = HUM_Webservice_Callout__c.getInstance('EESEnhancedService');
            Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutEES.Timeout__c)/1000;
            Continuation oConEES = new Continuation(TIMEOUT_INT_SECS);
            oConEES.ContinuationMethod='EESServiceProcessResponseOther';
            Http http = new Http();
            HttpRequest request = new HttpRequest();
            HttpResponse response;
            request.setEndpoint(objWebserviceCalloutEES.End_Point_URL__c);
            request.setMethod(objWebserviceCalloutEES.Request_Type__c);
            request.setHeader('Content-Type', objWebserviceCalloutEES.Content_Type__c);
            if(!Test.isRunningTest())
            request.setClientCertificateName(objWebserviceCalloutEES.Certificate_Name__c);
            request.setBody(HUMMemberSearchEnrollment_H_EESService.ESSServiceRequestBody(objSSN.fName.toUpperCase(), objSSN.lName.toUpperCase(), objSSN.DOB, objSSN.sState));
            oConEES.state = oConEES.addHttpRequest(request);
            return oConEES;    
    }
   	/*
   * Method Name   :    EESServiceProcessResponse_Other
   * Description   :    Process successful response into search results                     
   * Return Type   :    
   * Parameters    :    NA
   */
  @AuraEnabled(cacheable=true)
   public static string EESServiceProcessResponseOther(list<string> labels, object state)
   {
       String metvanceSearchModal='';
       try 
       {
           HttpResponse response = Continuation.getResponse((string)state);
           string lstResults = response.getBody();
           system.debug('EESServiceProcessResponseOther body :'+lstResults);
            Map<string,Object> responseMap = (Map<string,Object>)JSON.deserializeUntyped(lstResults);
            Map<string,Object> getPlatformMembersResponseMap = ( Map<string,Object>)responseMap?.get('GetPlatformMembersResponse');
            IF(getPlatformMembersResponseMap != null)
            {
                if(getPlatformMembersResponseMap?.containsKey('platformMembers')){
                    system.debug('getPlatformMembersResponseMap'+getPlatformMembersResponseMap.get('platformMembers'));
                    FOR(object platformMember : (List<Object>)getPlatformMembersResponseMap.get('platformMembers'))
                    {
                    Map<string, object> mso = (map<string, object>) platformMember;
                    system.debug('mso--->'+mso);
                    if(platformMember != null)
                    {
                        if((string)mso.get('ssn') != null && (string)mso.get('ssn') != '0')
                        {
                            metvanceSearchModal = '{"sSSN":"'+(string)mso.get('ssn')+'"}';
                            system.debug('metvanceSearchModal'+metvanceSearchModal);
                        }
                    }
                    }
                    
                }               
            } 
       }
       catch(CalloutException e)
       {
           throw new HUMCustomException('CalloutException '+e.getMessage());
       }
      catch(Exception ex)
       {
          throw new HUMCustomException(ex.getMessage());	
       }
      return metvanceSearchModal;
    }

    /*
   * Method Name   :    EESServiceProcessXmlDemographicResponse
   * Description   :    Callback Method that will be invoked for EES Service                      
   * Return Type   :    Object
   * Parameters    :    NA
   */
  @AuraEnabled(cacheable=true)
  public static Object EESServiceProcessXmlDemographicResponse(object state)
  {
      try
      { 
        Map<String,Object> mResponse=(Map<String,Object>)state;
        String SSN= String.valueOf(mResponse.get('SSN'));
        AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture oEESResponse=(AsyncHUMLegacyMemberSearchService.GetPlatformMembersResponse_elementFuture)mResponse.get('LegacyMemberSearchServiceClass');
        EESLegacyMemberSearch_LCH_HUM oEesLegacy = new EESLegacyMemberSearch_LCH_HUM();
        List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService= new List<MemberSearchEnrollmentHelper_LC_HUM>();
          if(!Test.isRunningTest())   lstLegacyService.addAll(oEesLegacy.parseEEESResponseNew(oEESResponse.getValue(), SSN));

          return lstLegacyService;
      }
      catch(CalloutException e)
       {
           throw new HUMCustomException('CalloutException '+e.getMessage());
       }
      catch(Exception ex)
       {
           throw new HUMCustomException(ex.getMessage());	
       }
      
  }
}