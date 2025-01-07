/**************************************************************************** 
Apex Class Name  : RTISummaryDataLogic_T_HUM
Version          : 1.0  
Created Date     : 30th January 2017
Function         : Test class for RTISummary_D_HUM
****************************************************************************
Modification Log:
* Developer Name                Code Review #         Date                Description
*---------------------------------------------------------------------------------------------------
*  Pallavi Ravishankar          61268                 01/30/2017          Original Version
*  Kritika Popat                86349                 07/04/2016          Phase 2 Service changes
*  Kritika Popat                86349                 10/04/2016          Pagination
*  Sumit Pharande			 							                  REQ-386780 -Removed the 18 to 15 Digit Id conversion    
*  Amar Gadewar                                       01/28/2020          USER STORY 729651 and USER STORY 729659 and REQ - 416082 Move Icons from Communication Panel to Under Page Titles on Member Account page / Policy Member Page
***************************************************************************************************/
@isTest
private Class RTISummaryDataLogic_T_HUM
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
          Product2 oProd = new Product2(Name = 'Laptop X200',Segment_Type__c='MCR',ProductCode='MED', Major_LOB__c='MER');
          insert oProd;
          System.assertNotEquals(oProd.Id, NULL, 'Value not matched');
          
           Account objAcc = HUMTestDataHelperTest.getAccount(); 
           objAcc.FirstName = 'Test';
           objAcc.LastName = 'Account';
           objAcc.Tenant_Id__c = '00';
           objAcc.Enterprise_ID__c = '';
           objAcc.General_Account__c = true;
           objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(System.label.HUMCOBMemberLabel).getRecordTypeId();
           insert objAcc;
           System.assertEquals(objAcc.Tenant_Id__c,'00');
           
           Policy__c objpol = HUMTestDataHelperTest.getPolicy();
           objpol.Name='testPolicy';
           objpol.Group_Name__c = objAcc.Id;
           objpol.Major_LOB__c = 'MER';
           objpol.Source_Cust_Cov_Key__c = '1023880950';
           objpol.Product__c = oProd.Id;
           objpol.Product__r = oProd;
           objpol.Platform__c = 'LV';
           objpol.Client_Number__c = '57';
           insert objpol;
           system.assertEquals(objpol.Source_Cust_Cov_Key__c,'1023880950');
            
           Policy_Member__c objPolMem = new Policy_Member__c();
           objPolMem.Name = 'testpolmem11';
           objPolMem.Relationship__c = 'Self';
           objPolMem.Relationship_Code__c = '00';
           objPolMem.Subscriber__c = objPolMem.Id;
           objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
           objPolMem.End_Date__c=Date.today().addDays(1).format();
           objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
           objPolMem.Member__c = objAcc.Id;
           objPolMem.Policy__c = objpol.Id;
           objPolMem.Policy__r = objpol;
           insert objPolMem;
           system.assertEquals(objPolMem.Relationship__c, 'Self');

            Interaction__c objInt = HUMTestDataHelperTest.getInteraction();
            objInt.Interacting_With_type__c = 'Member';
            objInt.Interacting_With__c = objAcc.Id;
            insert objInt;
            System.assertEquals(objInt.Interacting_With_type__c,'Member');
        }
    }   
  
  /*
   * Method Name : runGetEnterpriseIDTest
   * Description : Used to run scenerios of test data
   * Return type : void
   * Paramater   : NA
   */ 
   static testMethod void runGetEnterpriseIDTest()
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
           
           Policy_Member__c oPolMem =  [Select Id,Relationship__c, Member__c, Member__r.Enterprise_ID__c from Policy_Member__c where Member__c =: oAccount.Id];
           System.assertEquals(oPolMem.Relationship__c, 'Self'); 
           String sId = oAccount.Id;
           String sCode = sId.substring(0,3);          
           Account objAcc = RTISummary_D_HUM.getAccountDetails(sId);
           
          // system.assertEquals(objAcc.Enterprise_ID__c,oAccount.Enterprise_ID__c);   
           sId = oPolMem.Id;
           sCode = sId.substring(0,3);          
           objAcc = RTISummary_D_HUM.getAccountDetails(sId);
           
           List<Interaction__c> objIn = [SELECT Id, Name, Interacting_With__r.Name, Interacting_With__r.Id,Interacting_With__c FROM Interaction__c where Interacting_With__c = : sId];
          system.assert(objIn != null); 
          //system.assertEquals(objAcc.Enterprise_ID__c,oPolMem.Member__r.Enterprise_ID__c);  
           sId = null;
           objAcc = RTISummary_D_HUM.getAccountDetails(sId);
         Test.stopTest();    
        }   
    }   
}