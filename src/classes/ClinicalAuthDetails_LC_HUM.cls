/*****************************************************************************************
Apex class Name    : ClinicalAuthDetails_LC_HUM
Version            : 1.0
Function           : This service class is used to request Authsummary details 
Created On         : 07/19/2022
Test Class         : ClinicalAuthDetails_LT_HUM 

Modification Log:
*   Developer                   Code Review           Date               Description
* ------------------------------------------------------------------------------------------------------------------------------ 
*  Rajesh Narode                                    07/19/2022         3470586-Contact Information - Backend Code Conversion 
* Suraj patil										12/25/2022			REQ - 4046927 T1PRJ0193102 - MF 3223793 - DF 6667:- CRM Service Benefits 2022 Auth/Referrals: Lightning Communication Records Retrieve Next Record Set Not Working
* Raj Paliwal										03/03/2023		   User story 4003693 - Auth/Referral: Lightning Verification: Toast/Error/Info Messages.
********************************************************************************************************************************/

public with sharing class ClinicalAuthDetails_LC_HUM {
    @TestVisible
    public static String sRequest  = '';
    private static String sResponse = '';
    public static ClinicalAuthDetails_LS_HUM oServiceAuth = new ClinicalAuthDetails_LS_HUM(); 
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstCommunicationRecords {get; set;}
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstLettersRecords {get; set;}
    public static List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthResponse = new List<AuthRefferal_Wrapper_LDTO_HUM>();
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    public static string sEnterpriseID;
    public static string sDateOfBirth;
    public static ClinicalAuthDetailsWrapper_LDTO_HUM oAuthDetailsWrapper{get;set;}
    public static ClinicalAuthDetailsWrapper_LDTO_HUM oAuthDetailsWrapperIP{get;set;}
    public static ClinicalAuthDetailsWrapper_LDTO_HUM oAuthDetailsWrapperOP{get;set;}
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProvider{get;set;}
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstDiagnosis{get;set;}
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProcedure{get;set;}
    public static List<ClinicalAuthDetailsWrapper_LDTO_HUM> lstProcedureOP{get;set;}
    public static string sLettersPageNumber {get;set;}
    public static string sCommunicationPageNumber {get;set;}
    public static Boolean bServiceError{get;set;}
    public static Boolean bSearchError{get;set;}
    public static Boolean bAuthTypeIP{get;set;}
    public static Boolean bAuthTypeOP{get;set;}
    public static String sPolicyMemRecID {get; set;}
    public static String standardMapvalue {get; set;}
    public static String sAuthID{get;set;}
    public static string sTotalCommunicationRecords {get;set;}
    public static string sTotalLettersRecords {get;set;}     
    public static Boolean bIsError{get;set;}
    public static Boolean bShowError{get;set;}
    public static Boolean bShowMessage{get;set;}
    public static Boolean bShowResults{get;set;}
    public static Boolean bShowServiceError{get;set;}
    public static String sRecId {get;set;} 
    public static List<AuthRefferal_Wrapper_LDTO_HUM> lstAuth;
    public static String sUserinputAuthId {get;set;}
    public static Boolean bTotalRecordFlag {get;set;}
    public static String sKMDocRequest = '';
    public static AuthDetailRequest AuthDetailRequest;
	
    /*
* Method Name   :  authDetailsRequest
* Description   :  This method is used to Call the Service while loading the page
* Return Type   :  Object
* Parameters    :  None
*/
    @AuraEnabled(cacheable = true continuation=true)
    Public Static Object authDetailsRequest(String authId,  string sMemberPlanRecID) 
    {
        ClinicalAuthDetailsRequest_LH_HUM oclinicalAuthdetailLWC = new ClinicalAuthDetailsRequest_LH_HUM();
		oclinicalAuthdetailLWC = ClinicalAuthDetails_LS_HUM.assignValuesToHelperV2(authId, sMemberPlanRecID);
        HUM_Webservice_Callout__c oServiceCalloutClinicalV2 = HUM_Webservice_Callout__c.getValues('Clinical_AuthDetail_V2'); 
        Integer iTIMEOUTINTSECS;
        String sJsonRequest;
        HttpRequest oRequest = new HttpRequest();
        iTIMEOUTINTSECS = Integer.valueof(oServiceCalloutClinicalV2.Timeout__c) / 1000;                
        sJsonRequest = JSON.serialize(oclinicalAuthdetailLWC);
        oRequest = setHttpRequest(oServiceCalloutClinicalV2, sjsonRequest);  
        Continuation con = new Continuation(iTIMEOUTINTSECS );
        con.continuationMethod='processResponseLWC';
        sRequest = con.addHttpRequest(oRequest);
        return con;
    }
    
    /*
* Method Name   :  processResponseLWC
* Description   :  This is the Callback method for Continous Object to process the Response data
* Return Type   :  Object
* Parameters    :  None
*/
    @AuraEnabled(cacheable=true)
    public static Object processResponseLWC(List<string> labels, Object State) { 
        HttpResponse oResponse = Continuation.getResponse(labels[0]);
        if(oResponse != null){
            return responseWrap(oResponse);
        }
        return oResponse;
        
    }
    
    /*
* Method Name   :  responseWrap
* Description   :  This method is used to deserialize the response and diplay the response on Page
* Return Type   :  HttpResponse sResponse
* Parameters    :  HttpResponse
*/  
    public static String responseWrap(HttpResponse sResponse)
    {
		
        ClinicalAuthDetailsResponse_LH_HUM oResponse;
        try{
            oResponse  = (ClinicalAuthDetailsResponse_LH_HUM)JSON.deserialize(sResponse.getBody(),ClinicalAuthDetailsResponse_LH_HUM.class);
			if(oResponse != NULL)
			{
            ClinicalAuthDetailsResponse_LH_HUM.AuthorizationsResponse objAuthReponse = new ClinicalAuthDetailsResponse_LH_HUM.AuthorizationsResponse();
            ClinicalAuthDetailsResponse_LH_HUM.MemberInfo objMemberInfo = new ClinicalAuthDetailsResponse_LH_HUM.MemberInfo();
            List<ClinicalAuthDetailsResponse_LH_HUM.Authorization> lstAuthorizations = new List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>();
            List<ClinicalAuthDetailsResponse_LH_HUM.CommunicationRecords> lstCommunicationResponse = new List<ClinicalAuthDetailsResponse_LH_HUM.CommunicationRecords>();
			List<ClinicalAuthDetailsResponse_LH_HUM.LettersRecords> lstLettersRecords = new List<ClinicalAuthDetailsResponse_LH_HUM.LettersRecords>();          
          
            
           
            
            objAuthReponse = oResponse.AuthorizationsResponse;
            objMemberInfo = objAuthReponse.MemberInfo;
            objMemberInfo.DateOfBirth = formateDate(objMemberInfo.DateOfBirth);
            
            lstAuthorizations = objAuthReponse.Authorizations.Authorization;
            
            for(ClinicalAuthDetailsResponse_LH_HUM.Authorization obj :lstAuthorizations){
                obj.createdDate = formateDate(obj.createdDate);
                obj.updatedDate = formateDate(obj.updatedDate);
                obj.NextReviewDate = formateDate(obj.NextReviewDate);
                obj.InpatientDetails.AdmissionDate = formateDate(obj.InpatientDetails.AdmissionDate);
                obj.InpatientDetails.LastCoveredDate = formateDate(obj.InpatientDetails.LastCoveredDate);
                obj.InpatientDetails.DischargeDate = formateDate(obj.InpatientDetails.DischargeDate);
                obj.OutpatientDetails.LastDay = formateDate(obj.OutpatientDetails.LastDay);
                obj.OutpatientDetails.FirstDay = formateDate(obj.OutpatientDetails.FirstDay);
            }
	     if(oResponse.LettersResponse != null)
            {
                lstLettersRecords = oResponse.LettersResponse.LettersRecords;
                for(ClinicalAuthDetailsResponse_LH_HUM.LettersRecords obj : lstLettersRecords)
                {
                    if(obj.CreatedDate != null) obj.CreatedDate = formatDateTime(obj.CreatedDate);
                     if(obj.LastModifiedDate != null) 
		    {
		     obj.LastModifiedDateFormated = obj.LastModifiedDate.format();
		    }
                }
            }

            if(oResponse.CommunicationRecordsResponse != null)
            {
                lstCommunicationResponse = oResponse.CommunicationRecordsResponse.CommunicationRecords;
                for(ClinicalAuthDetailsResponse_LH_HUM.CommunicationRecords obj :lstCommunicationResponse)
                {
                    if(obj.CreatedDate != null) obj.CreatedDate = formatDateTime(obj.CreatedDate);
                    if(obj.LastModifiedDate != null) 
		    {
		     obj.LastModifiedDateFormated = obj.LastModifiedDate.format();
		    }
                }
            }
			}
			else{
				return System.Label.ClinicalServiceError_HUM;
			}
            
        }catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'AuthSummaryDetail_LC_HUM', 'responseWrap');
			return System.Label.ClinicalServiceError_HUM;
        }  
        return oResponse != null ? JSON.serialize(oResponse) :'';
    }
    
    /**
*  This method is used to format the date into MM/dd/yyyy
*  @name formateDate
*  @param String - Datetime value in string format
*  @return String  - Datetime value in MM/dd/yyyy format
*  @throws NA
*/
    private static string formateDate(String sDateTime)
    {
        if(String.isBlank(sDateTime))  {
            return null;
        }
        String[] sArrayDate = (sDateTime.substringBefore('T')).split('-');
        String sDate = sArrayDate[1]+'/'+sArrayDate[2]+'/'+sArrayDate[0];
        return sDate;
    }
    
    public static HttpRequest setHttpRequest(HUM_Webservice_Callout__c oSvcClinicalAuthDetail, String sRequestBody){
        HttpRequest oRequest = new HttpRequest();
        oRequest.setMethod(oSvcClinicalAuthDetail.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oSvcClinicalAuthDetail.End_Point_URL__c);
        if(!Test.isRunningTest()){ oRequest.setClientCertificateName(oSvcClinicalAuthDetail.Certificate_Name__c); }
        oRequest.setHeader(Label.HUMServiceTesterController_content_type, oSvcClinicalAuthDetail.Content_Type__c);
        return oRequest;
    }
    
    public class RequestWrapper{
        public AuthDetailRequest AuthDetailRequest;
    }
    public class AuthDetailRequest {
        public String AuthID;
    }
	
	
	  /*
* Method Name   :  call Letters Service
* Description   :  This method is used to deserialize the response and diplay the response on Page
* Return Type   :  void
* Parameters    :  List<ClinicalAuthDetailsResponse_LH_HUM.Authorization>
*/  
    @auraEnabled(cacheable = true continuation=true)
    public static object callLettersService(string sAuthID, string sPolicyMemRecID, string sLettersPageNumber)
    {
        try
        {
            LettersRecordsRequest_LH_HUM  oLettersReq = new LettersRecordsRequest_LH_HUM();
            oLettersReq = ClinicalAuthDetails_LS_HUM.assignValuesToLettersReq(sAuthID,sPolicyMemRecID, sLettersPageNumber);              
            if(oLettersReq != null)
            {
                HUM_Webservice_Callout__c oLettersRecordsService = HUM_Webservice_Callout__c.getValues('AuthDetails_Letters');                  
                Integer iTIMEOUT_INT_SECSs = Integer.valueof(oLettersRecordsService.Timeout__c)/1000;  
                Continuation con = new Continuation(iTIMEOUT_INT_SECSs);
                con.continuationMethod = 'processLettersResponse';
                string sJsonRequest = JSON.Serialize(oLettersReq);                
                HttpRequest oRequest = new HttpRequest();
                oRequest = ClinicalAuthDetails_LS_HUM.setHttpRequestLettersRecs(oRequest,oLettersRecordsService,sJsonRequest);                
                sRequest = con.addHttpRequest(oRequest);
                return con;
            }
            else
            {
                return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimDetails_LC_HUM_V2', 'claimDetailsRequest');
            return null;
        }
    }
    
    
    
    
    /*
* Method Name   :  processLettersResponse
* Description   :  callback method for Letters records service
* Return Type   :  object
* Parameters    :  NA
*/  
    @auraEnabled(cacheable = true)
    public static Object processLettersResponse(List<string> labels, Object State) 
    {   
        HttpResponse oResponse = Continuation.getResponse(labels[0]);         
        if(oResponse != null)
        {            
			LettersRecordsResponse_LH_HUM oServiceResponse = (LettersRecordsResponse_LH_HUM)System.JSON.deserialize(oResponse.getBody(),LettersRecordsResponse_LH_HUM.class); 
            if(oServiceResponse.LettersResponse != null)
			{
                List<LettersRecordsResponse_LH_HUM.LettersRecords> lstLettersResponse = new List<LettersRecordsResponse_LH_HUM.LettersRecords>();
                lstLettersResponse = oServiceResponse.LettersResponse.LettersRecordsList.LettersRecords;
                for(LettersRecordsResponse_LH_HUM.LettersRecords obj :lstLettersResponse)
                {
                    if(obj.CreatedDate != null) obj.CreatedDate = formateDate(obj.createdDate);
                    if(obj.LastModifiedDate != null) 
                    {
                        obj.LastModifiedDateFormated = obj.LastModifiedDate.format();
                    }
                }
            }
            return JSON.serialize(oServiceResponse);            
        }
        return null;
    } 
	
	/*
	* Method Name   :  callCommunicationService
	* Description   :  This method is used to call communication records service on click of Retrieve next records button
	* Return Type   :  object
	* Parameters    :  NA
	*/ 
    @auraEnabled(cacheable = true continuation=true)
    public static object callCommunicationService(string sAuthID,string  RecId,string sCommunicationPageNumber)
    {
        try
        {
            CommunicationRecordsRequest_H_HUM  oCommReq = new CommunicationRecordsRequest_H_HUM();
            Account oAccount = AuthReferral_Query_LD_HUM.getMemberAccount(RecId);
            sEnterpriseID = oAccount.Enterprise_ID__c;
            sDateOfBirth = oAccount.Birthdate__c;
            ocommReq = ClinicalAuthDetails_LS_HUM.assignValuesToCommReq(sAuthID, sCommunicationPageNumber,sEnterpriseID,sDateOfBirth);
            if(ocommReq != null)
            {
                HUM_Webservice_Callout__c oCommRecordsService = HUM_Webservice_Callout__c.getValues('AuthDetails_Communication_Records');  
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oCommRecordsService.Timeout__c)/1000;  
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = 'processCommunicationResponse';
                string sJsonRequest = JSON.Serialize(ocommReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest = ClinicalAuthDetails_LS_HUM.setHttpRequestCommRecs(oRequest,oCommRecordsService,sJsonRequest);
                sRequest = con.addHttpRequest(oRequest);
                return con; 
            }
            else 
            {
                return null;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClinicalAuthDetails_LC_HUM', 'callCommunicationService');
            return null;
        } 
    }
	
	/*
	* Method Name   :  processCommunicationResponse
	* Description   :  callback method for communication records service
	* Return Type   :  object
	* Parameters    :  NA
	*/  
    @auraEnabled(cacheable = true)
    public static string processCommunicationResponse(List<string> labels, Object State) 
    {  
        try {
            HttpResponse oResponse = Continuation.getResponse(labels[0]);
            if(oResponse != null) 
            {
                if(String.isNotBlank(oResponse.getBody()))
				{
					CommunicationRecordsResponse_H_HUM oServiceResponse = (CommunicationRecordsResponse_H_HUM)System.JSON.deserialize(oResponse.getBody(),CommunicationRecordsResponse_H_HUM.class); 
					if(oServiceResponse.CommunicationRecordsResponse != null)
					{
						List<CommunicationRecordsResponse_H_HUM.CommunicationRecords> lstCommunicationResponse = new List<CommunicationRecordsResponse_H_HUM.CommunicationRecords>();
						lstCommunicationResponse = oServiceResponse.CommunicationRecordsResponse.CommunicationRecordsList.CommunicationRecords;
						for(CommunicationRecordsResponse_H_HUM.CommunicationRecords obj :lstCommunicationResponse)
						{
							if(obj.CreatedDate != null) obj.CreatedDate = formateDate(obj.createdDate);
							if(obj.LastModifiedDate != null) 
							{
								obj.LastModifiedDateFormated = obj.LastModifiedDate.format();
							}
						}
					}
					return JSON.serialize(oServiceResponse);
				}
                else return null;
            }
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClinicalAuthDetails_LC_HUM', 'processCommunicationResponse');
            return null;
        }
    }
	
	public static string formatDateTime(String sDateTime)

    {

        String []splitDateTime = sDateTime.split('T');

        String[] splitDates = splitDateTime[0].split('-');

        String[] splitTime = splitDateTime[1].split(':');               

        DateTime vDateTime = DateTime.newInstance(Integer.valueOf(splitDates[0]), Integer.valueOf(splitDates[1]), Integer.valueOf(splitDates[2]), Integer.valueOf(splitTime[0]), 0, 0);

        String formattedDateTime = vDateTime.formatGMT('MM/dd/YYYY hh:mm a');            

        return formattedDateTime;

    }
 
    
}