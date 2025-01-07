/**************************************************************************************************************************************************
Apex class Name    :  RTISummary_T_HUM
Version            :  1.0
Function           :  This is the test class for RTISummary_C_HUM controller class

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------
 *  Kritika Popat                                        06/26/2017         Original version
 *  Kritika Popat                                        05/15/2018         REQ - 357638 Collapse Panel for Provider
 *  Sumit Pharande 									     10/06/2018	        NBA Rollback	
 *  Sumit Pharande                                       12/9/2018          NBA Changes
 *  Sumit Pharande			 							                    REQ-386780 -Removed the 18 to 15 Digit Id conversion
 *  Anjani Vaddadi                                       04/09/2020         REQ-845546
*  Rahul Chaurasia								   03/22/2021				  1931279 - PR00094226-MF 4-Tech-Decommissioned RTI Service

*****************************************************************************************************************************/
@isTest
public  with sharing class RTISummary_T_HUM
{
   /*
    * Method Name : prepareTestData
    * Description : Used to create wrapper class data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test';
            objAcc1.LastName = 'Account';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1023880950';
            objAcc1.General_Account__c = true;
            objAcc1.Home_Office_Account__c = true;
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');
                                 
            Account account = New Account();
            account.NPI_ID__c='11111';
            account.Name='Test Provider';
            account.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMLaunchGCPCCP_Provider).getRecordTypeId();
            insert account;
            System.assertEquals(account.NPI_ID__c,'11111');

            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.Name = 'Test Account2';
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMSearchTypeProvider).getRecordTypeId();
            insert objAcc2;
            System.assertEquals(objAcc2.Name,'Test Account2');
            
            Interaction__c objInt = HUMTestDataHelperTest.getInteraction();
            objInt.Interacting_With_type__c = 'Provider';
            objInt.Interacting_With__c = objAcc2.Id;
            insert objInt;
            System.assertEquals(objInt.Interacting_With_type__c,'Provider');
            
            Interaction_Member__c objIntMem = HUMTestDataHelperTest.getInteractionMember();
            objIntMem.Interacting_About__c = objAcc1.Id;
            objIntMem.Interacting_About_type__c = 'Member';
            objIntMem.Interaction__c = objInt.Id;
            insert objIntMem;
            System.assertEquals(objIntMem.Interacting_About_type__c,'Member');  

        List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name = 'HOME_OFFICE_ALL',StringValue__c = 'Home Office All',IntegerValue__c = 0));
            lstConstants.add(new HUMConstants__c(Name = 'HOME_OFFICE_MEDICAL',StringValue__c = 'HOME_OFFICE_MEDICAL',IntegerValue__c = 0));
            lstConstants.add(new HUMConstants__c(Name = 'HOME_OFFICE_DENTAL',StringValue__c = 'HOME_OFFICE_DENTAL',IntegerValue__c = 0));
            insert lstConstants;
            system.assertEquals(lstConstants.size(), 3); 
        
        }
    }   
    
    /*
     * Method Name : runPositiveData
     * Description : Used to run scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void runData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where LastName = 'Account'];  
           System.assertEquals(oAccount.Tenant_Id__c, '00');
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Member');
           
           System.currentPageReference().getParameters().put('id', oAccount.Id);

           ApexPages.currentPage().getParameters().put('Id',oAccount.Id);
           RTISummary_C_HUM oRTISummary = new RTISummary_C_HUM();
           oRTISummary.urlString =  System.currentPageReference().getParameters().put('id', oAccount.Id);
           oRTISummary.getVerifyAccountType();
           System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oAccount.Id);
           
           
		   Test.stopTest();  
        }
        
    }

    static testMethod void runNegativeData()
     {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
           
        System.runAs (oCCSUser) 
        { 
           Test.StartTest();
           Account oAccount = [Select Id,Name,Tenant_Id__c,RecordType.Name,Enterprise_ID__c  from Account where NPI_ID__c= '11111'];  
           String sAccRecType = oAccount.RecordType.Name;
           System.assertEquals(sAccRecType,'Provider');
           
           System.currentPageReference().getParameters().put('id', oAccount.Id);

           ApexPages.currentPage().getParameters().put('Id',oAccount.Id);
           RTISummary_C_HUM oRTISummary = new RTISummary_C_HUM();
           oRTISummary.urlString =  System.currentPageReference().getParameters().put('id', oAccount.Id);
           oRTISummary.getVerifyAccountType();
           System.assertEquals(ApexPages.currentPage().getParameters().get('Id'),oAccount.Id);
           
           
           Test.stopTest();  
        }
        
    }
}