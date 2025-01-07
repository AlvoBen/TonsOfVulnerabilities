//public class CheckDuplicatesMETMilestone_H_HUM {
/*****************************************************************************************************
Apex Class Name  : CheckDuplicatesMETMilestone_H_HUM
Version   : 1.0 
Created Date  : 01/23/2016
Function  : Holds the logic for trigger events on MET Milestone Object for duplicate check
Test Class  : CheckDuplicatesMETMilestone_T_HUM

Modification Log :
* Developer                 Code Review         Date                   Description
* -----------------------------------------------------------------------------------------------------                 
* Manoj Chaurasia           202594              03/09/2018              REQ - 357740: Displays an error message if the MET Milestone record is already exist.
*******************************************************************************************************/

public class CheckDuplicatesMETMilestone_H_HUM
{
    /**
*  Checks for duplicate MET Milestone record on insert
*  @name checkForDuplicateMETMilestoneOnInsert
*  @param lstNewMETMilestoneRec Holds the list of all MET Milestone Records
*  @return void
*  @throws NA
*/
    
    public static void checkForDuplicateMETMilestoneOnInsert(List<MET_Milestone__c> lstNewMETMilestoneRec)
    {
        Map<string,MET_Milestone__c> mapCaseType = new Map<string,MET_Milestone__c>();
        Map<string,MET_Milestone__c> mapCaseSubtype = new Map<string,MET_Milestone__c>();
        Map<id,MET_Milestone__c> mapTask = new Map<id,MET_Milestone__c>();
        Map<id,MET_Milestone__c> mapAction = new Map<id,MET_Milestone__c>();
        Map<id,MET_Milestone__c> mapSource = new Map<id,MET_Milestone__c>();
        
        for(MET_Milestone__c oMETMilestone : lstNewMETMilestoneRec)
        {
            mapCaseType.put(oMETMilestone.Case_Type__c,oMETMilestone);
            mapCaseSubtype.put(oMETMilestone.Case_Subtype__c,oMETMilestone);
            mapTask.put(oMETMilestone.Task__c,oMETMilestone);
            mapAction.put(oMETMilestone.Action__c, oMETMilestone);
            mapSource.put(oMETMilestone.Source_Name__c,oMETMilestone);
        }
        
        fetchvalue(lstNewMETMilestoneRec,mapCaseType,mapCaseSubtype,mapTask,mapAction,mapSource);
    }
    
    /**
*  Checks for duplicate MET Milestone record on update
*  @name checkForDuplicateMETMilestoneOnUpdate
*  @param lstNewMETMilestoneRec Holds new MET Milestone records
*  @param mapOldMETMilestoneRecHolds old MET Milestone records
*  @return void
*  @throws NA
*/
    
    public static void checkForDuplicateMETMilestoneOnUpdate(List<MET_Milestone__c> lstNewMETMilestoneRec , Map<id,MET_Milestone__c> mapOldMETMilestoneRec)
    {
        Map<string,MET_Milestone__c> mapCaseType = new Map<string,MET_Milestone__c>();
        Map<string,MET_Milestone__c> mapCaseSubtype = new Map<string,MET_Milestone__c>();
        Map<id,MET_Milestone__c> mapTask = new Map<id,MET_Milestone__c>();
        Map<id,MET_Milestone__c> mapAction = new Map<id,MET_Milestone__c>();
        Map<id,MET_Milestone__c> mapSource = new Map<id,MET_Milestone__c>();
        
        for (MET_Milestone__c oMETMilestone : lstNewMETMilestoneRec )
        {
            if((oMETMilestone.Case_Type__c != Null && (oMETMilestone.Case_Type__c != mapOldMETMilestoneRec.get(oMETMilestone.id).Case_Type__c))
               || (oMETMilestone.Case_Subtype__c != Null && (oMETMilestone.Case_Subtype__c != mapOldMETMilestoneRec.get(oMETMilestone.id).Case_Subtype__c))
               || (oMETMilestone.Task__c != Null && (oMETMilestone.Task__c != mapOldMETMilestoneRec.get(oMETMilestone.id).Task__c))
               || (oMETMilestone.Action__c != Null && (oMETMilestone.Action__c != mapOldMETMilestoneRec.get(oMETMilestone.id).Action__c))
               || (oMETMilestone.Source_Name__c != Null && (oMETMilestone.Source_Name__c != mapOldMETMilestoneRec.get(oMETMilestone.id).Source_Name__c)))
            {
                mapCaseType.put(oMETMilestone.Case_Type__c,oMETMilestone);
                mapCaseSubtype.put(oMETMilestone.Case_Subtype__c,oMETMilestone);
                mapAction.put(oMETMilestone.Action__c, oMETMilestone);
                mapSource.put(oMETMilestone.Source_Name__c,oMETMilestone);
                mapTask.put(oMETMilestone.Task__c,oMETMilestone);
            }
        }
        fetchvalue(lstNewMETMilestoneRec,mapCaseType,mapCaseSubtype,mapTask,mapAction,mapSource);

    }
    
