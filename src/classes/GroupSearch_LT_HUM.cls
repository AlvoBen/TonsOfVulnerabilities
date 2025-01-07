/*******************************************************************************************************************************
Apex Class Name : GroupSearch_LT_HUM
Version         : 1.0
Created On      : 09/24/2020
Function        : This class is the test class for GroupSearch_LC_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                  	09/24/2020                	Original Version
*    2.0           Kajal Namdev                                     05/07/2021                 Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class
*******************************************************************************************************************************************************************/

@isTest
public class GroupSearch_LT_HUM {
	
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
            Schema.RecordTypeInfo oMemberRecordType_Test =  accountRecordTypeMap.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.HumMemberRecordType);
            Map<String,String> stateLst = SearchUtilty_H_HUM.getStateValues();
           
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
        
        
        List<GroupSearch_LC_HUM> lstHumMemberSearch = new List<GroupSearch_LC_HUM>();
        GroupSearch_LC_HUM oHumMemberSearch = new GroupSearch_LC_HUM();
        
        
        GroupSearchInputWrapper_LH_HUM usewrapper = new GroupSearchInputWrapper_LH_HUM();
        usewrapper.sUnknownGroupCheck = true;
        usewrapper.sGroupName = 'MED';
        usewrapper.sGroupNumber = '675717';
        usewrapper.sState = 'WI';
        
        GroupSearchInputWrapper_LH_HUM usewrapper1 = new GroupSearchInputWrapper_LH_HUM();
        usewrapper1.sUnknownGroupCheck = false;
        usewrapper1.sGroupName = '';
        usewrapper1.sGroupNumber = '11111';
        usewrapper1.sState = 'WI';
        Test.startTest();
        
        GroupSearch_LC_HUM.oGroupSearchInputWraper =usewrapper;
        GroupSearch_LC_HUM.searchGroupAccount(usewrapper);
        
        GroupSearch_LC_HUM.oGroupSearchInputWraper =usewrapper1;
        GroupSearch_LC_HUM.searchGroupAccount(usewrapper1);
       
        Test.stopTest();
    }
	
	static testmethod void prepareTestSearchResultsfail()
    {
        
        User SUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(SUser.UserName,'user1user1@humcrmstest.com');
        List<Account> lstAcc = new List<Account>();
        Account oAcc1 = [select id,Group_Number__c,recordtypeid,lastname,MiddleName,Home_Office_Account__c,PersonBirthDate from Account where Group_Number__c = '675717' limit 1];
        lstAcc = [select id,Group_Number__c,recordtypeid,lastname,MiddleName,Home_Office_Account__c,PersonBirthDate from Account where Group_Number__c = '675717' limit 1];
        List<String> recTypesLst = new List<String>();
        RecordType oMemRecType = [SELECT Id FROM RecordType WHERE Name = 'Member' Limit 1];
        RecordType oGroupRecType = [SELECT Id FROM RecordType WHERE Name = 'Group' Limit 1];
        GroupSearch_LD_HUM.dataSecurity(lstAcc);
        system.assertNotEquals(lstAcc.size(), 10);
        //GroupSearch_LD_HUM.requestGroupAccountListBySOQL();
        
        List<GroupSearch_LC_HUM> lstHumMemberSearch = new List<GroupSearch_LC_HUM>();
        GroupSearch_LC_HUM oHumMemberSearch = new GroupSearch_LC_HUM();
        
        
        GroupSearchInputWrapper_LH_HUM usewrapper = new GroupSearchInputWrapper_LH_HUM();
        usewrapper.sUnknownGroupCheck = true;
        usewrapper.sGroupName = 'MED';
        usewrapper.sGroupNumber = '675717';
        usewrapper.sState = 'WI';
        
        GroupSearchInputWrapper_LH_HUM usewrapper1 = new GroupSearchInputWrapper_LH_HUM();
        usewrapper1.sUnknownGroupCheck = false;
        usewrapper1.sGroupName = '';
        usewrapper1.sGroupNumber = '11111';
        usewrapper1.sState = 'WI';
        Test.startTest();
        
        GroupSearch_LC_HUM.oGroupSearchInputWraper =usewrapper;
        GroupSearch_LC_HUM.searchGroupAccount(usewrapper);
        
        GroupSearch_LC_HUM.oGroupSearchInputWraper =usewrapper1;
        GroupSearch_LC_HUM.searchGroupAccount(usewrapper1);
       
        Test.stopTest();
    }
}