/*******************************************************************************************************************************
Trigger Name    : DuplicateUserCheckOnPreferences_G_HUM
Version         : 1.0
Created On      : 06/27/2019 
Function        : Trigger execution logic to find duplicate user value records for Preferences object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*  Santhosh Ganji                               06/27/2019            REQ - 402630 to avoid duplicate user for Source version
*******************************************************************************************************************************/
trigger DuplicateUserCheckOnPreferences_G_HUM on Storefront_Preference__c (before insert,before update) {
   Map<String, Object> params = new Map<String, Object>();
    try{
       if(Trigger.isBefore)
       {
          if(Trigger.isInsert)
          {
              DuplicateUserOnPreferencesHelper_H_HUM.checkForDuplicateUsersOnInsert(Trigger.New);
          }
          if(Trigger.isUpdate)
          {
              DuplicateUserOnPreferencesHelper_H_HUM.checkForDuplicateUsersOnUpdate(Trigger.New,Trigger.oldMap);
          }
       }
    }
    catch(Exception e)
    {
         HUMExceptionHelper.logErrors(e, 'DuplicateUserOnPreferences_G_HUM', 'DuplicateUserOnPreferences_G_HUM');
    }
}