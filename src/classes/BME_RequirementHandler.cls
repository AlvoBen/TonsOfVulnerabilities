/**************************************************************
Apex Class: BME_RequirementHandler
Description: Utility class for trigger on Requirement object
Created By: Sagar Tapadia
Created On:  02/25/2014

Modification Log :
-----------------------------------------------------------------------------
* Developer                 Date                    Description
* ----------------------------------------------------------------------------                 
* Sagar Tapadia             02/25/2014              Original version
* Dinesh Subramaniyan		06/28/2018				REQ - 366993 - SF - USER - Enable Reporting on Release Date Field in CSI
* Prafull Verma             3/17/2021               US-2095692 Security Fix
**************************************************************/


public with sharing class BME_RequirementHandler {

  /*******************************************
  Method Name: updateSprintTotalStoryPoints
  Description: Roll up Story points from Requirement 
               to Total Story Points field in Sprint.
  *******************************************/
  public static void updateSprintTotalStoryPoints(Set<Id> sprintIds){
              list<Sprint__c>            updateSprintList     =      new list<Sprint__c>();

			//Looping over Sprint record and related Requirement records.
              for(Sprint__c spr:[Select Id,Total_Story_Points__c,(Select id,Story_Points__c from Requirements__r) from Sprint__c where ID IN:sprintIds]){
                     spr.Total_Story_Points__c = 0;
                     
                     //Looping over related Requirements to Sprint
                     for(Requirement__c req:spr.Requirements__r)
                     {
                     	//If Requirement has story points then Sum up requirement Story Points to Total Story Points in Sprint.
                     	   if(req.Story_Points__c!=null)
                           spr.Total_Story_Points__c += req.Story_Points__c;
                     }
                     updateSprintList.add(spr);
              }
       
       		//Update Sprint
              if(updateSprintList != null && updateSprintList.size()>0){
                     update updateSprintList;
              }
       }
       
	/**
    * Method Name - InsertReleaseModified
    * Assign values to release Last modified Date and Release Last modified by Name fields in requirment object. When a record get inserted into requirement object.
    * @ Param 		List variable of type Requirement__c
    * @ Return 		NA
    */
          
	public static void insertReleaseModified(list<Requirement__c> lstReleaseModified){
  		list<Requirement__c> lstUpdateRelease = new list<Requirement__c>();
			for(Requirement__c req:lstReleaseModified)
			{
				
			   if(req.Release__c !=null)
			   {
  
					lstUpdateRelease.add(new Requirement__c(Id=req.Id,Release_LastModified_By__c=req.LastModifiedByid,Release_Last_Modified_Date__c = String.ValueofGmt(req.LastModifiedDate)));

			   }
			}
			if(!lstUpdateRelease.isempty())
			update lstUpdateRelease;
    }

 	/**
 	* Method Name - UpdateReleaseModified
    * Assign values to release Last modified Date and Release Last modified by Name fields in requirment object. When a record is updated in requirement object.
    * @ Param 		List variable of type Requirement__c
    * @ Return 		NA
    */
           
	public static void updateReleaseModified(list<Requirement__c> lstNewReleaseModified,map<Id,Requirement__c> mapOldReleaseModified){
		list<Requirement__c> lstUpdateRelease = new list<Requirement__c>();
			for(Requirement__c req:lstNewReleaseModified)
			{

				if(req.Release__c != mapOldReleaseModified.get(req.Id).Release__c)
				{
					lstUpdateRelease.add(new Requirement__c(Id=req.Id,Release_LastModified_By__c=req.LastModifiedByid,Release_Last_Modified_Date__c = String.ValueofGmt(req.LastModifiedDate)));
				}

			}
			if(!lstUpdateRelease.isempty())
			update lstUpdateRelease;
    }
 
 
}