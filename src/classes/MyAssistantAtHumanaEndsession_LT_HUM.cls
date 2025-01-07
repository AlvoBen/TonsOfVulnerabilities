/*******************************************************************************************************************************
Test Class Name : MyAssistantAtHumanaEndsession_LT_HUM
Version         : 1.0
Created On      : 09/27/2023   
Function        : Test class of MyAssistantAtHumanaEndsession_LC_HUM.
                 
Modification Log: 
*  Developer Name                Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------
*  Kiran Kotni		          09/27/2022                  Original version
**********************************************************************************************************************************/
@isTest
private class MyAssistantAtHumanaEndsession_LT_HUM {
    
    
    public class EndsessionSuccessMockResponse implements HttpCalloutMock{
        
        public HTTPResponse respond(HttpRequest req){
            HttpResponse httpRes = new Httpresponse ();
            httpRes.setBody('{"chatSessionId": "3"}');
            httpRes.setHeader('Content-Type', 'application/json');
            httpRes.setStatusCode(200);
            return httpRes; 
        }
    }
    public class EndsessionFailureMockResponse implements HttpCalloutMock{
        public HTTPResponse respond(HttpRequest req){ 
            HttpResponse httpRes = new Httpresponse ();
            httpRes.setBody('{"error": {"timestamp": "2023-09-27T10:30:20Z","error": {"description": "/chatSessionId expected type: String, found: Null","errorType": "BAD_REQUEST","exception": "MULE"}}}');
            httpRes.setHeader('Content-Type', 'application/json');
            httpRes.setStatusCode(400);
            return httpRes; 
    }
    }
    
     /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
    @testSetup 
    static void setUpTestData()
    {
    HUM_Webservice_Callout__c endSessionService = new HUM_Webservice_Callout__c(Name = 'MyAssistantAtHumanaEndSessionService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://dev-appgw.humana.com/genai-proc-api-dev/api/endsession' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json');
    insert endSessionService;     
    
    HUMConstants__c clientId = new HUMConstants__c(Name='MyAssistantAtHumana_clientId',StringValue__c ='a0de92397ae34869860a542b04e44c28');
    insert clientId;
        
    HUMConstants__c clientSecret = new HUMConstants__c(Name='MyAssistantAtHumana_clientSecret',StringValue__c ='E19c65984a374388a6e321cD77C74E98');
    insert clientSecret;
    
    User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
    insert objUser; 
    }
    
     /*
    * Method to test success response for Endsession
    * @param        None
    * @return       None
    */
    @isTest static void testSuccessEndSessionRequest(){
        string endsessionJson = '{"chatSessionId": "3"}';
        User testUser = [Select Id,Name from User where LastName='test User1'];
        
        System.runAs(testUser){
        Test.startTest();
        Test.setMock(HttpCalloutMock.class,new EndsessionSuccessMockResponse());
        Integer statusCode = MyAssistantAtHumanaEndsession_LC_HUM.sendEndsessionRequest(endsessionJson);
        system.assertEquals(200,statusCode);
        Test.stopTest();            
        }
    }
    
    /*
    * Method to test failure response for Endsession
    * @param        None
    * @return       None
    */
    @isTest static void testFailureEndSessionRequest(){
        string endsessionJson = '{"chatSessionId": null}';
        User testUser = [Select Id,Name from User where LastName='test User1'];
        
        System.runAs(testUser){
        Test.startTest();
        Test.setMock(HttpCalloutMock.class,new EndsessionFailureMockResponse());
       
        Integer statusCode = MyAssistantAtHumanaEndsession_LC_HUM.sendEndsessionRequest(endsessionJson);
        system.assertEquals(400,statusCode);
        Test.stopTest();            
        }
    }
    
    /*
    * Method to test exception response for Endsession
    * @param        None
    * @return       None
    */
    @isTest static void testExceptionEndSessionRequest(){
        string endsessionJson = '{}';
        User testUser = [Select Id,Name from User where LastName='test User1'];
        
        System.runAs(testUser){
        Test.startTest();
        Integer statusCode = MyAssistantAtHumanaEndsession_LC_HUM.sendEndsessionRequest(endsessionJson);
        system.assertNotEquals(200,statusCode);
        Test.stopTest();            
        }
    }

}