/*****************************************************************************************************************************************************************************
    @Apex Class Name  : HUMCBISSearch_S_T_HUM
    @Version          : 1.0
    @Created Date     : May 12, 2021
    @Function         : Test class for HUMCBISSearch_S_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Firoja Begam                                  12/05/2021                  Original Version
******************************************************************************************************************************************************************************/
@isTest
public class HUMCBISSearch_S_T_HUM {
    
    
    /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
  @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
    }  
    
    /*
    * Method to test success response for CbisSearch REST
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessBuildRequest()
    {
         User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            HUMSearchEnrollmentHelper searchEnrol = new HUMSearchEnrollmentHelper();
            searchEnrol.sFirstName='Linot';
            searchEnrol.sLastName='Litte';
            searchEnrol.sBirthdate='02/24/1987';
            searchEnrol.sState='TX';
            HUMCBISSearch_S_HUM searchRequest = new HUMCBISSearch_S_HUM();
            searchRequest.buildRequest(searchEnrol);  
            Test.StopTest();
        }
    }
    
    /*
    * Method to test Exception for CBISSearch
    * @param        None
    * @return       None
    */
    private static testmethod void testBuildRequestException()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            HUMSearchEnrollmentHelper searchEnrol;
            HUMCBISSearch_S_HUM searchRequest = new HUMCBISSearch_S_HUM();
            searchRequest.buildRequest(searchEnrol);  
            Test.StopTest();
        }
    }
    
    /*
    * Method to test success response for CbisSearch REST
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessSetUpRequest()
    {
         User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            HUM_Webservice_Callout__c objCbisRequestD = new HUM_Webservice_Callout__c();
            objCbisRequestD.Name = 'HUMCbisSearchEnrollment_V2';
            objCbisRequestD.Active__c = True;
            objCbisRequestD.Enable_Service_Tester__c = True;
            objCbisRequestD.Certificate_Name__c = 'dummy_ConsmrMgt';
            objCbisRequestD.Content_Type__c = 'application/json';
            objCbisRequestD.Request_Type__c = 'GET';
            objCbisRequestD.Timeout__c = '120000';
            objCbisRequestD.End_Point_URL__c = 'https://dummy.humana.com/EnrollmentService/getSearchtest';
            objCbisRequestD.Service_Name__c = '/getSearchtest';
            insert objCbisRequestD;
            HUMSearchEnrollmentHelper searchEnrol = new HUMSearchEnrollmentHelper();
            searchEnrol.sFirstName='Linot';
            searchEnrol.sLastName='Litte';
            searchEnrol.sBirthdate='02/24/1987';
            searchEnrol.sState='TX';
            HttpRequest request = new HttpRequest();
            HUMCBISSearch_S_HUM searchRequest = new HUMCBISSearch_S_HUM();
            searchRequest.setupRequest(request,objCbisRequestD,searchEnrol);  
            Test.StopTest();
        }
    }
}