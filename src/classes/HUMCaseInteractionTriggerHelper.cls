/*******************************************************************************************************************************
Apex Class Name : HUMCaseInteractionTriggerHelper
Version         : 1.0
Created Date    : 06/27/2014
Function        : 1.This is a Trigger handler class for HUMCaseInteractionTrigger.Trigger.
				  Before Inserting record, this class checks for any existsing record which
				  exhibits similar relation as new one and indicates user about it by 
				  throwing user friendly error message.
Test Class      : HUMCaseInteractionTriggerHelperTest				  

Modification Log: 
 * Modification Id			Developer Name          Code Review                        Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0						Ninad Patil              16484                             06/27/2014                 Original Version
 * 1.1						Ninad Patil              17100                             08/05/2014	              Resubmitting after coding standards correction.
 * 1.2						Harshith Mandya											   18/11/2016				  Defect 248994.0001  - Populate lookup fields on Interaction object 
 * 1.3 						Sharan Shanmugam 		 50226						  	   11/30/2106				  Implemented REQ - 290990 -  removed all lookup related fields from 
																												  Case/Task/Interaction/Attachment to Work Queue Setup and Queue View
*  1.4                      			Moshitha Gunasekaran                                       01/20/2023                 US3788451 - T1PRJ0337737 - 2022 - SF- additional Access for Mulesoft user for Case and Interaction details
 *******************************************************************************************************************************/
public class HUMCaseInteractionTriggerHelper 
{
	/**
    * <p>
    * Execute all on before Insert Trigger Events
    *
    * @param     lstNewCaseIntRecords     List of Case Interaction records
    * @param	 bTriggerContextAction	  Boolean to check the context
    * @param	 bTriggerContextEvent	  Boolean to check the Event
    * @return    Void
    */
	public static void onBeforeInsert(List<Case_Interaction__c> lstNewCaseIntRecords, boolean bTriggerContextAction, boolean bTriggerContextEvent)
	{
		checkForDuplicateAssociation(lstNewCaseIntRecords, bTriggerContextAction, bTriggerContextEvent);
	}

	/**
    * <p>
    * This Method checkes if Case and Interaction ids being associated 
    * 				 are already associated with each other or not, if yes, It will throw error 
    * 				 on screen and will not save record.
    *
    * @param     lstNewCaseIntRecords     List of Case Interaction records
    * @param	 bTriggerContextAction	  Boolean to check the context
    * @param	 bTriggerContextEvent	  Boolean to check the Event
    * @return    Void
    */
	public static void checkForDuplicateAssociation(List<Case_Interaction__c> lstNewCaseIntRecords, 
													boolean bTriggerContextAction, 
													boolean bTriggerContextEvent)
	{	
		if(bTriggerContextAction == true && bTriggerContextEvent == true) 
		{
			//Using Map to store Case Ids and their corresponding InteractionIds.
			MAP<ID,List<ID>> mapCaseInt = new MAP<ID,List<Id>>();
			
			// Fetching the current user queue
			User currentUser = [SELECT Id, Current_Queue__c FROM User where id = :userInfo.getUserId()];
			for(Case_Interaction__C oCaseInt : lstNewCaseIntRecords)
			{
				mapCaseInt.put(oCaseInt.case__C, null);
			}
	
			for(Case_Interaction__c oCaseInt : [SELECT Interaction__C, Case__C 
												FROM Case_Interaction__C 
												WHERE Case__C IN :mapCaseInt.Keyset()])
			{
				List<ID> lstIntIDs = mapCaseInt.get(oCaseInt.case__C);
				if(lstIntIDs == null) lstIntIDs = new List<ID>();
				lstIntIDs.add(oCaseInt.Interaction__C);
				if(!lstIntIDs.isEmpty()) mapCaseInt.put(oCaseInt.case__C,lstIntIDs);
			}
	
			//Checking whether interaction id already associated to case id, if yes then throwing error on screen.
			for(Case_interaction__C oCaseInt : lstNewCaseIntRecords)
			{
				Set<ID> setIntIds = new Set<ID>();
				if(mapCaseInt.containsKey(oCaseInt.case__C))
				{
					List<ID> lstIntIDs = new List<ID>();
					lstIntIDs = mapCaseInt.get(oCaseInt.case__C);
					if(lstIntIDs!=null) setIntIds.addAll(lstIntIDs);
					if(setIntIds.contains(oCaseInt.Interaction__C)) oCaseInt.AddError(System.Label.HUMDupCaseInteractionError);
				}
			} 
		}
	} 
	
	/**
    * <p>
    * Update Created By Queue field on Interaction
    *
    * @param     lstCaseInteractions     List of Case Interaction records
    * @return    Void 
    */
    public static void updateCreatedByQueue(List<Case_Interaction__c> lstCaseInteractions) 
    {
    	try
    	{
    		Set<Id> setInteractionIds = new Set<Id>();
    		List<Interaction__c> lstInteractions = new List<Interaction__c>();
    		List<Work_Queue_Setup__c> lstWorkQueueSetup = new List<Work_Queue_Setup__c>();
    		// Fetching the current user's details
            User currentUser = [SELECT Id,Profile.name,current_Queue__c FROM User Where id =:UserInfo.getUserId()  LIMIT 1];
            // Fetching the current user's queue deails
            if(currentUser.Profile.name!= 'ETL API Access' && !HUMUtilityHelper.hasUserMulesoftAccess())
            {
    		for(Case_Interaction__c interaction :  lstCaseInteractions)
    		{
    			setInteractionIds.add(interaction.Interaction__c);
    		}
    		// Update the Created By Queue on Interaction records
    		for(Id interactionId : setInteractionIds)
    		{
    			lstInteractions.add(new Interaction__c(Id = interactionId,Created_By_Queue__c = currentUser.current_Queue__c));
    		}
    		update lstInteractions;
    	}
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Attachment_H_HUM','updateCreatedByQueue');
        }
    }
}