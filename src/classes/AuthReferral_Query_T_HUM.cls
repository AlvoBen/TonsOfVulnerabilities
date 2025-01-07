/*
 * Apex class Name    : AuthReferral_Query_T_HUM  
 * Version            : 1.0
 * Function           : Test class for AuthReferral_Query_T_HUM
 * Created Date       : 12/09/2015
 * Author            : Manish Kumar 
 *
 * Modification Log:
 * Developer Name            Review Number          Date                       Description
 -----------------------------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar              29042                  12/09/2015                 Original Version
 * Ranjeeth Nagishetty       29042                  01/04/2016                 updated test method
 * Sruthi Adivikolanu        29042                  01/11/2016                 updated code to increase code coverage
 * Sruthi Adivikolanu        29646                  01/27/2016                 updated code to increase code coverage
 * Santhi Mandava            30647                  03/26/2016                 Fixed code coverage issue.
 * Prasanthi Kandula                                04/06/2016                 Updated test method And increased code coverage.
 * Praveen Kumar Parimi      28468                  05/26/2016                 Removed getInteractions() method since Req#196131 Is cancelled.
 * Harika Devi Kalidindi     36595                  08/24/2016                 Defect Fix 238145.0001 -Added getMemberName method 
 * Harika Devi Kalidindi     112000                 07/20/2017                 REQ - 323435 :Added Policy_Group_Number__c field value While inserting policy 
 * Sai Ayyagari              184451                 02/22/2018                 REQ - 350853 Fix for HUMExceptionHelper, store the list of exceptions capture
 ************************************************************************************************************************************************/

