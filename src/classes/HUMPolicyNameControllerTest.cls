/*******************************************************************************************************************************
Apex Class Name :HUMPolicyNameControllerTest
Version         : 1.0
Created On      : 08/05/2014
Function        : It is a test class for HUMPolicyNameController.

Modification Log: 
* Developer Name            Code Review                Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish                    17076                      8/5/2014                    Orginal Version   
* Asha Deepika Koyyana      18256                      10/1/2014                   Code fix for CC#18256 
* Asha Deepika Koyyana      18355                      10/07/2014                  Updated for Req #2411 
* Manish Kumar              19728 	                   12/05/2014                  Fixed production error
* Harika Devi Kalidindi     112000                     07/20/2017                  REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
* Prudhvi Pamarthi          165023                     01/19/2018                  REQ - 328929 Fix for HUMExceptionHelper: DML 
*******************************************************************************************************************************/
@isTest
private class HUMPolicyNameControllerTest
{
    /*
    * Method Name : policyNameTest
    * Description : Test method to Test HUMPolicyNameContoller
    * Return type : Void
    * Parameter   : NA
    */
    private static testMethod void policyNameTest()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs (oCCSUser)
        {
            Account oAccount = new Account(Name ='Test Account');
            insert oAccount;
            system.assertNotEquals(oAccount,null,'Account not Inserted');
            
            Account oAccountGroup = new Account(Name ='Test Group');
            insert oAccountGroup;
            system.assertNotEquals(oAccountGroup,null,'Account not Inserted');
            
            Policy__c oPolicy = new Policy__c(name = 'Test Policy', Group_Name__c = oAccountGroup.Id,Policy_Group_Number__c='11111');
            insert oPolicy; 
            system.assertNotEquals(oPolicy,null,'Policy not Inserted');
            
            Policy__c oPolicy1 = new Policy__c(name = 'Test Policy', Group_Name__c = oAccountGroup.Id,Policy_Group_Number__c='11111');
            insert oPolicy1; 
            system.assertNotEquals(oPolicy1,null,'Policy not Inserted');
            
            Policy_Member__c oPolicyMem = new Policy_Member__c(name = 'Test Policy Mem', Policy__c = oPolicy.Id, Member__c = oAccount.id );
            insert oPolicyMem; 
            system.assertNotEquals(oPolicyMem,null,'PolicyMember not Inserted');
            
            Policy_Member__c oPolicyMem1 = new Policy_Member__c(name = 'Test Policy Mem1',Policy__c = oPolicy1.Id, Member__c =  oAccount.id );
            insert oPolicyMem1; 
            system.assertNotEquals(oPolicyMem1,null,'PolicyMember not Inserted');
            
            ApexPages.currentPage().getParameters().put('accountId',oAccount.id);
            
            test.startTest();
            
            HUM_CBIS_Enabled__c oHUMCbisFlag = new HUM_CBIS_Enabled__c(Name = 'HUM_CBIS_Flag', CBIS_Flag__c = true );
            insert oHUMCbisFlag ;
            System.assertNotEquals(oHUMCbisFlag , NULL, 'CBIS flag is not inserted');
            
            HUMPolicyNameController oPolicyName = new HUMPolicyNameController();
            System.assertNotEquals(oPolicyName , NULL, 'Object not instantiated');
            
            oPolicyName.checkboxVerifyFamily();
            System.assert(true,oPolicyName.checkboxVerifyFamily()!= NULL);   
            
            oPolicyName.verifyFamily();
            System.assert(true,oPolicyName.verifyFamily()!= NULL); 
            List<Error_Log__c> lstLog = new List<Error_Log__c>();            
            Error_Log__c error = new Error_Log__c(Status__c = 'HUMPolicyNameController');            
            lstLog.add(error);                        
            oPolicyName.listErrorLogs = lstLog;            
            System.assert(oPolicyName.listErrorLogs.size() > 0);
            test.stopTest();
        }   
    }
}