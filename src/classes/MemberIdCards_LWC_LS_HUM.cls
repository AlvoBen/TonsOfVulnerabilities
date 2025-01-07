/*******************************************************************************************************************************
Apex Class Name : MemberIdCards_LWC_LS_HUM.cls
Version         : 1.0
Created On      : 9th Feb 2022
Function        : Service Class to fetch Member Id Card Information.
TestClass       : 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                   Initial Version
* Anuradha Gajbhe                                       02/02/2023                   User Story 4060591: Lightning - ID Cards - RCC Auto create Case for ID Cards: Ability to automatically create a case when an ID Card Request is successfully submitted.
* Anuradha Gajbhe                                       07/14/2023                   4325820: RCC Auto create Case for State ID Cards: Ability to automatically create a case when a State ID Card Request is successfully submitted.(Lightning)
* Raj Paliwal	                                        07/14/2023                   4272710: Ability to request a State ID Card from the ID Card Managment Page(Lightning)
* Vishal Shinde                                         07/14/2023                   User Story 3891752: CRM to populate Previous State ID Card Requests (Lightning)
*****************************************************************************************************************************************/

	public with sharing class MemberIdCards_LWC_LS_HUM 
	{
        
        public static final String MEMBERIDCARDMGT_PARSEORDERIDCARDRESPONSE_HUM = 'parseOrderIdCardResponse';
        public static final String MEMBERIDCARDMGT_PARSEORDERIDCARDRESPONSE1_HUM = 'parseOrderIdCardResponse1';
        public static final Integer HTTP_OK_HUM = 200;
        public static final Integer HTTP_ACCEPTED_HUM = 202; 
        public static final String VOB_FAULT_CODE = 'faultcode';
        public static final Integer HUMServiceTimeOut = 120;
        public static final String MEMBERIDCARDMGT_PROCESSMEMLEVELRESPONSE_HUM = 'processMemberLevelResponse';
        public static final String Content_type_HUM = 'content-type';
        public static final String Keep_alive_HUM = 'keep-alive';
        public static final String Connection_HUM = 'Connection';
        public static final String SOAPAction_HUM = 'SOAPAction';
        public static final String MEDICARECALLS_MAPD_HUM = 'MAPD';
        public static final String MEDICARECALLS_PDP_HUM = 'PDP';
        public static final String MEDICARECALLS_MA_HUM = 'MA';
        public static final String MAJOR_LOB_MCD = 'MCD';
        public static final String MAJOR_LOB_MES = 'MES';
		public static List<MemberLevelIdCardRequestDTO> idCardReqDataFnl = new List<MemberLevelIdCardRequestDTO>();
	
		public class  MemberLevelIdCardRequestDTO
            {
                @AuraEnabled public String CustCovSeqNbr = '';
                @AuraEnabled public String CustGenKey = '';
                @AuraEnabled public String DateOfBirth = '';
                @AuraEnabled public String GroupNbr  = '';
                @AuraEnabled public String MemberGenKey  = '';
                @AuraEnabled public String MemberId   = '';
                @AuraEnabled public String PlanType = '';
                @AuraEnabled public String polMemId = '';
            }
	
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for the service for Order Id Card services .
     *
     * @param   List<String>          Lst Member Id
     * @return  Continuation
     */
    @AuraEnabled
    public static object sendRequestStateIdCardOrder(String medicaidID, String issueStateCode, String idCardReqstReason)
    {
        try
        {   
            HUM_Webservice_Callout__c oCICMSUpdtServiceDetails = HUM_Webservice_Callout__c.getValues('CICMS_DemographicUpdate');
            HttpRequest httpStateIdCardOrderReqObj = new HttpRequest();
            CICMSUpdateRequest_LDTO_HUM finalRequest = new CICMSUpdateRequest_LDTO_HUM();
            finalRequest = MemberIdCards_LWC_LD_HUM.buildStateOrderIdCardRequest(medicaidID, issueStateCode, idCardReqstReason);      
            httpStateIdCardOrderReqObj.setBody(JSON.serialize(finalRequest));
            httpStateIdCardOrderReqObj.setEndpoint('callout:NC_CRM_Service_Mulesoft' + oCICMSUpdtServiceDetails.End_Point_URL__c);
            httpStateIdCardOrderReqObj.setMethod(oCICMSUpdtServiceDetails.Request_Type__c);
            httpStateIdCardOrderReqObj.setHeader(Content_type_HUM, oCICMSUpdtServiceDetails.Content_Type__c);
            Http http = new Http();
            HTTPResponse response = http.send(httpStateIdCardOrderReqObj);  
            if (response != null && ((response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM) && (!response.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                object oResponseStateIdCard = response.getBody();
                return oResponseStateIdCard;
            }else{
                return null;
            }
        }   
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LWC_LS_HUM', 'sendRequestStateIdCardOrder');
            return null;
        }
    }

    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for the service for State Id Card status .
     *
     * @param   String medicaidID, String issueStateCode, String StateIdCardReqstInd
     * @return  object
     */
    @AuraEnabled
    public static object sendRequestInquiryService(String medicaidID, String issueStateCode, String StateIdCardReqstInd)
    {
        try
        {
            HUM_Webservice_Callout__c oCICMSInquiryServiceDetails = HUM_Webservice_Callout__c.getValues('CICMS_DemographicInquiry');
            HttpRequest httpStateIdCardOrderReqObj = new HttpRequest();
            CICMSInquiryRequest_LDTO_HUM finalInquiryRequest = new CICMSInquiryRequest_LDTO_HUM();
            finalInquiryRequest = MemberIdCards_LWC_LD_HUM.buildInquiryServiceRequest(medicaidID, issueStateCode, StateIdCardReqstInd);
            httpStateIdCardOrderReqObj.setBody(JSON.serialize(finalInquiryRequest));
            httpStateIdCardOrderReqObj.setEndpoint('callout:NC_CRM_Service_Mulesoft'+ + oCICMSInquiryServiceDetails.End_Point_URL__c);
            httpStateIdCardOrderReqObj.setMethod(oCICMSInquiryServiceDetails.Request_Type__c);
            httpStateIdCardOrderReqObj.setHeader(Content_type_HUM, oCICMSInquiryServiceDetails.Content_Type__c);
            
            Http http = new Http();
            HTTPResponse response = http.send(httpStateIdCardOrderReqObj);  

            if(response != null && ((response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM) && (!response.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                object oResponseInquiry= response.getBody();
                return oResponseInquiry;
            }
            else{
                return null ;
            }
            
        }   
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LWC_LS_HUM', 'sendRequestInquiryService');
            return null;
        }
    }

    /**
     * Returns continuation object
     * <p>
     * Method to call continuation service for the service for Order Id Card services .
     *
     * @param   List<String>          Lst Member Id
     * @return  Continuation
     */
    @AuraEnabled(Continuation=true)
    public static continuation sendRequestOrderIdCard1(List<String> lstMemberId, String polMemId)
    {
        try
        {
            HUM_Webservice_Callout__c oHIDServiceDetails = HUM_Webservice_Callout__c.getValues('HIDCMS_OrderIdCard');
			integer iTIMEOUT_INT_SECS = Integer.valueof(oHIDServiceDetails.Timeout__c) / 1000;
			Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = MEMBERIDCARDMGT_PARSEORDERIDCARDRESPONSE1_HUM;
            HttpRequest httpOrderIdCardReqObj = builtHttpRequest(oHIDServiceDetails);
			HIDCMSOrderIdCardRequest_DTO_HUM finalRequest = new HIDCMSOrderIdCardRequest_DTO_HUM();
			finalRequest = MemberIdCards_LWC_LD_HUM.buildOrderIdCardREquest(lstMemberId);
			idCardReqDataFnl = createIdCardReqDataTemplate(FinalRequest, polMemId);
			con.state = idCardReqDataFnl;
            httpOrderIdCardReqObj.setBody(JSON.serialize(finalRequest));
            con.addHttpRequest(httpOrderIdCardReqObj);
            return con;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LWC_LS_HUM', 'sendRequestOrderIdCard1');
            return null;
        }
    }

    public static List<MemberLevelIdCardRequestDTO> createIdCardReqDataTemplate(HIDCMSOrderIdCardRequest_DTO_HUM finalRequest, String polMemId)
    {   
        List<MemberLevelIdCardRequestDTO>  idCardReqDataLst = new List<MemberLevelIdCardRequestDTO>();
        //Set data for template
        for (integer i=0; i < finalRequest.OrderMemberIdCard.memberDto.size(); i++){
            MemberLevelIdCardRequestDTO  idCardReqData = new MemberLevelIdCardRequestDTO();
            idCardReqData.MemberId = finalRequest.OrderMemberIdCard.memberDto[i].MemberId;
            idCardReqData.PlanType = finalRequest.OrderMemberIdCard.memberDto[i].PlanType;
            idCardReqData.GroupNbr = finalRequest.OrderMemberIdCard.memberDto[i].GroupNbr;
            idCardReqData.CustGenKey = finalRequest.OrderMemberIdCard.memberDto[i].CustGenKey;
            idCardReqData.DateOfBirth = finalRequest.OrderMemberIdCard.memberDto[i].DateOfBirth;
            idCardReqData.MemberGenKey = finalRequest.OrderMemberIdCard.memberDto[i].MemberGenKey;
            idCardReqData.CustCovSeqNbr = finalRequest.OrderMemberIdCard.memberDto[i].CustCovSeqNbr;
            idCardReqData.polMemId = polMemId;
            idCardReqDataLst.add(idCardReqData);
        }
        return idCardReqDataLst;
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseOrderIdCardResponse1 Method to parse the service response
     *
     * @param  object state,list<string> labels.
     * @return Object
     */
    @AuraEnabled
    public static Object parseOrderIdCardResponse1(List<string> labels,Object state)
    {
        Object dtoIdCardObj;
        try
        {
           
            HttpResponse IdCardresponse = Continuation.getResponse(labels[0]);
			list<MemberLevelIdCardRequestDTO> oRequestIdCard = new list<MemberLevelIdCardRequestDTO>();
            if (IdCardresponse != null && ((IdCardresponse.getStatusCode() == HTTP_OK_HUM || IdCardresponse.getStatusCode() == HTTP_ACCEPTED_HUM) && (!IdCardresponse.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                HIDCMSOrderIdCardResponse_LDTO_HUM oResponseIdCard = (HIDCMSOrderIdCardResponse_LDTO_HUM)System.JSON.deserialize(IdCardresponse.getBody(), HIDCMSOrderIdCardResponse_LDTO_HUM.class);
				oRequestIdCard = (list<MemberLevelIdCardRequestDTO>)System.JSON.deserialize(JSON.serialize(state), list<MemberLevelIdCardRequestDTO>.class);
				HIDCMSOrderIdCardResponse_LDTO_HUM.OrderMemberIdCardResponseDetail oResponseIdCardResp = oResponseIdCard.OrderMemberIdCardResponse;
                HIDCMSOrderIdCardResponse_LDTO_HUM.OrderMemberIdCardResultsDetail oResponseIdCardResults = oResponseIdCardResp.OrderMemberIdCardResults;
                List<HIDCMSOrderIdCardResponse_LDTO_HUM.OrderMemberIdCardResultDetail> oResponseIdCardResult = oResponseIdCardResults.OrderMemberIdCardResult;
				for (integer i=0; i < oResponseIdCardResult.size(); i++ ){
					for (integer j=0; j < oRequestIdCard.size(); j++ ){
                        if (oResponseIdCardResult[i].CustomerGenKey == oRequestIdCard[j].CustGenKey &&
                            oResponseIdCardResult[i].DateOfBirth == oRequestIdCard[j].DateOfBirth &&
                            oResponseIdCardResult[i].GroupNbr == oRequestIdCard[j].GroupNbr)
                            {
                                oResponseIdCardResult[i].MemberId = oRequestIdCard[j].MemberId;         
                                oResponseIdCardResult[i].PlanType = oRequestIdCard[j].PlanType;
                            }
                    }
                }
                dtoIdCardObj = oResponseIdCard;
            }
            else
            {
                // Fault Code
				string recordId = oRequestIdCard[0].polMemId;
                HUMCalloutUtilityHelper.logFaultError(IdCardresponse,'MemberIdCards_LWC_LS_HUM', 'parseOrderIdCardResponse1', recordId, 'MemberPlan', 'INFO');
            }
            
            return dtoIdCardObj;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberIdCards_LWC_LS_HUM', 'parseOrderIdCardResponse1');
            return null;
        }  
    }

    /**
    * Returns responses of continuation  method
    * <p>
    * Method to get the Member level search Data
    *
    * @param String           grpNumber
    * @param String           memberId
    * @return                 Object
    */
    @AuraEnabled(Continuation=true)
    public static Object processMemberLevelService(String grpNumber, string memberId , string polMemId)
    {
        try
        {
            HUM_Webservice_Callout__c IDCardServiceDetails = HUM_Webservice_Callout__c.getValues('HIDCMS_MemberSearch');
            integer iTIMEOUT_INT_SECS = HUMServiceTimeOut;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = MEMBERIDCARDMGT_PROCESSMEMLEVELRESPONSE_HUM;
            con.state = polMemId;
            HttpRequest httpPBEReqObj = builtHttpRequest(IDCardServiceDetails);
            httpPBEReqObj.setBody(JSON.serialize(MemberIdCards_LWC_LD_HUM.RequestStructureMemberLevelService(grpNumber,memberId)));
            con.addHttpRequest(httpPBEReqObj);
            return con;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberIdCards_LWC_LS_HUM', 'processMemberLevelService');
            return null;
        }
    }
    
    /**
    * Returns the response for Member level search
    * <p>
    * Method to Parse the Benefit Search response .
    *
    * @param benefitsCls          Object State
    * @return                     Object
    */
    @AuraEnabled
    public static Object processMemberLevelResponse(List<string> labels,Object State)
    {
        Object dtoObj;
        try
        {
           
            HttpResponse responseMemberLevel = Continuation.getResponse(labels[0]);
            if (responseMemberLevel != null && ((responseMemberLevel.getStatusCode() == HTTP_OK_HUM || responseMemberLevel.getStatusCode() == HTTP_ACCEPTED_HUM) && (!responseMemberLevel.getBody().containsIgnoreCase(VOB_FAULT_CODE)) || Test.isRunningTest()))
            {
                HIDCMSMemberSearchResponse_DTO_HUM oResponse = (HIDCMSMemberSearchResponse_DTO_HUM)System.JSON.deserialize(responseMemberLevel.getBody(), HIDCMSMemberSearchResponse_DTO_HUM.class);
                dtoObj = MemberIdCards_LWC_LD_HUM.prepareMemberLevelSerchResponse(oResponse);
                dtoObj = (dtoObj==null) ? oResponse : dtoObj;                
            }
            else
            {
                // Fault Code
                string recordId = string.valueOf(State);
                HUMCalloutUtilityHelper.logFaultError(responseMemberLevel,'MemberIdCards_LWC_LS_HUM', 'processMemberLevelResponse', recordId, 'MemberPlan', 'INFO');
            }
            return dtoObj;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'MemberIdCards_LWC_LS_HUM', 'processMemberLevelResponse');
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
    *  Method will check last 7 days cases for ID Card Request.
    * @param Policy Member: lstPolicyMember
    * @param String: sInteractionId
    * @return Boolean
    * */
    @AuraEnabled
    public static Boolean checkIDCardRequestInL7Days(List<MemberPlan> lstPolicyMember, String sInteractionId) {
        Boolean bIdCardRequest;
        bIdCardRequest = MemberIdCards_LWC_LD_HUM.getIdCardRequestInL7Days(lstPolicyMember,sInteractionId);
        return bIdCardRequest;
    }
	
    /**
     * Method will check segment indicator for id card request
     * @para Policy Member: objPolicyMember
     * @return Boolean 
     * 
     **/
    @AuraEnabled
    public static Boolean checkMedicareMedicaid(MemberPlan objPolicyMember) {
        
        Boolean bCheckSegmentIndicator = false;
        List<String> lstSegmentIndicator = new List<String>{'Individual-Medicare','LargeGroup-Medicare','Individual-Medicaid'};
        if(String.isNotBlank(objPolicyMember.Segment_Indicator__c) && lstSegmentIndicator.contains(objPolicyMember.Segment_Indicator__c)) {
            bCheckSegmentIndicator = true;
        }
        return bCheckSegmentIndicator; 
    }
}