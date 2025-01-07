/*******************************************************************************************************************************
Apex Class Name     : SoftphoneLinkBulkAssignAgents_S_HUM 
Version             : 1.0
Created Date        : August 10 2019
Function            : This servers as service layer class for bulk assign softphone link vf page.
                
Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Joel George                                          08/10/2019                 
*******************************************************************************************************************************/
public class SoftphoneLinkBulkAssignAgents_S_HUM 
{

    public Continuation invokeSoftPhoneService(SoftphoneLinkBulkAssignAgents_C_HUM oInstance)
    {
        try
        {
            HUM_Webservice_Callout__c softphoneCallout = new HUM_Webservice_Callout__c();
            HttpRequest httpReqObj = new HttpRequest();
            
            if(oInstance.allRequest)
            {
                softphoneCallout = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.SOFTPHONE_WEBSERVICE_BULK_DB_SYNC);
                httpReqObj = SoftPhoneSync_S_HUM.builtHttpRequest(softphoneCallout);
                httpReqObj.setBody(JSON.serialize(oInstance.sfUpdateRequest));
            }
            else
            {
                softphoneCallout = HUM_Webservice_Callout__c.getValues(GLOBAL_CONSTANT_HUM.SOFTPHONE_WEBSERVICE_DB_SYNC);
                httpReqObj = SoftPhoneSync_S_HUM.builtHttpRequest(softphoneCallout);
                httpReqObj.setBody(JSON.serialize(oInstance.softphoneAddAgent));
            }

            integer iTIMEOUT_INT_SECS = Integer.valueof(softphoneCallout.Timeout__c) / 1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);      
            con.continuationMethod = GLOBAL_CONSTANT_HUM.SOFTPHONE_CALLBACK;
            con.state = oInstance.currentObj;           
            oInstance.sConRequest = con.addHttpRequest(httpReqObj);
            return con;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'SoftphoneLinkBulkAssignAgents_S_HUM', 'invokeSoftPhoneService');
            return Null;
        }
    }

  /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
    public Object parseSoftphoneTransfer(SoftphoneLinkBulkAssignAgents_C_HUM oInstance)
    {
       try
        {
            HttpResponse oHTTPResponse = Continuation.getResponse(oInstance.sConRequest);
            if(oInstance.allRequest)
            {
                map<String, Object> oResponse = new map<String, Object>();
                List<Softphone_Agent__c> lstAgentsToUpdate;

                if (oHTTPResponse != null && ((oHTTPResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || oHTTPResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) && (!oHTTPResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
                {
                    oResponse = (Map<String, Object>)JSON.deserializeUntyped(oHTTPResponse.getBody());
                }
                else
                {
                    // Fault Code
                    HUMCalloutUtilityHelper.logFaultError(oHTTPResponse,'SoftphoneLinkBulkAssignAgents_S_HUM', 'parseSoftphoneTransfer', '', 'Softphone_Agent__c', 'INFO');
                }
                if(oResponse != null)
                {
                    if(string.isNotBlank((String)oResponse.get('ErrorDesc')))
                    {
                        oInstance.ErrorMsg = (String)oResponse.get('ErrorDesc');
                        oInstance.isServiceCallFail = true;
                        oInstance.isDefaultToDefault = oInstance.isAltToAlt = oInstance.isDefaultToAlt = false;
                        return NULL;
                    }
                    else
                    {
                    oInstance.isServiceCallDone = true;

                    lstAgentsToUpdate = new List<Softphone_Agent__c>();
                 
                        if(oInstance.isDefaultToDefault || oInstance.isDefaultToAlt)
                        {

                            for(Softphone_Agent__c agent : [Select id,Name, Sites__r.Name , Default_SoftphoneLinks__c ,Alternate_SoftphoneLinks__r.Name ,  
                                    Agent_Extension__c, DB2_Status__c,Integration_Update__c from Softphone_Agent__c where 
                                    Default_SoftphoneLinks__c =: oInstance.strCurrentSoftphonLinkId ])
                            {
                                if(oInstance.isDefaultToDefault)
                                {
                                    agent.Default_SoftphoneLinks__c = oInstance.selectedSiteLink;
									agent.Integration_Update__c = true;
                                    lstAgentsToUpdate.add(agent);
                                    
                                }
                                else if(oInstance.isDefaultToAlt)
                                {
                                    agent.Alternate_SoftphoneLinks__c = oInstance.selectedSiteLink;
									agent.Integration_Update__c = true;
                                    lstAgentsToUpdate.add(agent);
                                    
                                }
                            }

                        }
                        else if(oInstance.isAltToAlt)
                        {
                            for(Softphone_Agent__c agent : [Select id,Name, Sites__r.Name , Default_SoftphoneLinks__c ,Alternate_SoftphoneLinks__r.Name ,  
                                    Agent_Extension__c, DB2_Status__c,Integration_Update__c from Softphone_Agent__c where 
                                    Alternate_SoftphoneLinks__c =: oInstance.strCurrentSoftphonLinkId ])
                            {
                                agent.Alternate_SoftphoneLinks__c = oInstance.selectedSiteLink;
								agent.Integration_Update__c = true;
                                lstAgentsToUpdate.add(agent);
                            }

                        }

                        if(!lstAgentsToUpdate.IsEmpty())
                        {       
                            update lstAgentsToUpdate;
                        }
                    }
                }
            }
            else
            {
                map<string, Softphone_Agent__c> mapAgents = new map<string, Softphone_Agent__c>();
                SoftPhoneLinkDB2Response_DTO_HUM oResponse = null;
        
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
                    Softphone_Agent__c oAgent;
                    for(Softphone_Agent__c agent: oInstance.lstSfAgent)
                    {
                        mapAgents.put(agent.User_Id__c, agent);
                    }

                    if(oResponse.insertList != null && !oResponse.insertList.IsEmpty())
                    {
                        for(SoftPhoneLinkDB2Response_DTO_HUM.softPhoneLinkDB2 oResponseAgent: oResponse.insertList)
                        {
                            oAgent = new Softphone_Agent__c();
                            oAgent = mapAgents.get(oResponseAgent.UserId);
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
                            if(oInstance.isDefaultToDefault) oAgent.Default_SoftphoneLinks__c = oInstance.selectedSiteLink ;   

                            if(oInstance.isAltToAlt || oInstance.isDefaultToAlt) oAgent.Alternate_SoftphoneLinks__c = oInstance.selectedSiteLink ;
                            mapAgents.put(oResponseAgent.UserId, oAgent);
                        }
                    }
                    
                    if(oResponse.updateList != null && !oResponse.updateList.IsEmpty())
                    {
                        for(SoftPhoneLinkDB2Response_DTO_HUM.softPhoneLinkDB2 oResponseAgent: oResponse.updateList)
                        {
                            oAgent = new Softphone_Agent__c();
                            oAgent = mapAgents.get(oResponseAgent.UserId);
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
                            if(oInstance.isDefaultToDefault)
                            oAgent.Default_SoftphoneLinks__c = oInstance.selectedSiteLink ;   

                            if(oInstance.isAltToAlt || oInstance.isDefaultToAlt)
                            oAgent.Alternate_SoftphoneLinks__c = oInstance.selectedSiteLink ;
                            mapAgents.put(oResponseAgent.UserId, oAgent); 
                        }
                    }
                    
                }

                oInstance.isServiceCallDone = true;
                List<Softphone_Agent__c> lsttoUpdate = new List<Softphone_Agent__c>();
                Softphone_Agent__c saObj = new Softphone_Agent__c();
                for (String userId : mapAgents.keySet())
                {
                    saObj = mapAgents.get(userId);
                    lsttoUpdate.add(saObj);
                }
                if(!lsttoUpdate.IsEmpty()) update lsttoUpdate;
            }
            return NULL;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'SoftphoneLinkBulkAssignAgents_S_HUM', 'parseSoftphoneTransfer');
            return null;
        }
    }   


}