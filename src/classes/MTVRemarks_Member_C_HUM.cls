/*******************************************************************************************************************************
Apex Class Name : Public class MTVRemarks_Member_C_HUM
Version         : 1.0
Created On      : 04/05/2019   
Function        : This class is used to get data from  MTV member service.
Test Class      : MTVRemarks_Member_T_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Suraj patil		                                  06/01/2019                  Original version
*******************************************************************************************************************************************************************/
public with sharing class MTVRemarks_Member_C_HUM
{
    public static MTVRemarks_Member_S_HUM oMTVRemarksMembers = new MTVRemarks_Member_S_HUM(); 
    public static String sRequest  = '';
    public String sPolicyMemberName{ get; set; }
    
	/*
	Method Name : initiateMTVRequest
	Functionality : initiateMTVRequest is to call service for getting remarks from 'GetMTVRemarks' service.
	Return type : continuation object.
	*/
    @RemoteAction
    public static continuation initiateMTVRequest(String idPolicyMember)
    {
        try
        {
            MTVRemarksServiceGetMemberRequest_H_HUM oMTVRemarksReq = new MTVRemarksServiceGetMemberRequest_H_HUM();            
            oMTVRemarksReq = oMTVRemarksMembers.BuildRequestBody(idPolicyMember);            
            if(oMTVRemarksReq != null)
            {
                HUM_Webservice_Callout__c oSvcHeaderClaimMTVRemarks = HUM_Webservice_Callout__c.getValues('GetMTVRemarks'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimMTVRemarks.Timeout__c)/1000;
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.continuationMethod = 'processMTVResponse';
                String sJsonRequest = JSON.Serialize(oMTVRemarksReq);
                HttpRequest oRequest = new HttpRequest();
                oRequest = oMTVRemarksMembers.setupRequest(oRequest,oSvcHeaderClaimMTVRemarks,sjsonRequest);
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
            HUMExceptionHelper.logErrors(ex, 'MTVRemarks_Member_C_HUM', 'initiateMTVRequest');
            return NULL;
        }
    }
    
	/*
	Method Name : processMTVResponse
	Functionality : processMTVResponse is to parse reponse from 'GetMTVRemarks' service.
	Return type : list of MTVMembers_Remarks_DTO_HUM.
	*/
    public static object processMTVResponse(List < String > labels, Object State)
    {   
        return MTVRemarks_Member_S_HUM.processResponse(labels, State);
    } 
}