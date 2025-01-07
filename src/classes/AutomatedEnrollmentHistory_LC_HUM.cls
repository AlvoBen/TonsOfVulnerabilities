/**********************************************************************************************************************************************************************
Apex Class Name : AutomatedEnrollmentHistory_LC_HUM
Version         : 1.0
Created On      : 10/14/2020

Test Class      : AutomatedEnrollmentHistory_LCT_HUM

Modification Log: 
* Developer Name           Code Review                  Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Joel  George                                        10/14/2020                   Original Version
*   Ritik Agarwal                                      10/16/2020                   Fixed the issue of Maintaining State across transactions 
*  Kajal Namdev                                        05/07/2021                   Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
**********************************************************************************************************************************************************************/
public class AutomatedEnrollmentHistory_LC_HUM 
{
    public static  Map<String,Object> mpOfCallback = new Map<String,Object>();
    public static Map<String,Object> contstate;
    /*
    * Method Name   :    searchAEService()
    * Description   :    Method which will be called for searching Member in Automated Enrollment System                      
    * Return Type   :    Continuation
    * Parameters    :    AESearchModel - input search fields comes from UI
    */
    @AuraEnabled(continuation=true)
    public static Continuation searchAEService(String AESearchModel)
    {
        AutomatedSearch objAESearch =(AutomatedSearch)JSON.deserialize(AESearchModel,AutomatedSearch.class);
        HUM_Webservice_Callout__c objWebServiceCalloutAE = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_LH_HUM.HUMAEHistSvc); //AEHistoryService
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebServiceCalloutAE.Timeout__c)/1000;
        AsyncHUMAEServicesHistoryResponse.ResponseComplexTypeFuture oResponse = null;
        Continuation oConAE;
        if(objWebServiceCalloutAE != null)
        {
            if(objWebServiceCalloutAE.Active__c)
            {
                oConAE = new Continuation(TIMEOUT_INT_SECS);
                oConAE.ContinuationMethod = 'AEServiceProcessResponse';
                oResponse = AEHistorySearch(oConAE,objAESearch);
                mpOfCallback.put('AutomatedSearch',objAESearch);
                mpOfCallback.put('oResponse',oResponse);
                contstate = mpOfCallback;
                oConAE.state=contstate;
            }
        }
        return oConAE;
    }
    
    /*
    * Method Name   :    AEServiceProcessResponse()
    * Description   :    Callback Method which will be invoked for Automated Enrollment Service                     
    * Return Type   :    Object
    * Parameters    :    NA
    */
    @AuraEnabled
    public static Object AEServiceProcessResponse(Object state)
    { 
        try
        {
            Map<String,Object> mpOfMaintainState = (Map<String,Object>)state;
            List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService =NULL;
            
            AutomatedSearch objAESearchCallback = (AutomatedSearch)mpOfMaintainState.get('AutomatedSearch');
            AsyncHUMAEServicesHistoryResponse.ResponseComplexTypeFuture oResponseMsg = (AsyncHUMAEServicesHistoryResponse.ResponseComplexTypeFuture)mpOfMaintainState.get('oResponse');
            lstLegacyService = processAEResponseResult(oResponseMsg.getValue(),objAESearchCallback);
            return lstLegacyService;
        }
        catch(CalloutException e)
        {
            throw new HUMCustomException(e.getMessage());
        }
        catch(Exception ex)
        {
            throw new HUMCustomException(ex.getMessage());
        }
    }
    /*
    * Method Name   :    processAEResponseResult
    * Description   :    Method to fetch the value returned from the callout                        
    * Return Type   :    NA
    * Parameters    :    NA
    */
    @testVisible private static List<MemberSearchEnrollmentHelper_LC_HUM> processAEResponseResult(HUMAEServicesHistoryResponse.ResponseComplexType oResponseComplex , AutomatedSearch objAESearchCallback2)
    {
        List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService = new List<MemberSearchEnrollmentHelper_LC_HUM>();
        if(oResponseComplex != null && oResponseComplex.PendHistoryList != null)
        {
            HUMAEServicesHistoryResponse.PendHistoryList oPendHistoryList = oResponseComplex.PendHistoryList;
            HUMAEServicesHistoryResponse.PendHistory oPendHistory = NULL;
            if(oPendHistoryList.PendHistory != null) 
            {
                for(Integer iIndex = 0; iIndex < oPendHistoryList.PendHistory.size(); iIndex++)
                {
                    if(oPendHistoryList.PendHistory[iIndex].Member_SSN == objAESearchCallback2.sSSN || oPendHistoryList.PendHistory[iIndex].SSN == objAESearchCallback2.sSSN)
                    {
                        oPendHistory = oPendHistoryList.PendHistory[iIndex];
                        break;
                    }
                }
                if(oPendHistory != null)
                {
                    MemberSearchEnrollmentHelper_LC_HUM humLegMemberObj = new MemberSearchEnrollmentHelper_LC_HUM(oPendHistory);
                    lstLegacyService.add(humLegMemberObj);
                }
                return lstLegacyService;
            }
        }
        return null;
    }
    /*
    * Method Name   :    AEHistorySearch()
    * Description   :    This Method is for calling AE History to search for a member not found in salesforce.
    * Return Type   :    AsyncHUMAEServicesHistoryResponse.ResponseComplexTypeFuture
    * Parameters    :    NA
    */
    public static AsyncHUMAEServicesHistoryResponse.ResponseComplexTypeFuture AEHistorySearch(Continuation oCon, AutomatedSearch objAESearch) 
    { 
        try
        {
            AsyncHUMAEServicesHistoryResponse.ResponseComplexTypeFuture oResponseMsg = NULL;
            String sMethodName = GLOBAL_CONSTANT_LH_HUM.HUMSearchEnrollmentAEHistory;
            HUM_Webservice_Callout__c svcHeaders = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_LH_HUM.HUMAEHistSvc); 
            AsyncHUMAETempURI.AsyncBasicHttpBinding_ITwoWayAsync oAEHistoryService = new AsyncHUMAETempURI.AsyncBasicHttpBinding_ITwoWayAsync();
            oAEHistoryService.endpoint_x = svcHeaders.End_Point_URL__c;
            oAEHistoryService.clientCertName_x = svcHeaders.Certificate_Name__c;
            oAEHistoryService.timeout_x = Integer.valueOf(svcHeaders.Timeout__c);
            oResponseMsg = oAEHistoryService.beginGetPendHistoryDetails(oCon, GLOBAL_CONSTANT_LH_HUM.HUMAEHistReqHeader, objAESearch.sSSN, '', '', Date.parse(objAESearch.sEffectiveDate), Date.parse(objAESearch.sEndDate));        
            return oResponseMsg;
        }
        catch(Exception e)
        {
            throw new HUMCustomException(e.getmessage()); 
        }
    }
    
    /*Inner class which is used to store the input search data that comes from UI */
    public class AutomatedSearch extends MetavanceEnrollmentSearch_LC_HUM.SearchSSNEnrollment
    {
        public AutomatedSearch(){}
    }
}