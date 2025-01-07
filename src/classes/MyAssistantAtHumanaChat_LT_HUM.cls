/*******************************************************************************************************************************
Test Class Name : MyAssistantAtHumanaChat_LT_HUM
Version         : 1.0
Created On      : 09/27/2023   
Function        : Test class of MyAssistantAtHumanaChat_LC_HUM.
                 
Modification Log: 
*  Developer Name                Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------
*  Kiran Kotni		          09/27/2022                  Original version
**********************************************************************************************************************************/
@isTest
private class MyAssistantAtHumanaChat_LT_HUM {
    
    public class ChatSuccessMockResponse implements HttpCalloutMock{
        public HttpResponse respond(HTTPRequest req){
            HttpResponse res = new HttpResponse();
            res.setBody('{"chatSessionId": "543","query": "can I use this benefit to pay my rent?","sequenceId": "3","timestamp": "2023-09-28T08:47:46.576926+00:00","result": {"response": "Test Response","chainOfThought": [{"tool": "BenefitTool","proof": [{"text": "You can buy Home and personal supplies","sourceUrl": "https://docushare-web.apps.external.pioneer.humana.com"}],"observation": null}]}}');
            res.setHeader('Content-Type','application/json');
            res.setStatusCode(200);
            return res;
        }
    }
    
