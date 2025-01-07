/**********************************************************************************************************************************************************************
Apex Class Name  : ClaimSecurityDataLogic_T_HUM
Version          : 1.0 
Created On       : 03/15/2016
Function         : Test class to ClaimSecurityDataLogic_D_HUM apex class

Modification Log :
* Developer Name          Code Review                   Date                       Description
*----------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Rajaram Korivi                                        04/22/2016                 Original Version
* Harika Devi Kalidindi        30521                    06/17/2016                 Defect 230083.0001 Fix
* Suraj Patil 			       277418					03/11/2021				   REQ - 1279689 Claims old version Decommission 
* Anuradha Gajbhe                                       06/18/2021                 User Story 2315210 MF 6 - SF - Tech - Performance Tuning - Authorization Summary and Detail Pages.
***********************************************************************************************************************************************************************/

@isTest
private class ClaimSecurityDataLogic_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to create test data for the class 
    * Return type : void
    * Parameter   : NA
    */
    @testSetup static void setup() 
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        oUser.Current_Queue__c = 'Cincinnati CRU1';
        oUser.Network_User_Id__c = 'SXS2222';
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
               
        system.runAs(oUser)
        {
            HUM_Webservice_Callout__c oHUMClaimSummaryService = new HUM_Webservice_Callout__c();
            oHUMClaimSummaryService.name='ClaimsService_GetClaimsSummary_V3';
            oHUMClaimSummaryService.Active__c=true;
            oHUMClaimSummaryService.Certificate_Name__c='testts';
            oHUMClaimSummaryService.End_Point_URL__c='https://testst-cmservicews.hum.com/';
            oHUMClaimSummaryService.Timeout__c='120000';
            oHUMClaimSummaryService.Content_Type__c='application/json';
            oHUMClaimSummaryService.Request_Type__c='POST';
            insert oHUMClaimSummaryService;
            system.assert(oHUMClaimSummaryService.id!=null);
            system.assert(oHUMClaimSummaryService.name == 'ClaimsService_GetClaimsSummary_V3');
            
            Account objAcc = HUMTestDataHelperTest.getAccount(); 
            objAcc.FirstName = 'Test';
            objAcc.LastName = 'Account';
            objAcc.Tenant_Id__c = '00';
            objAcc.Enterprise_ID__c = '1005577525';
            objAcc.General_Account__c = true;
            objAcc.Home_Office_Account__c = true;
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc;
            System.assertEquals(objAcc.Tenant_Id__c,'00');
            
            Account objAcc1 = HUMTestDataHelperTest.getAccount(); 
            objAcc1.FirstName = 'Test';
            objAcc1.LastName = 'Account1';
            objAcc1.Tenant_Id__c = '00';
            objAcc1.Enterprise_ID__c = '1005577528';
            objAcc1.General_Account__c = false;
            objAcc1.CoKY_Account__c = false;
            objAcc1.DST_Account__c = false;
            objAcc1.Home_Office_Account__c = false;
            objAcc1.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc1;
            System.assertEquals(objAcc1.Tenant_Id__c,'00');
            
            Account objAcc2 = HUMTestDataHelperTest.getAccount(); 
            objAcc2.FirstName = 'Test';
            objAcc2.LastName = 'Account2';
            objAcc2.Tenant_Id__c = '00';
            objAcc2.Enterprise_ID__c = '1005577526';
            objAcc2.General_Account__c = true;
            objAcc2.CoKY_Account__c = true;
            objAcc2.DST_Account__c = true;
            objAcc2.Home_Office_Account__c = false;
            objAcc2.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
            insert objAcc2;
            System.assertEquals(objAcc2.Tenant_Id__c,'00');
            
            Product2 oProd = new Product2(Name = 'MED',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
            insert oProd;
            System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            objpol.Major_LOB__c = 'MER';
            objpol.Source_Cust_Cov_Key__c = '123456789';
            objpol.Product__c = oProd.Id;
            objpol.Product__r = oProd;
            objpol.Platform__c = 'LV';
            insert objpol;
            system.assertEquals(objpol.Source_Cust_Cov_Key__c,'123456789');
            
            Policy_Member__c objPolMem2 = new Policy_Member__c();
            objPolMem2.Name = 'testpolmem16';
            objPolMem2.Relationship__c = 'Self';
            objPolMem2.Subscriber__c = objPolMem2.Id;
            objPolMem2.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem2.End_Date__c=Date.today().addDays(1).format();
            objPolMem2.Policy_Member_External_ID__c='00|ODS|6925004434816|12345678|34567|2013-07-01';
            objPolMem2.Member__c = objAcc2.Id;
            objPolMem2.Policy__c = objpol.Id;
            objPolMem2.Policy__r = objpol;
            insert objPolMem2;
            system.assertEquals(objPolMem2.Relationship__c, 'Self'); 
            
            Policy_Member__c objPolMem1 = new Policy_Member__c();
            objPolMem1.Name = 'testpolmem12';
            objPolMem1.Relationship__c = 'Self';
            objPolMem1.Subscriber__c = objPolMem1.Id;
            objPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem1.End_Date__c=Date.today().addDays(1).format();
            objPolMem1.Policy_Member_External_ID__c='00|ODS|6925004434818|12345678|34567|2013-07-01';
            objPolMem1.Member__c = objAcc1.Id;
            objPolMem1.Policy__c = objpol.Id;
            objPolMem1.Policy__r = objpol;
            insert objPolMem1;
            system.assertEquals(objPolMem1.Relationship__c, 'Self'); 
            
            Policy_Member__c objPolMem = new Policy_Member__c();
            objPolMem.Name = 'testpolmem11';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Subscriber__c = objPolMem.Id;
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            objPolMem.Policy__r = objpol;
            insert objPolMem;
            system.assertEquals(objPolMem.Relationship__c, 'Self');
            
            Member_ID__c objMemberID = new Member_ID__c();
            objMemberID.policy_member__c = objPolMem.id;
            objMemberID.policy__c = objpol.Id;
            objMemberID.Name = 'H1234567';
            objMemberID.Member_Card_External_ID__c = 'ODS1234';  
            objMemberID.TYPE__C='Member-Id-Base';      
            insert objMemberID;
            system.assertequals('H1234567',objMemberID.Name );
        }
    }
    
    /*
     * Method Name : testFunctionality
     * Description : This is the main method to test the over all functionality.
     * Return type : void
     * Paramater   : None
     */ 
    static testMethod void testFunctionality() 
    {
        User oUser = [Select Id,Network_User_Id__c from User where LastName = 'test User1'];
        System.assertEquals('SXS2222', oUser.Network_User_Id__c);
        
        System.runAs(oUser)
        {
            Test.StartTest();
            List<Policy_Member__c> lstPolMems = [Select Id,Name from Policy_Member__c where Name = 'testpolmem11']; 
            System.assertEquals('testpolmem11',lstPolMems[0].Name);
            
            List<Account> lstAcc = [Select Id,Enterprise_ID__c from Account where Enterprise_ID__c = '1005577525'];
            System.assertEquals('1005577525',lstAcc[0].Enterprise_ID__c );

            Id iAccId = ClaimSecurityDataLogic_D_HUM.getMemberAccountID(lstPolMems[0].Id);         
            System.assertEquals(iAccId ,lstAcc[0].Id);
            
            List<Policy_Member__c> lstResults = ClaimSecurityDataLogic_D_HUM.getPolicyMemberDetails(iAccId );
            System.assertEquals(lstResults[0].Name ,lstPolMems[0].Name);
            
            String sName = ClaimSecurityDataLogic_D_HUM.getMemberIdName(lstPolMems[0].Id);
            System.assertEquals('H1234567' ,sName );
            
            List<POLICY_Member__C> lstResults1 = ClaimSecurityDataLogic_D_HUM.getHomeOfficeMembers(iAccId );
            System.assertEquals('Test Account' ,lstResults1[0].policy__r.Group_Name__r.name);
            
            lstResults1 = ClaimSecurityDataLogic_D_HUM.getPolicyMembersBasedonProduct(iAccId );
            System.assertEquals('testpolmem11' ,lstResults1[0].Name);
            
            Id iGroupId = ClaimSecurityDataLogic_D_HUM.getPublicGroupID('Home Office All');
            System.assertEquals('testpolmem11' ,lstResults1[0].Name);
            
            Set<Id> setGrpIds = ClaimSecurityDataLogic_D_HUM.getGroupMemberIds(iGroupId,'Queue');
            System.assertEquals('testpolmem11' ,lstResults1[0].Name);
            
            ClaimSecurityDataLogic_D_HUM.getUserAccessDetails(oUser.Id,setGrpIds );
            System.assertEquals('testpolmem11' ,lstResults1[0].Name);
            
            ClaimSecurityDataLogic_D_HUM.isHomeOfficeMember(iAccId );
            
            List<Policy_Member__c> lstPolMems1 = [Select Id,Name from Policy_Member__c where Name = 'testpolmem12']; 
            System.assertEquals('testpolmem12',lstPolMems1[0].Name);
            
            List<Account> lstAcc1 = [Select Id,Enterprise_ID__c from Account where Enterprise_ID__c = '1005577528'];
            System.assertEquals('1005577528',lstAcc1[0].Enterprise_ID__c );

            Id iAccId1 = ClaimSecurityDataLogic_D_HUM.getMemberAccountID(lstPolMems1[0].Id);  
            
            ClaimSecurityDataLogic_D_HUM.isHomeOfficeMember(iAccId1 );
            
            List<Policy_Member__c> lstPolMems2 = [Select Id,Name from Policy_Member__c where Name = 'testpolmem16']; 
            System.assertEquals('testpolmem16',lstPolMems2[0].Name);
            
            List<Account> lstAcc2 = [Select Id,Enterprise_ID__c from Account where Enterprise_ID__c = '1005577526'];
            System.assertEquals('1005577526',lstAcc2[0].Enterprise_ID__c );

            Id iAccId2 = ClaimSecurityDataLogic_D_HUM.getMemberAccountID(lstPolMems2[0].Id);  
            
            ClaimSecurityDataLogic_D_HUM.isHomeOfficeMember(iAccId2 );
            Test.StopTest();
        }
    }
}