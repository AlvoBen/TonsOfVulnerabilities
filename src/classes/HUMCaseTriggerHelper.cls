/*******************************************************************************************************************************
Apex Class Name : HUMCaseTriggerHelper
Version         : 1.0
Created On      : 05/21/2014
Function        : 1.This class is called from Trigger on Case.
                  2.This Creates Association between Case and Interaction on click of Save.
                  3.It fetches the latest Interaction created by the logged in user and 
                    Associates it to current case being saved.
                  4.This class is called from trigger and needs to read all Case/Case_Interaction__c/Interaction__C
                    records irrespective of data model so that duplicacy can be avoided, Hence running this class 
                    explicitly in "Without Sharing Mode".  
Test class		: HUMCaseTriggerHelperTest                    

Modification Log: 
 * Modification ID			Developer Name          Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0						Ninad Patil             	16484                   05/21/2014                 Original Version
 * 1.1						Ninad Patil             	16484                   06/20/2014                 Code Changed for Avoiding dependancy on latest
                                                                              								interaction record added by the user.
 * 1.2						Ninad Patil			        16484    		        07/16/2014                 Fixed code review coments.
 * 1.3                      Ninad Patil                 17100                   08/09/2014                 Resubmitting after fixing code review Comments.                                                                             
 *******************************************************************************************************************************/
public without sharing class HUMCaseTriggerHelper 
{
	/*
	 * Method Name   :    onAfterInsert
	 * Description   :    Execute all on After Insert Trigger Events.
	 * Return Type   :    None
	 * Parameters    :    List<Case>
	 					  Boolean bTriggerContextAction : sends context variable for trigger mode(Insert/Update/Delete etc)
	 				 	  Boolean bTriggerContextEvent  : sends context variable for trigger event(isBefore/isInsert/isUpdate etc)
	 */
	public static void onAfterInsert(List<Case> lstNewCaseList, boolean bTriggerContextAction, boolean bTriggerContextEvent)
	{
		createCaseInteractionRecord(lstNewCaseList, bTriggerContextAction, bTriggerContextEvent);
	}

	/*
	 * Method Name : createCaseInteractionRecord
	 * Description : This method associates Case being saved to the Interaction if any.
	 * Return type : void
	 * Paramater   : List<Case> (List of Case object being modified)
	 				 Boolean bTriggerContextAction : sends context variable for trigger mode(Insert/Update/Delete etc)
	 				 Boolean bTriggerContextEvent  : sends context variable for trigger event(isBefore/isInsert/isUpdate etc) 
	 */ 
	public static void createCaseInteractionRecord(List<Case> lstNewCaseList, boolean bTriggerContextAction, boolean bTriggerContextEvent)
	{
		if(bTriggerContextAction == true && bTriggerContextEvent == true) 
		{
			try
			{
				//Initializing variables for storing Interaction and Cases for bulkification of trigger.
				Map <ID, List<ID>> MapInteractionCaseIDs = new Map<ID, List<ID>>();
				List<Case_Interaction__c> lstCaseInteraction = new List<Case_Interaction__c>();
	
				//Populating Map for storing all the interaction IDs from Case being modified.
				for(Case oCase : lstNewCaseList)
				{
					MapInteractionCaseIDs.put(oCase.Interaction_ID__c, null);
				}
	
				//Creating Map for Interaction IDs verses Case IDs. 
				for(Case_Interaction__c oCaseInt : [Select Interaction__c,Case__c from Case_Interaction__c 
				                                    WHere Interaction__c IN :MapInteractionCaseIDs.keyset()])
				{
					List<ID> lstCaseID = MapInteractionCaseIDs.get(oCaseInt.Interaction__c);
					if(lstCaseID == null) lstCaseID = new List<ID>();
					if(!String.isblank(oCaseInt.Case__c))lstCaseID.add(oCaseInt.Case__c);
					MapInteractionCaseIDs.put(oCaseInt.Interaction__c,lstCaseID);
				}
	
				//Checking Whether Case ID is aleardy associated with Interaction ID, if not, then associate it.
				for(Case oCase :lstNewCaseList)
				{ 
					if(MapInteractionCaseIDs.containsKey(oCase.Interaction_ID__c))
					{
						List<ID> lstCaseIDs = MapInteractionCaseIDs.get(oCase.Interaction_ID__c);
						Set<ID> setCaseIDs = new Set<ID>();
						if(lstCaseIDs != null)setCaseIDs.addall(lstCaseIDs);
						if(!setCaseIDs.contains(oCase.ID))
						{
							if(!String.isblank(oCase.Interaction_ID__c))
							{
								Case_Interaction__c oCaseInt=new Case_Interaction__c(Case__c=oCase.ID,
										Interaction__c=oCase.Interaction_ID__c);
								lstCaseInteraction.add(oCaseInt);
							}
						}
					}          
				}
	
				savepoint sp = database.setSavepoint();
	
				//Inserting list for Case Interaction object.
				if(!lstCaseInteraction.isEmpty())
				{
					Database.SaveResult[] lstSvr = Database.insert(lstCaseInteraction,false);
					boolean isError=HUMExceptionHelper.processSaveResults(lstSvr ,lstCaseInteraction ,
					'HUMCaseTriggerHelper','createCaseInteractionRecord','case_Interaction__c');
					if(isError == true) Database.Rollback(sp);  
				}
			}
			Catch(Exception e)
			{
				HUMExceptionHelper.logErrors(e,'HUMCaseTriggerHelper','createCaseInteractionRecord');
			}
		} 
	}
}