@isTest
Private Class AuthReferral_Query_T_HUM
{
    /*
     * Method Name : dataSetup
     * Description : This method is used to setup data to use in rest of class
     * Return type : NA
     * Parameter   : NA
     */
    @testSetup static void dataSetup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        group objgroup  = new group();
        objgroup.name ='test group';
        objgroup.type ='Queue';
        insert objgroup;
        System.runAs(oUser)
        {
            List<Account> lstAcc = new List<Account>();
            lstAcc.add(new Account(Name='Test0',Home_Office_Account__c=true,Group_Number__c='000000'));
            lstAcc.add(new Account(Name='Test1',Home_Office_Account__c=true,Group_Number__c='000001'));
            lstAcc.add(new Account(Name='Test2',Home_Office_Account__c=false,Group_Number__c='000002'));
            lstAcc.add(new Account(Name='Test3',Home_Office_Account__c=true,Group_Number__c='000003'));
            lstAcc.add(new Account(Name='Test4',Home_Office_Account__c=false,Group_Number__c='000004'));
            lstAcc.add(new Account(Name='Test5',Home_Office_Account__c=true,Group_Number__c='000005'));
            lstAcc.add(new Account(Name='Test6',Home_Office_Account__c=false,Group_Number__c='000006'));
            lstAcc.add(new Account(Name='Test7',Home_Office_Account__c=true,Group_Number__c='000007'));
            lstAcc.add(new Account(Name='Test8',Home_Office_Account__c=false,Group_Number__c='000008'));
            lstAcc.add(new Account(Name='Test9',Home_Office_Account__c=true,Group_Number__c='000009'));
            lstAcc.add(new Account(Name='Test10',Home_Office_Account__c=false,Group_Number__c='000010'));
            lstAcc.add(new Account(Name='Test11',Home_Office_Account__c=true,Group_Number__c='000011'));
            lstAcc.add(new Account(Name='Test12',Home_Office_Account__c=false,Group_Number__c='000012'));
            lstAcc.add(new Account(Name='Test13',Home_Office_Account__c=true,Group_Number__c='000013'));
            lstAcc.add(new Account(Name='Test14',Home_Office_Account__c=false,Group_Number__c='000014'));
            lstAcc.add(new Account(Name='Test15',Home_Office_Account__c=true,Group_Number__c='000015'));
            lstAcc.add(new Account(Name='Test16',Home_Office_Account__c=false,Group_Number__c='000016'));
            lstAcc.add(new Account(Name='Test17',Home_Office_Account__c=true,Group_Number__c='000017'));
            lstAcc.add(new Account(Name='Test18',Home_Office_Account__c=false,Group_Number__c='000018'));
            lstAcc.add(new Account(Name='Test19',Home_Office_Account__c=true,Group_Number__c='000019'));
            lstAcc.add(new Account(Name='Test20',Home_Office_Account__c=false,Group_Number__c='000020'));
            insert lstAcc;
            
            for(Integer i = 0 ; i < lstAcc.size() ; i++)
            {
                System.assertEquals(lstAcc[i].Name,'Test' + i);
            }
            
        Id grpRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.name = 'test';
        acc.Source_Platform_Code__c ='EM';
        acc.Source_Customer_Key__c = 'XY';
        acc.Recordtypeid = grpRecordTypeId ;
        insert acc;
        system.assertNotEquals(Null,acc.id);
        system.assertequals(grpRecordTypeId ,acc.Recordtypeid);
        
        Policy__c policy = new Policy__c();
        policy.Platform__c = 'LV';
        policy.Group_Name__c = acc.Id;
        policy.Policy_Group_Number__c='11111';
        insert policy ;
        system.assertequals('LV',policy.Platform__c );
        
        Policy_Member__c objPolMemeber = new Policy_Member__c();
        objPolMemeber.Policy__c = policy.Id;
        objPolMemeber.Source_Coverage_ID__c = '12345';
        objPolMemeber.Member__c =acc.Id;
        insert objPolMemeber;
        system.assertequals('12345',objPolMemeber.Source_Coverage_ID__c );
        
        Account oAcc = HUMTestDataHelperTest.getAccount();
        oAcc.FirstName = 'TestAcc';
        oAcc.LastName = 'TestAcc';
        oAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        insert oAcc;
        
        Interaction__C oInt = new Interaction__C();
        oInt.Caller_name__C = 'Test Caller';
        oInt.Interacting_with_type__C = 'Member';
        oInt.Interacting_with__c = oAcc.Id;
        oInt.Interaction_Origin__c = 'Inbound Call';
        insert oInt ;
        }
    }
    
     /*
     * Method Name : getAccountGroupRecords
     * Description : This method is used for applying security measures on data received from Service
     * Return type : NA
     * Parameter   : NA
     */
  testMethod static void getAccountGroupRecords()
    {
        User oUser = [Select Id,LastName ,Current_Queue__c from User where LastName = 'test User1'];
        System.assertEquals('test User1',oUser.LastName);
        System.runAs(oUser)
        {
            Set<String> setGrpId = new Set<String>();
            Set<ID> setUserID = new Set<ID>();
            for(Account oAcc : [Select Id,Group_Number__c,Home_Office_Account__c,Name From Account Limit 10])
            {
                setGrpId.add(oAcc.Group_Number__c);
            }
            System.assertEquals(setGrpId.size(),10);
            account acct = [Select Id,Group_Number__c,Home_Office_Account__c,Name From Account where name =:'Test13' Limit 1];
           
            Test.Starttest();
            AuthReferral_Query_D_HUM oDataLayer = new AuthReferral_Query_D_HUM();
            
            system.assertEquals(oDataLayer.checkHOAccountMember(acct.id),true);
            
            Id recId = Null;
            system.assertEquals(oDataLayer.checkHOAccountMember(recId),false);
            
            oDataLayer.getAccountGroupRecords(setGrpId);
            
            setGrpId = new Set<String>();
            system.assertEquals(oDataLayer.getAccountGroupRecords(setGrpId),Null);
            
            for(Account oAcc : [Select Id,Group_Number__c,Home_Office_Account__c,Name From Account Limit 10])
            {
                setUserID.add(oAcc.ID);
            }
            System.assertEquals(setUserID.size(),10);
            
            oDataLayer.getUserRecordAccess(setUserID );
            
            setUserID = new Set<ID>();
            system.assertEquals(oDataLayer.getUserRecordAccess(setUserID),Null);
            
            Policy_Member__c objPolMemeber1 =[select id,Source_Coverage_ID__c from Policy_Member__c  where Source_Coverage_ID__c =:'12345' limit 1];
            system.assertequals('12345',objPolMemeber1.Source_Coverage_ID__c );
            AuthReferral_Query_D_HUM oAuth = New AuthReferral_Query_D_HUM();
             List<POLICY_Member__C> oMemberList = oAuth.getMemberName(objPolMemeber1.id);
            
            oDataLayer.getRelatedPolicies(objPolMemeber1.id);
            system.assertNotEquals(oDataLayer.getMemberAccount(objPolMemeber1.id),Null);                  
            
            Test.Stoptest();
        }
    }
}