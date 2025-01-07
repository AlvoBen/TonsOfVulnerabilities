/*****************************************************************************************************
 Apex Class Name  : CheckDuplicatesOnSource_H_HUM
 Version   : 1.0 
 Created Date  : 11/10/2016
 Function  : Holds the logic for trigger events on Source Object
 Test Class  :  CheckDuplicatesOnSource_T_HUM
 
 Modification Log :
* Developer                 Code Review         Date                   Description
* -----------------------------------------------------------------------------------------------------                 
* Anupama Tavva              49268            11/25/2016             REQ - 290277-Displays an error message if the source name already exists.
*******************************************************************************************************/

public class CheckDuplicatesOnSource_H_HUM
{
   /**
    *  Checks for duplicate source records with same source name on insert
    *  @name checkForDuplicateSourcesOnInsert
    *  @param lstSourceRecords Holds the list of all Source records
    *  @return void
    *  @throws NA
    */
    public static void checkForDuplicateSourcesOnInsert(List<MET_Source__c> lstSourceRecords)
    {
        Map<String,MET_Source__c> mapSource = new Map<String,MET_Source__c>();
        for(MET_Source__c oSource : lstSourceRecords)
        {
            mapSource.put(oSource.Name.toUpperCase(),oSource);
        }
        checkAndMarkErrorForDuplicates(mapSource);
    } 
    
    /**
    *  Checks for duplicate source records with same source name on update
    *  @name checkForDuplicateSourcesOnUpdate
    *  @param List<MET_Source__c> lstNewSourceRecords- Holds the list of all updated Source records
    *  @param Map<Id,MET_Source__c> mapOldSourceRecords- Holds a map with old source record values
    *  @return void
    *  @throws NA
    */
    public static void checkForDuplicateSourcesOnUpdate(List<MET_Source__c> lstNewSourceRecords,Map<Id,MET_Source__c> mapOldSourceRecords)
    {
        Map<String,MET_Source__c> mapSource = new Map<String,MET_Source__c>();
        for(MET_Source__c oSource : lstNewSourceRecords)
        {
            if(String.isNotBlank(oSource.Name) && !oSource.Name.equalsIgnoreCase(mapOldSourceRecords.get(oSource.Id).Name))
            {
                mapSource.put(oSource.Name.toUpperCase(),oSource);
            }
        }
        checkAndMarkErrorForDuplicates(mapSource);
    } 
    
    /**
    *  Queries the list of Sources matching the source name, if any duplicate source record exists an error will be thrown
    *  @name checkAndMarkErrorForDuplicates
    *  @param Map<String,MET_Source__c> mapSource
    *  @return void
    *  @throws NA
    */
    private static void checkAndMarkErrorForDuplicates(Map<String,MET_Source__c> mapSource)
    {
        for(MET_Source__c oSources : [SELECT Id, Name FROM MET_Source__c WHERE Name IN :mapSource.keyset()])
        { 
            mapSource.get(oSources.Name.toUpperCase()).addError(Label.UNIQUERECORD_SOURCE_HUM);
        }
    }
}