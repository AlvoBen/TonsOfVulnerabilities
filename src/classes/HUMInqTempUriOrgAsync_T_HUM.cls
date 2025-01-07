/*************************************************************************************************************************
Apex Class Name :  HUMInqTempUriOrgAsync_T_HUM
Version: 1.0
Created Date     : 12th October 2017
Function         : Test class to HUMInqTempUriOrgAsyncService

* Modification Log 
* Modification ID     Developer                Code Review            Date                 Description
* -------------------------------------------------------------------------------------------------------------------------
* 1.0               Shivam Anand                140629                12/10/2017         REQ - 332334: Code Coverage Fix 
**************************************************************************************************************************/

@isTest
Private Class HUMInqTempUriOrgAsync_T_HUM
{
    /*
    * Method Name : setup
    * Description : create test records
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void setup() 
    {
      User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
      insert u;
      system.assert(u.Id != null);
      system.assertequals(u.LastName,'test User1');
        system.runAs(u)
        {
           HUM_Webservice_Callout__c vfs = New HUM_Webservice_Callout__c(Name = 'RetrieveInquiry', Certificate_Name__c = 'TestCertificate', Timeout__c = '3000', End_Point_URL__c = 'SampleURL', Active__c=true);
            insert vfs;
            system.assertEquals(vfs.id!=null, true);
            system.assertEquals(vfs.End_Point_URL__c == 'SampleURL', true);
            system.assertEquals(vfs.Certificate_Name__c == 'TestCertificate', true);
            system.assertEquals(vfs.Timeout__c == '3000', true);
            
             HUM_Webservice_Callout__c cstGCP = New HUM_Webservice_Callout__c(Name = 'HUMLaunchGCP', Certificate_Name__c = 'TestCertificate1', Timeout__c = '3000', End_Point_URL__c = 'SampleURL', Active__c=true);
            insert cstGCP ;
            system.assertEquals(cstGCP.id!=null, true);
            system.assertEquals(cstGCP.End_Point_URL__c == 'SampleURL', true);
           system.assertEquals(cstGCP.Certificate_Name__c == 'TestCertificate1', true);
            system.assertEquals(cstGCP.Timeout__c == '3000', true);
        }
     }
            
      Static testMethod void coverTypes()
      {
        User u = [select Id from User where LastName = 'test user1' limit 1];
        system.runAs(u)
        {
          Test.startTest();
          New HUMInqTempUriOrgAsyncService.GetInquiriesResponse_elementFuture();
          New HUMInqTempUriOrgAsyncService.GetInquiriesXMLResponse_elementFuture();
         
          HUMInqTempUriOrgAsyncService.Asyncsoap o = New HUMInqTempUriOrgAsyncService.Asyncsoap();
          
          Test.stopTest(); 
        }
      }
    
     Public Class InqTempUriOrgAsyncMockImpl Implements WebServiceMock
    {
        Public void doInvoke
        (
            Object stub, Object request, Map < String, Object > response,
            String endpoint, String soapAction, String requestName,
            String responseNS, String responseName, String responseType
        )
        {
          If (request instanceof HUMInqTempUriOrgService.GetInquiries_element)
             response.put('response_x', new HUMInqTempUriOrgAsyncService.GetInquiriesResponse_elementFuture());
          Return;
        }
    }
    
   Public Static testMethod void coverMethods()
    {
     User u = [select Id from User where LastName = 'test user1' limit 1];
      system.runAs(u)
        {
          Test.startTest();
          Test.setMock(WebServiceMock.class, New InqTempUriOrgAsyncMockImpl());
          Continuation objCon = New Continuation(120);
          New HUMInqTempUriOrgAsyncService.Asyncsoap().beginGetInquiries(objCon,null);
        New HUMInqTempUriOrgAsyncService.Asyncsoap().beginGetInquiriesXML(objCon,null);
        New HUMInqTempUriOrgAsyncService.GetInquiriesResponse_elementFuture().getValue();
        New HUMInqTempUriOrgAsyncService.GetInquiriesXMLResponse_elementFuture().getValue();
        Test.stopTest();    
        }
     }
 
            
 }