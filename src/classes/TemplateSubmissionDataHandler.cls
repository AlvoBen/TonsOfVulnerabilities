/*******************************************************************************************************************************
Class Name    	: TemplateSubmissionDataHandler
Version         : 1.0
Created On      : 03/25/2019
Function        : Trigger execution sequence logic for Template_Submission_data__c  object.
Test Class      : TemplateSubmissionDataHandler_T_HUM
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                               03/25/2019                 original version
* Mohammed Noor		     350978    	          04/12/2019			     REQ - 396223 - Changes to support TRR Process BAM tracking.
* Mohammed Noor          353757               04/30/2019                 Code Change to Handle empty Case Action for Create Case scenario.
* Mohammed Noor          355698               05/13/2019                 REQ - 396223 - Fix for Defect# 415166.0001
* Mohammed Noor                               09/05/2019                 REQ - 408201 - Optimize Template Submission Query
*******************************************************************************************************************************/

public class TemplateSubmissionDataHandler
{
    
    /*
    * This Method is Used to get the Set of Case Ids for Omni Case Evaluation on TRR Template
    * <p>
    * @ Param     :    List<Template_Submission_data__c>
    * @ Return    :    Void
    */
    public static void evaluateOmniEligibility(List<Template_Submission_data__c> lstTemplateData)
    {
        Try
        {
            Set<String> setCaseIDs = New Set<String>();
            set<String> setTemplateSubmissionIDs = new set<String>();                                    
            map<string, set<String>> mapCurrTemplateSubByCases = new map<string, set<string>>();  //map of CaseID and List of Template SubmissionIDs            
            if(lstTemplateData!=null && !lstTemplateData.isEmpty())
            {
                for(Template_Submission_Data__c templateData : lstTemplateData)
                {
                    if(templateData.Template_Submission__c !=null)  setTemplateSubmissionIDs.add(templateData.Template_Submission__c);
                }
				
				if(setTemplateSubmissionIDs!=null && !setTemplateSubmissionIDs.isEmpty())
				{
					List<Template_Submission__c> lstTemplateSub = new List<Template_Submission__c>();
					lstTemplateSub = [SELECT Id, Name, Submission_Owner__c, Submission_Owner__r.Object_Owner_ID__c, Submission_Owner__r.Object_Owner_Type__c 
									  FROM Template_Submission__c WHERE ID IN :setTemplateSubmissionIDs AND Template__r.Unique_Template_Name__c =: GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_NAME];
					if(lstTemplateSub!=null && !lstTemplateSub.isEmpty())
					{
						for(Template_Submission__c tSub : lstTemplateSub)
						{
							if(String.isNotBlank(tSub.Submission_Owner__r.Object_Owner_ID__c))
								setCaseIDs.add(tSub.Submission_Owner__r.Object_Owner_ID__c);
							
							if(mapCurrTemplateSubByCases!=null && mapCurrTemplateSubByCases.containsKey(tSub.Submission_Owner__r.Object_Owner_ID__c))
								mapCurrTemplateSubByCases.get(tSub.Submission_Owner__r.Object_Owner_ID__c).add(tSub.ID);                        
                        	else
                        		mapCurrTemplateSubByCases.put(tSub.Submission_Owner__r.Object_Owner_ID__c, new set<String>{tSub.ID});                   
						}
					}
				}
				if(setCaseIDs!=null && !setCaseIDs.isEmpty())
				{
					//Call the evaluate method based on set of CaseIDs
					OmniCaseAssignmentRuleEvaluation_H_HUM.evaluateforTemplate(setCaseIDs);
					
					//call the method to procees for BAM tracking
					evaluateForMilestone(setCaseIDs, mapCurrTemplateSubByCases);
				}
				
            }
                                   
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'TemplateSubmissionDataHandler', 'evaluateOmniEligibility');
        }
    }
    
    
    /*
    * This Method is used to evaulate the TRR Template Submission Data created and identify the Milestones to be  
    * written to BAM.
    * <p>
    * @ Param     :    setCaseIDs (set of CaseIDs), mapCurrTemplateSubByCases (map of CaseID and List of Template SubmissionIDs) 
    * @ Return    :    void
    */    
    public static void evaluateForMilestone(set<String> setCaseIDs, map<string, set<String>> mapCurrTemplateSubByCases)
    {
        try
        {            
            //set<String> setCaseIDs = new set<String>();
            set<String> setTemplateSubmissionIDs = new set<String>();
			
			//map of Template Submission ID (Map of FieldName, TSubmission Data)                        
            map<string, map<string, Template_Submission_data__c>> mapTemplateDatabyFields= new map<string, map<string, Template_Submission_data__c>>();
			
			//map of CaseID and List of Template SubmissionIDs (Existing + current template submissions for each case)                        
            map<string, set<String>> mapAllTemplateSubByCases = new map<string, set<string>>();
			
			//map of CaseID and List of Template SubmissionIDs - These template will be evaluated in the current execution.                        
            //map<string, set<String>> mapCurrTemplateSubByCases = new map<string, set<string>>();
			
			//map of Cases by ID                        
            map<ID, Case> mapCaseDetails = new map<ID, Case>(); 
            
            //map of key(WQ+QV+ReplyCodeType+caseAction) and Case_Milestone__c           
            map<string, Case_Milestone__c> mapMilestones = new map<string, Case_Milestone__c>();
			
			//map of key(Reply code+Trans Code) and TRR_Rejection_Category__mdt                        
            map<string, TRR_Rejection_Category__mdt> mapRejectCategory = new map<string, TRR_Rejection_Category__mdt>();
                          
            //map of CaseID, map of sourceID and list of Enrollment track history - All Data
            map<string, map<string, list<Enrollment_Track_History__c>>> mapEnrollHist = new map<string, map<string, list<Enrollment_Track_History__c>>>();
                                                                                                           
            //Get the CaseIDs from Template Submission Owner                                 
            if(setCaseIDs!=null && !setCaseIDs.isEmpty() && mapCurrTemplateSubByCases!=null && !mapCurrTemplateSubByCases.isEmpty())
            {
                    
				//Get the Case Details and Template Submissions by Case. This is to handle the template submission data split across 200 chunks for the same case as well as same submission                                                           
                list<Case> lstCases = [SELECT Id, Casenumber, Owner_Queue__c, Work_Queue_View_Name__c, status FROM Case WHERE ID IN :setCaseIDs AND IsClosed = FALSE AND Owner_Queue__c!=null AND Work_Queue_View_Name__c!=null];
                if(lstCases!=null && !lstCases.isEmpty())
                {
                    mapCaseDetails.putAll(lstCases);    
                }                
                list<Template_Submission__c> lstTSubmission = [SELECT Id, Submission_Owner__r.Object_Owner_ID__c, Submission_Owner__r.Object_Owner_Type__c, Template__c, 
                											  (SELECT Id, Name, Name__c, Value__c FROM Template_Submission_Data__r) FROM Template_Submission__c 
                											  WHERE Submission_Owner__r.Object_Owner_ID__c IN :setCaseIDs AND Template__r.Unique_Template_Name__c = :GLOBAL_CONSTANT_HUM.TRR_TEMPLATE_NAME];
                        
				if(lstTSubmission!=null && !lstTSubmission.isEmpty())
				{
					for(Template_Submission__c templateSub : lstTSubmission)
					{
						if(String.isNotBlank(templateSub.Submission_Owner__r.Object_Owner_ID__c) && templateSub.Template_Submission_Data__r != null)
						{			                            
							if(mapAllTemplateSubByCases!=null && mapAllTemplateSubByCases.containsKey(templateSub.Submission_Owner__r.Object_Owner_ID__c))
							{
								mapAllTemplateSubByCases.get(templateSub.Submission_Owner__r.Object_Owner_ID__c).add(templateSub.Id);
							}
							else
							{
								mapAllTemplateSubByCases.put(templateSub.Submission_Owner__r.Object_Owner_ID__c, new set<string>{templateSub.Id});
							}
							map<string, Template_Submission_data__c> mapFieldByValue = new map<string, Template_Submission_data__c>();
							for(Template_Submission_Data__c templateData : templateSub.Template_Submission_Data__r)
							{								
								mapFieldByValue.put(templateData.Name__c, templateData);
							}
							mapTemplateDatabyFields.put(templateSub.Id, mapFieldByValue);
						}									                     
					}					
                }
				//Identify the exact template submissions we need to process for Tracking in the current execution.
				if(mapCurrTemplateSubByCases!=null && !mapCurrTemplateSubByCases.isEmpty() && mapTemplateDatabyFields!=null && !mapTemplateDatabyFields.isEmpty())
				{
					for(string sCaseRecID : mapCurrTemplateSubByCases.keySet())
					{                        		
						set<string> setSubIDs = new set<string>();
						for(string sSubID : mapCurrTemplateSubByCases.get(sCaseRecID))
						{                        	
							//select the Submission record if any of the mandatory field is in current chunk, but process if all the fields are available in the template submission map.
							if(mapTemplateDatabyFields.containsKey(sSubID) && (mapTemplateDatabyFields.get(sSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY) || mapTemplateDatabyFields.get(sSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID) 
							|| mapTemplateDatabyFields.get(sSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_CASEACTION) || mapTemplateDatabyFields.get(sSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCODE) || mapTemplateDatabyFields.get(sSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_TRANSCODE)))
							{
								setSubIDs.add(sSubID);
							}
						}
						if(setSubIDs!=null && !setSubIDs.isEmpty())
						{
							mapCurrTemplateSubByCases.put(sCaseRecID, setSubIDs);
						}
					}
				}                          				             
				mapEnrollHist = getTrackHistoryRecs(setCaseIDs);                                                			
				if(mapCaseDetails!=null && !mapCaseDetails.isEmpty())
				{                        
					mapMilestones = getMilestoneMap();
					mapRejectCategory = getTRRRejectCategoryMap();
										   
					String sKey = '';
					String sReplyCodeType = '';
					String sSourceID = '';
					String sCaseAction = '';
					for(case caseRec: mapCaseDetails.values())
					{                                                         
						if(mapCurrTemplateSubByCases!=null && mapCurrTemplateSubByCases.containsKey(caseRec.ID))
						{
							set<string> setTempSub = new set<string>();
							setTempSub = mapCurrTemplateSubByCases.get(caseRec.ID);
							if(setTempSub!=null && !setTempSub.isEmpty())
							{
								for(String TempSubID : setTempSub)
								{
									sKey = '';
									sReplyCodeType = '';
									sSourceID = '';
									sCaseAction = '';
									if(mapTemplateDatabyFields!=null && mapTemplateDatabyFields.containsKey(TempSubID) && mapTemplateDatabyFields.get(TempSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY) && mapTemplateDatabyFields.get(TempSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID) 
									   && mapTemplateDatabyFields.get(TempSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_CASEACTION) && mapTemplateDatabyFields.get(TempSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCODE) && mapTemplateDatabyFields.get(TempSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_TRANSCODE))
									{
										if(mapTemplateDatabyFields.get(TempSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY))
										{
											sReplyCodeType = mapTemplateDatabyFields.get(TempSubID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY).Value__c;
										}
										if(mapTemplateDatabyFields.get(TempSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID))
										{
											sSourceID = mapTemplateDatabyFields.get(TempSubID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c;
										}
										if(mapTemplateDatabyFields.get(TempSubID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_CASEACTION))
										{
											//BPIC will send Case Action as UPD for Update case Scenario and Empty for Create Case Scenario.
											sCaseAction = String.isNotBlank(mapTemplateDatabyFields.get(TempSubID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_CASEACTION).Value__c) ? mapTemplateDatabyFields.get(TempSubID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_CASEACTION).Value__c : GLOBAL_CONSTANT_HUM.TRR_CASEACTION_CRE;
										}
										if(string.isNotBlank(sReplyCodeType) && string.isNotBlank(sSourceID) && string.isNotBlank(sCaseAction))
										{
											if(sCaseAction.equals(GLOBAL_CONSTANT_HUM.TRR_CASEACTION_CRE))
											{
												if(sReplyCodeType.equals(GLOBAL_CONSTANT_HUM.TRR_UISERIES_CATEGORY))
												{														
													if(mapEnrollHist!=null)														
													{
														if(!mapEnrollHist.containsKey(caseRec.ID))
														{
														sKey = caseRec.Owner_Queue__c + caseRec.Work_Queue_View_Name__c + sReplyCodeType + sCaseAction;															
														}
														else
														{
															/*Start logic to identify and UI Series Template is already tracked.*/
															map<string, list<Enrollment_Track_History__c>> mapCaseEnrollHistRecs = new map<string, list<Enrollment_Track_History__c>>();
															mapCaseEnrollHistRecs = mapEnrollHist.get(caseRec.ID);
															set<string> setSourceIDs = new set<string>();
															Boolean bUITrackExists = false;
															if(mapCaseEnrollHistRecs!=null && !mapCaseEnrollHistRecs.isEmpty() && mapAllTemplateSubByCases!=null && mapAllTemplateSubByCases.containsKey(caseRec.ID))
															{
																for(String sSubmissionID : mapAllTemplateSubByCases.get(caseRec.ID))
																{
																	if(mapTemplateDatabyFields.containsKey(sSubmissionID) && mapTemplateDatabyFields.get(sSubmissionID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID) && string.isNotBlank(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c)
																		&& mapCaseEnrollHistRecs.containsKey(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c)
																		&& mapTemplateDatabyFields.get(sSubmissionID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY) && string.isNotBlank(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY).Value__c) 
																		&& mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY).Value__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TRR_UISERIES_CATEGORY)
																		) 																			
																	{
																		bUITrackExists = true;																																							
																		break;
																	}
																}
																if(!bUITrackExists)
																{
																	sKey = caseRec.Owner_Queue__c + caseRec.Work_Queue_View_Name__c + sReplyCodeType + sCaseAction;
																}	
															}
															/*End logic to identify and UI Series Template is already tracked.*/																
														}
													}																											
												}
												else if(sReplyCodeType.equals(GLOBAL_CONSTANT_HUM.TRR_REJECTIONREPLY_CATEGORY))
												{																												
													if(mapEnrollHist!=null && mapEnrollHist.containsKey(caseRec.ID)) //if any template already created and tracked.
													{															
														//logic to identify the Priority and create stop milestones
														map<string, list<Enrollment_Track_History__c>> mapCaseEnrollHistRecs = new map<string, list<Enrollment_Track_History__c>>();
														set<string> setSourceIDs = new set<string>();
														mapCaseEnrollHistRecs = mapEnrollHist.get(caseRec.ID);
														map<string, list<Enrollment_Track_History__c>> mapRejEnrollHistRecs = new map<string, list<Enrollment_Track_History__c>>();
																													
														if(mapCaseEnrollHistRecs!=null && !mapCaseEnrollHistRecs.isEmpty())
														{
															list<Enrollment_Track_History__c> setEnrollHist = new list<Enrollment_Track_History__c>();
															/*Start Logic to identify only rejection replies */
															if(mapAllTemplateSubByCases!=null && mapAllTemplateSubByCases.containsKey(caseRec.ID))
															{
																for(String sSubmissionID : mapAllTemplateSubByCases.get(caseRec.ID))
																{
																	if(mapTemplateDatabyFields.containsKey(sSubmissionID) && mapTemplateDatabyFields.get(sSubmissionID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID) && string.isNotBlank(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c)
																		&& mapTemplateDatabyFields.get(sSubmissionID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY) && string.isNotBlank(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY).Value__c) 
																		&& mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY).Value__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TRR_REJECTIONREPLY_CATEGORY)
																		&& mapCaseEnrollHistRecs.containsKey(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c)) 																			
																	{
																		mapRejEnrollHistRecs.put(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c, mapCaseEnrollHistRecs.get(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c));																					
																	}
																}		
															}																	
															/*End Logic to identify only rejection replies */
															
															if(mapRejEnrollHistRecs!=null && !mapRejEnrollHistRecs.isEmpty())
															{
																															
															for(String sSourceTrackID : mapRejEnrollHistRecs.keySet())
															{
																setEnrollHist = mapRejEnrollHistRecs.get(sSourceTrackID);
																if(!setEnrollHist.isEmpty())
																{
																	if(setEnrollHist.size() == 1 && !setEnrollHist[0].Milestone_Id__c.equals(GLOBAL_CONSTANT_HUM.TRR_REJ_STOP_MS))
																	{
																		setSourceIDs.add(sSourceTrackID);	
																	}
																	else if(setEnrollHist.size() > 1 && !setEnrollHist[setEnrollHist.size()-1].Milestone_Id__c.equals(GLOBAL_CONSTANT_HUM.TRR_REJ_STOP_MS))
																	{
																		setSourceIDs.add(sSourceTrackID);
																	}
																}
															}
															if(setSourceIDs!=null && !setSourceIDs.isEmpty())
															{
																if(!mapCaseEnrollHistRecs.containsKey(sSourceID))	setSourceIDs.add(sSourceID);  //add the current Template submission sourceID if it is not already tracked as well.
																																																	
																//map of source trackID and Key (Reply code+Trans code) 
																map<string, string> mapSourceIDKey = new map<string, string>();
																																																		
																//map<string, string> mapSourceIDCategory = new map<string, string>();																	
																map<string, list<string>> mapSourceIDCategory = new map<string, list<string>>();
																
																//Get the Template fields data for Reply code, trans code
																//determine the priority
																//and stop accordingly
																if(mapAllTemplateSubByCases!=null && mapAllTemplateSubByCases.containsKey(caseRec.ID))
																{
																	for(String sSubmissionID : mapAllTemplateSubByCases.get(caseRec.ID))
																	{
																		if(mapTemplateDatabyFields.containsKey(sSubmissionID) && mapTemplateDatabyFields.get(sSubmissionID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID) && string.isNotBlank(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c)
																			&& setSourceIDs.contains(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c) && mapTemplateDatabyFields.get(sSubmissionID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_TRANSCODE) && string.isNotBlank(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_TRANSCODE).Value__c) 
																			&& mapTemplateDatabyFields.get(sSubmissionID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCODE) && string.isNotBlank(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCODE).Value__c))
																		{
																			if(!mapSourceIDKey.containsKey(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c))
																			{
																			mapSourceIDKey.put(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c, mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCODE).Value__c + mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_TRANSCODE).Value__c);
																			}																				
																		}
																	}
																	
																	if(mapSourceIDKey!=null && !mapSourceIDKey.isEmpty())
																	{
																		for(string sSourceKey : mapSourceIDKey.keySet())
																		{																				
																			if(mapRejectCategory.containsKey(mapSourceIDKey.get(sSourceKey)))
																			{
																				if(mapSourceIDCategory!=null && mapSourceIDCategory.containsKey(mapRejectCategory.get(mapSourceIDKey.get(sSourceKey)).Rejection_Category__c))
																				{
																					mapSourceIDCategory.get(mapRejectCategory.get(mapSourceIDKey.get(sSourceKey)).Rejection_Category__c).add(sSourceKey);
																				}
																				else
																				{
																					mapSourceIDCategory.put(mapRejectCategory.get(mapSourceIDKey.get(sSourceKey)).Rejection_Category__c, new list<string>{sSourceKey});
																				}																					
																			}																					
																		}
																		
																		for(String sRejCategory : GLOBAL_CONSTANT_HUM.TRR_REJ_CATERGORY_PRIORITY)
																		{
																			if(mapSourceIDCategory.containsKey(sRejCategory))
																			{																					
																				list<string> lstSrcIDs = new list<string>();
																				lstSrcIDs = mapSourceIDCategory.get(sRejCategory);																																		
																				for(integer i=0; i<lstSrcIDs.size(); i++)
																				{
																					if(setSourceIDs.contains(lstSrcIDs[i]))
																					{
																						setSourceIDs.remove(lstSrcIDs[i]);
																						if(i==0 && !mapCaseEnrollHistRecs.containsKey(sSourceID) && lstSrcIDs[i].equals(sSourceID))	sKey = caseRec.Owner_Queue__c + caseRec.Work_Queue_View_Name__c + sReplyCodeType + sCaseAction;
																					}																						
																				}
																				break;																					
																			}
																		}
																		//for each sourceTrackID remaining in setSourceIDs if already exists in enroll history then stop it, if not also stop it
																		if(setSourceIDs!=null && !setSourceIDs.isEmpty())
																		{
																			Enrollment_Track_History__c enrollHist = new Enrollment_Track_History__c();
																			for(String sSrcTrkID: setSourceIDs)
																			{
																				if(mapCaseEnrollHistRecs!=null)
																				{
																					if(mapCaseEnrollHistRecs.containsKey(sSrcTrkID))
																					{
																						list<Enrollment_Track_History__c> lstEnrollHist = mapCaseEnrollHistRecs.get(sSrcTrkID);
																						
																						if(lstEnrollHist[lstEnrollHist.size()-1].ID ==null && !lstEnrollHist[lstEnrollHist.size()-1].Milestone_Id__c.equals(GLOBAL_CONSTANT_HUM.TRR_REJ_STOP_MS))
																						{
																							lstEnrollHist[lstEnrollHist.size()-1].Milestone_Id__c = GLOBAL_CONSTANT_HUM.TRR_REJ_STOP_MS;
																						}
																						else
																						{
																						enrollHist = new Enrollment_Track_History__c();
																						enrollHist.Milestone_Id__c = GLOBAL_CONSTANT_HUM.TRR_REJ_STOP_MS;
																						enrollHist.Source_App__c = 'CRM';
																						enrollHist.Recipient_App__c = 'CRM';
																						enrollHist.Status__c = 'Pending';
																						enrollHist.Case__c = caseRec.ID;
																						enrollHist.Source_Track_Id__c = sSrcTrkID;
																						lstEnrollHist.add(enrollHist);
																						}
																						mapCaseEnrollHistRecs.put(sSrcTrkID, lstEnrollHist);
																					}
																					else
																					{
																						enrollHist = new Enrollment_Track_History__c();
																						enrollHist.Milestone_Id__c = GLOBAL_CONSTANT_HUM.TRR_REJ_STOP_MS;
																						enrollHist.Source_App__c = 'CRM';
																						enrollHist.Recipient_App__c = 'CRM';
																						enrollHist.Status__c = 'Pending';
																						enrollHist.Case__c = caseRec.ID;
																						enrollHist.Source_Track_Id__c = sSrcTrkID;
																						mapCaseEnrollHistRecs.put(sSrcTrkID, new list<Enrollment_Track_History__c>{enrollHist});	
																					}
																				}																					
																			}
																			mapEnrollHist.put(caseRec.ID, mapCaseEnrollHistRecs);
																		}																			
																	}																		
																}																	
															}
														  }
														  else
														  {
															sKey = caseRec.Owner_Queue__c + caseRec.Work_Queue_View_Name__c + sReplyCodeType + sCaseAction;	
														  } 
														}
													}
													else if(mapEnrollHist!=null && !mapEnrollHist.containsKey(caseRec.ID))  //Very first Rejection Replies Create Template 
													{
														sKey = caseRec.Owner_Queue__c + caseRec.Work_Queue_View_Name__c + sReplyCodeType + sCaseAction;
													}
												}													
											}
											else if(sCaseAction.equals(GLOBAL_CONSTANT_HUM.TRR_CASEACTION_UPD))
											{													
												//For Update if the Source ID is already stopped, skip it. Else get the update milestone.
												if(mapEnrollHist==null || (mapEnrollHist!=null && !mapEnrollHist.containsKey(caseRec.ID)) || (mapEnrollHist!=null && mapEnrollHist.containsKey(caseRec.ID) && !mapEnrollHist.get(caseRec.ID).containsKey(sSourceID)))												
												{																										
													
													if(caseRec.Owner_Queue__c.equals(GLOBAL_CONSTANT_HUM.CASE_RSO_ENROLLMENT_QUEUENAME) && caseRec.Work_Queue_View_Name__c.equals(GLOBAL_CONSTANT_HUM.TRR_Third_Party_Sub_View) && sReplyCodeType.equals(GLOBAL_CONSTANT_HUM.TRR_UISERIES_CATEGORY))
													{
														boolean bThirdPartyUIExists = false;														
														if(mapEnrollHist!=null && mapEnrollHist.containsKey(caseRec.ID))
														{
															/*Start logic to identify ThirdParty Submission UI Series Template is already tracked.*/
															map<string, list<Enrollment_Track_History__c>> mapCaseEnrollHistRecs = new map<string, list<Enrollment_Track_History__c>>();
															mapCaseEnrollHistRecs = mapEnrollHist.get(caseRec.ID);
															
															if(mapCaseEnrollHistRecs!=null && !mapCaseEnrollHistRecs.isEmpty() && mapAllTemplateSubByCases!=null && mapAllTemplateSubByCases.containsKey(caseRec.ID))
															{
																for(String sSubmissionID : mapAllTemplateSubByCases.get(caseRec.ID))
																{
																	if(mapTemplateDatabyFields.containsKey(sSubmissionID) && mapTemplateDatabyFields.get(sSubmissionID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID) && string.isNotBlank(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c)
																		&& mapCaseEnrollHistRecs.containsKey(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_SOURCETRACKID).Value__c)
																		&& mapTemplateDatabyFields.get(sSubmissionID).containsKey(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY) && string.isNotBlank(mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY).Value__c) 
																		&& mapTemplateDatabyFields.get(sSubmissionID).get(GLOBAL_CONSTANT_HUM.TRR_FIELD_REPLYCATEGORY).Value__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TRR_UISERIES_CATEGORY)
																		) 																			
																	{
																		bThirdPartyUIExists = true;				
																		break;
																	}
																}
																	
															}
														}														
														if(!bThirdPartyUIExists)
														{
															sKey = caseRec.Owner_Queue__c + caseRec.Work_Queue_View_Name__c + sReplyCodeType + sCaseAction;
														}
													}
													else
													{
														sKey = caseRec.Owner_Queue__c + caseRec.Work_Queue_View_Name__c + sReplyCodeType + sCaseAction;
													}														
												}
											}	
										}
										
										if(String.isNotBlank(sKey))
										{																																			
											//get the milestone based on sKey and create new Enrollment History entry and add it to new map and old map
											map<string, list<Enrollment_Track_History__c>> mapNewTrackHisRecs = new map<string, list<Enrollment_Track_History__c>>();
											Enrollment_Track_History__c TrackHisRec = new Enrollment_Track_History__c();  
											if(mapMilestones!=null && mapMilestones.containsKey(sKey))
											{
												TrackHisRec = new Enrollment_Track_History__c();
												TrackHisRec.Milestone_Id__c = mapMilestones.get(sKey).Milestone__c;
												TrackHisRec.Source_App__c = 'CRM';
												TrackHisRec.Recipient_App__c = 'CRM';
												TrackHisRec.Status__c = 'Pending';
												TrackHisRec.Case__c = caseRec.ID;
												TrackHisRec.Source_Track_Id__c = sSourceID;                                        			                                        			
												if(mapEnrollHist!=null && mapEnrollHist.containsKey(caseRec.ID))
												{
													mapNewTrackHisRecs = mapEnrollHist.get(caseRec.ID);                                        			
												}
												if(mapNewTrackHisRecs!=null && mapNewTrackHisRecs.containsKey(sSourceID))
												{
													mapNewTrackHisRecs.get(sSourceID).add(TrackHisRec);
												}
												else
												{
													mapNewTrackHisRecs.put(sSourceID, new list<Enrollment_Track_History__c>{TrackHisRec});
												}
												mapEnrollHist.put(caseRec.ID, mapNewTrackHisRecs);	                                      			
											}												
										}											                                                                                         
									}                                                                                
								}                                   
							}
						}
					}
				}
                                
                if(mapEnrollHist!=null && !mapEnrollHist.isEmpty())		enqueueTrackService(mapEnrollHist);
			}                                                                
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'TemplateSubmissionDataHandler', 'evaluateForMilestone');
        }   
    }
    
    /*
    * This Method is Used to identify the new Enrollment Track Hstory Entries from the current execution and 
    * Enqueue the job for Track Service callouts.
    * <p>
    * @ Param     :    mapEnrollHist - map of all Track History Entries.
    * @ Return    :    void
    */
    @TestVisible
    private static void enqueueTrackService(map<string, map<string, list<Enrollment_Track_History__c>>> mapEnrollHist)
    { 		
        //map of CaseID, map of sourceID and list of Enrollment track history - New Data for Track Service callout
        map<string, map<string, set<Enrollment_Track_History__c>>> mapNewEnrollHist = new map<string, map<string, set<Enrollment_Track_History__c>>>();
        
        //list of new Enrollment Track History Entries for Insert
        list<Enrollment_Track_History__c> lstEnrollHistForInsert = new list<Enrollment_Track_History__c>();   	
        
        if(mapEnrollHist!=null && !mapEnrollHist.isEmpty())
        {
        	map<string, list<Enrollment_Track_History__c>> mapByCase = new map<string, list<Enrollment_Track_History__c>>(); 
        	for(string sCaseID : mapEnrollHist.keySet())
        	{
        		mapByCase = new map<string, list<Enrollment_Track_History__c>>();
        		mapByCase = mapEnrollHist.get(sCaseID);
        		for(string sSrcID : mapByCase.keySet())
        		{
        			for(Enrollment_Track_History__c TrackHist : mapByCase.get(sSrcID))
        			{
        				if(TrackHist.ID == null)	lstEnrollHistForInsert.add(TrackHist);        				
        			}
        		}                	
        	}
        	        	
        	if(lstEnrollHistForInsert!=null && !lstEnrollHistForInsert.isEmpty())
        	{
        		insert lstEnrollHistForInsert;        		
        		map<string, set<Enrollment_Track_History__c>>	mapTrackHist = new map<string, set<Enrollment_Track_History__c>>();        		
        		for(Enrollment_Track_History__c histRec : lstEnrollHistForInsert)
        		{
        			mapTrackHist = new map<string, set<Enrollment_Track_History__c>>();
        			if(mapNewEnrollHist!=null && mapNewEnrollHist.containsKey(histRec.Case__c))
        			{
						mapTrackHist = mapNewEnrollHist.get(histRec.Case__c);								                				
        			}
        			if(mapTrackHist.containsKey(histRec.Source_Track_Id__c))
        			{
        				mapTrackHist.get(histRec.Source_Track_Id__c).add(histRec);	
        			}
        			else
        			{
        				mapTrackHist.put(histRec.Source_Track_Id__c, new set<Enrollment_Track_History__c>{histRec});
        			}
        			mapNewEnrollHist.put(histRec.Case__c, mapTrackHist);
        		}
        	}
        }                   
	    if(mapNewEnrollHist!=null && !mapNewEnrollHist.isEmpty())
	    {
	    	system.enqueueJob(new TrackService_Q_HUM(mapNewEnrollHist));
	    }
    }
    
    /*
    * This Method is Used to get the map of Enrollment Track History Entries.
    * <p>
    * @ Param     :    setCaseIDs - set of Case IDs to retrieve Enrollment track History entries for.
    * @ Return    :    mapTrackHistory - map of Enrollment Track History entries.
    */
    @TestVisible
    private static map<string, map<string, list<Enrollment_Track_History__c>>> getTrackHistoryRecs(set<String> setCaseIDs)
    {
    	map<string, map<string, list<Enrollment_Track_History__c>>> mapTrackHistory = new map<string, map<string, list<Enrollment_Track_History__c>>>();
		if(setCaseIDs!=null && !setCaseIDs.isEmpty())
		{					       
			//Query Case Based Enrollment Track History
			list<Enrollment_Track_History__c> lstEnrollTrackHist= [SELECT Id, Name, Case__c, Recipient_App__c, Source_App__c, Recipient_Track_ID__c, Source_Track_Id__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, createddate FROM Enrollment_Track_History__c 
																WHERE Case__c IN :setCaseIDs AND Milestone_MET_Id__c = null AND Source_Track_Id__c!=null AND Status__c!='Failed'];  
			if(lstEnrollTrackHist!=null && !lstEnrollTrackHist.isEmpty())
			{
				for(Enrollment_Track_History__c trackHist : lstEnrollTrackHist)
				{
					map<string, list<Enrollment_Track_History__c>> mapHistBySourceID = new map<string, list<Enrollment_Track_History__c>>();
					list<Enrollment_Track_History__c> setTrackHistory = new list<Enrollment_Track_History__c>();                        	
					if(mapTrackHistory!=null && mapTrackHistory.containsKey(trackHist.Case__c))
					{
						mapHistBySourceID = mapTrackHistory.get(trackHist.Case__c);                		
					}
					if(mapHistBySourceID!=null && mapHistBySourceID.containsKey(trackHist.Source_Track_Id__c))
					{
						setTrackHistory = mapHistBySourceID.get(trackHist.Source_Track_Id__c);
					}
					setTrackHistory.add(trackHist);
					mapHistBySourceID.put(trackHist.Source_Track_Id__c, setTrackHistory);
					mapTrackHistory.put(trackHist.Case__c, mapHistBySourceID);
				}
			}
		}
		return mapTrackHistory;
    }
    
    /*
    * This Method is Used to get the map of TRR Milestones from the Custom setting
    * <p>
    * @ Param     :    None
    * @ Return    :    mapMilestones - map TRR Milestones
    */
    @TestVisible
    private static map<string, Case_Milestone__c> getMilestoneMap()
    {
    	map<string, Case_Milestone__c> mapMilestones = new map<string, Case_Milestone__c>();
    	list<Case_Milestone__c> lstMilestones = [SELECT Id, Name, Milestone__c, Work_Queue__c, Is_Start_Milestone__c, Work_Queue_View__c, Reply_Code_Category__c, Case_Action__c FROM Case_Milestone__c 
                                        WHERE Work_Queue__c!=null AND Work_Queue_View__c!=null AND Reply_Code_Category__c!=null AND Case_Action__c!=null];
                                                
        if(lstMilestones!=null && !lstMilestones.isEmpty())
        {                            
            for(Case_Milestone__c milestone : lstMilestones)
            {                           
                mapMilestones.put(milestone.Work_Queue__c + milestone.Work_Queue_View__c + milestone.Reply_Code_Category__c + milestone.Case_Action__c, milestone);
            }            
        }
        return mapMilestones;
    }
    
    /*
    * This Method is Used to get the map of TRR Template Rejection Category
    * <p>
    * @ Param     :    None
    * @ Return    :    mapRejectCategory - map TRR Template Rejection Category
    */
    @TestVisible
    private static map<string, TRR_Rejection_Category__mdt> getTRRRejectCategoryMap()
	{
		map<string, TRR_Rejection_Category__mdt> mapRejectCategory = new map<string, TRR_Rejection_Category__mdt>();
		
        list<TRR_Rejection_Category__mdt> lstRejectCategory = [SELECT Rejection_Category__c, Reply_Code__c, Trans_Code__c FROM TRR_Rejection_Category__mdt];        
		if(lstRejectCategory!=null && !lstRejectCategory.isEmpty())
		{
			for(TRR_Rejection_Category__mdt rejectRec : lstRejectCategory)
			{
				mapRejectCategory.put(rejectRec.Reply_Code__c+rejectRec.Trans_Code__c, rejectRec);
			}			
		}
		return mapRejectCategory;
	}   
}