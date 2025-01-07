/***************************************************************************************************
Apex Class Name  : DuplicateUserOnPreferencesHelper_H_HUM 
Version          : 1.0
Created Date     : 06/27/2019
Function         : Helper class for DuplicateUserOnPreferences_G_HUM Trigger
**************************************************************************************************
Modification Log:

* Developer Name       Review                       Date                        Description
*--------------------------------------------------------------------------------------------------
*  Santhosh Ganji                               06/27/2019            Original Version. 
***************************************************************************************************/
public class DuplicateUserOnPreferencesHelper_H_HUM {
   
    public class controllerException extends Exception {}
    
    /**
    *  Checks for duplicate preference records with same user name on insert
    *  @name checkForDuplicateUsersOnInsert
    *  @param lstRecordsHolds the list of all preference records
    *  @return void
    *  @throws NA
    */
    public static void checkForDuplicateUsersOnInsert(List<Storefront_Preference__c> lstRecords)
    {
        
        Map<String,Storefront_Preference__c> mapUser = new Map<String,Storefront_Preference__c>();
        for(Storefront_Preference__c oRecord : lstRecords)
        {
            mapUser.put(oRecord.User__c,oRecord);
        }
        checkAndMarkErrorForDuplicates(mapUser);
    } 
    
     /**
    *  Checks for duplicate preference records with same user name on update
    *  @name checkForDuplicateUsersOnUpdate
    *  @param List<Default_Location__c> lstNewRecords- Holds the list of all updated preference records
    *  @param Map<Id,Default_Location__c> mapOldRecords- Holds a map with old preference record values
    *  @return void
    *  @throws NA
    */
    public static void checkForDuplicateUsersOnUpdate(List<Storefront_Preference__c> lstNewRecords, Map<Id,Storefront_Preference__c> mapOldRecords)
    {
        Map<String,Storefront_Preference__c> mapUser = new Map<String,Storefront_Preference__c>();
        
        for(Storefront_Preference__c oRecord : lstNewRecords)
        {
            if(String.isNotBlank(oRecord.User__C) && !oRecord.User__C.equals(mapOldRecords.get(oRecord.Id).User__C))
            {
                mapUser.put(oRecord.User__C,oRecord);
            }
        }
        checkAndMarkErrorForDuplicates(mapUser);
    }
    
    /**
    *  Queries the list of preference matching the user name, if any duplicate user record exists an error will be thrown
    *  @name checkAndMarkErrorForDuplicates
    *  @param Map<String,Default_Location__c> mapUser
    *  @return void
    *  @throws NA
    */
   private static void checkAndMarkErrorForDuplicates(Map<String,Storefront_Preference__c> mapUser)
    {
        for(Storefront_Preference__c oRecords : [SELECT Id, User__C FROM Storefront_Preference__c WHERE User__C IN :mapUser.keyset()])
        {     
            mapUser.get(oRecords.User__C).addError(Label.UNIQUERECORD_PREFERENCES_HUM);
        }
        
        if(Test.isRunningTest())
            {
                throw new controllerException();
            }
    } 
}