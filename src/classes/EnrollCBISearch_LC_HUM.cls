/*******************************************************************************************************************************
Apex Class Name : EnrollCBISearch_LC_HUM
Version         : 1.0
Created On      : 10/09/2020
Function        : This class is used for showing CBI searc data table 

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                        10/09/2020               Original Version
*    2.0           Visweswararao Jyavarapu                              04/07/2023               User Story 4404841: T1PRJ0865978 - MF24206 - Add SSN field in UI and enhance the search capability for CBIS in enrollment search
*******************************************************************************************************************************************************************/
public class EnrollCBISearch_LC_HUM
{   
    
    public static String processResponseSpecialty = 'processResponseCBISSpecialty';
    public static String cbisSearchEnrollmentV2 = 'HUMCbisSearchEnrollment_V2';
    public static HUMCBISSearch_S_HUM oServiceCBISSpecialty = new HUMCBISSearch_S_HUM ();
    public static string sRequest;
    public static HUMCBISSearchResponse_H_HUM respond = new HUMCBISSearchResponse_H_HUM();
    public static String humSearchEnrollmentNoRes = 'No Result(s) found';
    public class ServiceDownException extends Exception {}
    public static boolean bIsWarningExist{get;set;}
	@TestVisible private static String continuationState;
    @TestVisible private static String continuationLabel;
    
    public static AsyncHUMCBISSearchService.GetPendHistoryTermsDetailsResponse_elementFuture contstate;
    /* 
    * Method name : searchCBISCode
    * Description : This method will return the list of CBI search Data
    * Parameter   : CBi search input data which comes from UI
    */
    @AuraEnabled(continuation=true)
    public static Continuation searchCBISCode(String CBISearchJson)
    {
        CBISearchModal objCbi = (CBISearchModal) JSON.deserialize(CBISearchJson, CBISearchModal.class);
        
        HUM_Webservice_Callout__c objWebserviceCalloutCBIS = HUM_Webservice_Callout__c.getInstance('HUMCbisSearchEnrollment');
        Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCalloutCBIS.Timeout__c)/1000;
        
        HUMCBISSearchServiceHelper.PendHistoryandTermsRequestDTO cbisRequestDTO;
        AsyncHUMCBISSearchService.GetPendHistoryTermsDetailsResponse_elementFuture oResponseCBIS=NULL;
        // Initializing continuation instance and defining callback method
        Continuation oConCBIS;
        
        // Creating request for async callout 
        AsyncHUMCBISSearchService.AsyncEndPoint_GetPendHistoryAndTermsDetails requestService= new AsyncHUMCBISSearchService.AsyncEndPoint_GetPendHistoryAndTermsDetails();
        requestService.clientCertName_x = objWebserviceCalloutCBIS.Certificate_Name__c;
        requestService.timeout_x = Integer.valueOf(objWebserviceCalloutCBIS.Timeout__c);
        requestService.endpoint_x = objWebserviceCalloutCBIS.End_Point_URL__c;
        
        cbisRequestDTO = new HUMCBISSearchServiceHelper.PendHistoryandTermsRequestDTO();
        if(String.isNotBlank(objCbi.fName)) cbisRequestDTO.FirstName=objCbi.fName; 
        if(String.isNotBlank(objCbi.lName)) cbisRequestDTO.LastName=objCbi.lName; 
        if(NULL != objCbi.DOB) cbisRequestDTO.DOB = date.parse(objCbi.DOB);
        cbisRequestDTO.stateCode= objCbi.sState;
        
