/*******************************************************************************************************************************
Apex Class Name : GroupIdCards_LS_HUM.cls
Version         : 1.0
Created On      : 12/08/2021 
Function        : Service Class for GroupIdCards_LC_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Suraj patil               212450                  12/08/2021                  Original Version  REQ - 2865657
*********************************************************************************************************************************/
public with sharing class GroupIdCards_LS_HUM 
{
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final String Content_type_HUM = 'content-type';
    Public Static final String Connection_HUM = 'Connection';
    Public Static final String Keep_alive_HUM = 'keep-alive';
    Public Static final String VOB_FAULT_CODE = 'faultcode';
    Public Static final String SOAPAction_HUM = 'SOAPAction';

    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for the service for GetGroupResponse services .
     *
     * @param   String          sGroupID
     * @return  Continuation
     */
     @AuraEnabled
    public static Continuation sendRequestGroupResponse(String sGroupID, String sRecordId)
    {
        HUM_Webservice_Callout__c oRSServiceDetails = HUM_Webservice_Callout__c.getValues('HIDCMS_GroupSearch');
        // Create continuation with a timeout
        integer iTIMEOUT_INT_SECS = Integer.valueof(oRSServiceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS); 
        con.state = sRecordId;
        con.continuationMethod = 'parseGroupResponse';   
        HttpRequest httpRSGetgroupIdCardReqObj = builtHttpRequest(oRSServiceDetails);
        httpRSGetgroupIdCardReqObj.setBody(JSON.serialize(GroupIdCards_LD_HUM.requestStructureGroupResponse(sGroupID)));
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
     @AuraEnabled
    public static list<GroupSearchDetail_LC_Wrapper> parseGroupResponse(List <string> labels, Object State)
    {   
        system.debug('inside parsing method');
        HttpResponse oHttpResponse = Continuation.getResponse(labels[0]);
        system.debug('oHttpResponse.getStatusCode()-->'+oHttpResponse.getStatusCode());
        String IdCardresponseBody;
        HIDCMSGroupSearchResponse_LDTO_HUM oResponseIdCard;
        List<GroupSearchDetail_LC_Wrapper> lstGroupSerachwrapper = new List<GroupSearchDetail_LC_Wrapper>();
        
        if (oHttpResponse != null && ((oHttpResponse.getStatusCode() == HTTP_OK_HUM || oHttpResponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!oHttpResponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
        {    
            IdCardresponseBody = oHttpResponse.getBody();
            oResponseIdCard = (HIDCMSGroupSearchResponse_LDTO_HUM) System.JSON.deserialize(IdCardresponseBody, HIDCMSGroupSearchResponse_LDTO_HUM.class);

            HIDCMSGroupSearchResponse_LDTO_HUM.GroupSearchResponseDTO oGroupSearchResponse = new HIDCMSGroupSearchResponse_LDTO_HUM.GroupSearchResponseDTO();
            oGroupSearchResponse = oResponseIdCard.GroupSearchResponse;
            List<HIDCMSGroupSearchResponse_LDTO_HUM.GroupSearchDetailDTO> objDetails = new List<HIDCMSGroupSearchResponse_LDTO_HUM.GroupSearchDetailDTO>();
            objDetails = oGroupSearchResponse.GroupSearchDetail;
            If(objDetails.Size() > 0 && objDetails != NULL)
            {
                for(HIDCMSGroupSearchResponse_LDTO_HUM.GroupSearchDetailDTO obj : objDetails)            
                {
                    GroupSearchDetail_LC_Wrapper oGroupSerachwrapper = new GroupSearchDetail_LC_Wrapper();
                    oGroupSerachwrapper.CardCount = String.IsNotBlank(obj.CardCount) ? obj.CardCount : '';
                    oGroupSerachwrapper.CardRequestDate = String.IsNotBlank(obj.CardRequestDate ) ? obj.CardRequestDate : '';
                    oGroupSerachwrapper.Platform = String.IsNotBlank(obj.Platform ) ? obj.Platform : '';
                    oGroupSerachwrapper.GroupNbr = String.IsNotBlank(obj.GroupNbr ) ? obj.GroupNbr : '';
                    lstGroupSerachwrapper.add(oGroupSerachwrapper);
                }
            }
            else return lstGroupSerachwrapper;
        }
        else
        { 
            HUMCalloutUtilityHelper.logFaultError(oHttpResponse,'GroupIdCards_LS_HUM', 'parseGroupResponse', State!= null ? String.valueof(State) : '', 'Account', 'info');
        }
        return lstGroupSerachwrapper;
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
    @AuraEnabled(Continuation=true)
    public static Continuation SendRequestGetGroupMemberResponse(String sGroupNumber, String sPlatform, String sCardReqDate, string sRecordId, String fname, String lname, String memId)
    {
        HUM_Webservice_Callout__c RSServiceDetails = HUM_Webservice_Callout__c.getValues('HIDCMS_GroupMemberLevelSearch');
        integer iTIMEOUT_INT_SECS = Integer.valueof(RSServiceDetails.Timeout__c) / 1000;
        Continuation con = new Continuation(iTIMEOUT_INT_SECS);
        con.state = sRecordId;
        con.continuationMethod = 'parseRSGetGroupMemberDetailResponse';
        HttpRequest ohttpRSGetGroupMemberDetailReq = buildGrpMemHttpRequest(RSServiceDetails);
        ohttpRSGetGroupMemberDetailReq.setBody(JSON.serialize(GroupIdCards_LD_HUM.requestStructureGetGroupMemberResponse(sGroupNumber, sPlatform, sCardReqDate, fname, lname, memId)));
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
    @AuraEnabled
    public static Object parseRSGetGroupMemberDetailResponse(list<string> labels, Object State)
    {
        try
        {
            HIDCMSGroupMemLvlSearchResp_LDTO_HUM oResponseGroupMemberLevel;
            
            HttpResponse responseGroupMemberLevel = Continuation.getResponse(labels[0]);
            String GroupMemberResponseBody;
            if (responseGroupMemberLevel != null && ((responseGroupMemberLevel.getStatusCode() == HTTP_OK_HUM || responseGroupMemberLevel.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responseGroupMemberLevel.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                GroupMemberResponseBody = responseGroupMemberLevel.getBody();
                oResponseGroupMemberLevel = (HIDCMSGroupMemLvlSearchResp_LDTO_HUM)System.JSON.deserialize(GroupMemberResponseBody, HIDCMSGroupMemLvlSearchResp_LDTO_HUM.class);
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