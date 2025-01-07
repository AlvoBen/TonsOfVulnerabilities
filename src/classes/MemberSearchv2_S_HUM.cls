/*******************************************************************************************************************************
Apex Class Name :  MemberSearchv2_S_HUM.cls
Version: 1.0
Created On      : 04/11/2018 
Function        : Service Class for MemberSearchv2_S_HUM.

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Charan konakandla         289399                     08/27/2018                  Original Version
* Ajay Chakradhar                                      05/31/2021                  REQ - 1755482 - Replacing constants to improve performance
********************************************************************************************************************************/
public with sharing class MemberSearchv2_S_HUM {

@testVisible
private String sRequestLabelPID='';
    Private String PID_CALLBACKMETHOD = 'parseMBEPlusResponse';
    Private String PID_MEMBERNOTAVIALBLE = 'Member is not found for this PID number';
    Private Static String VOB_CURRENT_HUM = 'Current';
    Private Static String VOB_PAST_HUM = 'Past';
    Private Static String VOB_FUTURE_HUM = 'Future';
/**
	* Returns continuation object
	* <p>
	* Method to call continuation service for the service for MBE.
	*
	* @param objMemSearchPlus              HUMMemberSearchControllerV2  class instance
	* @return                                continuation
*/
	public Continuation sendRequestMBEPIDService(HUMMemberSearchControllerV2  objMemSearchPlus)
	{
		try{
			HUM_Webservice_Callout__c mbeServiceRecord = HUM_Webservice_Callout__c.getValues('MBEPlusService');
			Integer timOutVariable = Integer.valueof(mbeServiceRecord.Timeout__c) / 1000;
            Continuation con = new Continuation(timOutVariable);
			con.continuationMethod = PID_CALLBACKMETHOD;
			HttpRequest httpMBEReqObj = MemberSearchv2_S_HUM.buildHttpRequest(mbeServiceRecord);
			httpMBEReqObj.setBody(JSON.serialize(RequestStructureMBEPlusService(objMemSearchPlus.sourcePersonId)));
			sRequestLabelPID  = con.addHttpRequest(httpMBEReqObj);
			return con;
		}catch(Exception e){
          	HUMExceptionHelper.logErrors(e, 'MemberSearchv2_S_HUM', 'sendRequestMBEPIDService');
			return Null;
		}
	}
/**
	* Returns null but sets the values of responses to UI
	* <p>
	* Callback method to process the serive for MBE services .
	*
	* @param humMemSearchCont          HUMMemberSearchControllerV2 class instance
	* @return                                   NULL
	*/	
	public Object processMBEPIDRequest(HUMMemberSearchControllerV2 humMemSearchCont){
	  try
	  {
	   		String sPIDResponse;
        	HttpResponse response = Continuation.getResponse(sRequestLabelPID);
       		SearchMBEPlusResponse_DTO_HUM oResponse;
            if (response != null &&((response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM) && 
	           (!response.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE))|| Test.isRunningTest()))
	        {
	     	 	oResponse = (SearchMBEPlusResponse_DTO_HUM)System.JSON.deserialize(response.getBody(), SearchMBEPlusResponse_DTO_HUM.class);
            }
            if(oResponse != null &&oResponse.GetMemberResponse != null &&oResponse.GetMemberResponse.Body != null && 
               oResponse.GetMemberResponse.Body.Member != null && oResponse.GetMemberResponse.Body.Member.EnterpriseId != null)
            {
             	humMemSearchCont.sEnterpriseId = oResponse.GetMemberResponse.Body.Member.EnterpriseId; 
            }
            else
            {
           		humMemSearchCont.sEnterpriseId='';
             	humMemSearchCont.addErrorMessageToPage(PID_MEMBERNOTAVIALBLE); 
            }
            humMemSearchCont.buildEnterpriseIDQuery();
        return null;	
	}
	Catch(Exception ex){
		HUMExceptionHelper.logErrors(ex, 'MemberSearchv2_S_HUM'	, 'processMBE_PIDRequest');
		return Null;
	}	
	}/**
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
				if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, ServiceObj.Content_Type__c);
				request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM, GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
				request.setEndpoint(ServiceObj.End_Point_URL__c);
				request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM, '');
				if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
			}
			return request;
	}
	/**
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
            platformlst.add(String.valueOf(ConsumerManagementConstants_HUM.VOB_PLATFORM_LV_HUM));
            platformlst.add(String.valueOf(ConsumerManagementConstants_HUM.VOB_PLATFORM_EM_HUM));
			List<String> periodLst=new List<String>{VOB_CURRENT_HUM,VOB_PAST_HUM,VOB_FUTURE_HUM};
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