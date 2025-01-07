/*******************************************************************************************************************************
Test Class Name : MyAssistantAtHumanaFeedback_LT_HUM
Version         : 1.0
Created On      : 09/27/2023   
Function        : Test class of MyAssistantAtHumanaFeedback_LC_HUM.
                 
Modification Log: 
*  Developer Name                Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------
*  Kiran Kotni		          09/27/2022                  Original version
**********************************************************************************************************************************/
@isTest
private class MyAssistantAtHumanaFeedback_LT_HUM {
    
    public class FeedbackSuccessMockResponse implements HttpCalloutMock{
        public HttpResponse respond(HTTPRequest req){
            HttpResponse res = new HttpResponse();
            res.setBody('{"chatSessionId": "123","sequenceId": "12","timestamp": "2023-09-27T11:21:01.322158+00:00","created": true}');
            res.setHeader('Content-Type','application/json');
            res.setStatusCode(200);
            return res;
        }
    }
    
    public class FeedbackFailureMockResponse implements HttpCalloutMock{
        public HttpResponse respond(HttpRequest req){
            HttpResponse res = new HttpResponse();
            res.setHeader('Content-Type','application/json');
            res.setBody('{"error": {"timestamp": "2023-09-27T11:28:07Z","error": {"description": "/chatSessionId expected type: String, found: Null","exception": "MULE"}}}');
            res.setStatusCode(400);
            return res;
        }
    }
    
     /*
    * This method creates test data for test class
    * @param        None
    * @return       None
    */
    @testSetUp
    static void setUpTestData(){
        
    HUM_Webservice_Callout__c endSessionService = new HUM_Webservice_Callout__c(Name = 'MyAssistantAtHumanaFeedbackService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://dev-appgw.humana.com/aiapi-dev/uat/v1/feedback' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json');
    insert endSessionService;     
    
    HUMConstants__c clientId = new HUMConstants__c(Name='MyAssistantAtHumana_clientId',StringValue__c ='a0de92397ae34869860a542b04e44c28');
    insert clientId;
        
    HUMConstants__c clientSecret = new HUMConstants__c(Name='MyAssistantAtHumana_clientSecret',StringValue__c ='E19c65984a374388a6e321cD77C74E98');
    insert clientSecret;
    
    User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
    insert objUser; 
    }
    
     /*
    * Method to test success response for Feedback
    * @param        None
    * @return       None
    */
    @isTest static void testSuccessSendFeedbackRequest(){
        string feedbackJson = '{"chatSessionId": "123","sequenceId": "12","timestamp": "2023-08-25T16:32:23.675029","feedback": {"isHelpful": false,"userComment": "This is a wrong answer. It should have been - yes, fish is covered as part of food"}}';
        User testUser = [Select Id,Name from User where LastName='test User1'];  
        
        System.runAs(testUser){
            test.startTest();
            test.setMock(HttpCalloutMock.class,new FeedbackSuccessMockResponse());
            Integer statusCode = MyAssistantAtHumanaFeedback_LC_HUM.sendFeedbackRequest(feedbackJson);
            system.assertEquals(200,statusCode);
            test.stopTest();
        } 
    }
    
     /*
    * Method to test failure response for Feedback
    * @param        None
    * @return       None
    */
     @isTest static void testFailureSendFeedbackRequest(){
        string feedbackJson = '{"chatSessionId": null,"sequenceId": "12","timestamp": "2023-08-25T16:32:23.675029","feedback": {"isHelpful": false,"userComment": "This is a wrong answer. It should have been - yes, fish is covered as part of food"}}';
        User testUser = [Select Id,Name from User where LastName='test User1'];  
        
        System.runAs(testUser){
            test.startTest();
            test.setMock(HttpCalloutMock.class,new FeedbackFailureMockResponse());
            Integer statusCode = MyAssistantAtHumanaFeedback_LC_HUM.sendFeedbackRequest(feedbackJson);
            system.assertEquals(400,statusCode);
            test.stopTest();
        }
    }
    
    /*
    * Method to test exception response for Feedback
    * @param        None
    * @return       None
    */
    @isTest static void testExceptionSendFeedbackRequest(){
        string feedbackJson = '{}';
        User testUser = [Select Id,Name from User where LastName='test User1'];  
        
        System.runAs(testUser){
            test.startTest();
            Integer statusCode = MyAssistantAtHumanaFeedback_LC_HUM.sendFeedbackRequest(feedbackJson);
            system.assertNotEquals(200,statusCode);
            test.stopTest();
        }
    }
}