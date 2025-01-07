/*******************************************************************************************************************************
Apex Class Name : CoachingUnknownMemberEdit_LC_HUM 
Version         : 1.0
Created On      : 06/04/2021
Function        : This class is the controller class for coachingCreateUnknownMember LWC.
Test Class      : CoachingUnknownMemberEdit_LT_HUM
                  
Modification Log: 
* Developer Name                    Code Review         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                                         06/04/2021                 Initial version created for USER STORY 2081412.
* Jasmeen Shangari                                      11/02/2021                 Fix for defect 2831496
************************************************************************************************************************************/
public with sharing class CoachingUnknownMemberEdit_LC_HUM 
{
    private static final string STATUS_SUCCESS = 'success';
    private static final string STATUS_ERROR = 'error';

    /*
     * Method Name : createUnknownMember
     * Description : Method to create the Uknown Member account
     * Return type : String
     * Paramater   : Account
     */
    @AuraEnabled
    public static String createUnknownMember(Account acc) {
        String sRespJSON = null;
        try
        {            
            if(acc!=null)
            {
                if(Schema.SObjectType.Account.Fields.recordTypeId.isAccessible())
                {
                    acc.recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Member').getRecordTypeId();            
                }
                //Update the account fields similar to Existing classic unknown Member logic.
                if(Schema.SObjectType.Account.Fields.Individual_First_Name__c.isAccessible()) acc.Individual_First_Name__c = acc.FirstName;
                if(Schema.SObjectType.Account.Fields.Individual_Last_Name__c.isAccessible()) acc.Individual_Last_Name__c = acc.LastName;
                if(Schema.SObjectType.Account.Fields.General_Account__c.isAccessible()) acc.General_Account__c = true;
                if(Schema.SObjectType.Account.Fields.Tenant_Id__c.isAccessible()) acc.Tenant_Id__c = '00';
            }
            insert acc;            

            //Update the Account External ID in the default format
            if(Schema.SObjectType.Account.Fields.Account_External_Id__c.isAccessible()) acc.Account_External_Id__c = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC + acc.id;
            update acc;

            ResponseWrapper respWrap = new ResponseWrapper();
            respWrap.status = STATUS_SUCCESS;
            respWrap.accountId = acc.Id;

            sRespJSON = JSON.serialize(respWrap);

        }
        
        catch(exception ex)
        {
            Error_log__c errorLog = HUMExceptionHelper.logOtherExceptions(ex, 'CoachingUnknownMemberEdit_C_HUM','createAccount');
            if(errorLog!=null)
            {
                insert errorLog;
            }
            list<Error_Log__c> lstErrLogs = [SELECT Id, Name FROM Error_Log__c Where ID = :errorLog.Id];
            if(lstErrLogs!=null && !lstErrLogs.isEmpty())
            {
                
                ResponseWrapper errRespWrap = new ResponseWrapper();
                errRespWrap.status = STATUS_ERROR;
                errRespWrap.errorMsg = Label.Error_Contact_Administrator + ' : ' + lstErrLogs[0].Name;

                sRespJSON = JSON.serialize(errRespWrap);
            }        
        } 
        return sRespJSON;
    }
    
    /*
     * Inner wrapper class to create the response JSON string.
     */
    public class ResponseWrapper
    {
        public string status = '';
        public string accountId = '';
        public string errorMsg = '';
    }    
}