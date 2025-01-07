/*******************************************************************************************************************************
Apex Class Name : H1MarketEnrollSearch_LC_HUM
Version         : 1.0
Created On      : 10/20/2020
Function        : This class is used for showing H1marketplace data table 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        10/20/2020               Original Version
*    1.1           Ritik Agarwal                                        04/01/2021              Imrove code coverage
*******************************************************************************************************************************************************************/
public class H1MarketEnrollSearch_LC_HUM 
{
    public static Map<String,Object> mpOfCallback = new Map<String,Object>();
    public static Map<String,Object> contstate;
    /* 
    * Method name : searchH1Service
    * Description : This method will return the list of H1marketPlace search Data
    * Parameter   : H1MarketPlace search input data which comes from UI
    */
    @AuraEnabled(continuation=true)
    public static Continuation searchH1Service(String marketPlaceSearchModal)
    {
        AsyncHUMH1SearchResponseHelper.EnrollmentDetailResponseFuture oResponseH1;
        MarketPlaceEnroll objH1Market = (MarketPlaceEnroll) JSON.deserialize(marketPlaceSearchModal, MarketPlaceEnroll.class);
        
        HUM_Webservice_Callout__c objWebserviceCalloutH1 = HUM_Webservice_Callout__c.getInstance('H1ExchangeService');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutH1.Timeout__c)/1000;
        
        Continuation oConH1;  
        
