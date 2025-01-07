/*
  Apex Class Name  : OmniQueueUserDataTable_X_HUM
  Version          : 1.0 
  Created Date     : OCT 25 2017
  Function         : Class to load users in the OmniQueueUserDataTable_VF_HUM page.
  Test Class       : OmniQueueUserDataTable_T_HUM
*********************************************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name             Review Number               Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Syed Mubarak T                                      10/25/2017                      Original Version
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------*/

public with sharing class OmniQueueUserDataTable_X_HUM
{

    public List<User> omniUsers {get;set;}
    public OmniQueue__c omniQueueRec = NULL;
    
    public OmniQueueUserDataTable_X_HUM(ApexPages.StandardController controller)
    {
     if(!Test.isRunningTest())
     {
        controller.addFields(new List<String>{'Omni_Queue_ID__c'});
     }
     omniQueueRec = (OmniQueue__c)controller.getRecord();
    }
    
     public void fetchOmniUsers()
    {
            omniUsers = new List<User>();
            set<String> userIds = new set<String>();
            if(String.isNotBlank(omniQueueRec.Omni_Queue_ID__c))
            {
                for(GroupMember groupMem : [SELECT Id, GroupId, UserorGroupId FROM GroupMember where GroupID =:omniQueueRec.Omni_Queue_ID__c] )
                {
                    userIds.add(groupMem.UserorGroupId);
                }
            }
            if(!userIds.isEmpty())
            {
                omniUsers = [SELECT Id, FirstName, LastName, Email from User where Id IN :userIds];
            }  
    } 


}