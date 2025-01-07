/*******************************************************************************************************************************
Apex Class Name : EmailTaskCallBackRemainder_D_HUM
Version         : 1.0
Created On      : 10/25/2021
Function        : Helper Class for batch EmailTaskCallBackRemainder_B_HUM
Test Class      : EmailTaskCallBackRemainder_T_HUM

Modification Log: 
* Developer Name                         Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Vinay Lingegowda                                                  10/25/2021                 USER STORY 2769842 T1PRJ0001827 - IR 5050 - Send Task Call Back reminder emails to supervisors (RF)
*******************************************************************************************************************************/
public with sharing class EmailTaskCallBackRemainder_D_HUM{
    
    /**
     * get Profile details
     * @return Id
     */
    public static Id getProfileID(){
        String sProfileID = [SELECT Id FROM Profile WHERE Name ='System Administrator' Limit 1].Id;  
        return sProfileID;
    }
    
    
    /**
     * get OrgWide default no-reply Email Address
     * @param String 
     * @return Id
     */
    public static Id getOrgWideEmailAddress(String sNoReplyAddress) {
        String sId = [SELECT Id, DisplayName FROM OrgWideEmailAddress WHERE Address = :sNoReplyAddress].Id;
        return sId;
    }
    

    /**
     * get query for User list
     * @param Set<Id>
     * @param List<String>
     */
    public static Map<Id,User> getSupervisiorDetails(Set<Id> setSupUserId, List<String> sProfiles){ 
        Map<Id,User> mapUserDetails = new  Map<Id,User>([SELECT Id, Email, Manager_Name__c, profile.name, Supervisor_Name__c, Supervisor_Email_Address__c, Network_User_Id__c 
                                                        FROM User 
                                                        WHERE ID IN :setSupUserId 
                                                        AND PROFILE.Name IN :sProfiles 
                                                        AND Supervisor_Email_Address__c like '%@humana.com' WITH SECURITY_ENFORCED]); 

        return mapUserDetails;
    }


    /**
     * method to check email and send accordingly.
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