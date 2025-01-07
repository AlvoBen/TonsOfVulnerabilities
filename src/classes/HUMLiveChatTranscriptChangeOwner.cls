/*******************************************************************************************************************************
Apex Class Name : HUMLiveChatTranscriptChangeOwner 
Version         : 1.0
Created On      : 09/17/2014
Function        : 1.This class is called from Trigger on LiveChatTranscript.
                  2.This Creates Association between Case and LiveChatTranscript.
Test Class      : HUMLiveChatTranscriptTriggerHelperTest
Sharing Mode    : "Without" sharing is choosen because when there is transfer of chat between Hp and Non-HP, owner will not change in case of with sharing.

Modification Log: 
 * Modification ID  Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0              Joel George                                     02/24/2020                Original Version  

 *******************************************************************************************************************************/
public Without Sharing class HUMLiveChatTranscriptChangeOwner
{ 
    public static void changeCaseOwnerShip(Map<Id,Id> mapOfCasesWithOwners)
    {
        Set<Id> SetOfOwnerIds = new Set<Id>();
        List<Case> lstOfCasesToUpdate = new List<Case>();
        Set<String> setCurrentQueue = new Set<String>();
        Work_Queue_Setup__c currentWQ;
        for(Case objCase : [SELECT Id, ownerId from Case where id in:mapOfCasesWithOwners.KeySet()])
        {
            if(objCase.OwnerId != mapOfCasesWithOwners.get(objCase.Id))
            {
                SetOfOwnerIds.add(mapOfCasesWithOwners.get(objCase.Id));
                lstOfCasesToUpdate.add(objCase);
            }
        }
        
        Map<Id,User> mapOfUsers = new Map<Id,User>([Select name,Current_Queue__c, id from user where id =: SetOfOwnerIds]);
        for(User objUser : mapOfUsers.values())
        {
            setCurrentQueue.add(objUser.Current_Queue__c);
        }
        map<String,Work_Queue_Setup__c> mapWorkQueueSetup = new Map<String,Work_Queue_Setup__c>();
        for(Work_Queue_Setup__c objWorkQueue : [Select Name, id, Classification_Type__r.Name, Classification_Type__c, Department__c, Public_Group_Name__c, Service_Center__c, Work_Queue_Name__c from Work_Queue_Setup__c Where IsActive__c = TRUE and Name = :setCurrentQueue ])
        {
            mapWorkQueueSetup.put(objWorkQueue.Name, objWorkQueue);
        }
        for(Case objCasetoUpdate : lstOfCasesToUpdate)
        {
            
            objCasetoUpdate.OwnerId = mapOfCasesWithOwners.get(objCasetoUpdate.Id);
            currentWQ = mapWorkQueueSetup.get(mapOfUsers.get(mapOfCasesWithOwners.get(objCasetoUpdate.Id)).Current_Queue__c);
            objCasetoUpdate.Owner_Queue__c = currentWQ.name;
            objCasetoUpdate.Case_Owner__C = mapOfUsers.get(mapOfCasesWithOwners.get(objCasetoUpdate.Id)).Name;
            objCasetoUpdate.Classification_Type__c = currentWQ.Classification_Type__r.Name;
            objCasetoUpdate.Department__c = currentWQ.Department__c;
            objCasetoUpdate.Service_Center__c = currentWQ.Service_Center__c;
            objCasetoUpdate.LastModifiedby_Queue__c = currentWQ.Name; 
        }
        update lstOfCasesToUpdate;
    }
}