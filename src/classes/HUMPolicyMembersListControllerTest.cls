/**********************************************************************************************************************************************
Apex Class Name  : HUMPolicyMembersListControllerTest
Version          : 1.0
Created Date     : October 30 2014
Function         : Test class for HUMPolicyMembersListControllerTest 
Modification Log :
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma             18875                       10/30/2014                     Original Version
 * Prudhvi Pamarthi         165023                      01/19/2018                     REQ - 328929 Fix for HUMExceptionHelper: DML 
**********************************************************************************************************************************************/
@isTest 
private class HUMPolicyMembersListControllerTest
{
    
    static Policy_Member__c selfPolMember;
    static Policy_Member__c dependentPolMember;
    static Policy_Member__c polMember;

    /*
    * Method Name : testSelfPolicyMember
    * Description : Test method to test Self Policy Member
    * Return type : void
    * Paramater   : NA
    */
    static testmethod void testSelfPolicyMember() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        
        system.runAs(u) 
        {
            createTestData();
            Test.startTest();
                ApexPages.StandardController stdCntrlSelf = new ApexPages.StandardController(selfPolMember);
                HUMPolicyMembersListController selfRealtedList = new HUMPolicyMembersListController(stdCntrlSelf);
                List<Policy_Member__c> lstPolicyMember = selfRealtedList.getDependentPolicyMembers();
                system.assert(lstPolicyMember.size() != 0);
            Test.StopTest();
        }
    }
    
    
    /*
    * Method Name : testDependentPolicyMember
    * Description : Test method to test Dependent Policy Member
    * Return type : void
    * Paramater   : NA
    */
    static testmethod void testDependentPolicyMember() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        
        system.runAs(u) 
        {
            createTestData();
            Test.startTest();
                ApexPages.StandardController stdCntrlDepndent = new ApexPages.StandardController(dependentPolMember);
                HUMPolicyMembersListController dependentRealtedList = new HUMPolicyMembersListController(stdCntrlDepndent);
                List<Policy_Member__c> lstPolicyMember = dependentRealtedList.getDependentPolicyMembers();
                system.assert(lstPolicyMember.size() != 0);
                List<Error_Log__c> lstLog = new List<Error_Log__c>();            
                Error_Log__c error = new Error_Log__c(Status__c = 'HUMPolicyMembersListController');            
                lstLog.add(error);                        
                dependentRealtedList.listErrorLogs = lstLog;            
                System.assert(dependentRealtedList.listErrorLogs.size() > 0);
            Test.StopTest();
        }
    }
    
    /*
    * Method Name : testNoRecords
    * Description : Test method to test Policy member with no dependent data
    * Return type : void
    * Paramater   : NA
    */
    static testmethod void testNoRecords() 
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        
        system.runAs(u) 
        {
            createTestData();
            Test.startTest();
                ApexPages.StandardController stdCntrlDepndent = new ApexPages.StandardController(polMember);
                HUMPolicyMembersListController polMemberRealtedList = new HUMPolicyMembersListController(stdCntrlDepndent);
                List<Policy_Member__c> lstPolicyMember = polMemberRealtedList.getDependentPolicyMembers();
                system.assert(lstPolicyMember == null);
            Test.StopTest();
        }
    }
    
    
    /*
    * Method Name : createTestData
    * Description : This method is used to create test data 
    * Return type : void
    * Paramater   : NA
    */
    static void createTestData()
    {
         Account objAcc = HUMTestDataHelperTest.getAccount();
        objAcc.Name = 'testacc';
        insert objAcc;
        system.assertEquals(objAcc.id!=null, true);
        
        Policy__c objpol = HUMTestDataHelperTest.getPolicy();
        objpol.Name='testPolicy';
        objpol.Group_Name__c = objAcc.Id;
        insert objpol;
        system.assertEquals(objpol.id!=null, true);
        
        selfPolMember = new Policy_Member__c();
        selfPolMember.Name = 'testpolmem';
        selfPolMember.Relationship__c = 'Self';
        selfPolMember.Effective_Date__c=Date.today().addDays(-1).format();
        selfPolMember.End_Date__c=Date.today().addDays(1).format();
        selfPolMember.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
        selfPolMember.Member__c = objAcc.Id;
        selfPolMember.Policy__c = objpol.Id;
        insert selfPolMember;
        system.assertEquals(selfPolMember.id!=null, true);
        
        dependentPolMember = new Policy_Member__c();
        dependentPolMember.Name = 'testpolmem1';
        dependentPolMember.Relationship__c = 'Dependent';
        dependentPolMember.Effective_Date__c=Date.today().addDays(-1).format();
        dependentPolMember.End_Date__c=Date.today().addDays(1).format();
        dependentPolMember.Policy_Member_External_ID__c='00|ODS|6925004434817|1234567|3456|2012-07-01';
        dependentPolMember.Member__c = objAcc.Id;
        dependentPolMember.Policy__c = objpol.Id;
        dependentPolMember.Subscriber__c = selfPolMember.Id;
        insert dependentPolMember;
        system.assertEquals(dependentPolMember.id!=null, true);
        
        polMember = new Policy_Member__c();
        polMember.Name = 'testpolmem2';
        polMember.Relationship__c = 'Self';
        polMember.Effective_Date__c=Date.today().addDays(-1).format();
        polMember.End_Date__c=Date.today().addDays(1).format();
        polMember.Policy_Member_External_ID__c='00|ODS|5004434817|12345678|34567|2013-07-01';
        polMember.Member__c = objAcc.Id;
        polMember.Policy__c = objpol.Id;
        insert polMember;
        system.assertEquals(polMember.id!=null, true);

    }
}