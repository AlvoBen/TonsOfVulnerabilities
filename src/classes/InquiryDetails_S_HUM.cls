/*******************************************************************************************************************************
Apex Class Name : InquiryDetails_S_HUM.cls
Version         : 1.0
Created On      : 08/27/2020
Function        : Service Class for InquiryDetails_C_HUM.
TestClass       : InquiryDetails_T_HUM
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							          09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
* Riya Agarwal							              09/07/2020                 REQ - 1338123 - PR00090723 - MF 3 - CCP GCP Archived Inquiry Details Page - Task List Section  
* Sayali Nemade										  10/01/2020                 DF - 1718 Corrected Task Details Date Mapping
*****************************************************************************************************************************************/

public with sharing class InquiryDetails_S_HUM
{
    //List For Inquiry Notes
    public static List<InquiryNotesWrapper_DTO_HUM> lstInquiryNotesWrapper = new List<InquiryNotesWrapper_DTO_HUM>();
    public static List<InquiryNotesWrapper_DTO_HUM> listInqNotesResp = new List<InquiryNotesWrapper_DTO_HUM>();
    //List For Inquiry Audit
    public static List<InquiryDetAuditWrapper_DTO_HUM> lstInquiryWrapper = new List<InquiryDetAuditWrapper_DTO_HUM>();
    public static List<InquiryDetAuditWrapper_DTO_HUM> listInqResp = new List<InquiryDetAuditWrapper_DTO_HUM>();
    //List for Inquiry Attachment
    public static List<InquiryDetAttachWrapper_DTO_HUM> lstInquiryAttachWrapper = new List<InquiryDetAttachWrapper_DTO_HUM>();
	public static List<InquiryDetAttachWrapper_DTO_HUM> listAttachResp = new List<InquiryDetAttachWrapper_DTO_HUM>();
    //List for Task Details
    public static List<InquiryTaskDetailsWrapper_DTO_HUM> listInqTaskDet = new List<InquiryTaskDetailsWrapper_DTO_HUM>();
	 
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
            if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GLOBAL_CONSTANT_HUM.Content_type_HUM, ServiceObj.Content_Type__c);
            request.setHeader(GLOBAL_CONSTANT_HUM.Connection_HUM, GLOBAL_CONSTANT_HUM.Keep_alive_HUM);
            request.setEndpoint(ServiceObj.End_Point_URL__c);
            request.setHeader(GLOBAL_CONSTANT_HUM.SOAPAction_HUM,'');
        }
        return request;
    }  
    
     /**
     * Returns continuation object
     * <p>
     * Method to call continuation for Inquiry Details
     *
     * @param   String sContactId, String sInquiryId
     * @return  Continuation
     */
    public static object requestInquiryDetails(String sContactId, String sInquiryId)
    {
        try
        {
            InquiryDetailsRequest_H_HUM oInqDetailsReq = new InquiryDetailsRequest_H_HUM();
            oInqDetailsReq = InquiryDetails_D_HUM.requestInquiryDetails(sContactId, sInquiryId);
            HUM_Webservice_Callout__c inqDetailsCallout = HUM_Webservice_Callout__c.getValues('InquiryDetailsService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(inqDetailsCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_HUM.PARSEINQUIRYDETAILS;
            HttpRequest httpRequestInqDetails = builtHttpRequest(inqDetailsCallout);
            string sRequest = JSON.serialize(oInqDetailsReq);
            httpRequestInqDetails.setBody(sRequest);
            con.addHttpRequest(httpRequestInqDetails);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'requestInquiryDetails');
            return null;
        }
    }
    
    public static object requestInquiryNotes(String sContactId, String sInquiryId)
    {
        try
        {
            InquiryNotesRequest_H_HUM oInqNotesReq = new InquiryNotesRequest_H_HUM();
            oInqNotesReq = InquiryDetails_D_HUM.requestInquiryNotes(sContactId, sInquiryId);
            HUM_Webservice_Callout__c inqNotesCallout = HUM_Webservice_Callout__c.getValues('InquiryNotesService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(inqNotesCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_HUM.PARSEINQUIRYNOTES;
            HttpRequest httpRequestInqNotes = builtHttpRequest(inqNotesCallout);
            string sRequest = JSON.serialize(oInqNotesReq);
            httpRequestInqNotes.setBody(sRequest);
            con.addHttpRequest(httpRequestInqNotes);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'requestInquiryNotes');
            return null;
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseInquiryDetails Method to parse the service response
     *
     * @param  object state,List<string> labels 
     * @return String
     */
    public static Object parseInquiryDetails(List<string> labels,object state)
    {
        try
        {
            HttpResponse inquiryDetailsResponse = Continuation.getResponse(labels[0]);
            if(inquiryDetailsResponse!= null && !inquiryDetailsResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)  && ((inquiryDetailsResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || inquiryDetailsResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                String sInquiryDetailsResponseBody = inquiryDetailsResponse.getBody();
                InquiryDetailsResponse_H_HUM oResponseInquiryDetails;
                oResponseInquiryDetails = (InquiryDetailsResponse_H_HUM)System.JSON.deserialize(sInquiryDetailsResponseBody,InquiryDetailsResponse_H_HUM.class);
                return oResponseInquiryDetails;
            } 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'parseInquiryDetails');
        }
        return null;
    }
    
	/**
     * Returns responses of continuation object
     * <p>
     * parseInquiryNotes Method to parse the service response
     *
     * @param  object state,List<string> labels 
     * @return String
     */
    public static Object parseInquiryNotes(List<string> labels,object state)
    {
        try
        {
            HttpResponse inquiryNotesResponse = Continuation.getResponse(labels[0]);
            if(inquiryNotesResponse!= null && !inquiryNotesResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)  && ((inquiryNotesResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || inquiryNotesResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                String sInquiryNotesResponseBody = inquiryNotesResponse.getBody();
                InquiryNotesResponse_H_HUM oResponseInquiryNotes;
                listInqNotesResp = new List<InquiryNotesWrapper_DTO_HUM>();
                oResponseInquiryNotes = (InquiryNotesResponse_H_HUM)System.JSON.deserialize(sInquiryNotesResponseBody,InquiryNotesResponse_H_HUM.class);
                if(oResponseInquiryNotes != null && oResponseInquiryNotes.DRNotes != null && !oResponseInquiryNotes.DRNotes.isEmpty()) {
                    listInqNotesResp = parseInquiryNotesResponse(oResponseInquiryNotes);
                } 
                return listInqNotesResp;
            } 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'parseInquiryNotes');
        }
        return null;
    }
    
   /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   parseResponse
    *  @param  InquiryNotesResponse_H_HUM response - obbject of InquiryNotesResponse_H_HUM with response details
    *  @return List<InquiryNotesWrapper_DTO_HUM> - list of wrapper class objects with the reponse values 
    *  @throws NA
    */ 
    public static List<InquiryNotesWrapper_DTO_HUM> parseInquiryNotesResponse(InquiryNotesResponse_H_HUM response)
    {   
        InquiryNotesResponse_H_HUM inquiryNotesResponse = response;
        List<InquiryNotesResponse_H_HUM.DRNotes> oInquiryNotesResponseList = inquiryNotesResponse.DRNotes;
        lstInquiryNotesWrapper = buildWrapperList(oInquiryNotesResponseList);
        return lstInquiryNotesWrapper;
    } 
    
     /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperList
    *  @param  InquiryNotesResponse_H_HUM response - obbject of InquiryNotesResponse_H_HUM with response details
    *  @return List<InquiryNotesWrapper_DTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */
     public static List<InquiryNotesWrapper_DTO_HUM> buildWrapperList(List<InquiryNotesResponse_H_HUM.DRNotes> oInquiryNotesResponseList)
    {
        lstInquiryNotesWrapper  = new List<InquiryNotesWrapper_DTO_HUM>();
        List<InquiryNotesWrapper_DTO_HUM> lstInquiryNotes = new List<InquiryNotesWrapper_DTO_HUM>();
        for(InquiryNotesResponse_H_HUM.DRNotes obj: oInquiryNotesResponseList)
        {
            InquiryNotesWrapper_DTO_HUM objectwrapper = new InquiryNotesWrapper_DTO_HUM();
			if(obj.Type != '' && obj.Type != null) {
				if(obj.NOTE_DESC != null) objectWrapper.sNotes = obj.NOTE_DESC;
				if(obj.Type != null) objectWrapper.sType  = obj.Type;
				lstInquiryNotes.add(objectwrapper);
			}
        }
      return lstInquiryNotes;
    }
    
      /**
     * Returns continuation object
     * <p>
     * Method to call continuation for Inquiry Audit Details
     *
     * @param   String sContactId, String sInquiryId
     * @return  Continuation
     */
    public static object requestInquiryAudit(String sContactId, String sInquiryId )
    {
        try
        {
            InquiryDetAuditRequest_H_HUM oInqAuditReq = new InquiryDetAuditRequest_H_HUM();
            oInqAuditReq = InquiryDetails_D_HUM.requestInquiryAudit(sContactId, sInquiryId);
            HUM_Webservice_Callout__c inqAuditCallout = HUM_Webservice_Callout__c.getValues('InquiryAuditTrailService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(inqAuditCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_HUM.PARSEINQUIRYDETAILAUDIT;
            HttpRequest httpRequestInqAudit = builtHttpRequest(inqAuditCallout);
            string sRequest = JSON.serialize(oInqAuditReq );
            httpRequestInqAudit.setBody(sRequest);
            con.addHttpRequest(httpRequestInqAudit);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'requestInquiryAudit');
            return null;
        }
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * parseInquiryAudit Method to parse the service response
     *
     * @param  object state,List<string> labels 
     * @return String
     */
    public static Object parseInquiryAudit(List<string> labels,object state)
    {
        try
        {
            HttpResponse inquiryAuditResponse = Continuation.getResponse(labels[0]);
            if(inquiryAuditResponse != null && !inquiryAuditResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)  && ((inquiryAuditResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || inquiryAuditResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                String sInquiryResponseBody = inquiryAuditResponse.getBody();
                InquiryDetAuditResponse_H_HUM oResponseInquiry;
                listInqResp = new List<InquiryDetAuditWrapper_DTO_HUM>();
                oResponseInquiry = (InquiryDetAuditResponse_H_HUM)System.JSON.deserialize(sInquiryResponseBody,InquiryDetAuditResponse_H_HUM.class);
               if(oResponseInquiry != null) 
                {
                    listInqResp = parseInquiryAuditResponse(oResponseInquiry);
                }
				return listInqResp;
            } 
         }
        
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'parseInquiryAudit');
        }
        return null;
    }
    /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   parseResponse
    *  @param  InquiryDetAuditResponse_H_HUM response - obbject of InquiryDetAuditResponse_H_HUM  with response details
    *  @return List<InquiryDetAuditWrapper_DTO_HUM> - list of wrapper class objects with the reponse values 
    *  @throws NA
    */ 
    public static List<InquiryDetAuditWrapper_DTO_HUM> parseInquiryAuditResponse(InquiryDetAuditResponse_H_HUM response)
    {   
        InquiryDetAuditResponse_H_HUM inquiryAuditResponse = response;
        List<InquiryDetAuditResponse_H_HUM.DRAuditDetails> oInquiryAuditResponseList= inquiryAuditResponse.DRAuditDetails;
        lstInquiryWrapper = buildWrapperAuditList(oInquiryAuditResponseList);
        return lstInquiryWrapper;
    } 
    
     /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildWrapperAuditList
    *  @param  InquiryDetAuditResponse_H_HUM response - obbject of InquiryDetAuditResponse_H_HUM with response details
    *  @return List<InquiryDetAuditWrapper_DTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */
     public static List<InquiryDetAuditWrapper_DTO_HUM> buildWrapperAuditList(List<InquiryDetAuditResponse_H_HUM.DRAuditDetails> oInquiryAuditResponseList)
    {
        lstInquiryWrapper  = new List<InquiryDetAuditWrapper_DTO_HUM>();
        List<InquiryDetAuditWrapper_DTO_HUM> lstInquiryAudit = new List<InquiryDetAuditWrapper_DTO_HUM>();
        String sFirstName='';
        String sLastName ='';
        for(InquiryDetAuditResponse_H_HUM.DRAuditDetails obj: oInquiryAuditResponseList)
        {
            InquiryDetAuditWrapper_DTO_HUM objectwrapper = new InquiryDetAuditWrapper_DTO_HUM();
            if(obj.ACTION_TYPE_DESC != null) objectWrapper.sAttachTypeDesc = obj.ACTION_TYPE_DESC ;
            if(obj.Modified_On != null) objectWrapper.sModifiedOn = obj.Modified_On ;           
            if(obj.PREVIOUS_DESC!= null) objectWrapper.sPREVIOUSDESC = obj.PREVIOUS_DESC;
            if(obj.CURRENT_DESC != null) objectWrapper.sCURRENTDESC = obj.CURRENT_DESC ;
            if(obj.CREATED_FIRST_NAME != null && obj.CREATED_FIRST_NAME!= '') sFirstName = obj.CREATED_FIRST_NAME;
            if(obj.CREATED_LAST_NAME != null && obj.CREATED_LAST_NAME != '') sLastName = obj.CREATED_LAST_NAME;
            objectWrapper.sModifiedBy = sFirstName + ' ' +sLastName ;  
			lstInquiryAudit.add(objectwrapper);
        }
      return lstInquiryAudit ;
    }
	
     /**
     * Returns continuation object
     * <p>
     * Method to call continuation for Inquiry Web Response
     *
     * @param   String sContactId, String sInquiryId
     * @return  Continuation
     */
    public static object requestInquiryWebResponse(String sContactId, String sInquiryId)
    {
        try
        {
            InquiryWebResponseRequest_H_HUM oInqWebReq = new InquiryWebResponseRequest_H_HUM ();
            oInqWebReq = InquiryDetails_D_HUM.requestInquiryWebResponse(sContactId, sInquiryId);
            HUM_Webservice_Callout__c inqWebCallout = HUM_Webservice_Callout__c.getValues('InquiryWebResponseService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(inqWebCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_HUM.PARSEINQWEBRESPONSE ;
            HttpRequest httpRequestWebRes = builtHttpRequest(inqWebCallout);
            string sRequest = JSON.serialize(oInqWebReq );
            httpRequestWebRes.setBody(sRequest);
            con.addHttpRequest(httpRequestWebRes );
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'requestInquiryWebResponse');
            return null;
        }
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * parseInquiryWebResponse Method to parse the service response
     *
     * @param  object state,List<string> labels 
     * @return String
     */
    public static Object parseInquiryWebResponse(List<string> labels,object state)
    {
        try
        {
            HttpResponse inquiryWebResponse = Continuation.getResponse(labels[0]);
            if(inquiryWebResponse != null && !inquiryWebResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)  && ((inquiryWebResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || inquiryWebResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
				String sInquiryResponseBody = inquiryWebResponse.getBody();
                InquiryWebResponse_Response_H_HUM oResponseInquiry;              
                oResponseInquiry = (InquiryWebResponse_Response_H_HUM)System.JSON.deserialize(sInquiryResponseBody,InquiryWebResponse_Response_H_HUM.class);
                InquiryWebResponse_Response_H_HUM.DRWebResponse objWebRes = new InquiryWebResponse_Response_H_HUM.DRWebResponse();
                if(oResponseInquiry != null) 
                {
                    objWebRes= oResponseInquiry.DRWebResponse;                                        
                } 
				return objWebRes;
            } 
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'parseInquiryWebResponse');
        }
        return null;
    } 
	
    /**
     * Returns continuation object
     * <p>
     * Method to call continuation for Inquiry Details Attach
     *
     * @param   String sContactId, String sInquiryId
     * @return  Continuation
     */
    public static object requestInquiryAttachment(String sContactId, String sInquiryId)
    {
        try
        {
            InquiryDetAttachRequest_H_HUM oInqAttachReq = new InquiryDetAttachRequest_H_HUM();
            oInqAttachReq = InquiryDetails_D_HUM.requestInquiryAttachment(sContactId, sInquiryId);
            HUM_Webservice_Callout__c inqAttachCallout = HUM_Webservice_Callout__c.getValues('InquiryDetailAttachmentService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(inqAttachCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_HUM.PARSEINQUIRYDETAILATTACHMENT;
            HttpRequest httpRequestInqAttach = builtHttpRequest(inqAttachCallout);
            string sRequest = JSON.serialize(oInqAttachReq );
            httpRequestInqAttach.setBody(sRequest);
            con.addHttpRequest(httpRequestInqAttach );
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'requestInquiryAttachment');
            return null;
        }
    }
    
     /**
     * Returns responses of continuation object
     * <p>
     * parseInquiryAttachment Method to parse the service response
     *
     * @param  object state,List<String> labels 
     * @return String
     */
    public static Object parseInquiryAttachment(List<String> labels,object state)
    {
        try
        {
            HttpResponse inquiryAttachResponse = Continuation.getResponse(labels[0]);
            if(inquiryAttachResponse != null && !inquiryAttachResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)  && ((inquiryAttachResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || inquiryAttachResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                String sAttachResponseBody = inquiryAttachResponse.getBody();
                InquiryDetAttachResponse_H_HUM oResponseAttach;
                listAttachResp = new List<InquiryDetAttachWrapper_DTO_HUM>();
                oResponseAttach= (InquiryDetAttachResponse_H_HUM )System.JSON.deserialize(sAttachResponseBody ,InquiryDetAttachResponse_H_HUM.class);
                if(oResponseAttach!= null) 
                {
                    listAttachResp = parseInquiryAttachResponse(oResponseAttach);
                }
				return listAttachResp;
            } 
         }
        
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'parseInquiryAttachment');
        }
        return null;
    }
	
    /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   parseResponse
    *  @param  InquiryDetAttachResponse_H_HUM response - obbject of InquiryDetAttachResponse_H_HUMresponse with response details
    *  @return List<InquiryDetAttachWrapper_DTO_HUM> - list of wrapper class objects with the reponse values 
    *  @throws NA
    */ 
    public static List<InquiryDetAttachWrapper_DTO_HUM> parseInquiryAttachResponse(InquiryDetAttachResponse_H_HUM response)
    {   
        InquiryDetAttachResponse_H_HUM inquiryAttachResponse = response;
        List<InquiryDetAttachResponse_H_HUM.DRAttachmentDetails> oInquiryAttachResponseList = inquiryAttachResponse.DRAttachmentDetails;
        lstInquiryAttachWrapper = buildAttachWrapperList(oInquiryAttachResponseList);
        return lstInquiryAttachWrapper ;
    } 
    
     /*
    *  This method is used to assign the response to variables in wrapper Class
    *  @name   buildAttachWrapperList
    *  @param  InquiryDetAttachResponse_H_HUMresponse - obbject of InquiryDetAttachResponse_H_HUMresponse with response details
    *  @return List<InquiryDetAttachWrapper_DTO_HUM> - list of wrapper class objects with the reponse values stored in it
    *  @throws NA
    */
     public static List<InquiryDetAttachWrapper_DTO_HUM> buildAttachWrapperList(List<InquiryDetAttachResponse_H_HUM.DRAttachmentDetails> oInquiryAttachResponseList)
    {
        lstInquiryAttachWrapper = new List<InquiryDetAttachWrapper_DTO_HUM>();
        List<InquiryDetAttachWrapper_DTO_HUM> lstInquiryAttach = new List<InquiryDetAttachWrapper_DTO_HUM>();
        List<InquiryDetAttachResponse_H_HUM.ATTACH_DESC> lstAttachDesc = new List<InquiryDetAttachResponse_H_HUM.ATTACH_DESC>();
        
        for(InquiryDetAttachResponse_H_HUM.DRAttachmentDetails objAttach: oInquiryAttachResponseList)
        {
            InquiryDetAttachWrapper_DTO_HUM objectwrapper = new InquiryDetAttachWrapper_DTO_HUM();
            if(objAttach.ATTACH_TYPE_DESC != null) objectWrapper.sAttachTypeDesc = objAttach.ATTACH_TYPE_DESC;
            if(objAttach.ATTACH_TYPE != null) objectWrapper.sAttachType = objAttach.ATTACH_TYPE;
            if(objAttach.Doc_Id!= null) objectWrapper.sDocId = objAttach.Doc_Id;           
            if(objAttach.Version_String != null) objectWrapper.sVersionNumber = objAttach.Version_String ;                        
            if(objAttach.ATTACH_DESC!= null) objectwrapper.lstAttachDesc = objAttach.ATTACH_DESC;          
            lstInquiryAttach.add(objectwrapper);
        }
      return lstInquiryAttach ;
    }
	
	/**
     * Returns continuation object
     * <p>
     * Method to call continuation for Inquiry Task Details 
     *
     * @param   String MemberGenKey and Integer Reference Number
     * @return  Continuation
     */
    public static object requestInquiryTaskDetails(String sContactId, String sInquiryId)
    {
        try
        {
            InquiryTaskDetailsRequest_H_HUM oInqTaskDetReq = new InquiryTaskDetailsRequest_H_HUM ();
            oInqTaskDetReq = InquiryDetails_D_HUM.requestInquiryTaskDetails(sContactId, sInquiryId);
            HUM_Webservice_Callout__c inqTaskDetCallout = HUM_Webservice_Callout__c.getValues('InquiryDetailsTaskService');
            integer iTIMEOUT_INT_SECS = Integer.valueof(inqTaskDetCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = GLOBAL_CONSTANT_HUM.PARSEINQTASKDETAILS;
            HttpRequest httpRequestInqSingRef = builtHttpRequest(inqTaskDetCallout);
            string sRequest = JSON.serialize(oInqTaskDetReq);
            httpRequestInqSingRef.setBody(sRequest);
            con.addHttpRequest(httpRequestInqSingRef);
            return con;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'requestInquiryTaskDetails');
            return null;
        }
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * parseInqTask Method to parse the service response
     *
     * @param  object state,List<string> labels 
     * @return String
     */
    public static Object parseInquiryTaskDetails(List<string> labels,object state)
    {	
		String createdFirstName='';
        String createdLastName='';
        String createdBy = '';
        String createdTeamName = '';
        String createdDeptName = '';
        String createdSiteName = '';
        String ownerFirstName = '';
        String ownerLastName = '';
        try
        {
            HttpResponse inquiryTaskDetResponse = Continuation.getResponse(labels[0]);
            if(inquiryTaskDetResponse != null && !inquiryTaskDetResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)  && ((inquiryTaskDetResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || inquiryTaskDetResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
            String sInquiryTaskDetResponseBody = inquiryTaskDetResponse.getBody();
                InquiryTaskDetailsResponse_H_HUM oResponseInquiryTaskDet;
                listInqTaskDet = new List<InquiryTaskDetailsWrapper_DTO_HUM>();
                oResponseInquiryTaskDet = (InquiryTaskDetailsResponse_H_HUM)System.JSON.deserialize(sInquiryTaskDetResponseBody,InquiryTaskDetailsResponse_H_HUM.class);
                if(oResponseInquiryTaskDet!= null && oResponseInquiryTaskDet.TaskDetails!= null && !oResponseInquiryTaskDet.TaskDetails.isEmpty()) 
                {
                    List<InquiryTaskDetailsResponse_H_HUM.TaskDetails> lstTaskDetails = oResponseInquiryTaskDet.TaskDetails;
                    for(InquiryTaskDetailsResponse_H_HUM.TaskDetails objTaskList :lstTaskDetails)
                    {
                        InquiryTaskDetailsWrapper_DTO_HUM objectWrapper = new InquiryTaskDetailsWrapper_DTO_HUM();
                        if(objTaskList.TASK_ID != null) objectWrapper.taskID = objTaskList.TASK_ID;
                        if(objTaskList.CREATED_TS != null) objectWrapper.sCreatedOn = objTaskList.CREATED_TS;
						if(objTaskList.ACTION_DATE != null) objectWrapper.sDate = objTaskList.ACTION_DATE;
                        if(objTaskList.AGE != null) objectWrapper.sAge = objTaskList.AGE;
                        if(objTaskList.ACTION_CD_DESC != null) objectWrapper.sActionDesc = objTaskList.ACTION_CD_DESC;
                        if(objTaskList.RESULT_CD_DESC != null) objectWrapper.sResultDesc = objTaskList.RESULT_CD_DESC;
                        if(objTaskList.SHORT_DESC != null) objectWrapper.sShortDesc = objTaskList.SHORT_DESC;           
                        if(objTaskList.STATUS_CD != null) objectWrapper.sStatus = objTaskList.STATUS_CD;
                        if(objTaskList.DUE_DATE == GLOBAL_CONSTANT_HUM.DUEDATEFORMATONE ||objTaskList.DUE_DATE == GLOBAL_CONSTANT_HUM.DUEDATEFORMATTWO ||objTaskList.DUE_DATE == GLOBAL_CONSTANT_HUM.DUEDATEFORMATTHREE ||objTaskList.DUE_DATE == GLOBAL_CONSTANT_HUM.DUEDATEFORMATFOUR)
                        {
                        objectWrapper.sDueDate ='';
                        }
                        else if(objTaskList.DUE_DATE != null){
                        objectWrapper.sDueDate = objTaskList.DUE_DATE;
                        }
                        if(objTaskList.CLOSED_TS != null) objectWrapper.sDateClosed = objTaskList.CLOSED_TS;
                        //mapping left for created by
                        if(objTaskList.CREATED_FIRST_NAME != null && objTaskList.CREATED_FIRST_NAME != '') createdFirstName = objTaskList.CREATED_FIRST_NAME;
                        if(objTaskList.CREATED_LAST_NAME != null && objTaskList.CREATED_LAST_NAME != '')  createdLastName =  objTaskList.CREATED_LAST_NAME;
                        if(objTaskList.CREATED_BY != null && objTaskList.CREATED_BY != '') createdBy = objTaskList.CREATED_BY;
                        if(objTaskList.CREATED_TEAM_NAME != null && objTaskList.CREATED_TEAM_NAME != '') createdTeamName = objTaskList.CREATED_TEAM_NAME;
                        if(objTaskList.CREATED_DEPT_LGNM != null && objTaskList.CREATED_DEPT_LGNM != '') createdDeptName = objTaskList.CREATED_DEPT_LGNM;
                        if(objTaskList.CREATED_SITE_LGNM != null && objTaskList.CREATED_SITE_LGNM != '') createdSiteName = objTaskList.CREATED_SITE_LGNM;
                        objectWrapper.sCreatedBy = createdLastName + ', '+ createdFirstName +'('+ createdBy +')/'+ createdTeamName +'/'+ createdDeptName +'/'+ createdSiteName;
                        if(objTaskList.ACTION_TIME != null) objectWrapper.sActionTime = objTaskList.ACTION_TIME;
                        if(objTaskList.OWNER_SITE_LGNM != null) objectWrapper.sSrvCtr = objTaskList.OWNER_SITE_LGNM;
                        if(objTaskList.PRIORITY_CD_DESC != null) objectWrapper.sPriorityDesc = objTaskList.PRIORITY_CD_DESC;
                        if(objTaskList.OWNER_DEPT_LGNM != null) objectWrapper.sDept = objTaskList.OWNER_DEPT_LGNM;
                        if(objTaskList.TASK_TYPE_DESC != null) objectWrapper.sTaskTypeDesc = objTaskList.TASK_TYPE_DESC;
                        if(objTaskList.OWNER_TEAM_NAME != null) objectWrapper.sTeam = objTaskList.OWNER_TEAM_NAME;
                        //mapping left for user
						if(objTaskList.OWNER_FIRST_NAME != null && objTaskList.OWNER_FIRST_NAME != '') ownerFirstName = objTaskList.OWNER_FIRST_NAME;
                        if(objTaskList.OWNER_LAST_NAME != null && objTaskList.OWNER_LAST_NAME != '') ownerLastName = objTaskList.OWNER_LAST_NAME;
                        objectWrapper.sUser = ownerFirstName +' '+ ownerLastName;

                        if(objTaskList.NotesDetails !=null) objectWrapper.NoteDetails = objTaskList.NotesDetails;
                        if(objTaskList.AuditDetails !=null) objectWrapper.AuditDetails = objTaskList.AuditDetails;
                        if(objTaskList.AttachmentDetails !=null) objectWrapper.AttachmentDetails = objTaskList.AttachmentDetails;
                       

                    listInqTaskDet.add(objectWrapper);
                   }
                   
                } 
				return listInqTaskDet;
                
           } 
         }
        
        catch(Exception ex)
        {
            System.debug('Exception'+ex);
            HUMExceptionHelper.logErrors(ex, 'InquiryDetails_S_HUM', 'parseInquiryTaskDetails');
        }
        return null;
    }	
}