        oResponseCBIS = new AsyncHUMCBISSearchService.GetPendHistoryTermsDetailsResponse_elementFuture();
        if(objWebserviceCalloutCBIS != null)
        {
            if(objWebserviceCalloutCBIS.Active__c)
            {
                oConCBIS = new Continuation(TIMEOUT_INT_SECS);
                oConCBIS.continuationMethod = 'CBISCodeProcessResponse';
                // callout to CBIS System
                contstate = requestService.beginGetPendHistoryTermsDetails(oConCBIS,cbisRequestDTO);
                oConCBIS.state = contstate;
            }
        } 
        return oConCBIS; 
    }
    
    /*Inner class which is used to store the input search data that comes from UI */
    public class CBISearchModal extends MemberSearchEnrollment_LC_HUM.TRRSearchModal
    {
        public String sState;
        public CBISearchModal(){}
    }
    
    /* 
    * Method name : CBISCodeProcessResponse
    * Description : This method will process the response from callout 
    * Parameter   : state for maintain previous data across async transaction
    */
    @AuraEnabled
    public static Object CBISCodeProcessResponse(Object state)
    {
        AsyncHUMCBISSearchService.GetPendHistoryTermsDetailsResponse_elementFuture oResponseCBICallback = ( AsyncHUMCBISSearchService.GetPendHistoryTermsDetailsResponse_elementFuture)state;
        HUMCBISSearchServiceHelper.PendHistoryandTermsResponseDTO oResponse;
        try
        {
            oResponse = oResponseCBICallback.getValue();
            // parsing the response 
            HUMCBISSearchServiceHelper.ResponseItem oMember = NULL;
            
            List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService = new List<MemberSearchEnrollmentHelper_LC_HUM>();
            
            if(oResponse != null && oResponse.ResponseItems != null && oResponse.ResponseItems.ResponseItem != null)
            {
                for(Integer iIndex = 0; iIndex < oResponse.ResponseItems.ResponseItem.size(); iIndex++)
                {    
                    oMember = oResponse.ResponseItems.ResponseItem[iIndex];   
                    MemberSearchEnrollmentHelper_LC_HUM humLegMemberObj = new MemberSearchEnrollmentHelper_LC_HUM(oMember,iIndex+1); 
                    lstLegacyService.add(humLegMemberObj);  
                }
            }
            
            return lstLegacyService;
        }
        catch(CalloutException e)
        {
            throw new HUMCustomException(e.getMessage());  
        }
        catch(Exception e)
        {      
            if(e.getmessage() == system.label.HUMCBISRestrictedError)
            {
                throw new HUMCustomException(system.label.HUMCBISRestrictedError);
            }
            throw new HUMCustomException(e.getmessage()); 
        } 
    }   
   /*
   * Method Name   :    seachCBISCodeSpeciality()
   * Description   :    Method which will be called for searching Member in CBIS_V2 for REST Service.                        
   * Return Type   :    Continuation
   * Parameters    :    NA
   */
     @AuraEnabled(continuation=true cacheable=true)
    public static Continuation seachCBISCodeSpeciality(String CBISearchJson)
    {
        
        HUMSearchEnrollmentHelper oSearchWrapperRequest = new HUMSearchEnrollmentHelper();
        CBISearchModal oSearchWrapper = (CBISearchModal) JSON.deserialize(CBISearchJson, CBISearchModal.class);
        if(String.isBlank(oSearchWrapper.sSSN))
        {
            oSearchWrapperRequest.sFirstName = oSearchWrapper.fName;
            oSearchWrapperRequest.sLastName = oSearchWrapper.lName;
            if(NULL != oSearchWrapper.DOB && String.isNotBlank(oSearchWrapper.DOB)){
                Date dt = Date.parse(oSearchWrapper.DOB);
                DateTime dtAndTime = DateTime.newInstance(dt.year(),dt.month(),dt.day(),0,0,0);
                oSearchWrapperRequest.sBirthdate = dtAndTime.format('yyyy-MM-dd\'T\'HH:mm:\'00\'');
            }
            oSearchWrapperRequest.sState = oSearchWrapper.sState;
        }
        else
        {
            oSearchWrapperRequest.sFirstName ='' ;
            oSearchWrapperRequest.sLastName = '';
            oSearchWrapperRequest.sBirthdate = '';
            oSearchWrapperRequest.sState = '';
        	oSearchWrapperRequest.sSSN = oSearchWrapper.sSSN;
        }
           
        try{
             HUM_Webservice_Callout__c ServiceObj = HUM_Webservice_Callout__c.getValues(cbisSearchEnrollmentV2);
             Integer iTIMEOUT_INT_SECS = Integer.valueof(ServiceObj.Timeout__c)/1000;
             Continuation cont = new Continuation(iTIMEOUT_INT_SECS );
             cont.continuationMethod=processResponseSpecialty;
             HttpRequest request = new HttpRequest();
             oServiceCBISSpecialty.setupRequest(request, ServiceObj, oSearchWrapperRequest); 
             cont.state = cont.addHttpRequest(request);
			 if(Test.isRunningTest()){
			 continuationLabel = cont.addHttpRequest(request);
             continuationState = 'data';
            }
             return cont;
            }
            catch(Exception ex)
            {    
                HUMExceptionHelper.logErrors(ex,'EnrollCBISearch_LC_HUM','seachCBISCodeSpeciality');     
            } 
            return NULL;
    }
    
    /*
     * Method Name   :    processResponseCBISSpecialty
     * Description   :    This method process Http Response from CBIS Specialty REST Service 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
     
    @AuraEnabled(cacheable=true)
    public static object processResponseCBISSpecialty(list<string> labels, Object state)
    {   
         Object cbisResponseResults=null;
        try 
        { 
          HttpResponse response = Continuation.getResponse((string)state); 
          boolean isResponseValid = checkResponseValidity(response);
           
          if(isResponseValid)
              { 
                respond = (HUMCBISSearchResponse_H_HUM)System.JSON.deserialize(response.getBody(),HUMCBISSearchResponse_H_HUM.class);
			    if(respond.GetPendHistoryTermsDetailsResponse.Status.IsSuccesful<>null && respond.GetPendHistoryTermsDetailsResponse.Status.IsSuccesful.equals(GlobalCommonConstants_HUM.STRING_TRUE))
                {
                 if(respond<>null && respond.GetPendHistoryTermsDetailsResponse<>null && respond.GetPendHistoryTermsDetailsResponse.ResponseItems<>null && respond.GetPendHistoryTermsDetailsResponse.ResponseItems.ResponseItem<>null)
                 { 
                   cbisResponseResults = setCBISMemberSpecialtyDetails(respond); 
                 }
                }else{
                 throw new HUMCustomException(humSearchEnrollmentNoRes);   
                }
              }
              else
              { 
                HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+response.getBody()), 'EnrollCBISearch_LC_HUM', 'processResponseCBISSpecialty');
              }
        }
        catch(Exception e)
        {   
         HUMExceptionHelper.logErrors(e,'EnrollCBISearch_LC_HUM', 'processResponseCBISSpecialty');
        }
 		return cbisResponseResults;  
    }
    /*
     * Method Name   :  setCBISMemberSpecialtyDetails()
     * Description   :  This method is used too set CBIS Specialty Search Result from RESTService
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
       @AuraEnabled(cacheable=true)
       public static Object setCBISMemberSpecialtyDetails(HUMCBISSearchResponse_H_HUM resObj){
       bIsWarningExist=false;
       HUMCBISSearchResponse_H_HUM.ResponseItem oMember = NULL;
       List<MemberSearchEnrollmentHelper_LC_HUM> lstLegacyService = new List<MemberSearchEnrollmentHelper_LC_HUM>();
      MemberSearchEnrollmentHelper_LC_HUM humLegMemberObj=NULL;
       if(resObj != NULL)
       {
         if(resObj.GetPendHistoryTermsDetailsResponse<>null && resObj.GetPendHistoryTermsDetailsResponse.ResponseItems<>null && resObj.GetPendHistoryTermsDetailsResponse.ResponseItems.ResponseItem<>null)
		 {
          for(Integer iIndex = 0; iIndex < resObj.GetPendHistoryTermsDetailsResponse.ResponseItems.ResponseItem.size(); iIndex++)
            {    
              oMember = resObj.GetPendHistoryTermsDetailsResponse.ResponseItems.ResponseItem[iIndex];   
               humLegMemberObj = new MemberSearchEnrollmentHelper_LC_HUM(oMember);
               lstLegacyService.add(humLegMemberObj); 
            }
			if(!bIsWarningExist) 
            {
              bIsWarningExist=humLegMemberObj.bdisplayInteractbutton ;
            }
            if(bIsWarningExist!=null && bIsWarningExist) throw new HUMCustomException(system.label.HUMCBISRestrictedError);
         }
        }
        if(lstLegacyService.isEmpty()) 
         {
          throw new HUMCustomException(humSearchEnrollmentNoRes);
         }
            return lstLegacyService;
       }
    

     /*
     * Method Name   :    checkResponseValidity
     * Description   :    This method to check HttpResponseStatus 
     * Return Type   :    Boolean
     * Parameters    :    HttpResponse response
     */
  
   
    public static boolean checkResponseValidity(HttpResponse response) {
        boolean returnValue = false;
        try {
            if(response != null) {
                integer statusCode = response.getStatusCode();
                Boolean isStatusOk = (statusCode == GlobalCommonConstants_HUM.HTTP_OK_HUM) ? true : (statusCode == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM) ? true : false;
                Boolean isNoFault = (response.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE)) ? false : true;
                returnValue = (isStatusOk && isNoFault) ? true : false;
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex,'EnrollCBISearch_LC_HUM','checkResponseValidity');
            returnValue = false;
        }
        return returnValue;
    }
}