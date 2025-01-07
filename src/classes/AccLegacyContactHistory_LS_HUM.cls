public with sharing class AccLegacyContactHistory_LS_HUM {

/*******************************************************************************************************************************
Apex Class Name : AccLegacyContactHistory_LS_HUM.cls
Version         : 1.0
Created On      : 03/08/2021
Function        : Service Class for AccLegacyContactHistory_LC_HUM.
TestClass       : AccLegacyContactHistory_LT_HUM
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                                       03/08/2021                 Service callout and parsing response 
* Kajal Namdev                                        05/07/2021                 Replace GLOBAL_CONSTANT_HUM with GLOBAL_CONSTANT_LH_HUM class
*****************************************************************************************************************************************/
    public static List<InquirySummaryWrapper_LC_DTO_HUM> lstInquiryWrapper = new List<InquirySummaryWrapper_LC_DTO_HUM>();
    public static List<InquirySummaryWrapper_LC_DTO_HUM> listInqResp = new List<InquirySummaryWrapper_LC_DTO_HUM>();
    public static Boolean bContactDet {get;set;}
	public static Boolean bServiceError {get;set;} 
	
     /**
     * Returns HttpRequest request with request body
     * <p>
     * Method to prepare httpRequest details and certification details
     *
     * @param  HUM_Webservice_Callout__c record .
     * @return HttpRequest
     */
    public static HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        if (ServiceObj != null && ServiceObj.Active__c)
        {
            request.setMethod(ServiceObj.Request_Type__c);
            if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GLOBAL_CONSTANT_LH_HUM.Content_type_HUM, ServiceObj.Content_Type__c);
            request.setHeader(GLOBAL_CONSTANT_LH_HUM.Connection_HUM, GLOBAL_CONSTANT_LH_HUM.Keep_alive_HUM);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(GLOBAL_CONSTANT_LH_HUM.SOAPAction_HUM,'');
        }
        return request;
    }  
    
     /**
     * Returns continuation object
     * <p>
     * Method to call continuation for Inquiry Summary
     *
     * @param   String sMembergenkey, String sMemberId, Integer startCount, Integer endCount, String sStartDate, String sEndDate, String sEnterpriseID, String sRecordType
     * @return  Continuation
     */
    public static object requestInquirySummary(String sMembergenkey, String sMemberId, Integer startCount, Integer endCount, String sStartDate, String sEndDate, String sEnterpriseID, String sRecordType)
    {
        try
        {
            InquirySummaryRequest_H_HUM oInqSummaryReq = new InquirySummaryRequest_H_HUM();
            oInqSummaryReq = InquirySummary_D_HUM.requestStructureInqSummary(sMembergenkey, sMemberId,startCount,endCount,sStartDate,sEndDate,sEnterpriseID,sRecordType);
            HUM_Webservice_Callout__c inqSummaryCallout = HUM_Webservice_Callout__c.getValues('InquirySummaryService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(inqSummaryCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_LH_HUM.PARSEINQUIRYSUMMARY;
            HttpRequest httpRequestInqSummary = builtHttpRequest(inqSummaryCallout);
            string sRequest = JSON.serialize(oInqSummaryReq);
            httpRequestInqSummary.setBody(sRequest);
            con.addHttpRequest(httpRequestInqSummary);
            return con;
        }
        catch(Exception ex)
        {
            system.debug('message--'+ex.getMessage()+'--line--'+ex.getLineNumber()+'cause--'+ex.getCause());
            HUMExceptionHelper.logErrors(ex, 'InquirySummary_S_HUM', 'requestInquirySummary');
            return null;
        }
    }
    
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation for Inquiry Single Reference
     *
     * @param   String sMembergenkey, String sReferenceNbr, String sEnterpriseID, String sRecordType
     * @return  Continuation
     */
    public static object requestInquirySingleReference(String sMembergenkey, String sReferenceNbr, String sEnterpriseID, String sRecordType)
    {
        try
        {
            InquirySingleReferenceRequest_H_HUM oInqSingleRefReq = new InquirySingleReferenceRequest_H_HUM();
            oInqSingleRefReq = InquirySummary_D_HUM.requestStructureInqSingleRef(sMembergenkey, sReferenceNbr, sEnterpriseID,sRecordType);
            HUM_Webservice_Callout__c inqSingleRefCallout = HUM_Webservice_Callout__c.getValues('InquirySingleReferenceService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(inqSingleRefCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_LH_HUM.PARSEINQUIRYSUMMARY;
            HttpRequest httpRequestInqSingRef = builtHttpRequest(inqSingleRefCallout);
            string sRequest = JSON.serialize(oInqSingleRefReq);
            httpRequestInqSingRef.setBody(sRequest);
            con.addHttpRequest(httpRequestInqSingRef);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquirySummary_S_HUM', 'requestInquirySingleReference');
            return null;
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseInquirySummary Method to parse the service response
     *
     * @param  object state,List<string> labels 
     * @return String
     */
    public static Object parseInquirySummary(List<string> labels,object state)
    {
        try
        {
            HttpResponse inquiryResponse = Continuation.getResponse(labels[0]);
            if(inquiryResponse!= null && !inquiryResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.VOB_FAULT_CODE)  && ((inquiryResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK_HUM || inquiryResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                String sInquiryResponseBody = inquiryResponse.getBody();
                InquirySummaryResponse_H_HUM oResponseInquiry;
                listInqResp = new List<InquirySummaryWrapper_LC_DTO_HUM>();
                oResponseInquiry = (InquirySummaryResponse_H_HUM)System.JSON.deserialize(sInquiryResponseBody,InquirySummaryResponse_H_HUM.class);
                if(oResponseInquiry != null) 
                {
                    listInqResp = parseInquiryResponse(oResponseInquiry, oResponseInquiry.count, oResponseInquiry.FLAG_24_MONTHS);
                    return listInqResp;
                } 
                else
                {
                     return listInqResp;
                }
            } 
			else if(inquiryResponse.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.TimeOutStatusCode) {
                Error_Log__c errorLog = new Error_Log__c(Error_Message__c = GLOBAL_CONSTANT_LH_HUM.TimeoutErrorMsg, class_name__c = 'InquirySummary_S_HUM', Method_Name__c = 'parseInquirySummary', Severity__c = 'Log', Type__c = 'Integration Error',Error_Code__c = 'Customize Error');
                if (errorLog != null) insert errorLog;
                bServiceError  = true;
                return bServiceError;
            } 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquirySummary_S_HUM', 'parseInquirySummary');
        }
        return null;
    }
    
   /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   parseResponse
    *  @param  InquirySummaryResponse_H_HUM response - obbject of InquirySummaryResponse_H_HUM with response details
    *  @return List<InquirySummaryWrapper_LC_DTO_HUM> - list of wrapper class objects with the reponse values 
    *  @throws NA
    */ 
    public static List<InquirySummaryWrapper_LC_DTO_HUM> parseInquiryResponse(InquirySummaryResponse_H_HUM response, String sCount, String sFlag_24_Months)
    {   
        InquirySummaryResponse_H_HUM inquiryResponse = response;
        List<InquirySummaryResponse_H_HUM.ContactDetails> oInquirySummaryResponseList = inquiryResponse.ContactDetails;
        lstInquiryWrapper = buildWrapperList(oInquirySummaryResponseList, sCount, sFlag_24_Months);
        return lstInquiryWrapper;
    } 
    
     /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperList
    *  @param  InquirySummaryResponse_H_HUM response - obbject of InquirySummaryResponse_H_HUM with response details
    *  @return List<InquirySummaryWrapper_LC_DTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */
    public static List<InquirySummaryWrapper_LC_DTO_HUM> buildWrapperList(List<InquirySummaryResponse_H_HUM.ContactDetails> oInquirySummaryResponseList, String sCount, String sFlag_24_Months)
    {
        lstInquiryWrapper  = new List<InquirySummaryWrapper_LC_DTO_HUM>();
        List<InquirySummaryWrapper_LC_DTO_HUM> lstInquirysummary = new List<InquirySummaryWrapper_LC_DTO_HUM>();
		InquirySummaryWrapper_LC_DTO_HUM objwrapper = new InquirySummaryWrapper_LC_DTO_HUM();
        if(oInquirySummaryResponseList.size()==0 && sFlag_24_Months == 'Y' && oInquirySummaryResponseList.isEmpty())
        {
            bContactDet = false;
            objwrapper.bContactDet = bContactDet;
            lstInquirysummary.add(objwrapper);
        }
		if(oInquirySummaryResponseList.size()>0)
        {
			String sInquiredForFirstName ='';
            String sInquiredForLastName ='';
            String sInquiredAboutFirstName ='';
            String sInquiredAboutLastName ='';
			String sOwnerFirstName ='';
            String sOwnerLastName ='';
			for(InquirySummaryResponse_H_HUM.ContactDetails obj: oInquirySummaryResponseList)
			{
            InquirySummaryWrapper_LC_DTO_HUM objectwrapper = new InquirySummaryWrapper_LC_DTO_HUM();
			bContactDet = true;
            if(obj.CONTACT_ID != null) objectWrapper.sReferenceID = obj.CONTACT_ID;
            if(obj.INQUIRY_ID != null) objectWrapper.sInquiryID = obj.INQUIRY_ID;
            if(obj.INQ_TYPE_DESC != null) objectWrapper.sType = obj.INQ_TYPE_DESC;
            if(obj.INQ_TYPE_DESC != null) objectWrapper.sPolicy = obj.POLICY_NAME;
            if(obj.CATEGORY_CD_DESC != null) objectWrapper.sCategory = obj.CATEGORY_CD_DESC;
            if(obj.REASON_CD_DESC != null) objectWrapper.sReason = obj.REASON_CD_DESC;           
            if(obj.DISPOSITION_DESC != null) objectWrapper.sDisposition = obj.DISPOSITION_DESC;
            if(obj.CREATED_TIMESTAMP != null) objectWrapper.sCreatedOn = obj.CREATED_TIMESTAMP;
            if(obj.INQUIRYFORTYPE != null) objectWrapper.sInquiryType = obj.INQUIRYFORTYPE;
            if(obj.INQR_ID!= null) objectWrapper.sInquiredForID = obj.INQR_ID;
            if(obj.INQUIRYFOR_FIRSTNAME != null && obj.INQUIRYFOR_FIRSTNAME != '') sInquiredForFirstName = obj.INQUIRYFOR_FIRSTNAME;
            if(obj.INQUIRYFOR_LASTNAME!= null && obj.INQUIRYFOR_LASTNAME!= '') sInquiredForLastName = obj.INQUIRYFOR_LASTNAME;
			objectWrapper.sInquiredForName = sInquiredForFirstName + ' ' +sInquiredForLastName;
            if(obj.INQUIRYABOUTTYPE != null) objectWrapper.sInquiredAboutType = obj.INQUIRYABOUTTYPE;
            if(obj.INQA_ID != null) objectWrapper.sInquiredAboutID = obj.INQA_ID;           
            if(obj.INQUIRYABOUT_FIRSTNAME != null && obj.INQUIRYABOUT_FIRSTNAME != '') sInquiredAboutFirstName = obj.INQUIRYABOUT_FIRSTNAME;
            if(obj.INQUIRYABOUT_LASTNAME!= null && obj.INQUIRYABOUT_LASTNAME!= '') sInquiredAboutLastName = obj.INQUIRYABOUT_LASTNAME;
            objectWrapper.sInquiredAboutName = sInquiredAboutFirstName + ' ' +sInquiredAboutLastName;
            if(obj.OWNER_FIRST_NAME != null && obj.OWNER_FIRST_NAME != '') sOwnerFirstName = obj.OWNER_FIRST_NAME;
            if(obj.OWNER_LAST_NAME!= null && obj.OWNER_LAST_NAME!= '') sOwnerLastName = obj.OWNER_LAST_NAME;
            objectWrapper.sOwnedBy = sOwnerFirstName + ' ' +sOwnerLastName;
            if(obj.OWNER_TEAM_NAME != null) objectWrapper.sOwnerTeam = obj.OWNER_TEAM_NAME;
            if(obj.OWNER_DEPT_NAME != null) objectWrapper.sOwnerDept = obj.OWNER_DEPT_NAME;  
            if(obj.TASKS != null) objectWrapper.sTasks = obj.TASKS;
            if(obj.PRIORITY != null) objectWrapper.sPriority = obj.PRIORITY;
            if(obj.STATUS != null) objectWrapper.sStatus = obj.STATUS;
            if(obj.RECEIVED_TS != null) objectWrapper.sReceivedDate = obj.RECEIVED_TS ;
            if(sCount != null) objectWrapper.sCount= sCount;
            if(sFlag_24_Months != null) objectWrapper.sFlag_24_Months = sFlag_24_Months;
			objectWrapper.bContactDet = bContactDet;
            lstInquirysummary.add(objectwrapper);
			}
		}
      return lstInquirysummary;
    }
}