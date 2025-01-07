/*******************************************************************************************************************************
Apex Class Name     : SoftPhoneSync_C_HUM 
Version             : 1.0
Created Date        : August 10 2019
Function            : This service layer class for SoftPhoneSync_VF_HUM.
                
Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Joel George                                          08/10/2019                 
*******************************************************************************************************************************/
public with sharing class SoftPhoneSync_S_HUM 
{
    
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
        try
        {
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(HUMConstants.HUM_PCP_SER_CONTENTTYPE, ServiceObj.Content_Type__c);
                request.setHeader(HUMConstants.HUM_PCP_SER_CONECTION, HUMConstants.HUM_PCP_SER_KEEPALIVE);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(HUMConstants.HUM_PCP_SER_SOAPACTION, '');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'SoftPhoneSync_S_HUM', 'builtHttpRequest');
            return Null;
        }
    }
        
    /**
	* Returns continuation object
	* <p>
	* Method to call continuation service for the Softphone.
	*
	* @param oInstance              SoftPhoneSync_C_HUM class instance
	* @return                                continuation
	*/
    public Continuation invokeSoftPhoneService(SoftPhoneSync_C_HUM oInstance)
    {
        try
        {
            HUM_Webservice_Callout__c softphoneCallout = HUM_Webservice_Callout__c.getValues('AgentSoftPhoneDBSync');            
            // Create continuation with a timeout
            integer iTIMEOUT_INT_SECS = Integer.valueof(softphoneCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);            
            con.continuationMethod = GLOBAL_CONSTANT_HUM.SOFTPHONE_PARSE_METHOD;    
            HttpRequest httpReqObj = builtHttpRequest(softphoneCallout);
            string sReq = JSON.serialize(oInstance.softphoneAddAgent);
            httpReqObj.setBody(sReq);            
            oInstance.sConRequest = con.addHttpRequest(httpReqObj);
            return con;            
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'SoftPhoneSync_S_HUM', 'invokeSoftPhoneService');
            return Null;
        }
    }
    
    /**
	* Returns null but sets the values of responses to UI
	* <p>
	* Callback method to process the service Softphone
	*
	* @param oInstance          SoftPhoneSync_C_HUM class instance
	* @return                                   NULL
	*/
    public Object parseSoftPhoneService(SoftPhoneSync_C_HUM oInstance)
    {
        try
        {
            map<string, Softphone_Agent__c> mapAgents = new map<string, Softphone_Agent__c>();
            list<Softphone_Agent__c> lstDelAgents = new list<Softphone_Agent__c>() ;
			SoftPhoneLinkDB2Response_DTO_HUM oResponse = null;
            HttpResponse oHTTPResponse = Continuation.getResponse(oInstance.sConRequest);
            if (oHTTPResponse != null && ((oHTTPResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || oHTTPResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) && (!oHTTPResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
	        {
	            oResponse = (SoftPhoneLinkDB2Response_DTO_HUM)System.JSON.deserialize(oHTTPResponse.getBody(),SoftPhoneLinkDB2Response_DTO_HUM.class); 
	        }
			else
			{
				// Fault Code
				HUMCalloutUtilityHelper.logFaultError(oHTTPResponse,'SoftPhoneSync_S_HUM', 'parseSoftPhoneService', '', 'Softphone_Agent__c', 'INFO');
			}
            
            if(oResponse != null)
            {
            	for(Softphone_Agent__c oAgent: oInstance.lstAgents){
            		mapAgents.put(oAgent.User_Id__c, oAgent);
            	}
            	
            	if(oResponse.insertList != null && !oResponse.insertList.IsEmpty())
            	{
            		for(SoftPhoneLinkDB2Response_DTO_HUM.softPhoneLinkDB2 oResponseAgent: oResponse.insertList){
	            		Softphone_Agent__c oAgent = mapAgents.get(oResponseAgent.UserId);
	            		if(string.isBlank(oResponseAgent.ErrorDesc))
	            		{
	            			oAgent.DB2_Status__c =  GLOBAL_CONSTANT_HUM.SOFTPHONE_SYNCED;
	            			oAgent.DB2_Error_Message__c = '';
	            			oAgent.DB2_Sync__c = true;
	            		}
	            		else
	            		{
	            			oAgent.DB2_Status__c = GLOBAL_CONSTANT_HUM.SOFTPHONE_INSERT_FAILED;
	            			oAgent.DB2_Error_Message__c = oResponseAgent.ErrorDesc;
	            		}
	            		oAgent.Integration_Update__c = true;
	            		mapAgents.put(oResponseAgent.UserId, oAgent);
	            	}
            	}
            	
            	if(oResponse.updateList != null && !oResponse.updateList.IsEmpty())
            	{
            		for(SoftPhoneLinkDB2Response_DTO_HUM.softPhoneLinkDB2 oResponseAgent: oResponse.updateList){
	            		Softphone_Agent__c oAgent = mapAgents.get(oResponseAgent.UserId);
	            		if(string.isBlank(oResponseAgent.ErrorDesc))
	            		{
	            			oAgent.DB2_Status__c =  GLOBAL_CONSTANT_HUM.SOFTPHONE_SYNCED;
	            			oAgent.DB2_Error_Message__c = '';
	            		}
	            		else
	            		{
	            			oAgent.DB2_Status__c = GLOBAL_CONSTANT_HUM.SOFTPHONE_UPDATE_FAILED;
	            			oAgent.DB2_Error_Message__c = oResponseAgent.ErrorDesc;
	            		}
	            		oAgent.Integration_Update__c = true;
	            		mapAgents.put(oResponseAgent.UserId, oAgent); 
	            	}
            	}
            	
            	if(oResponse.deleteList != null && !oResponse.deleteList.IsEmpty())
            	{
            		for(SoftPhoneLinkDB2Response_DTO_HUM.softPhoneLinkDB2 oResponseAgent: oResponse.deleteList){
	            		Softphone_Agent__c oAgent = mapAgents.get(oResponseAgent.UserId);
	            		
	            		if(!oAgent.DB2_Sync__c || string.isBlank(oResponseAgent.ErrorDesc)) 
	            		{
	            			lstDelAgents.add(oAgent);	
	            		}
	            		else
	            		{
	            			oAgent.DB2_Status__c = GLOBAL_CONSTANT_HUM.SOFTPHONE_DELETE_FAILED;
	            			oAgent.DB2_Error_Message__c = oResponseAgent.ErrorDesc;	
	            		}
	            		oAgent.Integration_Update__c = true;
	            		mapAgents.put(oResponseAgent.UserId, oAgent); 
	            	}
            	}
            }
            
            Database.SaveResult[] lstResults = Database.update(mapAgents.values(),false);
            HUMExceptionHelper.processSaveResults(lstResults, mapAgents.values(), 'SoftPhoneSync_S_HUM','parseSoftPhoneService','Softphone_Agent__c');
            
            if(!lstDelAgents.isEmpty())
            {
                if (!Softphone_Agent__c.sObjectType.getDescribe().isDeletable())
                {
                    return NULL;
                }
                delete lstDelAgents;
            }           
            
            return NULL;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'SoftPhoneSync_S_HUM', 'parseSoftPhoneService');
            return null;
        }
    }
}