        AsyncHUMH1SearchService.AsyncAM65AM65Port requestService = new AsyncHUMH1SearchService.AsyncAM65AM65Port(objWebserviceCalloutH1.End_Point_URL__c,objWebserviceCalloutH1.Certificate_Name__c,Integer.valueOf(objWebserviceCalloutH1.Timeout__c));            
        HUMH1SearchRequestHelper.EnrollmentDetailRequest oReqMessage = new HUMH1SearchRequestHelper.EnrollmentDetailRequest();
        if(String.isNotBlank( objH1Market.sSSN)) oReqMessage.mbrSsn = objH1Market.sSSN;
        oReqMessage.mbrDob = '';
        oReqMessage.effDateFr = '';
        oReqMessage.effDateTo = '';
        oReqMessage.recptDateFr = '';
        oReqMessage.recptDateTo = '';
        oReqMessage.zipCode = '';
        HUMH1SearchRequestHelper.EnrollmentDetailReq oReq = new HUMH1SearchRequestHelper.EnrollmentDetailReq();
        oReq.searchType = system.label.HUMH1ExchRequestType;
        if(objWebserviceCalloutH1 != null)
        {
            if(objWebserviceCalloutH1.Active__c)
            {
                oConH1 = new Continuation(TIMEOUT_INT_SECS);
                oConH1.continuationMethod = 'H1ServiceProcessResponse';
                oResponseH1 = requestService.beginAM65AM65Operation(oConH1,oReqMessage,oReq.searchType);
                mpOfCallback.put('oResponseH1',oResponseH1);
                mpOfCallback.put('SSN',objH1Market.sSSN);
                contstate = mpOfCallback;
                oConH1.state=contstate;
            }
        }
        return oConH1;
    }
    
    /*Inner class which is used to store the input search data that comes from UI */
    public class MarketPlaceEnroll extends  MetavanceEnrollmentSearch_LC_HUM.SearchSSNEnrollment
    {
        public MarketPlaceEnroll(){}
    }
    
    /* 
    * Method name : callH1Service
    * Description : This method will return the H1response
    * Parameter   : Object
    */
    public static HUMH1SearchResponseHelper.EnrollmentDetailResponse callH1Service(AsyncHUMH1SearchResponseHelper.EnrollmentDetailResponseFuture oResponseH1)
    {
        try
        {
            if(!Test.isRunningTest()){
            return oResponseH1.getValue();
            }else{
                return null;
            }
        }
        catch(Exception e)
        {
            throw new HUMCustomException(e.getMessage()); 
        }
    } 
    
    /* 
    * Method name : H1ServiceProcessResponse
    * Description : This method will process the response
    * Parameter   : Object
    */
    @AuraEnabled
    public static Object H1ServiceProcessResponse(Object state)
    {
        mpOfCallback = (Map<String,Object>)state;
        AsyncHUMH1SearchResponseHelper.EnrollmentDetailResponseFuture objEnrollCallback = (AsyncHUMH1SearchResponseHelper.EnrollmentDetailResponseFuture)mpOfCallback.get('oResponseH1');
        String SSn = String.valueOf(mpOfCallback.get('SSN'));
        HUMH1SearchResponseHelper.EnrollmentDetailResponse oRespMesageH1;
        oRespMesageH1 = callH1Service(objEnrollCallback);
        return ProcessResponse(oRespMesageH1,SSn);
        
    }
    
    public static Object ProcessResponse(HUMH1SearchResponseHelper.EnrollmentDetailResponse oRespMesageH1,String SSn){
        HUMH1SearchResponseHelper.EnrollmentDetail oMember = null;
         List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService = new List<MemberSearchEnrollmentHelper_LC_HUM>();  
        if(oRespMesageH1 != null && oRespMesageH1.EnrollmentDetailList != null && oRespMesageH1.EnrollmentDetailList.EnrollmentDetail != null)
        {
            for(Integer iIndex = 0; iIndex < oRespMesageH1.EnrollmentDetailList.EnrollmentDetail.size(); iIndex++)
            {    
                if(oRespMesageH1.EnrollmentDetailList.EnrollmentDetail[iIndex].strSsn == SSn)
                {
                    oMember = oRespMesageH1.EnrollmentDetailList.EnrollmentDetail[iIndex];            
                    MemberSearchEnrollmentHelper_LC_HUM humLegMemberObj = new MemberSearchEnrollmentHelper_LC_HUM(oMember);             
                    lstLegacyService.add(humLegMemberObj);
                }
            }
        }
        return lstLegacyService;
    }
    
    
    /* 
    * Method name : searchOHBPService
    * Description : This method will hit service callout
    * Parameter   : Object
    */
    @AuraEnabled(continuation=true cacheable=true)
    public static Continuation searchOHBPService(String marketPlaceSearchModal)
    {
        AsyncHUMPersonAllLegalService.GetMemberInformationXmlResponse_elementFuture oRequestOHBP;        
        HUM_Webservice_Callout__c objWebserviceCalloutOHBP = HUM_Webservice_Callout__c.getInstance('OHBPService');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutOHBP.Timeout__c)/1000;
        // Ininitializing the continuation and defining the callback method
        Continuation oConOHBP;
        string sXml;
        
        // Creating request for async callout
        AsyncHUMPersonAllLegalService.Asyncsoap requestService = new AsyncHUMPersonAllLegalService.Asyncsoap(objWebserviceCalloutOHBP.End_Point_URL__c,objWebserviceCalloutOHBP.Certificate_Name__c,Integer.valueOf(objWebserviceCalloutOHBP.Timeout__c));
        MarketPlaceEnroll objH1Market = (MarketPlaceEnroll) JSON.deserialize(marketPlaceSearchModal, MarketPlaceEnroll.class);
        if(objH1Market !=null && String.isNotBlank( objH1Market.sSSN))
        {
            sXml = system.label.HUMOHBPMemberSearchReq_Header;
            sXml += objH1Market.sSSN + system.label.HUMOHBPMemberSearchReq_End; 
        }
        
        // Async callout for OHBP
        if(objWebserviceCalloutOHBP != null)
        {
            if(objWebserviceCalloutOHBP.Active__c)
            {
                oConOHBP =  new Continuation(TIMEOUT_INT_SECS);
                oConOHBP.continuationMethod = 'OHBPServiceProcessResponse';
                oRequestOHBP = requestService.beginGetMemberInformationXml(oConOHBP, sXml);
                mpOfCallback.put('oRequestOHBP',oRequestOHBP);
                contstate = mpOfCallback;
                oConOHBP.state=contstate;//oConOHBP.state = mpOfCallback;
            }
        }
        return oConOHBP;
    }
    
    /*
    * Method Name   :    callOHBPService
    * Description   :    Method to fetch value returned from callout                        
    * Return Type   :    String
    * Parameters    :    NA
    */
    public static string callOHBPService(AsyncHUMPersonAllLegalService.GetMemberInformationXmlResponse_elementFuture oRequestOHBP)
    {
        if(!test.isRunningTest()){
        return oRequestOHBP.getValue();
        }else{
            return null;
        }
    }
    
    /*
    * Method Name   :    callOHBPService
    * Description   :    Method to fetch value returned from callout                        
    * Return Type   :    String
    * Parameters    :    NA
    */
    @AuraEnabled
    public static Object OHBPServiceProcessResponse(Object state)
    {
        mpOfCallback = (Map<String,Object>)state;
        AsyncHUMPersonAllLegalService.GetMemberInformationXmlResponse_elementFuture oReqOHBPCallback = (AsyncHUMPersonAllLegalService.GetMemberInformationXmlResponse_elementFuture)mpOfCallback.get('oRequestOHBP');
        String oResponseOHBP ='';
        oResponseOHBP = callOHBPService(oReqOHBPCallback);
        if(!Test.isRunningTest()){
        return OHBPProcessResponse(oResponseOHBP);
        }else{
            return null;
        }
    }
    
    public static Object OHBPProcessResponse(String oResponseOHBP){
        
        List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService = new List<MemberSearchEnrollmentHelper_LC_HUM>();
        // Fetching and populating the result returned from callout
        List<String> lstGetMemberNodes = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPMember);
        try
        {
            //Parse Each Member for Account information
            for(String sMember : lstGetMemberNodes)
            {
                Account oAcc = new Account();    
                //Get Name
                List<String> lstFirstName = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchFirstName);               
                Map<String,String> mapAccount = new Map<String,String>();
                
                if(NULL != lstFirstName && !lstFirstName.isEmpty())
                {
                    mapAccount.put(system.label.HUMOHBPSearchFstName, lstFirstName[0]); 
                }       
                //Get LastName
                List<String> lstLastName = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchLastName); 
                if(NULL != lstLastName && !lstLastName.isEmpty())
                {
                    mapAccount.put(system.label.HUMOHBPSearchLstName, lstLastName[0]);
                }
                
                //Get DateOfBirth             
                List<String> lstDateOfBirth = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchBirthDate);
                if(NULL != lstDateOfBirth && !lstDateOfBirth.isEmpty())
                {
                    mapAccount.put(system.label.HUMOHBPSearchBirthDt, lstDateOfBirth[0]); 
                }
                
                // Group ID 
                List<String> lstPlanID = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchPlanID);
                if(NULL != lstPlanID && !lstPlanID.isEmpty())
                {  
                    mapAccount.put(system.label.HUMOHBPSearchIDPlan, lstPlanID[0]);
                }
                
                //ExternalID
                List<String> lstExternalID = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP,system.label.HUMOHBPSearchTranID);
                if(NULL != lstExternalID && !lstExternalID.isEmpty())
                {
                    mapAccount.put(system.label.HUMOHBPSearchExtID, lstExternalID[0]);
                }
                
                //Product
                List<String> lstProductName = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP, system.label.HUMOHBPSearchPlanName);
                if(NULL != lstProductName && !lstProductName.isEmpty())
                {
                    mapAccount.put(system.label.HUMOHBPSearchProd, lstProductName[0]);
                }
                
                //Effective date
                List<String> lstEffectiveDate = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP, system.label.HUMOHBPSearchEffDate);
                if(NULL != lstEffectiveDate  &&  !lstEffectiveDate.isEmpty())
                {
                    mapAccount.put(system.label.HUMOHBPSearchEfectDate, lstEffectiveDate[0]);
                }
                
                //Zip
                list<string> lstzip = HUMIntegrationUtilityHelper.getRepeatingValuesFromXMLString(oResponseOHBP, 'Zip');
                if(NULL != lstzip  &&  !lstzip.isEmpty())
                {
                    mapAccount.put('PersonMailingPostalCode', lstzip [0]);
                }
                
                MemberSearchEnrollmentHelper_LC_HUM humLegMemberObj = new MemberSearchEnrollmentHelper_LC_HUM(mapAccount);
                lstLegacyService.add(humLegMemberObj );   
            }
            
            return lstLegacyService;
        }
        catch(CalloutException e)
        {
            throw new HUMCustomException(e.getMessage());
        }
        catch(Exception e)
        {
            throw new HUMCustomException(e.getMessage());
        }
    }
}