/*************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryControllerTest
Version          : 1.0
Created Date     : August 06 2014
Function         : Test class to HUMInquiryHistoryController class

* Modification Log :
* Modification ID     Developer                Code Review            Date                 Description
* -------------------------------------------------------------------------------------------------------------------------
* 1.0              Shreya Choodamani              15767             08/06/2014           Original Version
* 1.1              Pradeep Raju                   15767             08/08/2014           Updated the code for code cleanup
* 1.2              Vishal Verma                   17783             09/03/2014           Upadated test class according to changes for sorting
* 1.3              Vishal Verma                   18931             10/30/2014           Upadated test class according to changes for security
* 1.4              Vishal Verma                   20677             02/02/2015           Defect : 169366.0001 Updated date range in the filter conditions.
* 1.5              Vishal Verma                   22273             03/406/2015          Req  - 166509 -  Updated test class to cover new methods for HUMInquiryHistoryController
* 1.6              Sruthi Adivikolanu             28156             11/24/2015           Added setup method to create test records and updated test class to cover new lines of code added as per REQ-198629
  1.7              Pallavi R                      71716             03/08/2017           changed assert statment as per req 300069                                                
**************************************************************************************************************************/

@isTest
private class HUMInquiryHistoryControllerTest
{
    /*
    * Method Name : setup
    * Description : create test records
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void setup() {
      User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
      insert u;
      system.assert(u.Id != null);
      system.assertequals(u.LastName,'test User1');
      
      Group grp = new Group(Name = 'Home Office Medical/Pharmacy' , Type='Regular');
      insert grp;
      system.assertEquals(grp.id!=null, true);
      system.assertEquals(grp.Name == 'Home Office Medical/Pharmacy', true);
          
      GroupMember grpMemm = new GroupMember(UserOrGroupId = u.Id , GroupId = grp.Id);
      insert grpMemm;
      system.assertEquals(grpMemm.id!=null, true);
      system.assertEquals(grpMemm.GroupId == grp.Id, true);
      
      system.runAs(u){
          Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgTest());
            HUMTestDataHelperTest.getHUMConstantsData();
            
            HUM_Webservice_Callout__c vfs = new HUM_Webservice_Callout__c(Name = 'RetrieveInquiry', Certificate_Name__c = 'TestCertificate', Timeout__c = '3000', End_Point_URL__c = 'SampleURL', Active__c=true);
            insert vfs;
            system.assertEquals(vfs.id!=null, true);
            system.assertEquals(vfs.End_Point_URL__c == 'SampleURL', true);
            
            HUM_Webservice_Callout__c cstGCP = new HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', Certificate_Name__c = 'TestCertificate1', Timeout__c = '3000', End_Point_URL__c = 'SampleURL', Active__c=true);
            insert cstGCP ;
            system.assertEquals(cstGCP.id!=null, true);
            system.assertEquals(cstGCP.End_Point_URL__c == 'SampleURL', true);
            
            Account objAcc = HUMTestDataHelperTest.getAccount();
            objAcc.FirstName = 'Test';
            objAcc.LastName ='Account Last';
            objAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
            objAcc.OwnerId = u.Id;
            insert objAcc;
            system.assertEquals(objAcc.id!=null, true);
            system.assertEquals(objAcc.FirstName == 'Test', true);
            
            Policy__c objpol = HUMTestDataHelperTest.getPolicy();
            objpol.Name='testPolicy';
            objpol.Group_Name__c = objAcc.Id;
            insert objpol;
            system.assertEquals(objPol.id!=null, true);
            system.assertEquals(objPol.Group_Name__c == objAcc.Id, true);
            
            Policy_Member__c objPolMem = HUMTestDataHelperTest.getPolicyMember();
            objPolMem.Name = 'testpolmem';
            objPolMem.Relationship__c = 'Self';
            objPolMem.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem.End_Date__c=Date.today().addDays(1).format();
            objPolMem.Policy_Member_External_ID__c='00|ODS|6925004434817|12345678|34567|2013-07-01';
            objPolMem.Member__c = objAcc.Id;
            objPolMem.Policy__c = objpol.Id;
            insert objPolMem;
            system.assertEquals(objPolMem.id!=null, true);
            system.assertEquals(objPolMem.Policy__c == objpol.Id, true);
            
            Policy_Member__c objPolMem1 = HUMTestDataHelperTest.getPolicyMember();
            objPolMem1.Name = 'testpolmem1';
            objPolMem1.Relationship__c = 'Self';
            objPolMem1.Effective_Date__c=Date.today().addDays(-1).format();
            objPolMem1.End_Date__c=Date.today().addDays(1).format();
            objPolMem1.Policy_Member_External_ID__c='00|ODS|6925004434817|1234567|3456|2012-07-01';
            objPolMem1.Member__c = objAcc.Id;
            objPolMem1.Policy__c = objpol.Id;
            insert objPolMem1;
            system.assertEquals(objPolMem1.id!=null, true);
            system.assertEquals(objPolMem1.Member__c  == objAcc.Id, true);
      }
    }
    
    /*
    * Method Name : testViewAll
    * Description : Test method to View All
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testViewAll()
    {
        User u = [select Id from User where LastName = 'test user1' limit 1];
        system.runAs(u)
        {
            Account objAcc = [select id from Account where FirstName = 'Test' limit 1];
           
            policy__c objpol = [select Id from Policy__c where Group_Name__c =: objAcc.Id limit 1];
            
            Policy_member__c objpolmem = [select id from policy_member__c where Member__c  =: objAcc.Id limit 1];
            
            ApexPages.CurrentPage().getparameters().put('bViewAll', 'Yes');
            ApexPages.CurrentPage().getparameters().put('refId', '123');
            ApexPages.CurrentPage().getparameters().put('fDate', '12/12/2012');
            ApexPages.CurrentPage().getparameters().put('tDate', '12/12/2015');
            ApexPages.CurrentPage().getparameters().put('id', objAcc.Id);
           
            Test.startTest();
            HUMInquiryHistoryController inqHistory = new HUMInquiryHistoryController();
            
            inqHistory.viewAllAction();
            system.assertequals(inqHistory.bViewAll,true);
            
            ApexPages.CurrentPage().getparameters().put('id', objpol.Id); 
            
            Test.setMock(WebServiceMock.class, new HUMInqTempUriOrgTest());
            
            HUMInquiryHistoryController inqHistory1 = new HUMInquiryHistoryController();
            inqHistory1.sFromDate ='12/02/2010';
            inqHistory1.sToDate ='12/12/2010';
            inqHistory1.iTableSize = 1; 
            inqHistory1.retrieveLegacyData();
            inqHistory1.previous();
            inqHistory1.first();
            system.assertequals(inqHistory1.bHasNext, false);
            inqHistory1.last();
            system.assertequals(inqHistory1.bHasPrevious, false);
            
            ApexPages.CurrentPage().getparameters().put('id', objpolmem.Id); 
            HUMInquiryHistoryController inqHistory2 = new HUMInquiryHistoryController();  
            
            Test.stopTest();
        }
    }
    /*
    * Method Name : testClearFilter
    * Description : Test method to clear filter
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testClearFilter(){
        User u = [select Id from User where LastName = 'test user1' limit 1];
        system.runAs(u){
            Account objAcc = [select id from Account where FirstName = 'Test' limit 1];
            
            ApexPages.CurrentPage().getparameters().put('id', objAcc.Id);
           
            Test.startTest();
            HUMInquiryHistoryController inqHistory = new HUMInquiryHistoryController();
            inqHistory.clearFilter();
            system.assertequals(inqHistory.bFamilyMem,false);
            Test.stopTest();

        } 
    }
    /*
    * Method Name : testExceptions
    * Description : Test method to test exceptions.
    * Return type : void
    * Paramater   : NA 
    */
    static testMethod void testExceptions(){
      User u = [select Id from User where LastName = 'test user1' limit 1];
        system.runAs(u){
            Account objAcc = [select id from Account where FirstName = 'Test' limit 1];
            
            ApexPages.CurrentPage().getparameters().put('id', objAcc.Id);
            
            Test.startTest();
               
                HUMInquiryHistoryController inqHistory = new HUMInquiryHistoryController();
                inqHistory.sFromDate ='';
                inqHistory.sToDate ='';
                HUMInquiryHistoryController.sSortExpression =  null;
                inqHistory.retrieveLegacyData();
                inqHistory.sortData();
                system.assert(inqHistory.lstReqInquiryHistorylist.size() == 0);
           
            Test.stopTest();

        }
    }
}