    /**
*  Checks for duplicate MET Milestone record 
*  @name fetchvalue
*  @param lstNewMilestone Holds new Milestone records
*  @param mapCaseType  Holds new Milestone records with key as Case Type
*  @param mapCaseSubtype  Holds new Milestone records with key as Case SubType
*  @param mapTask  Holds new Milestone records with key as Task
*  @param mapAction  Holds new Milestone records with key as Action
*  @param mapSource  Holds new Milestone records with key as Source
*  @return void
*  @throws NA
*/
    
    private static void fetchvalue( List<MET_Milestone__c> lstNewMilestone, Map<string,MET_Milestone__c> mapCaseType, Map<string,MET_Milestone__c> mapCaseSubtype, Map<id,MET_Milestone__c> mapTask, Map<id,MET_Milestone__c> mapAction , Map<id,MET_Milestone__c> mapSource)
    {
        Map<string,MET_Milestone__c> mapMETMilesone = new Map<string,MET_Milestone__c>();
        
        for (MET_Milestone__c oMilestone : lstNewMilestone )
        {
            mapMETMilesone.put(oMilestone.Case_Type__c+oMilestone.Case_Subtype__c+oMilestone.Task__c+oMilestone.Action__c+oMilestone.Source_Name__c, oMilestone);
            
            if(String.isBlank(oMileStone.Case_Type__c) || String.isBlank(oMileStone.Case_Subtype__c) || String.isBlank(oMileStone.Task__c)
               || String.isBlank(oMileStone.Action__c) || String.isBlank(oMileStone.Source_Name__c)){
                   mapMETMilesone.get(oMilestone.Case_Type__c+oMilestone.Case_Subtype__c+oMilestone.Task__c+oMilestone.Action__c+oMilestone.Source_Name__c).addError('Case Type, Case Subtype, Task, Action and Source values can not be blank.');
               }
        }
        
        for(MET_Milestone__c oDuplicateMilestoneRec : [SELECT Id, Name, Case_Type__c, Case_Subtype__c, Action__c, Task__c, Source_Name__c FROM MET_Milestone__c where  Case_Type__c IN : mapCaseType.keyset() and Case_Subtype__c IN : mapCaseSubtype.keyset() and Task__c IN : mapTask.keyset() and Action__c IN :mapAction.keyset() and Source_Name__c in :mapSource.keyset() ] )
        {
            mapMETMilesone.get(oDuplicateMilestoneRec.Case_Type__c+oDuplicateMilestoneRec.Case_Subtype__c+oDuplicateMilestoneRec.Task__c+oDuplicateMilestoneRec.Action__c+oDuplicateMilestoneRec.Source_Name__c).addError(Label.UNIQUERECORDASSOCIATEDSOURCE_ACTION);
        }
    }
}