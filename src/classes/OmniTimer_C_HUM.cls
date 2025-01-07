/************************************************************************************************************************************************
  Apex Class Name  : OmniTimer_C_HUM
  Version          : 1.0 
  Created Date     : 21st Jan 2019
  Function         : Class to retrieve the case using chat key and transfer the case to accepting agent.
  Test Class       : OmniTimer_T_HUM
  * Version		Developer Name		Code Review		Date         Description
 ------------------------------------------------------------------------------------------------------------------------------
  * 1.0			Pradeep Dani		350910			2/11/2019    Original Version
  * 1.1			Joel George							5/16/2019	 Added Error log information capture logic
  * 1.2 		Lakshmi Madduri						5/29/2019	 Passing casenumber to the result.
  * 1.3 		Akshay Pai							9/01/2020	 REQ - 1208691 HP Chat Associates See Issue Text In Chat Window
  *****************************************************************************************************************************************************************************/

public without sharing class OmniTimer_C_HUM 
{       
    public OmniTimer_C_HUM()
    {
        User objUser = [select id, username, UserPermissionsLiveAgentUser from user where Id=:UserInfo.getUserId()];
        if(objUser.UserPermissionsLiveAgentUser == true)
        {
            bLiveAgentUser = true;
        }
		bSwitch = HUMUtilityHelper.isCRMFunctionalityON('1208691');
    }

    public boolean bLiveAgentUser{get;set;}
	public boolean bSwitch{get;set;}

    /**
    * Returns responses of RemoteAction method
    * <p>
    * Method to get the case Id and the live chat transcript
    *
    * @param String           chatKey
    * @param String           accId
    * @param String           sSecChatId
    * @return                 Success Message
    */
    @RemoteAction
	public static String transferCase(string accId, string sSecChatId)
	{
		String returnMessage = 'FAIL';
        String exceptionDetails='';
        String wQSetupId ='';
		try
		{
            User objUser = new User();
            list<case> lstOfCase =  new List<case>();
			String workQueueName;
            if (string.isNotBlank(accId) && string.isNotBlank(sSecChatId))
			{
				lstOfCase = [SELECT Id, CaseNumber, OwnerId, Owner_Queue__c, Case_Owner__c, Classification_Type__c, Department__c, Service_Center__c, ChatKey__c, LastModifiedby_Queue__c FROM Case
										WHERE accountId = :accId AND CreatedDate = TODAY AND origin =: GLOBAL_CONSTANT_HUM.WEBCHAT_LIVEAGENT_HUM and chatkey__c = : sSecChatId
										ORDER BY createdDate DESC LIMIT 1];

				if (lstOfCase != null && !lstOfCase.isEmpty())
				{
                    objUser = [select Current_Queue__c, id, username, Name, profile.Name from user where id = :UserInfo.getUserId()];
					if (objUser != null && objUser.Current_Queue__c != null)
					{
						workQueueName = objUser.Current_Queue__c;
						if (objUser.profile.Name == GLOBAL_CONSTANT_HUM.PHARMACYSPECIALIST_PROFILE_HUM)
						{
							for (GroupMember objGroupMember : [SELECT Group.name, Id, UserOrGroupId FROM GroupMember where group.name = :GLOBAL_CONSTANT_HUM.HUMANAPHARMACYWEBCHATGROUP and UserOrGroupId = : objUser.id limit 1])
							{
								workQueueName = GLOBAL_CONSTANT_HUM.HUMANAPHARMACYWEBCHATWQ;
							}
						}
						for (Work_Queue_Setup__c objWorkQueue : [Select Name, id, Classification_Type__r.Name, Classification_Type__c, Department__c, Public_Group_Name__c, Service_Center__c, Work_Queue_Name__c, OwnerId from Work_Queue_Setup__c Where IsActive__c = TRUE and Name = :workQueueName limit 1])
						{
                            wQSetupId = objWorkQueue.Id;
							lstOfCase[0].OwnerId = objUser.Id;
							lstOfCase[0].Case_Owner__c = objUser.Name;
							lstOfCase[0].Owner_Queue__c = objWorkQueue.name;
							lstOfCase[0].Classification_Type__c = objWorkQueue.Classification_Type__r.Name;
							lstOfCase[0].Department__c = objWorkQueue.Department__c;
							lstOfCase[0].Service_Center__c = objWorkQueue.Service_Center__c;
							lstOfCase[0].LastModifiedby_Queue__c = objWorkQueue.name;
							returnMessage = 'SUCCESS_' + lstOfCase[0].Id + '_' +lstOfCase[0].CaseNumber;
						}
						update lstOfCase[0];
					}
                    else
					{
                        exceptionDetails ='Details : User or User WorkQueue is null \n';
                        exceptionHandler(exceptionDetails,sSecChatId,objUser,lstOfCase[0],accId,wQSetupId);
                    }
                }
                else
				{
                    exceptionDetails ='Details : Case is null \n';
                    exceptionHandler(exceptionDetails,sSecChatId,null, null,accId,wQSetupId);
                }
            }
            else
			{
                exceptionDetails ='Details : Account id or chat id is null \n';
                exceptionHandler(exceptionDetails,sSecChatId,null, null,accId,wQSetupId);
            }


        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'OmniTimer_C_HUM', 'transferCase');
        }
        return returnMessage;
    }
    
    /**
    * Method to log the errors coccured in javascript execution.
    *
    * @param String           sName
    * @param String           sMessage
    * @param String           sStack
    * @return                 NA
    */
   
    public static void exceptionHandler(string sStack, String sSecChatId, User objUser, Case caseObj, String accId, String wQSetupId)
    {
        try
        {
            sStack += 'The Chat Key Value is -'+sSecChatId+'\n The accountId is -'+accId + '\n The WQ Setup Id is-'+wQSetupId;
            if(caseObj != null ) 
            sStack += '\n The Case OwnerId is -'+caseObj.OwnerId +'\n The Case Owner Queue is -'+caseObj.Owner_Queue__c + '\n The Case Classification type is -'+caseObj.Classification_Type__c;
			if(objUser != null)
			sStack +=	'\n The User Name is-'+objUser.username +'\n The User WorkQueue is -'+objUser.Current_Queue__c;
            
            Error_Log__c errorLog = new Error_Log__c();
            errorLog = new Error_Log__c(Error_Code__c ='HUMCustomException',
            Error_Message__c = sStack,
            Request_Sent__c = true,
            Record_Id__c = userinfo.getUserId(),
            class_name__c = 'OmniTimer_C_HUM',
            Method_Name__c = 'exceptionHandler',
            Type__c = 'Application Error');        
            insert errorLog;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'OmniTimer_C_HUM', 'exceptionHandler');
        }
    }
}