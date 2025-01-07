/******************************************************************************************************************************
Apex Class Name  : DEAA_Process_H_HUM
Version          : 1.0 
Created Date     : November 20 2019
Function         : Controller to handle display of DEAA process form.                   
Test Class       : DEAA_Process_T_HUM
Modification Log :
   Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Mohammed Noor                           02/19/2020           Original version created for US725182 and US725058.
* Moshitha Gunasekaran                    02/19/2020           REQ - 410421 -Ability for CRM to automatically update existing DEAA cases to a Closed status in CRM and pass this information to CI-CMS. 
* Jasmeen Shangari                        04/21/2021           Checkmarx fixes Azure ID - 2163309
*********************************************************************************************************************************/
public with sharing class DEAA_Process_H_HUM 
{
	private list<string> lstCaseIDs;
	
	/**
	* Constructor method to initialize instance variables.
	*
	* @param    List<String>	list containing CaseIDs.	
	* @return   N/A
	*/
	public DEAA_Process_H_HUM(list<string> lstCaseIDs)
	{
		if(lstCaseIDs!=null && !lstCaseIDs.isEmpty())	this.lstCaseIDs = lstCaseIDs;
		else	lstCaseIDs = new list<string>();
	}
		
	/**
	* Method to invoke ZAPI Serivce callout. This method is bulkified and has a service callout to ZAPI for each case record passed in the input. (Maximum Limit - 10) callouts will be 
	* made or until any DML has occured. The remaining records will be chained for further processing. 
	*
	* @param    None	
	* @return   void
	*/
	public void invokeZAPIService()
	{
		map<Id, Case> mapCases = new map<Id, Case>();
		list<Template_Submission_Data__c> lstSubData = new list<Template_Submission_Data__c>();
		list<Template_Submission_Data__c> lstSubDataForUpdate = new list<Template_Submission_Data__c>();
		map<string, map<string, Template_Submission_Data__c>> mapSubData = new map<string, map<string, Template_Submission_Data__c>>();		
		string sErrorIntegration = HUMConstants__c.getInstance('ERROR_INTEGRATION') != null ? HUMConstants__c.getInstance('ERROR_INTEGRATION').StringValue__c : '';
		integer iMaxCalloutLimit, iCalloutCount;
		boolean bSuccess = false;
		boolean bHandledException = false;
		string sErrorMsg, sCurrentRecID;				 
		try
		{
			iMaxCalloutLimit = Limits.getLimitCallouts() - GLOBAL_CONSTANT_HUM.CONSTANT_10_HUM; //For defensive programming, we are keeping a buffer of 10 below the max callouts allowed. 
			if(lstCaseIDs!=null && !lstCaseIDs.isEmpty())
			{
				mapCases.putAll([SELECT Id, CaseNumber, Origin, Type, Subtype__c, Medicare_Track_ID__c, createdDate, Status FROM Case where Id IN :lstCaseIDs]);
				if(mapCases!=null && !mapCases.isEmpty())
				{
					list<string> lstTemplateFields = new list<string>{GLOBAL_CONSTANT_HUM.DEAA_MEMBEROPTOUTOPTION, GLOBAL_CONSTANT_HUM.DEAA_ISZAPISERVICECALLED};
					lstSubData = [SELECT Id, Name__c, Value__c, createddate, Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c FROM Template_Submission_Data__c WHERE Template_Submission__r.Template__r.Unique_Template_Name__c = :GLOBAL_CONSTANT_HUM.DEAA_Template 
								  AND Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c IN :lstCaseIDs AND Name__c IN :lstTemplateFields];
					
					if(lstSubData!=null && !lstSubData.isEmpty())
					{
						for(Template_Submission_Data__c subData : lstSubData)
						{
							map<string, Template_Submission_Data__c> mapDataByField = new map<string, Template_Submission_Data__c>(); 
							if(mapSubData!=null && mapSubData.containsKey(subData.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c))
							{
								mapDataByField =  mapSubData.get(subData.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c);																
							}
							mapDataByField.put(subData.Name__c, subData);
							mapSubData.put(subData.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c, mapDataByField);							
						}
					}					
					for(Case caseRec : mapCases.values())
					{
						if(Limits.getCallouts() < iMaxCalloutLimit)
						{
							sCurrentRecID = caseRec.Id;
							bSuccess = false;
							HttpResponse oResponse = new HttpResponse();
							DEAA_ZAPIServiceResponse_DTO_HUM oDEAA_Response = new DEAA_ZAPIServiceResponse_DTO_HUM();
							string sCaseNumber = caseRec.casenumber;						
							Datetime dtZAPI = datetime.now();
							if(mapSubData!=null && !mapSubData.isEmpty() && mapSubData.containsKey(caseRec.Id) && mapSubData.get(caseRec.Id).containsKey(GLOBAL_CONSTANT_HUM.DEAA_MEMBEROPTOUTOPTION))	dtZAPI = mapSubData.get(caseRec.Id).get(GLOBAL_CONSTANT_HUM.DEAA_MEMBEROPTOUTOPTION).createddate; 							
							string sRealtimeTransTimestamp = dtZAPI!=null ? dtZAPI.format(GLOBAL_CONSTANT_HUM.DEAA_ZAPIDATETIMEFORMAT, GLOBAL_CONSTANT_HUM.TRACKSERVICE_MILESTONETS_TIMEZONE_HUM) : '';																						
							string sE2ETrackId = caseRec.Medicare_Track_ID__c;
							string sOptOutRequired = '';						
						
							if(mapSubData!=null && mapSubData.containsKey(caseRec.Id) && mapSubData.get(caseRec.Id).containsKey(GLOBAL_CONSTANT_HUM.DEAA_MEMBEROPTOUTOPTION))
							{
								if(GLOBAL_CONSTANT_HUM.DEAA_OPTION_YES.equalsIgnoreCase(mapSubData.get(caseRec.Id).get(GLOBAL_CONSTANT_HUM.DEAA_MEMBEROPTOUTOPTION).Value__c))
									sOptOutRequired = GLOBAL_CONSTANT_HUM.TRACKSERVICE_TRACKINFO_OTRACKID_Y_HUM;	
								else if(GLOBAL_CONSTANT_HUM.DEAA_OPTION_NO.equalsIgnoreCase(mapSubData.get(caseRec.Id).get(GLOBAL_CONSTANT_HUM.DEAA_MEMBEROPTOUTOPTION).Value__c))
									sOptOutRequired = GLOBAL_CONSTANT_HUM.TRACKSERVICE_TRACKINFO_OTRACKID_N_HUM;
							}
							else	//No Template would exist for autoclosure of expired DEAA cases. The expectatio is to send 'N' by default.
							{							
								sOptOutRequired = GLOBAL_CONSTANT_HUM.TRACKSERVICE_TRACKINFO_OTRACKID_N_HUM;
							}
							if(String.isNotBlank(sCaseNumber) && String.isNotBlank(sRealtimeTransTimestamp) && String.isNotBlank(sE2ETrackId) && String.isNotBlank(sOptOutRequired))
							{								
								oResponse = DEAA_Process_S_HUM.callZAPIServiceForDEAA(sCaseNumber, sRealtimeTransTimestamp, sE2ETrackId, sOptOutRequired);							
								if(oResponse!=null)
								{
									if (oResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM && oResponse.getbody() != null && oResponse.getbody().contains(GLOBAL_CONSTANT_HUM.DEAA_SUCCESSRESPHEADER))
									{
										oDEAA_Response = (DEAA_ZAPIServiceResponse_DTO_HUM)System.JSON.deserialize(oResponse.getbody(), DEAA_ZAPIServiceResponse_DTO_HUM.class);									
										if(String.isNotBlank(oDEAA_Response.SavePartDOptOutResponse.Type))
										{
											if(GLOBAL_CONSTANT_HUM.DEAA_RESPONSETYPE_INFO.equalsIgnoreCase(oDEAA_Response.SavePartDOptOutResponse.Type))
											{
												bSuccess = true;
												if(mapSubData!=null && mapSubData.containsKey(caseRec.Id) && mapSubData.get(caseRec.Id).containsKey(GLOBAL_CONSTANT_HUM.DEAA_ISZAPISERVICECALLED))
												{
													Template_Submission_Data__c sData = mapSubData.get(caseRec.Id).get(GLOBAL_CONSTANT_HUM.DEAA_ISZAPISERVICECALLED);
													if(Schema.sObjectType.Template_Submission_Data__c.fields.Value__c.isUpdateable()) sData.Value__c = GLOBAL_CONSTANT_HUM.DEAA_OPTION_YES;                                             
													lstSubDataForUpdate.add(sData);
												}												
											}
											else if(GLOBAL_CONSTANT_HUM.CASEMGMT_ERROR.equalsIgnoreCase(oDEAA_Response.SavePartDOptOutResponse.Type))
											{																																		
												sErrorMsg = 'DEAA:CASE:'+caseRec.CaseNumber+'; CODE:'+ oDEAA_Response.SavePartDOptOutResponse.Code+'; TYPE:'+oDEAA_Response.SavePartDOptOutResponse.Type+'; DESC:'+oDEAA_Response.SavePartDOptOutResponse.Description;
												bSuccess = false;																																
											}
											else	//service response has type other than Information or Error
											{												
												sErrorMsg = 'DEAA:CASE:'+caseRec.CaseNumber+'; CODE:'+ oDEAA_Response.SavePartDOptOutResponse.Code+'; TYPE:'+oDEAA_Response.SavePartDOptOutResponse.Type+'; DESC:'+oDEAA_Response.SavePartDOptOutResponse.Description;
												bSuccess = false;																					
											}
										}									
									}
									else	//service response has failure status code or body is null and response node is missing.
									{																				
										sErrorMsg = 'DEAA:CASE:'+ sCaseNumber + '; Status Code: ' + oResponse.getStatus() + '; Body: ' + oResponse.getbody();
										bSuccess = false;																		
									}
								}
								else	//service response is null  
								{									
									sErrorMsg = 'DEAA:CASE:'+caseRec.CaseNumber+'OTHERERROR:CALLOUTREAD_OR_SERVICE_DOWN: Response:NULL: '+ oResponse;
									bSuccess = false;									
								}
							}
							else	//any of the mandatory parameter is missing.
							{								
								sErrorMsg = GLOBAL_CONSTANT_HUM.DEAA_ZAPI_ERRMSG + ' CaseNumber:' + sCaseNumber + '; ' + '; RealtimeTransTimestamp:' + sRealtimeTransTimestamp + '; E2ETrackId:' + sE2ETrackId + '; OptOutRequired:' + sOptOutRequired;							
								bSuccess = false;
							}
							mapCases.remove(caseRec.Id);	//remove the processed case from the map
							if(!bSuccess )
							{
								break;
							}
						}
						else
						{
							bSuccess = false;
							break;
						}
						if(Limits.getDMLStatements() > GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM)
						{
							bSuccess = false;
							break;
						}						
					}					
					if(lstSubDataForUpdate!=null && !lstSubDataForUpdate.isEmpty())
					{
						update lstSubDataForUpdate;
					}
					if(!bSuccess)	//if the loop was exited either because of max callout limit reached or any callout error while processing any of the case 
					{
						if(mapCases!=null && !mapCases.isEmpty())
						{
							list<string> lstCases = new list<string>();
							for(string sCaseId : mapCases.keySet())
							{
								lstCases.add(sCaseId);
							}
							if(lstCases!=null && !lstCases.isEmpty() && !Test.isRunningTest())	system.enqueueJob(new DEAA_Process_Q_HUM(lstCases));							
						}
						if(String.isNotBlank(sErrorMsg))
						{
							bHandledException = true;
							HUMCustomExceptionHelper custEx = new HUMCustomExceptionHelper('',sErrorMsg,'',sErrorIntegration);
							throw new HUMCustomException(custEx);
						}
					}
				}
			}
		}
		catch(Exception ex)
		{
			if(!bHandledException)	//Incase of unhandled exception, remove to current case record as a precaution incase if the exception was related to the case record. 
			{
				if(lstSubDataForUpdate!=null && !lstSubDataForUpdate.isEmpty())	//update any pending Template submission data records.
				{
					update lstSubDataForUpdate;
				}
				if(String.isNotBlank(sCurrentRecID))	mapCases.remove(sCurrentRecID);	//remove the case that caused exception (handled or unhandled exception)	
				if(mapCases!=null && !mapCases.isEmpty())
				{
					list<string> lstCases = new list<string>();
					for(string sCaseId : mapCases.keySet())
					{
						lstCases.add(sCaseId);
					}
					if(lstCases!=null && !lstCases.isEmpty() && !Test.isRunningTest())	system.enqueueJob(new DEAA_Process_Q_HUM(lstCases));							
				}
			}
			HUMExceptionHelper.logErrors(ex, 'DEAA_Process_H_HUM', 'invokeZAPIService');
		}	
	}		  
	
			/**
	* Method to update Case object Status, Classification and Intent, and Comments for DEAA batch job if 
	* current date is greater than Due date.
	*
	* @param    None	
	* @return   void
	*/
	public static void UpdateCaseDEAABatch(list<Case> listCase)
	{
		string ERROR_APPLICATION = HUMConstants__c.getInstance('ERROR_APPLICATION') != null ? HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c : '';
		list<case> newListCases = new list<case>();
		set<Id> setCaseId =new set<Id>();
		list<CaseComment> newCaseComments = new list<CaseComment>();
		set<Id> setOpenCases = new set<Id>();
		try
	  	{
	  		if(listCase != null && !listCase.isEmpty())
	  		{
		  	    list<CTCI_Junction__c> lstCTCIJunction = [SELECT Id, Classification__r.Id, Classification_Type__r.Id, Intent__r.Id FROM CTCI_Junction__c 
		                                              WHERE Classification_Type__r.Name = :GLOBAL_CONSTANT_HUM.DEAA_ClassificationType AND 
		                                              Classification__r.Name = :GLOBAL_CONSTANT_HUM.DEAA_Classification AND 
		                                              Intent__r.Name = :GLOBAL_CONSTANT_HUM.DEAA_Intent 
		                                              AND Intent__r.IS_ACTIVE__C= true
		                                              AND Classification__r.IS_ACTIVE__C=true AND CTCI_Junction__c.ISACTIVE__C=true];
		    
			    for(case caseObj : (list<case>)listCase )
			  	{
			  		//Consider the cases for closing the status which are not assigned to agent
			  		if((caseObj.Case_Owner__c.equalsIgnoreCase(caseObj.Owner_Queue__c)) && (caseObj.status != GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM && caseObj.status != GLOBAL_CONSTANT_HUM.CASEEDIT_CANCELLED_HUM) )
			        { 
                        if(Schema.sObjectType.case.fields.status.isUpdateable()) caseObj.status =  GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM;  
                        CaseComment objCaseComment = new CaseComment();           
                        if(Schema.sObjectType.CaseComment.fields.commentBody.isCreateable()) objCaseComment.commentBody = GLOBAL_CONSTANT_HUM.DEAA_AutoCloseComments;
                        if(Schema.sObjectType.CaseComment.fields.ParentId.isCreateable()) objCaseComment.ParentId = caseObj.Id; 
			            newCaseComments.add(objCaseComment);
			            
			            if(lstCTCIJunction.size() > 0)
			            {                     
                           if(Schema.sObjectType.case.fields.CTCI_List__c.isUpdateable()) caseObj.CTCI_List__c = lstCTCIJunction[0].Id;
                           if(Schema.sObjectType.case.fields.CLASSIFICATION_ID__c.isUpdateable()) caseObj.CLASSIFICATION_ID__c = lstCTCIJunction[0].Classification__r.Id;
                           if(Schema.sObjectType.case.fields.INTENT_ID__C.isUpdateable()) caseObj.INTENT_ID__C = lstCTCIJunction[0].Intent__r.Id;
			            } 
                        if(string.isBlank(caseObj.Complaint__c) && Schema.sObjectType.case.fields.Complaint__c.isUpdateable()) caseObj.Complaint__c = GLOBAL_CONSTANT_HUM.NO_Complaint_GARights;
                        if(string.isBlank(caseObj.G_A_Rights_Given__c) && Schema.sObjectType.case.fields.G_A_Rights_Given__c.isUpdateable()) caseObj.G_A_Rights_Given__c = GLOBAL_CONSTANT_HUM.NO_Complaint_GARights;
			            setCaseId.add(caseObj.ID);
			            newListCases.add(caseObj);
			        }
			  	}
			  	
			  	//Consider the cases for which Tasks are closed only else remove the cases from the list
			  	if(newListCases != null && !newListCases.isEmpty() && newListCases.size() > 0)
			  	{
			  		list<Task> lstTasks = [select ID, WhatId, Status from task  where WhatId  in :setCaseId AND 
			                    (Type = : GLOBAL_CONSTANT_HUM.CASEEDIT_WORK_TASK_HUM or Type = :HUMConstants.HUMExtensionTask) and 
			                    Status != : GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM];
			        if(lstTasks != null && !lstTasks.isEmpty() && lstTasks.size() > 0)
			        {
				        for(Task taskRec:lstTasks)
				        {
			            	setOpenCases.add(taskRec.WhatId);
			            }
			        }
			        
			        //Loop will be done only if open Work Task is available which is very rare scenario
			        if(setOpenCases != null && !setOpenCases.isEmpty() && setOpenCases.size() > 0)
			        {
			        	for (Integer i = newListCases.size() - 1; i >= 0 ; --i)
			        	{
			        		if (setOpenCases.contains(newListCases.get(i).Id))
			        		{
			        			newListCases.remove(i);
			        		}
			        		
			        	}
			        	if(newCaseComments != null && !newCaseComments.isEmpty() && newCaseComments.size() > 0)
				  	    {
					        for (Integer i = newCaseComments.size() - 1; i >= 0 ; --i)
				        	{
				        		if (setOpenCases.contains(newCaseComments.get(i).ParentId))
				       		    {
				        			newCaseComments.remove(i);
				        		}
				        	}
				  	     }
				    }
			  	}
			  	
			  	if(newListCases != null && !newListCases.isEmpty() && newListCases.size() > 0)
			  	{
			  		update newListCases;
			  	}
			  	if(newCaseComments != null && !newCaseComments.isEmpty() && newCaseComments.size() > 0)
			  	{
			  		insert newCaseComments;
			  	}
	  		}
		}
	  	catch(Exception e)
		{
	        Error_Log__c oErrorLog = new Error_Log__c(Error_Message__c = e.getMessage(), class_name__c = 'DEAA_Process_H_HUM', Method_Name__c = 'UpdateCaseDEAABatch', Object_Type__c = 'Case', Type__c = ERROR_APPLICATION);
	        insert oErrorLog;
	    }
	}
}