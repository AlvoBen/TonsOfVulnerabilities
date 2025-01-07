/*******************************************************************************************************************************
Apex Class Name : MemberSearch_LS_HUM
Version         : 1.0
Created On      : 04/11/2018 
Function        : Service Class for MemberSearch_LC_HUM
Test Class		  : MemberSearch_LT_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Joel George                                        09/18/2020                  Original Version
* Kajal Namdev                                       05/07/2021                 Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class
********************************************************************************************************************************/
public with sharing class MemberSearch_LS_HUM {

    @testVisible
    private static String sRequestLabelPID='';
       public static String sourcePersonId;
    /*
        * Returns continuation object
        * <p>
        * Method to call continuation service for the service for MBE.
        *
        * @param objMemSearchPlus              HUMMemberSearchControllerV2  class instance
        * @return                                continuation
    */
      @AuraEnabled( continuation=true cacheable=true)
    public static Continuation getInvokeMBEPlusService2(String  sPIdVal)
    {
        try{
            sourcePersonId = sPIdVal;
            HUM_Webservice_Callout__c mbeServiceRecord = HUM_Webservice_Callout__c.getValues(GLOBAL_SEARCH_CONSTANT_LH_HUM.sMbePlus);

            Integer timOutVariable = Integer.valueof(mbeServiceRecord.Timeout__c) / 1000;
            Continuation con = new Continuation(timOutVariable);
            con.continuationMethod ='getparseMBEPlusResponse2' ; //getparseMBEPlusResponse
            HttpRequest httpMBEReqObj = buildHttpRequest(mbeServiceRecord);
            httpMBEReqObj.setBody(JSON.serialize(RequestStructureMBEPlusService(sourcePersonId)));
           	sRequestLabelPID = con.addHttpRequest(httpMBEReqObj);    
            return con;
        }catch(Exception e){
                HUMExceptionHelper.logErrors(e, 'MemberSearch_lwc_S_HUM', 'sendRequestMBEPIDService');
          return Null;
        }
    }
    /*
    * Returns null but sets the values of responses to UI
    * <p>
    * Callback method to process the serive for MBE services .
    *
    * @param humMemSearchCont          HUMMemberSearchControllerV2 class instance
    * @return                                   NULL
    */ 
      @AuraEnabled( continuation=true cacheable=true) 
     public static Object getparseMBEPlusResponse2(List<String> labels)
     {
        try
        {
             String sPIDResponse;
             String sEnterpriseId;
              HttpResponse response = Continuation.getResponse(labels[0]);
               SearchMBEPlusResponse_DTO_HUM oResponse;
                if (response != null &&((response.getStatusCode() == GLOBAL_SEARCH_CONSTANT_LH_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_SEARCH_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM) && 
                 (!response.getBody().containsIgnoreCase(GLOBAL_SEARCH_CONSTANT_LH_HUM.VOB_FAULT_CODE))|| Test.isRunningTest()))
              {
                oResponse = (SearchMBEPlusResponse_DTO_HUM)System.JSON.deserialize(response.getBody(), SearchMBEPlusResponse_DTO_HUM.class);
                }
                if(oResponse != null &&oResponse.GetMemberResponse != null &&oResponse.GetMemberResponse.Body != null && 
                   oResponse.GetMemberResponse.Body.Member != null && oResponse.GetMemberResponse.Body.Member.EnterpriseId != null)
                {
                  sEnterpriseId = oResponse.GetMemberResponse.Body.Member.EnterpriseId; 
                }
                else
                {
                  sEnterpriseId='';
                }
            return sEnterpriseId;    
        }
    Catch(Exception ex){
        HUMExceptionHelper.logErrors(ex, 'MemberSearch_LS_HUM' , 'processMBE_PIDRequest');
        return Null;
    }   
    }
    
    /*
    * Returns HttpRequest request with request body
    * <p>
    * Method to prepare httpRequest details and certification details
    *
    * @param  HUM_Webservice_Callout__c record .
    * @return HttpRequest
    */
    public static HttpRequest buildHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                request.setHeader(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_PCP_SER_CONECTION, GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_PCP_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUM_PCP_SER_SOAPACTION, '');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
    }
    /*
    * Returns MBEPlusRequest_DTO_HUM object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for MBE service .
    *
    * @param    sourcePersonId   an policy member record object derived from SOQL results.
    * @return  MBEPlusRequest_DTO_HUM      request structure of the wrapper class MBEServiceRequest_H_HUM
    */
    public static SearchMBEPlusRequest_DTO_HUM RequestStructureMBEPlusService(String sourcePersonId)
    {
            SearchMBEPlusRequest_DTO_HUM objReqWrap =new SearchMBEPlusRequest_DTO_HUM();
            SearchMBEPlusRequest_DTO_HUM.GetMember getMemvar =new SearchMBEPlusRequest_DTO_HUM.GetMember ();
            List<string> platformlst=new List<string>();
            platformlst.add(String.valueOf(GLOBAL_SEARCH_CONSTANT_LH_HUM.VOB_PLATFORM_LV_HUM));
            platformlst.add(String.valueOf(GLOBAL_SEARCH_CONSTANT_LH_HUM.VOB_PLATFORM_EM_HUM));
            List<String> periodLst=new List<String>{GLOBAL_SEARCH_CONSTANT_LH_HUM.VOB_CURRENT_HUM,GLOBAL_SEARCH_CONSTANT_LH_HUM.VOB_PAST_HUM,GLOBAL_SEARCH_CONSTANT_LH_HUM.VOB_FUTURE_HUM};
            SearchMBEPlusRequest_DTO_HUM.PeriodList periodVar=new SearchMBEPlusRequest_DTO_HUM.PeriodList() ;
            periodVar.period=periodLst;
            SearchMBEPlusRequest_DTO_HUM.SourcePersonIdCriteria sourcePersonIdVar=new SearchMBEPlusRequest_DTO_HUM.SourcePersonIdCriteria();
            sourcePersonIdVar.SourcePersonId=sourcePersonId;
            sourcePersonIdVar.AsOfDate=String.valueOf(System.Today());
            sourcePersonIdVar.PeriodList=periodVar;
            sourcePersonIdVar.Platform=platformlst;
            SearchMBEPlusRequest_DTO_HUM.GetCriteria getCriteriaVar=new SearchMBEPlusRequest_DTO_HUM.GetCriteria ();
            getCriteriaVar.SourcePersonIdCriteria=sourcePersonIdVar;
            SearchMBEPlusRequest_DTO_HUM.PlatformPointerList platformPointerVar=new SearchMBEPlusRequest_DTO_HUM.PlatformPointerList();
            platformPointerVar.PlatformProxyKey='';
            SearchMBEPlusRequest_DTO_HUM.Member memberVar=new SearchMBEPlusRequest_DTO_HUM.Member();
            memberVar.PlatformPointerList=platformPointerVar;
            memberVar.GetCriteria=getCriteriaVar;
            getMemvar.member=memberVar;
            objReqWrap.GetMember =getMemvar;
            String PidReq=Json.serialize(objReqWrap); 
            return objReqWrap;
       }
}