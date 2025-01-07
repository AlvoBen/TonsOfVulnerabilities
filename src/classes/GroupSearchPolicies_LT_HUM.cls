/*******************************************************************************************************************************
Apex Class Name : GroupSearchPolicies_LT_HUM
Version         : 1.0
Created On      : 10/08/2020
Function        : This class is used for performing the Lightning Group Policies Search

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0          Sai Kumar Reddy                                  	10/08/2020                	Original Version
*    2.0          Ashish Kumar                                  	10/30/2020                	Code improvement
*    3.0          Kajal Namdev                                      05/07/2021                 Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
*	 4.0		  Ashish/Ritik 										05/17/2021					Replaced references of Policy__c object with PurchaserPlan Object
*******************************************************************************************************************************************************************/

@isTest
public class GroupSearchPolicies_LT_HUM{
    
    /*
* Method Name : prepareTestData
* Description : Create common test Data
* Return type : void
* Parameter   : NA
*/
    @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
        
        System.runAs (oCCSUser) 
        {
            RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
            RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
            
            List<HUMConstants__c> lstConstants = new List<HUMConstants__c>();
            lstConstants.add(new HUMConstants__c(Name='MembersearchConstantZero',StringValue__c='0',IntegerValue__c=0));
            lstConstants.add(new HUMConstants__c(Name='HUMServiceTimeOut',StringValue__c='12',IntegerValue__c=12));
            insert lstConstants;
            System.assertEquals(lstConstants.size(), 2);
            
            HUMSearchLimit__c oSearchLimit = new HUMSearchLimit__c(Name='Member_SearchLimit', Search_Limit__c=101);
            insert oSearchLimit;
            System.assertNotEquals(oSearchLimit, NULL, 'Value not matched');
            
            Account oAcc = HUMTestDataHelperTest.getAccount();
            oAcc.Group_Number__c = '675717';
            oAcc.recordtypeid = oMemRecType.Id;
            oAcc.Birthdate__c = '01/01/1984';
            oAcc.firstName = 'Daphiner';
            oAcc.lastName = 'Superman';
            oAcc.MiddleName = 'J';
            oAcc.Enterprise_ID__c='1030693160';
            oAcc.Home_Office_Account__c = false;
            
            insert oAcc;
            
            Account oAcc1 = HUMTestDataHelperTest.getAccount();
            oAcc1.Group_Number__c = '1234';
            
            oAcc1.recordtypeid = oMemRecType.Id;
            oAcc1.Birthdate__c = '01/01/1984';
            oAcc1.firstName = 'Daphiner';
            oAcc1.lastName = 'Superman';
            oAcc1.MiddleName = 'J';
            oAcc1.Enterprise_ID__c='1030693160';
            oAcc1.Home_Office_Account__c = false;
            
            insert oAcc1;
            
            System.AssertNotEquals(oAcc, NULL);
            
            Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
            Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
            Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(GLOBAL_CONSTANT_LH_HUM.HumMemberRecordType);
            
                    
            PurchaserPlan oPurchaserPlan = new PurchaserPlan(name = 'Test Policy', PayerId = oAcc1.Id,
                                                             Policy_Group_Number__c='11111',Major_LOB__c='MER',Source_Cust_Cov_Key__c='123456789',
                                                             Platform__c='LV');
            insert oPurchaserPlan;  
            system.assert(oPurchaserPlan.id != null);
            
            oPurchaserPlan = [Select Name, PayerId, Major_LOB__c, Source_Cust_Cov_Key__c, Product__c, Product__r.Segment_Type__c, LOB_Indicator__c 
                              From PurchaserPlan Where Id = : oPurchaserPlan.Id];
            
            List<PurchaserPlan> listPolicies= GroupSearchPolicies_LC_HUM.getPoliciesForGroup(oAcc1.Group_Number__c);
            
            GroupSearchPolicies_LC_HUM.getPurchaserPlanForGroup(oPurchaserPlan.id);
        }
    }
    
    /*
* Method Name : prepareTestSearchResults()
* Description : Test Search Criteria
* Return type : void
* Parameter   : NA
*/
    static testmethod void prepareTestSearchResults()
    {
        
        User SUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(SUser.UserName,'user1user1@humcrmstest.com');
        List<Account> lstAcc = new List<Account>();
        Account oAcc1 = [select id,Group_Number__c,recordtypeid,lastname,MiddleName,Home_Office_Account__c,PersonBirthDate from Account where Group_Number__c = '675717' limit 1];
        List<String> recTypesLst = new List<String>();
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
        RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
        List<PurchaserPlan> checkLstPolicies = new List<PurchaserPlan>();
        
        List<GroupSearchPolicies_LC_HUM> lstHumMemberSearch = new List<GroupSearchPolicies_LC_HUM>();
        GroupSearchPolicies_LC_HUM oHumMemberSearch = new GroupSearchPolicies_LC_HUM();
        Test.startTest();
        Account checkAcc =new Account();
        checkAcc.Group_Number__c = '926718';
        checkAcc.recordtypeid = oMemRecType.Id;
        checkAcc.Birthdate__c = '01/02/1999';
        checkAcc.firstName = 'John';
        checkAcc.lastName = 'Doe';
        checkAcc.MiddleName = 'M';
        checkAcc.Enterprise_ID__c='462378452';
        checkAcc.Home_Office_Account__c = false;
        insert checkAcc;
        List<PurchaserPlan> lstPolicies= GroupSearchPolicies_LC_HUM.getPoliciesForGroup('where');
        Test.stopTest();
    }
   
}