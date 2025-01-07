/*****************************************************************************************************************************************************************************
    @Apex Class Name  : OSAService_S_T_HUM
    @Version          : 1.0
    @Created Date     : May 11th, 2020
    @Function         : Test class for OSAService_S_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                  11/05/2020                    Original Version
******************************************************************************************************************************************************************************/
@isTest
private class OSAService_S_T_HUM
{
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
        
        System.runAs(oCCSUser) 
        {
            HUM_Webservice_Callout__c objOSAService = new HUM_Webservice_Callout__c();
            objOSAService.Name = 'OutsideServiceAreaService';
            objOSAService.Active__c = True;
            objOSAService.Enable_Service_Tester__c = True;
            objOSAService.Certificate_Name__c = 'Humana_GuidanceCare_neptune';
            objOSAService.Content_Type__c = 'application/json';
            objOSAService.Request_Type__c = 'GET';
            objOSAService.Timeout__c = '120000';
            objOSAService.End_Point_URL__c = 'https://dev-crmservicews.humana.com';
            objOSAService.Service_Name__c = '/OSAServiceREST/isOSA';
            insert objOSAService;
            system.assertequals('OutsideServiceAreaService',objOSAService.Name);
        }
    }        
    /*
    * Method to test success response for OSA Service
    * @param        None
    * @return       None
    */
    private static testmethod void testSuccessOSAService()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest();
            String isOSARequest ='{"isOSARequest":{"PlanID":["S5884-151-000-2020"],"FipsCode":"21111","ZipCode":"40222"}}';
            HttpRequest request = new HttpRequest();
            request = OSAService_S_HUM.getOSAServiceRequest(isOSARequest);
            System.assertNotEquals(null, request);    
            Test.StopTest();
        }
    } 
    /*
    * Method to test exception for OSA Service
    * @param        None
    * @return       None
    */
    private static testmethod void testExceptionOSAService()
    {
        User testUser = [Select Id,Name from User where LastName='test User1' Limit 1];
        system.runAs(testUser)
        { 
            Test.StartTest(); 
            String isOSARequest = '{""}';
            HttpRequest request = new HttpRequest();
            request = OSAService_S_HUM.getOSAServiceRequest(isOSARequest);
            System.assertEquals(null, request);    
            Test.StopTest();
        }
    } 
}