/*******************************************************************************************************************************
Apex Class Name : EmailOpenInventory_D_HUM
Version         : 1.0
Created On      : 04/08/2021
Function        : Helper Class for batch EmailOpenInventory_B_HUM
Test Class      : EmailOpenInventory_T_HUM 

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Subhamay Mandal                                                   04/15/2021                 Initial version
*******************************************************************************************************************************/
public with sharing class EmailOpenInventory_D_HUM {
    /**
     * get OrgWide default no-reply Email Address
     * @param String 
     * @return Id
     */
    public static Id getOrgWideEmailAddress(String sNoReplyAddress) {
        String sId = [SELECT Id, DisplayName FROM OrgWideEmailAddress where Address = :sNoReplyAddress].Id;
        return sId;
    }
    /**
     * get user details for batchable list
     */
    public static Map<Id, User> queryUserDetails(List<User> lstUser) {
        Map<Id, User> mapUser = new Map<Id, User>();
        if(!lstUser.isEmpty()) {
            mapUser = new Map<Id, User>([Select ID, Email, Name from User where Id in :lstUser 
                                                AND Current_Queue__c != '' AND Email like '%@humana.com' Order by Name]);
        }
        return mapUser;
    }

    /**
     * Get list of Case records for set of user.
     * @param Set<Id> 
     * @param String[] 
     * @param Integer 
     * @param Integer 
     * @return List<Case>
     */
    public static List<Case> queryCaseRecords(Set<Id> setId, String[] arCaseStatus, Integer iQueryLimit, Integer iQueryAgeLimit) {

        return [Select OwnerId, casenumber, Case_Age__c, Owner_Queue__c, Work_Queue_View_Name__c, LastModifiedDate, Follow_up_Due_Date__c, Response_Status__c 
                    FROM Case WHERE OwnerId IN : setId AND Owner.Type = 'User' 
                    AND Status IN :arCaseStatus AND Case_Age__c >= : iQueryAgeLimit  
                    ORDER BY OwnerId, Case_Age__c DESC, CreatedDate DESC limit :iQueryLimit];
    }

    /**
     * Get list of Task records for set of user.
     * @param Set<Id>
     * @param String[] 
     * @param Integer  
     * @param Integer 
     * @return List<Task>
     */
    public static List<Task> queryTaskRecords(Set<Id> setId, String[] arTaskStatus, Integer iQueryLimit, Integer iQueryAgeLimit) {
        
        return [Select OwnerId, QueueOrUserId__c, Task_number__c, Task_Age__c, Owner_Queue__c, Work_Queue_View_Name__c, LastModifiedDate, ActivityDate 
                    FROM Task where Owner.Type = 'User' AND OwnerId IN : setId AND QueueOrUserId__c IN: setId   
                    AND Status IN :arTaskStatus AND Task_age__c >= : iQueryAgeLimit  
                    ORDER BY OwnerId, Task_Age__c DESC, CreatedDate DESC limit :iQueryLimit];
    }

    /**
     * method to check individual email and send accordingly
     * @param List<Messaging.singleEmailMessage>
     * @return void
     */
    public static void manageSendEmail(List<Messaging.singleEmailMessage> lstSendMails) {
        List<Messaging.SendEmailResult> sendEmailResults = new List<Messaging.SendEmailResult>{};
        String sErrorMessage = '';
        sendEmailResults = Messaging.sendEmail(lstSendMails, false);

        for(Messaging.SendEmailResult sendEmailResult: sendEmailResults) {
            if(!sendEmailResult.isSuccess()) {
                for (Messaging.SendEmailError sendEmailError : sendEmailResult.getErrors()) {
                    sErrorMessage += 'Error Message: '+ sendEmailError.getMessage() + '\n';
                }
            }
        }
        if(String.isNotBlank(sErrorMessage)) {
            throw new EmailOpenInventory_H_HUM.EmailOICustomException(sErrorMessage);
        }
    } 
}