     public class ChatFailureMockResponse implements HttpCalloutMock{
        public HttpResponse respond(HTTPRequest req){
            HttpResponse res = new HttpResponse();
            res.setBody('{"error": {"timestamp": "2023-09-28T06:51:50Z","error": {"description": "/query expected type: String, found: Null","errorType": "BAD_REQUEST","exception": "MULE"}}}');
            res.setHeader('Content-Type','application/json');
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
        
    HUM_Webservice_Callout__c chatService = new HUM_Webservice_Callout__c(Name = 'MyAssistantAtHumanaChatService',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c = 'https://dev-appgw.humana.com/aiapi-dev/uat/v1/chat' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json');
    insert chatService;     
    
    HUMConstants__c clientId = new HUMConstants__c(Name='MyAssistantAtHumana_clientId',StringValue__c ='a0de92397ae34869860a542b04e44c28');
    insert clientId;
        
    HUMConstants__c clientSecret = new HUMConstants__c(Name='MyAssistantAtHumana_clientSecret',StringValue__c ='E19c65984a374388a6e321cD77C74E98');
    insert clientSecret;
    
    User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');   
    insert objUser; 
        
        Account objMemAccount = HUMTestDataHelperTest.getAccount();
        objMemAccount.FirstName = 'Test FName';
        objMemAccount.LastName = 'LName'; 
        objMemAccount.source_platform_code__c = 'EM' ;
        objMemAccount.Enterprise_Id__c = 'mbr234';
        objMemAccount.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId();
        objMemAccount.Birthdate__c = String.valueOf(System.today().month()+'/'+system.today().day()+'/'+System.today().year());
        insert objMemAccount;
        system.assertEquals(objMemAccount.LastName == 'LName', true);
        
        PurchaserPlan oMedicalPolicy = new PurchaserPlan();
        oMedicalPolicy.Policy_Group_Number__c='11111';
        oMedicalPolicy.Name = 'Medical Product';
        oMedicalPolicy.source_cust_cov_key__c = '12345678901234567890' ; 
        oMedicalPolicy.PayerId = objMemAccount.Id;
        oMedicalPolicy.Major_LOB__c ='EPO';  
        oMedicalPolicy.Platform__c = 'LV';
        insert oMedicalPolicy;
        system.assertEquals(oMedicalPolicy.Name == 'Medical Product', true);
        
        MemberPlan objMemberplan = new MemberPlan();
        objMemberplan.Name='MemberPlan1';
        objMemberplan.MemberId=objMemAccount.id;
        objMemberPlan.Member_Dependent_Code__c = '02';
        objMemberPlan.Member_Id_Base__c = 'HUM654332';
        objMemberPlan.GroupNumber = 'myid123';
        objMemberPlan.PlanId= oMedicalPolicy.Id;
        insert objMemberplan; 
        system.assert(objMemberPlan.GroupNumber == 'myid123');
        
        Interaction__c oInt = new Interaction__c();
        oInt.Interacting_With__c = objMemAccount.Id;
        oInt.Interacting_With_type__c = 'Member';
        oInt.Interaction_Origin__c = 'Inbound Call';
        insert oInt;
    }
    
      /*
    * Method to test success response for Chat
    * @param        None
    * @return       None
    */
    @isTest static void testSuccessSendChatRequest(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memPlan = [select id,Member_Id_Base__c,Member_Dependent_Code__c,GroupNumber,Policy_Platform__c from MemberPlan where Name='MemberPlan1' limit 1];
        Account acc = [select Id,Enterprise_Id__c from Account where LastName='LName' limit 1];
        Interaction__c inter = [select id from Interaction__c where Interacting_With__c = :acc.Id];
        string chatJson = '{"client": {"name": "CRM","id": "crm123","metadata": {"advocateId":"'+testUser.Id+'" ,"interactionId": "'+inter.Id+'"}},"query": "can I use this benefit to pay my rent?","chatSessionId": null,"context": {"member": {"id":"'+acc.Enterprise_ID__c+'","isProspect": false,"selectedCoverage": {"idBase":"'+memPlan.Member_Id_Base__c+'","dependentCode":"'+memPlan.Member_Dependent_Code__c+'","groupId":"'+memPlan.GroupNumber+'","platformCode":"'+memPlan.Policy_Platform__c+'","effectiveDate":"2023-10-06"}},"metadata": {"oldPlanName": "xyz"}}}';
       
        System.runAs(testUser){
            test.startTest();
            test.setMock(HttpCalloutMock.class,new ChatSuccessMockResponse());
            MyAssistantAtHumanaChatResponse_DTO_HUM response = MyAssistantAtHumanaChat_LC_HUM.sendChatRequest(chatJson);
            system.assertEquals('Test Response',response.Result.response);
            test.stopTest();
        } 
    }
    
     /*
    * Method to test failure response for Chat
    * @param        None
    * @return       None
   */
    @isTest static void testFailureSendChatRequest(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        MemberPlan memPlan = [select id,Member_Id_Base__c,Member_Dependent_Code__c,GroupNumber,Policy_Platform__c from MemberPlan where Name='MemberPlan1' limit 1];
        Account acc = [select Id,Enterprise_Id__c from Account where LastName='LName' limit 1];
        Interaction__c inter = [select id from Interaction__c where Interacting_With__c = :acc.Id];
        string chatJson = '{"client": {"name": "CRM","id": "crm123","metadata": {"advocateId":"'+testUser.Id+'" ,"interactionId": "'+inter.Id+'"}},"query": null,"chatSessionId": null,"context": {"member": {"id":"'+acc.Enterprise_ID__c+'","isProspect": false,"selectedCoverage": {"idBase":"'+memPlan.Member_Id_Base__c+'","dependentCode":"'+memPlan.Member_Dependent_Code__c+'","groupId":"'+memPlan.GroupNumber+'","platformCode":"'+memPlan.Policy_Platform__c+'","effectiveDate":"2023-10-06"}},"metadata": {"oldPlanName": "xyz"}}}';
       
        System.runAs(testUser){
            test.startTest();
            test.setMock(HttpCalloutMock.class,new ChatFailureMockResponse());
            MyAssistantAtHumanaChatResponse_DTO_HUM dto = MyAssistantAtHumanaChat_LC_HUM.sendChatRequest(chatJson);
            test.stopTest();
        } 
    } 
    
     /*
    * Method to test exception response for Chat
    * @param        None
    * @return       None
    */
    @isTest static void testExceptionSendChatRequest(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        
        string chatJson = '{}';
       
        System.runAs(testUser){
            test.startTest();
            MyAssistantAtHumanaChatResponse_DTO_HUM dto = MyAssistantAtHumanaChat_LC_HUM.sendChatRequest(chatJson);
            test.stopTest();
        } 
    }
    
    
}