/*******************************************************************************************************************************
Apex Class Name     : SoftPhoneSyncQueueable_H_HUM
Version             : 1.0
Created Date        : June 12 2020
Function            : Helper class of Queueable class to process sync softphone agents records
Test Class 			: SoftPhoneSyncQueueable_T_HUM

Modification Log: 
* Developer Name           Code Reveiw                     Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Akshay Pai                                    		06/12/2020                  Initial Version
* Anil Kumar Korada          2150839                   04/08/2021                    MF6 - Security - Checkmarx Medium Defects
*******************************************************************************************************************************/
public with sharing class SoftPhoneSyncQueueable_H_HUM
{	
	private SoftPhoneSyncQueueable_S_HUM oInstance;
	public static final string DB2_SYNC_FAILED = 'DB2 Sync Failed. Please manually sync this record.';
	public static final string DB2_SYNC_UPSERT_FAILED = ' DB2 Sync failed records upsert failed ,Errors - ';
	public static final string NEW_AGENT_UPSERT_FAILED = 'New agents upsert failed.Errors - ';
	public static final Integer LIMIT_FOR_NEXT_CALL = 100;
	public static final string NA = 'N/A';
		
	public SoftPhoneSyncQueueable_H_HUM(SoftPhoneSyncQueueable_S_HUM oInstance)
    {
        this.oInstance = oInstance;
    }

	/*
    * Method Name : init
    * Description : Method for initial check and start the transaction
    * Return type : None
    * Parameter   : None
    */
    public void init(){        
        if (oInstance.agentsToSync != null && oInstance.agentsToSync.size() > 0)
			sendSyncStatus();
        else 
			getAgentDetails();
    }
    
	/*
    * Method Name : sendSyncStatus
    * Description : Method that sends newly created Softphone records data to sync in DP
    * Return type : None
    * Parameter   : None
    */
    private void sendSyncStatus(){
		
		try{
			map<string, Softphone_Agent__c> mapAgents = new map<string, Softphone_Agent__c>();
			SoftPhoneLinkDB2Request_DTO_HUM softphoneRequest  = ProcessAgentRecordsSync(oInstance.agentsToSync);
			SoftPhoneLinkDB2Response_DTO_HUM oResponse = null;
			
			HUM_Webservice_Callout__c softphoneCallout = HUM_Webservice_Callout__c.getValues('AgentSoftPhoneDBSync');
			Http http = new Http();			                           
            HttpRequest httpReqObj = builtHttpRequest(softphoneCallout);
			string sReq = JSON.serialize(softphoneRequest);
            httpReqObj.setBody(sReq);
			HttpResponse oHTTPResponse = http.send(httpReqObj);		
			
			if (oHTTPResponse != null && ((oHTTPResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || oHTTPResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) && (!oHTTPResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
	        {
	            oResponse = (SoftPhoneLinkDB2Response_DTO_HUM)System.JSON.deserialize(oHTTPResponse.getBody(),SoftPhoneLinkDB2Response_DTO_HUM.class);
	        }
			else
			{
				// Fault Code
				HUMCalloutUtilityHelper.logFaultError(oHTTPResponse,'SoftPhoneSyncQueueable_S_HUM', 'SoftPhoneSyncQueueable_S_HUM.sendSyncStatus', '', 'Softphone_Agent__c', 'INFO');
			}
			            
			if(oResponse != null && oResponse.syncList != null && !oResponse.syncList.IsEmpty())
            {
            	for(Softphone_Agent__c oAgent: oInstance.agentsToSync){
            		mapAgents.put(oAgent.User_Id__c, oAgent);
            	}
				
				for(SoftPhoneLinkDB2Response_DTO_HUM.softPhoneLinkDB2 oResponseAgent: oResponse.syncList){
					
					if(string.isNotBlank(oResponseAgent.ErrorDesc))
					{
						Softphone_Agent__c oAgent = mapAgents.get(oResponseAgent.UserId);
						oAgent.DB2_Error_Message__c = oResponseAgent.ErrorDesc;
						oAgent.Integration_Update__c = true;
						oInstance.agentsToUpdateSet.add(oAgent);
						oInstance.agentsFailedToInsertSet.add(oAgent);
					}					
				}
			}
			else{
				for(Softphone_Agent__c oAgent: oInstance.agentsToSync){
					oAgent.DB2_Error_Message__c = DB2_SYNC_FAILED;
					oAgent.Integration_Update__c = true;
					oInstance.agentsToUpdateSet.add(oAgent);
					oInstance.agentsFailedToInsertSet.add(oAgent);
            	}
			}
			
			if(oInstance.totalNoOfAgents < LIMIT_FOR_NEXT_CALL  || oInstance.max == oInstance.counter){
				updateFinalSetOfAgents();
			}
			else{
				getAgentDetails();
			}
		}
		catch (Exception e)
        {
			HUMExceptionHelper.logErrors(e, 'SoftPhoneSyncQueueable_S_HUM', 'SoftPhoneSyncQueueable_H_HUM.sendSyncStatus');
        }
    }
    
	/*
    * Method Name : getAgentDetails
    * Description : Method that fetches new Agents records from DP and save in Salesforce
    * Return type : None
    * Parameter   : None
    */
    private void getAgentDetails(){
		
		try{
			Http http = new Http();
			HUM_Webservice_Callout__c softphoneCallout = HUM_Webservice_Callout__c.getValues('AgentSoftPhoneGetDBAgents');                           
            HttpRequest httpReqObj = builtHttpRequest(softphoneCallout);
			string sReq = '{'+
							'"SyncInd":"N"'+
						  '}';
            httpReqObj.setBody(sReq);
			HttpResponse oHTTPResponse = http.send(httpReqObj);
			
			SoftPhoneLinkDPResponse_DTO_HUM oResponse = NEW SoftPhoneLinkDPResponse_DTO_HUM();
			if (oHTTPResponse != null && ((oHTTPResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || oHTTPResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM) && (!oHTTPResponse.getBody().containsIgnoreCase(GLOBAL_CONSTANT_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
	        {
	            oResponse = (SoftPhoneLinkDPResponse_DTO_HUM)System.JSON.deserialize(oHTTPResponse.getBody(),SoftPhoneLinkDPResponse_DTO_HUM.class); 
	        }
			else
			{
				// Fault Code
				HUMCalloutUtilityHelper.logFaultError(oHTTPResponse,'SoftPhoneSyncQueueable_S_HUM', 'SoftPhoneSyncQueueable_H_HUM.getAgentDetails', '', 'Softphone_Agent__c', 'INFO');
			}
			
			if(oResponse != null && oResponse.RecordsInDB != null && oResponse.RecordsInDB.size() > 0 )
			{
				List<Softphone_Agent__c> agentsToInsert = new List<Softphone_Agent__c>();
				List<Softphone_Agent__c> agentsInserted = new List<Softphone_Agent__c>();
				Set<string> siteNamesSet = new Set<string>();
				Set<string> linkNamesSet = new Set<string>();
				List<SoftphoneLinks__c> softPhoneLinksList = new List<SoftphoneLinks__c>();
				List<Softphone_Sites__c> softPhoneSitesList = new List<Softphone_Sites__c>();
				Map<string,id> softPhoneLinksMap = new Map<string,id>();
				Map<string,id> softPhonesitesMap = new Map<string,id>();
				
				for (SoftPhoneLinkDPResponse_DTO_HUM.unsyncedAgentDP DBRec : oResponse.RecordsInDB){
					
					if (String.isNotBlank(DBRec.SiteName))
					siteNamesSet.add(DBRec.SiteName.trim());
				
					if (String.isNotBlank(DBRec.DefaultSoftphoneLinkName))
					linkNamesSet.add(DBRec.DefaultSoftphoneLinkName.trim());
				
					if (String.isNotBlank(DBRec.AlternateSoftphoneLinkName))
					linkNamesSet.add(DBRec.AlternateSoftphoneLinkName.trim());
				}
				
				if (linkNamesSet.size() > 0){
					softPhoneLinksList = [select id,name,ServerName__c from SoftphoneLinks__c where name in:linkNamesSet ];
					if (softPhoneLinksList.size() > 0){
						for (SoftphoneLinks__c link : softPhoneLinksList){
							softPhoneLinksMap.put(link.name,link.id);
							
							if (!oInstance.agentsLinkIdNameMap.containsKey(link.id))
							oInstance.agentsLinkIdNameMap.put(link.id,link.name);
						
							if (!oInstance.agentsLinkIdServerNameMap.containsKey(link.id))
							oInstance.agentsLinkIdServerNameMap.put(link.id,link.ServerName__c);
						}
					}
				}
				if (siteNamesSet.size() > 0){
					softPhoneSitesList = [select id,name from Softphone_Sites__c where name in:siteNamesSet ];
					if (softPhoneSitesList.size() > 0){
						for (Softphone_Sites__c site : softPhoneSitesList){
							softPhonesitesMap.put(site.name,site.id);

							if (!oInstance.agentsSiteIdNameMap.containsKey(site.id))
							oInstance.agentsSiteIdNameMap.put(site.id,site.name);							
						}
					}
				}
				
				for (SoftPhoneLinkDPResponse_DTO_HUM.unsyncedAgentDP unsyncAgent : oResponse.RecordsInDB ){
					
					Softphone_Agent__c newAgent = new Softphone_Agent__c();
					if(Schema.sobjectType.Softphone_Agent__c.isAccessible()){  //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
                        if(Schema.sobjectType.Softphone_Agent__c.fields.Name.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.Name.isUpdateable())
					      newAgent.Name = unsyncAgent.AgentId.trim();
					    if(Schema.sobjectType.Softphone_Agent__c.fields.Agent_Extension__c.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.Agent_Extension__c.isUpdateable())
					      newAgent.Agent_Extension__c = string.isNotBlank(unsyncAgent.AgentACDName)? unsyncAgent.AgentACDName : NA ;
					     if(Schema.sobjectType.Softphone_Agent__c.fields.User_Id__c.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.User_Id__c.isUpdateable())
					      newAgent.User_Id__c = unsyncAgent.UserId.trim();
					     if(Schema.sobjectType.Softphone_Agent__c.fields.Sites__c.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.Sites__c.isUpdateable())
					      newAgent.Sites__c = softPhonesitesMap.get(unsyncAgent.SiteName) ;
					     if(Schema.sobjectType.Softphone_Agent__c.fields.Default_SoftphoneLinks__c.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.Default_SoftphoneLinks__c.isUpdateable())
					       newAgent.Default_SoftphoneLinks__c = softPhoneLinksMap.get(unsyncAgent.DefaultSoftphoneLinkName) ;
					     if(Schema.sobjectType.Softphone_Agent__c.fields.Alternate_SoftphoneLinks__c.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.Alternate_SoftphoneLinks__c.isUpdateable())
					       newAgent.Alternate_SoftphoneLinks__c = (softPhoneLinksMap.get(unsyncAgent.AlternateSoftphoneLinkName) != Null)? softPhoneLinksMap.get(unsyncAgent.AlternateSoftphoneLinkName) : softPhoneLinksMap.get(unsyncAgent.DefaultSoftphoneLinkName) ;
					     if(Schema.sobjectType.Softphone_Agent__c.fields.DB2_Status__c.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.DB2_Status__c.isUpdateable())
					        newAgent.DB2_Status__c =  GLOBAL_CONSTANT_HUM.SOFTPHONE_SYNCED;
					     if(Schema.sobjectType.Softphone_Agent__c.fields.DB2_Error_Message__c.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.DB2_Error_Message__c.isUpdateable())
					        newAgent.DB2_Error_Message__c = '';    
						 if(Schema.sobjectType.Softphone_Agent__c.fields.DB2_Sync__c.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.DB2_Sync__c.isUpdateable())
					         newAgent.DB2_Sync__c = true;
					    if(Schema.sobjectType.Softphone_Agent__c.fields.Integration_Update__c.isCreateable() || Schema.sobjectType.Softphone_Agent__c.fields.Integration_Update__c.isUpdateable())
                             newAgent.Integration_Update__c = true;
                         }
							 	agentsToInsert.add(newAgent);  
				}
								
				if (agentsToInsert.size() > 0){
                    
                    Schema.SObjectField keyFieldName= Softphone_Agent__c.Fields.User_Id__c;
            		Database.UpsertResult [] lstSvr = Database.upsert(agentsToInsert,keyFieldName, false);
					HUMExceptionHelper.processUpsertResults(lstSvr, agentsToInsert,'SoftPhoneSyncQueueable_S_HUM','SoftPhoneSyncQueueable_H_HUM.getAgentDetails','Softphone_Agent__c');
					
					for(Integer i=0;i<lstSvr.size();i++)
					{
						if (lstSvr.get(i).isSuccess())
						{
							agentsInserted.add(agentsToInsert[i]);
						}
						else
						{
							agentsToInsert[i].DB2_Error_Message__c = NEW_AGENT_UPSERT_FAILED +lstSvr.get(i).getErrors()[0].getMessage();
							oInstance.agentsFailedToInsertSet.add(agentsToInsert[i]);
						}
					}
					
				}
				
				if (agentsInserted.size() > 0 && !Test.isRunningTest())
				{
					SoftPhoneSyncQueueable_S_HUM job = new SoftPhoneSyncQueueable_S_HUM(agentsInserted,oInstance.agentsToUpdateSet,oInstance.agentsFailedToInsertSet);
					job.totalNoOfAgents = agentsToInsert.size();
					job.counter = oInstance.counter+1;
					job.agentsSiteIdNameMap = oInstance.agentsSiteIdNameMap;
					job.agentsLinkIdNameMap = oInstance.agentsLinkIdNameMap;
					job.agentsLinkIdServerNameMap = oInstance.agentsLinkIdServerNameMap;
					
					if(oInstance.max >= job.counter)
                    {
						ID jobID = System.enqueueJob(job);						
					}	
				}
				else
				{
					updateFinalSetOfAgents();
				}				
			}
			else
			{
				updateFinalSetOfAgents();
			}
		}
		catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'SoftPhoneSyncQueueable_S_HUM', 'SoftPhoneSyncQueueable_H_HUM.getAgentDetails');   
        }
    }
	
	
	/*
    * Method Name : ProcessAgentRecordsSync
    * Description : Method to create the wrapper of the sync agent records
    * Return type : SoftPhoneLinkDB2Request_DTO_HUM
    * Parameter   : List<Softphone_Agent__c>
    */
	private SoftPhoneLinkDB2Request_DTO_HUM ProcessAgentRecordsSync(List<Softphone_Agent__c> agentsCreated)
	{ 
		if (agentsCreated != null && agentsCreated.size() > 0){ 
		
			SoftPhoneLinkDB2Request_DTO_HUM softphoneReq = new SoftPhoneLinkDB2Request_DTO_HUM();
			softphoneReq.synclist = new List<SoftPhoneLinkDB2Request_DTO_HUM.syncedAgentDP>();	
			
			for (Softphone_Agent__c sfAgent : agentsCreated){
				SoftPhoneLinkDB2Request_DTO_HUM.syncedAgentDP sfResponse = new SoftPhoneLinkDB2Request_DTO_HUM.syncedAgentDP();
				sfResponse.UserId = sfAgent.User_Id__c;
				sfResponse.SyncInd = 'Y';
				softphoneReq.synclist.add(sfResponse);
			}			
			return softphoneReq;
        }
		return null;
    }
	
	/*
    * Method Name : builtHttpRequest
    * Description : Method to create HTTP request record
    * Return type : HttpRequest
    * Parameter   : HUM_Webservice_Callout__c
    */
	private HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
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
            HUMExceptionHelper.logErrors(e, 'SoftPhoneSyncQueueable_S_HUM', 'SoftPhoneSyncQueueable_H_HUM.builtHttpRequest');
            return Null;
        }
    }
	
	/*
    * Method Name : updateFinalSetOfAgents
    * Description : method to update final list of agents records after all transactions
    * Return type : None
    * Parameter   : None
    */
    private void updateFinalSetOfAgents()
    {
        if (oInstance.agentsToUpdateSet.size() > 0)
        {
            List<Softphone_Agent__c> agentsToUpdateListFinal = new List<Softphone_Agent__c>();
            agentsToUpdateListFinal.addAll(oInstance.agentsToUpdateSet);
            Schema.SObjectField keyFieldName= Softphone_Agent__c.Fields.User_Id__c;
            
			Database.UpsertResult [] lstSvr = Database.upsert(agentsToUpdateListFinal,keyFieldName, false);
            HUMExceptionHelper.processUpsertResults(lstSvr, agentsToUpdateListFinal,'SoftPhoneSyncQueueable_S_HUM','SoftPhoneSyncQueueable_H_HUM.updateFinalSetOfAgents','Softphone_Agent__c');
			
			for(Integer i=0;i<lstSvr.size();i++)
			{
				if (!lstSvr.get(i).isSuccess())
				{
					agentsToUpdateListFinal[i].DB2_Error_Message__c += DB2_SYNC_UPSERT_FAILED +lstSvr.get(i).getErrors();
					oInstance.agentsFailedToInsertSet.add(agentsToUpdateListFinal[i]);
				}
			}
        }
		
		if (oInstance.agentsFailedToInsertSet.size() > 0)
		processFailedToInsertAgents();
    }
	
	/*
    * Method Name : processFailedToInsertAgents
    * Description : method to update final list of agents records which are failed to insert
    * Return type : None
    * Parameter   : None
    */
    private void processFailedToInsertAgents()
    {
        try
        {
			if (oInstance.agentsFailedToInsertSet.size() > 0)
			{
				string ERROR_INTEGRATION = HUMConstants__c.getInstance('ERROR_INTEGRATION') != null ? HUMConstants__c.getInstance('ERROR_INTEGRATION').StringValue__c : '';   
				Integer lengthOfMsgField = Error_Log__c.Error_Message__c.getDescribe().getLength();
				List<Error_Log__c> errorLogToInsert = new List<Error_Log__c>();
				
				for (Softphone_Agent__c failedAgent :oInstance.agentsFailedToInsertSet){
					Error_Log__c logError = new Error_Log__c();
				    if(Schema.sobjectType.Error_Log__c.isAccessible()){  //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
					if(Schema.sobjectType.Error_Log__c.fields.Error_Code__c.isCreateable() || Schema.sobjectType.Error_Log__c.fields.Error_Code__c.isUpdateable())
                      logError.Error_Code__c = 'SOFTPHONE DB2-SALESFORCE-DB2 SYNC FAILURE ON -'+system.today().format();
				    if(Schema.sobjectType.Error_Log__c.fields.class_name__c.isCreateable() || Schema.sobjectType.Error_Log__c.fields.class_name__c.isUpdateable())
					logError.class_name__c = 'SoftPhoneSyncQueueable_S_HUM';
				    if(Schema.sobjectType.Error_Log__c.fields.Object_Type__c.isCreateable() || Schema.sobjectType.Error_Log__c.fields.Object_Type__c.isUpdateable())
					logError.Object_Type__c = 'Softphone_Agent__c';
			        if(Schema.sobjectType.Error_Log__c.fields.Method_Name__c.isCreateable() || Schema.sobjectType.Error_Log__c.fields.Method_Name__c.isUpdateable())
					logError.Method_Name__c = 'SoftPhoneSyncQueueable_H_HUM.processFailedToInsertAgents';
					if(Schema.sobjectType.Error_Log__c.fields.Type__c.isCreateable() || Schema.sobjectType.Error_Log__c.fields.Type__c.isUpdateable())
					logError.Type__c = ERROR_INTEGRATION;
				  }
					string errorMsg = 'Error details-';
					errorMsg += '\n Date = '+ system.today().format();
					errorMsg += '\n User Id = '+ failedAgent.User_Id__c ;
					errorMsg += '\n Site Name = '+ oInstance.agentsSiteIdNameMap.get(failedAgent.Sites__c) ;
					errorMsg += '\n Default Link Name = '+ oInstance.agentsLinkIdNameMap.get(failedAgent.Default_SoftphoneLinks__c) ;
					errorMsg += '\n Default Server Name = '+ oInstance.agentsLinkIdServerNameMap.get(failedAgent.Default_SoftphoneLinks__c) ;
					errorMsg += '\n Alternate Link Name = '+ oInstance.agentsLinkIdNameMap.get(failedAgent.Alternate_SoftphoneLinks__c) ;
					errorMsg += '\n Alternate Server Name = '+ oInstance.agentsLinkIdServerNameMap.get(failedAgent.Default_SoftphoneLinks__c) ;				
					errorMsg += '\n Error = '+ failedAgent.DB2_Error_Message__c ;
				  if(Schema.sobjectType.Error_Log__c.fields.Error_Message__c.isCreateable() || Schema.sobjectType.Error_Log__c.fields.Error_Message__c.isUpdateable())
					logError.Error_Message__c = (errorMsg.Length() >= lengthOfMsgField) ? errorMsg.subString(0,lengthOfMsgField):errorMsg;
					errorLogToInsert.add(logError);
				}
				
				if (errorLogToInsert.size() > 0){
				Database.SaveResult[] lstResults = Database.insert(errorLogToInsert,false);
				HUMExceptionHelper.processSaveResults(lstResults, errorLogToInsert, 'SoftPhoneSyncQueueable_S_HUM','SoftPhoneSyncQueueable_H_HUM.processFailedToInsertAgents','Softphone_Agent__c');
				}				
			}
		}
		catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'SoftPhoneSyncQueueable_S_HUM', 'SoftPhoneSyncQueueable_H_HUM.processFailedToInsertAgents');
        }
    }
}