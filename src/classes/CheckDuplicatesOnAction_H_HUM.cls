/*****************************************************************************************************
 Apex Class Name  : CheckDuplicatesOnAction_H_HUM
 Version   : 1.0 
 Created Date  : 11/10/2016
 Function  : Holds the logic for trigger events on Action Object
 Test Class  :  CheckDuplicatesOnAction_T_HUM
 
 Modification Log :
* Developer                 Code Review         Date                   Description
* -----------------------------------------------------------------------------------------------------                 
* Prasanthi Kandula            48595            11/10/2016             REQ - 290280 Displays an error message if the action name already exists.
*******************************************************************************************************/

public class CheckDuplicatesOnAction_H_HUM
{
   /**
    *  Checks for duplicate action records with same action name on insert
    *  @name checkForDuplicateActionsOnInsert
    *  @param lstActionRecords Holds the list of all Action records
    *  @return void
    *  @throws NA
    */
    public static void checkForDuplicateActionsOnInsert(List<MET_Action__c> lstActionRecords)
    {
        Map<String,MET_Action__c> mapAction = new Map<String,MET_Action__c>();
        for(MET_Action__c oAction : lstActionRecords)
        {
            mapAction.put(oAction.Name.toUpperCase(),oAction);
        }
        checkAndMarkErrorForDuplicates(mapAction);
    } 
    
    /**
    *  Checks for duplicate action records with same action name on update
    *  @name checkForDuplicateActionsOnUpdate
    *  @param List<MET_Action__c> lstNewActionRecords- Holds the list of all updated Action records
    *  @param Map<Id,MET_Action__c> mapOldActionRecords - Holds a map with old action record values
    *  @return void
    *  @throws NA
    */
    public static void checkForDuplicateActionsOnUpdate(List<MET_Action__c> lstNewActionRecords,Map<Id,MET_Action__c> mapOldActionRecords)
    {
        Map<String,MET_Action__c> mapAction = new Map<String,MET_Action__c>();
        for(MET_Action__c oAction : lstNewActionRecords)
        {
            if(String.isNotBlank(oAction.Name) && !oAction.Name.equalsIgnoreCase(mapOldActionRecords.get(oAction.Id).Name))
            {
                mapAction.put(oAction.Name.toUpperCase(),oAction);
            }
        }
        checkAndMarkErrorForDuplicates(mapAction);
    } 
    
    /**
    *  Queries the list of Actions matching the action name, if any duplicate action record exists an error will be thrown
    *  @name fetchExistingRecords
    *  @param Map<String,MET_Action__c> mapAction
    *  @return void
    *  @throws NA
    */
    private static void checkAndMarkErrorForDuplicates(Map<String,MET_Action__c> mapAction)
    {
        for(MET_Action__c oActions : [SELECT Id, Name FROM MET_Action__c WHERE Name IN :mapAction.keyset()])
        {     
            mapAction.get(oActions.Name.toUpperCase()).addError(Label.UNIQUERECORD_ACTION_HUM);
        }
    }
}