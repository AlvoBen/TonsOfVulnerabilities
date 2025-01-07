/**************************************************************
Apex Class: BME_RequirementEventsTrigger
Description: Trigger on Requirement object
Created By: Sagar Tapadia
Created On:  02/25/2014

Modification Log :
-----------------------------------------------------------------------------
* Developer                 Date                    Description
* ----------------------------------------------------------------------------                 
* Sagar Tapadia             02/25/2014              Original version
* Dinesh Subramaniyan		06/28/2018				REQ - 366993 - SF - USER - Enable Reporting on Release Date Field in CSI
**************************************************************/
trigger BME_RequirementEventsTrigger on Requirement__c (after delete, after insert, after update) {

       //Declaration of Variables.
       Set<Id>         sprintSet     =      new Set<Id>();
       
       //Trigger for event after and insert/update/delete
       if(Trigger.IsAfter && (Trigger.isInsert || Trigger.isUpdate || Trigger.isDelete)){
              for(Requirement__c req:(Trigger.isDelete)?trigger.old:trigger.new){
                     if(req.Assigned_Sprint__c!=null){
                           //Getting Sprint for requirements. 
                           sprintSet.add(req.Assigned_Sprint__c);
                     }
              }
              if(sprintSet != null && sprintSet.size()>0){
                     //Calling method from RequirementUtility Class inorder to update Total Story Points.
                     try{
                     BME_RequirementHandler.updateSprintTotalStoryPoints(sprintSet);
                     }
                     catch(Exception e)
                     {
                     	System.debug('The following exception has occurred: ' + e.getMessage());
                     }
              }
       }
       
       // To Update the Last modified by Data/By value of the release field in requirement object.
       if(Trigger.isInsert){
       	 BME_RequirementHandler.insertReleaseModified(trigger.new);
       }
       
   	   if(Trigger.isUpdate){
   			BME_RequirementHandler.updateReleaseModified(trigger.new,trigger.oldmap);
   	   }
}