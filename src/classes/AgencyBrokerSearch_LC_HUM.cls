/*******************************************************************************************************************************
Apex Class Name : AgencyBrokerSearch_LC_HUM
Version         : 1.0
Created On      : 09/28/2020
Function        : Class contains methods for search on Agents/Brokers based on the parameters passed from the LWC
                  and passes the search results back to the LWC.
Test Class      : AgencyBrokerSearch_LT_HUM

Modification Log: 
 * Version Number             Developer Name              Code Review                Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 *     1.0                    Gaurav Dharra                                          09/28/2020                 Cleanup and refactoring on HUMAgencySearchControllerV2 class
 *     2.0                    Ashish Kumar                                           11/24/2020                 Upsert logic And unknown search logic
 *     3.0                    Kajal Namdev                                           05/07/2021                 Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class
 *     4.0                    Sai Kumar Boga                                         07/13/2021                 Changes for Unknown Agent/Broker Form
 *     5.0                    Nilanjana Sanyal         	           					 10/26/2022                 US-3813080: HUMExceptionHelper remediation for lightning consumer Management classes  
******************************************************************************************************************************************************************************/

public with sharing class AgencyBrokerSearch_LC_HUM {
    
    @testvisible private static String sClassName ='AgencyBrokerSearch_LC_HUM';
    @testvisible private static Boolean bAgencyTimedout = false;
    @testvisible private static String sMethodName = ''; 
    @testvisible private static Continuation_DTO_HUM conStateWrap;
    @testvisible private static AsyncHUMAgencySearchService.SearchProducerResponse_elementFuture resElementFuture = NULL;
    @testvisible private static AsyncHUMAgencySearchService.AsyncBasicHttpBinding_IProducer asyncProd = NULL;
    @testvisible private static Boolean bIsNameSearch ;
    @testVisible public static Boolean bIsUnknownProvider{get;set;} 
    @testVisible private static transient boolean bIsAgencyError{get;set;}
    @testVisible public static transient boolean bIsUnknownAgencySearch{get;set;}
    @testvisible public static AgencyBrokerSearch_DTO_HUM oSearchWrapper { get; set; }
    @testvisible public static HUM_Webservice_Callout__c objWebserviceCallout = HUM_Webservice_Callout__c.getInstance('AgencySearchService');
    @testvisible private static List<Account> lstSObjects = new List<Account>();
      
    /* 
     * Method Name : HUMAgencySearchController   
     * Description : Constructor for the class 
     * Return type : NA
     * Paramater   : NA
     */
    public AgencyBrokerSearch_LC_HUM() 
    {
        bIsAgencyError = false;  
        bIsUnknownAgencySearch = false;
        oSearchWrapper = new AgencyBrokerSearch_DTO_HUM();
    }
       
    public static List<Error_Log__c> lstLogErrors
    {
        get 
        {
            if(lstLogErrors == null)
                return new List<Error_Log__c>();
            else
                return lstLogErrors;
        }
        set;
    }
   
    
    public class Continuation_DTO_HUM {

        public AsyncHUMAgencySearchService.SearchProducerResponse_elementFuture request { get; set; }
        public AgencyBrokerSearch_DTO_HUM oSearchWrapper { get; set; }    
        public Continuation_DTO_HUM(AsyncHUMAgencySearchService.SearchProducerResponse_elementFuture req, AgencyBrokerSearch_DTO_HUM var1 ) {
            request = req;
            oSearchWrapper = var1;
        }
    }
    
    /*  
     * Method name : searchAgencyBroker
     * Description : This method will be called on clicking the search button on Agency search page.
     * Return Type : list of Account
     * Parameter   : NA
     */ 
    @AuraEnabled( continuation=true cacheable=true)
    public static object searchAgencyBroker(AgencyBrokerSearch_DTO_HUM agencySearchWrapper)
    {	
        bIsUnknownProvider =false; 
        bIsNameSearch =false;
        oSearchWrapper = agencySearchWrapper; 
        bIsUnknownAgencySearch = oSearchWrapper.isUnknownAgencySearch;
        sMethodName = 'searchAgencyBroker';
        oSearchWrapper.sAgencyName = oSearchWrapper.sFirstName;
        oSearchWrapper.sFirstName = (oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue1 
                                     && String.isNotBlank(oSearchWrapper.sAgencyName)) ? oSearchWrapper.sAgencyName:'';
        
        Continuation oCon; 
        resElementFuture = NULL;
        Integer iTIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCallout.Timeout__c)/1000; 
        HUMAgencySearchService.Producer prod = new HUMAgencySearchService.Producer();
        AgencyBrokerSearch_LC_HUM.wrapperclass objWrapperclass = new AgencyBrokerSearch_LC_HUM.wrapperclass(); 
        try
        {            
            if(!bIsUnknownAgencySearch || bIsUnknownAgencySearch == null)  
            {
                if ( asyncProd == null ) 
                    asyncProd = new AsyncHUMAgencySearchService.AsyncBasicHttpBinding_IProducer(
                        objWebserviceCallout.End_Point_URL__c,
                        objWebserviceCallout.Certificate_Name__c,
                        Integer.valueOf(objWebserviceCallout.Timeout__c));
                
                prod = callProdObject(); 
                if(objWebserviceCallout != null)
                {         
                    if(objWebserviceCallout.Active__c)
                    {
                        oCon = new Continuation(iTIMEOUT_INT_SECS);
                        oCon.continuationMethod = 'processResponse';
                            resElementFuture = asyncProd.beginSearchProducer(oCon,prod,false);
                            conStateWrap = new Continuation_DTO_HUM(resElementFuture,oSearchWrapper);
                            oCon.state = conStateWrap;
                       		return oCon;
                    }                 
                }
            }
            else
            {
                objWrapperclass = AgencyBrokerSearch_LD_HUM.queryAgencyAccount(oSearchWrapper);
                return objWrapperclass;
            }  
            if(lstLogErrors.size() > 0 ) HUMExceptionHelper.saveLog(lstLogErrors,true);      
        }
        catch(Exception e)
        {                        
            bIsAgencyError = true;
			HUMExceptionHelper.logErrors(e, sClassName, sMethodName); 
        }
        return null;
    }
    /*  
     * Method name : unknownCreateSearchAgencyBroker
     * Description : This method will be called On clicking the search button On Agency search page.
     * Return Type : list of Account
     * Parameter   : NA
     */   
    
    @AuraEnabled
    Public Static Object unknownCreateSearchAgencyBroker(AgencyBrokerSearch_DTO_HUM agencySearchWrapper)
    {	
        bIsUnknownProvider =false; 
        bIsNameSearch =false;
        oSearchWrapper = agencySearchWrapper; 
        bIsUnknownAgencySearch = oSearchWrapper.isUnknownAgencySearch;
        sMethodName = 'unknownCreateSearchAgencyBroker';        
        HUMAgencySearchService.Producer prod = New HUMAgencySearchService.Producer();
        AgencyBrokerSearch_LC_HUM.wrapperclass objWrapperclass = New AgencyBrokerSearch_LC_HUM.wrapperclass(); 
        Try
        {            
            If (bIsUnknownAgencySearch)  
            {
                objWrapperclass = UnknownAgencyBrokerSearch_LD_HUM.queryAgencyAccount(oSearchWrapper);
                Return objWrapperclass;
            }
           
            If (lstLogErrors.size() > 0) HUMExceptionHelper.saveLog(lstLogErrors,True);      
        }
        Catch(Exception e)
        {                        
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
        }
        Return null;
    }
     
    /*  
     * Method name : callProdObject
     * Description : This method will return the Prod object that is being passed as a parameter to the webservice callout
     * Return Type : HUMAgencySearchService.Producer
     * Parameter   : NA
     */ 
    
    @testvisible private static HUMAgencySearchService.Producer callProdObject()
    {
        HUMAgencySearchService.Producer prod = new HUMAgencySearchService.Producer();
            
        if(oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue1)
        {
            if(!String.isBlank(oSearchWrapper.sLastName) && !String.isBlank(oSearchWrapper.sAgencyName))
            {
                prod.Name = oSearchWrapper.sLastName + ',' + oSearchWrapper.sAgencyName;                
            
            }
            else if(String.isBlank(oSearchWrapper.sLastName) && !String.isBlank(oSearchWrapper.sAgencyName))
            {
                prod.Name = ',' + oSearchWrapper.sAgencyName;                              
            
            }
        }
        else if(oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue2)
        {
            prod.Name = oSearchWrapper.sAgencyName;            

} 

        prod.ProducerType=oSearchWrapper.sAgentType;
        prod.SysAssgnNbr = oSearchWrapper.sAgentId;
        prod.TaxNbr = oSearchWrapper.sTaxID;
        
        prod.StateCode = oSearchWrapper.sState;
        return prod;    
    }
    
    /*  
     * Method name : processResponse
     * Description : This method will be called when Continuation method process Response from integration / Call out
     * Return Type : Object
     * Parameter   : List of Strings and State Object  
     */
    @AuraEnabled
    public static object processResponse(List<string> labels, Object state)
    {  
        sMethodName = 'processResponse';
        AgencyBrokerSearch_LC_HUM.wrapperclass objWrappercls = new AgencyBrokerSearch_LC_HUM.wrapperclass();
        try
        {
            Continuation_DTO_HUM cStateClass = (Continuation_DTO_HUM)state;
            HUMAgencySearchService.ProducerSearchResult resEle;
            if(bIsUnknownAgencySearch != true) 
            {
                resEle = new HUMAgencySearchService.ProducerSearchResult(); 
                resEle = callService(cStateClass);
            }   
                        
            if(resEle == null || bIsUnknownAgencySearch == true)            
            {  
                objWrappercls = AgencyBrokerSearch_LD_HUM.queryAgencyAccount(cStateClass.oSearchWrapper); 
            }                
            else            
            {         
                objWrappercls = AgencyBrokerSearch_LS_HUM.createAccountList(resEle.ProducerList,cStateClass.oSearchWrapper); 
            }   
                
            if(lstLogErrors.size() > 0) HUMExceptionHelper.saveLog(lstLogErrors,true);   
            
            if(objWrappercls.lstSObjects.isEmpty())
            {
                throw new HUMCustomException(system.label.HUMAgentSearchNoResultFound);
            }
            
            if(objWrappercls.lstSObjects.size() > GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencyObjectSize)
            {
                throw new HUMCustomException(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMSearch_returned_more_than_100_entries);
            }
        }
        catch(CalloutException e)
        {
            bAgencyTimedout = true;
            bIsAgencyError = true;
			HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
        }
        catch(Exception e)
        {
            bIsAgencyError = true;
			HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            return null;
        }
        return objWrappercls; 
    }
    /*  
     * Method name : callService
     * Description : This method return the response of the webservice callout
     * Return Type : HUMAgencySearchService.ProducerSearchResult 
     * Parameter   : NA 
     */ 
    public static HUMAgencySearchService.ProducerSearchResult callService(Continuation_DTO_HUM cStateClass)
    {
        sMethodName = 'callService';
        try
        {
            return cStateClass.request.getValue();
       
        }
        catch(Exception e)
        {
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
        }
        return null;
    }
    /*  
     * Method name : insertAgentAccount
     * Description : This method return the response of inserted account
     * Return Type : String 
     * Parameter   : NA 
     */ 
     @AuraEnabled
    public static String insertAgentAccount(String consumerIds,String accountJson , String externalId) 
    {   
        String sIdOfAgent;
        try{    
            sIdOfAgent=AgencyBrokerSearch_LD_HUM.insertAgentAccount(consumerIds,accountJson,externalId);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'AgencyBrokerSearch_LC_HUM', 'insertAgentAccount');   
        }
        return sIdOfAgent;
    }
   
    //Wrapper class to send data from server to client
    public class wrapperclass{
        @AuraEnabled public List<Account> lstSObjects;
        @AuraEnabled public string sTaxIds;
    }
    
}