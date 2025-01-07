/*******************************************************************************************************************************
Apex Class Name : GroupIdCards_S_HUM.cls
Version         : 1.0
Created On      : 6th Mar 2018
Function        : Service Class for GroupIdCards_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Melkisan Selvaraj          212450               03/06/2018                  Original Version
* Shreya Choodamani			 220815	              04/26/2018                  Added Code for Group Member Level Details
* Prerna Prasad				 243782	   			  06/14/2018				  REQ - 366904
*********************************************************************************************************************************/
public with sharing class GroupIdCards_S_HUM 
{
	Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String Content_type_HUM = 'content-type';
    Public Static final String Connection_HUM = 'Connection';
    Public Static final String Keep_alive_HUM = 'keep-alive';
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final String SOAPAction_HUM = 'SOAPAction';
    Public Static final String GROUPIDCARDMGT_HIDCMSGROUPMEMBERLEVELSEARCH_HUM = 'HIDCMS_GroupMemberLevelSearch';
    Public Static final String GROUPMEMBERDETAILS_PARSERSGROUPMEMBERDETAILRESPONSE_HUM = 'parseRSGetGroupMemberDetailResponse';
    Public Static final String GROUPIDCARDMGT_HIDCMSGROUPSEARCH_HUM = 'HIDCMS_GroupSearch';
    Public Static final String GROUPIDCARDMGT_PARSEGROUPRESPONSE_HUM = 'parseGroupResponse';
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for the service for GetGroupResponse services .
     *
     * @param   String          sGroupID
     * @return  Continuation
     */
    public static Continuation sendRequestGroupResponse(String sGroupID, String sRecordId)
    {
        HUM_Webservice_Callout__c oRSServiceDetails = HUM_Webservice_Callout__c.getValues(GROUPIDCARDMGT_HIDCMSGROUPSEARCH_HUM);
        // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(oRSServiceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS); 
        con.state = sRecordId;
        con.continuationMethod = GROUPIDCARDMGT_PARSEGROUPRESPONSE_HUM;   
        HttpRequest httpRSGetgroupIdCardReqObj = builtHttpRequest(oRSServiceDetails);
        httpRSGetgroupIdCardReqObj.setBody(JSON.serialize(GroupIdCards_D_HUM.requestStructureGroupResponse(sGroupID)));
        con.addHttpRequest(httpRSGetgroupIdCardReqObj);
        return con;
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * parseRSGetMemberCreditResponse Method to parse the service response
     *
     * @param  list<string> lstLabels .
     * @param  object oState
     * @return Object
     */
    public static Object parseGroupResponse(list<string> lstLabels, Object oState)
    {
        HttpResponse oHttpResponse = Continuation.getResponse(lstLabels[0]);
        String IdCardresponseBody;
        HIDCMSGroupSearchResponse_DTO_HUM oResponseIdCard;
        if (oHttpResponse != null && ((oHttpResponse.getStatusCode() == HTTP_OK_HUM || oHttpResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!oHttpResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
        {
            IdCardresponseBody = oHttpResponse.getBody();
            oResponseIdCard = (HIDCMSGroupSearchResponse_DTO_HUM) System.JSON.deserialize(IdCardresponseBody, HIDCMSGroupSearchResponse_DTO_HUM.class);
        }
        else
        {
        	HUMCalloutUtilityHelper.logFaultError(oHttpResponse,'GroupIdCards_S_HUM', 'parseGroupResponse', oState != null ? String.valueof(oState) : '', 'Account', 'info');
        }
        return oResponseIdCard;
    }
           
    /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c oServiceObj)
    {
        HttpRequest request = new HttpRequest();
        if (oServiceObj != null && oServiceObj.Active__c)
        {
            request.setMethod(oServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(oServiceObj.Certificate_Name__c)) request.setClientCertificateName(oServiceObj.Certificate_Name__c);
            if (String.isNotBlank(oServiceObj.Content_Type__c)) request.setHeader(Content_type_HUM, oServiceObj.Content_Type__c);
            request.setHeader(Connection_HUM, Keep_alive_HUM);
            request.setEndpoint(oServiceObj.End_Point_URL__c);
            request.setHeader(SOAPAction_HUM,'');
        }
        return request;
    } 
    
	/**
	* Returns continuation object
	* <p>
	* Method to call continuation serive for the serive for GetMemberResponse services .
	*
	* @param   String          GroupNumber
	* @param   String          Platform
	* @param   String          CardReqDate
	* @return  Continuation
	*/
	public static Continuation SendRequestGetGroupMemberResponse(String sGroupNumber, String sPlatform, String sCardReqDate, string sRecordId, String fname, String lname, String memId, String depCode)
	{
		HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues(GROUPIDCARDMGT_HIDCMSGROUPMEMBERLEVELSEARCH_HUM);
		integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
		Continuation con = new Continuation(iTIMEOUT_INT_SECS);
		con.state = sRecordId;
		con.continuationMethod = GROUPMEMBERDETAILS_PARSERSGROUPMEMBERDETAILRESPONSE_HUM;
		HttpRequest ohttpRSGetGroupMemberDetailReq = buildGrpMemHttpRequest(RSServiceDetails);
		ohttpRSGetGroupMemberDetailReq.setBody(JSON.serialize(GroupIdCards_D_HUM.requestStructureGetGroupMemberResponse(sGroupNumber, sPlatform, sCardReqDate,fname,lname,memId, depCode)));
		con.addHttpRequest(ohttpRSGetGroupMemberDetailReq);
		return con;
	}

	/**
	* Returns responses of continuation object
	* <p>
	* parseRSGetGroupIdCardResponse Method to parse the service response
	*
	* @param  object state,List<string> labels .
	* @return Object
	*/
	public static Object parseRSGetGroupMemberDetailResponse(list<string> labels, Object State)
	{
		try
		{
			HIDCMSGroupMemLvlSearchResponse_DTO_HUM oResponseGroupMemberLevel;
			HttpResponse responseGroupMemberLevel = Continuation.getResponse(labels[0]);
			String GroupMemberResponseBody;
			if (responseGroupMemberLevel != null && ((responseGroupMemberLevel.getStatusCode() == HTTP_OK_HUM || responseGroupMemberLevel.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responseGroupMemberLevel.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
			{
				GroupMemberResponseBody = responseGroupMemberLevel.getBody();
				oResponseGroupMemberLevel = (HIDCMSGroupMemLvlSearchResponse_DTO_HUM)System.JSON.deserialize(GroupMemberResponseBody, HIDCMSGroupMemLvlSearchResponse_DTO_HUM.class);
			}
			return oResponseGroupMemberLevel;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'GroupIdCard_S_HUM', 'parseRSGetGroupIdCardResponse');
			return null;
		}
	}

	/**
	* Returns HttpRequest request with request body
	* <p>
	* Method to prepare httpRequest details and certification details
	*
	* @param  HUM_Webservice_Callout__c record .
	* @return HttpRequest
	*/
	public static HttpRequest buildGrpMemHttpRequest(HUM_Webservice_Callout__c ServiceObj)
	{
		HttpRequest request = new HttpRequest();
		if (ServiceObj != null && ServiceObj.Active__c)
		{
			request.setMethod(ServiceObj.Request_Type__c);
			if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
			if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(Content_type_HUM, ServiceObj.Content_Type__c);
			request.setHeader(Connection_HUM, Keep_alive_HUM);
			request.setEndpoint(ServiceObj.End_Point_URL__c);
			request.setHeader(SOAPAction_HUM, '');
		}
		return request;
	}
}