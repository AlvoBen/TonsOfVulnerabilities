/******************************************************************************************************************
Apex class Name    : CreateChatTranscriptService_T_HUM
Version            : 1.0
Function           : This class is used as test class for create transcript Rest Service
Created On         : June 08 ,2020
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Joel George                                    6/08/2020          Original Version

------------------------------------------------------------------------------------------------------------------- */

@isTest
private class CreateChatTranscriptService_T_HUM {

/**
    * This method is used to insert the data only once and called automicticaly in all methods 
    *
    * @param  None      
    * @return  Void
    */
    @testSetup static void setup()
    {
    
        User oUser = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        insert oUser;
      
        System.runAs(oUser) 
        {
                        
            //Classification
            Classification__c classificationRecDec = new Classification__c();
            classificationRecDec.Name = 'Affidavit';
            classificationRecDec.Is_Active__c = true;
            insert classificationRecDec;
            system.assertEquals(classificationRecDec.Name == 'Affidavit',true);
            
            //Intent
            Intent__c intentRec = new Intent__c();
            intentRec.Name = 'New';           
            intentRec.Is_Active__c = true;
            insert intentRec;
            system.assertEquals(intentRec.name == 'New',true);
            

             //Add Account
             List<Account> lstAccount = new  List<Account>();
             Account acc = new Account( Enterprise_Id__c = '223300104', firstname = 'test1' ,lastname = 'method1' , Birthdate__c= '01/01/1990',  ETL_Record_Deleted__c = false , recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Member').getRecordTypeId());
             lstAccount.Add(acc);
             Insert lstAccount;
             system.assertNotEquals(lstAccount, null , 'Accounts not created');
             
            //Case Details
            Case caseObj = HUMTestDataHelperTest.getCase();
            caseObj.Interacting_With__c = lstAccount[0].Id;
            caseObj.Interacting_With_Name__c = 'Test Name';
            caseObj.Classification_ID__c = classificationRecDec.ID;
            caseObj.Intent_ID__c  = intentRec.id;
            caseobj.Interacting_With_Type__c = 'Member';
            insert caseObj ;
            System.assertEquals(caseobj.Interacting_With_Type__c == 'Member', true);
            
            Interaction__c intObj = new Interaction__c();
            intObj.Interacting_With__c = lstAccount[0].Id;
            intObj.Interacting_With_type__c = 'Member';
            intObj.Interaction_Origin__c = 'Fax';
            insert intObj;
            
             User testUser = HUMTestDataHelperTest.createTestUser('Customer_Case_specialist_User', 'Customer Care Specialist');
            testUser.Current_Queue__c = 'Louisville RSO Enrollment';
            insert testUser;
            System.assertEquals(testUser.lastname == 'test Customer_Case_specialist_User', true);
            // RESTRICTED_USER_ID value Is org specific hence it Is Not consdered as GLOBAL_CONTSANTS
            HUMConstants__c hCons2 = New HUMConstants__c(Name ='RESTRICTED_USER_ID',StringValue__c=String.valueOf(testUser.Id));
            insert hCons2;
            System.assert(HUMConstants__c.getValues('RESTRICTED_USER_ID') != NULL);


        }
    }
    
     @isTest static void testHUMChatTranscriptService()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test User1',true);
        
        List<Case> lstCase =  [Select id,CaseNumber from Case where Interacting_With_Name__c='Test Name'];
        
        List<Interaction__c> lstInt =  [Select id,Name from Interaction__c where Interacting_With_type__c='Member'];

        
        System.runAs(oUser) 
        {
            CreateChatTranscriptRequest_DTO_HUM request = new CreateChatTranscriptRequest_DTO_HUM();
            CreateChatTranscriptRequest_DTO_HUM.CreateChatTranscriptRequest  CreateChatTranscriptRequest  = new CreateChatTranscriptRequest_DTO_HUM.CreateChatTranscriptRequest();
                        
                        
            CreateChatTranscriptRequest.InteractionNumber = lstInt[0].Name;
            CreateChatTranscriptRequest.CaseNumber = lstCase[0].CaseNumber ;   
            CreateChatTranscriptRequest.TranscriptBody ='test';
            CreateChatTranscriptRequest.ChatType ='IVANINA-Escalated';
            CreateChatTranscriptRequest.Issue ='test';
            CreateChatTranscriptRequest.PlanName ='test';
            CreateChatTranscriptRequest.URL ='test';
            CreateChatTranscriptRequest.AverageResponseTimeVisitor =1;
            CreateChatTranscriptRequest.MaxResponseTimeVisitor = 1;


            CreateChatTranscriptRequest.RequestTime ='2020-06-23 12:00:00';
            CreateChatTranscriptRequest.EndTime ='2020-06-23 12:00:00';
            CreateChatTranscriptRequest.StartTime ='2020-06-23 12:00:00';
                    
            CreateChatTranscriptRequest.EndedBy ='Agent';
            CreateChatTranscriptRequest.AverageResponseTimeOperator =1;
            CreateChatTranscriptRequest.MaxResponseTimeOperator =1;
            CreateChatTranscriptRequest.VisitorMessageCount =1;
            CreateChatTranscriptRequest.OperatorMessageCount =1;
            CreateChatTranscriptRequest.SourceApp ='IVANINA';
            CreateChatTranscriptRequest.SuccessfulResolution ='Yes';
            CreateChatTranscriptRequest.SourceKey ='Y123es';

            request.CreateChatTranscriptRequest = CreateChatTranscriptRequest;

            RestRequest req = new RestRequest();

            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();

            req.requestURI = '/LiveChatTranscriptService_V1.0/CreateChatTranscript';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);

            RestContext.request = req;
            RestContext.response= res;

            Test.StartTest();               
            CreateChatTranscriptService_I_HUM.createChatTranscriptInfo();
            RestResponse response = RestContext.response;

             CreateChatTranscriptResponse_DTO_HUM chatResponse = new CreateChatTranscriptResponse_DTO_HUM();
            chatResponse = (CreateChatTranscriptResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CreateChatTranscriptResponse_DTO_HUM.class);
            system.assertEquals(chatResponse.CreateChatTranscriptResponse.Success =='true',true); 
            Test.StopTest();
            

        }
    }
    
     @isTest static void testfaultHUMChatTranscriptService()
    {
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = [SELECT Id,lastname  FROM User  WHERE Lastname ='test User1' LIMIT 1];
        system.assertEquals(oUser.lastname == 'test User1',true);
        
        List<Case> lstCase =  [Select id,CaseNumber from Case where Interacting_With_Name__c='Test Name'];
        
        List<Interaction__c> lstInt =  [Select id,Name from Interaction__c where Interacting_With_type__c='Member'];
        
        System.runAs(oUser) 
        {
            CreateChatTranscriptRequest_DTO_HUM request = new CreateChatTranscriptRequest_DTO_HUM();
            CreateChatTranscriptRequest_DTO_HUM.CreateChatTranscriptRequest  CreateChatTranscriptRequest  = new CreateChatTranscriptRequest_DTO_HUM.CreateChatTranscriptRequest();
                        
                        
            CreateChatTranscriptRequest.InteractionNumber = lstInt[0].Name;
            CreateChatTranscriptRequest.CaseNumber = lstCase[0].CaseNumber ;   
            CreateChatTranscriptRequest.TranscriptBody ='test';
            CreateChatTranscriptRequest.ChatType ='IVANINA-Escalated';
            CreateChatTranscriptRequest.Issue ='test';
            CreateChatTranscriptRequest.PlanName ='test';
            CreateChatTranscriptRequest.URL ='test';
            CreateChatTranscriptRequest.AverageResponseTimeVisitor =1;
            CreateChatTranscriptRequest.MaxResponseTimeVisitor = 1;


            CreateChatTranscriptRequest.RequestTime ='2020-06-23 12:00:00';
            CreateChatTranscriptRequest.EndTime ='2020-06-23 12:00:00';
            CreateChatTranscriptRequest.StartTime ='2020-06-23 12:00:00';
                    
            CreateChatTranscriptRequest.EndedBy ='Aasgent';
            CreateChatTranscriptRequest.AverageResponseTimeOperator =1;
            CreateChatTranscriptRequest.MaxResponseTimeOperator =1;
            CreateChatTranscriptRequest.VisitorMessageCount =1;
            CreateChatTranscriptRequest.OperatorMessageCount =1;
            CreateChatTranscriptRequest.SourceApp ='IVANINA';
            CreateChatTranscriptRequest.SuccessfulResolution ='Yes';
            CreateChatTranscriptRequest.SourceKey ='Y123es';

            request.CreateChatTranscriptRequest = CreateChatTranscriptRequest;

            RestRequest req = new RestRequest();

            String JSONMsg = System.JSON.serialize(request);
            RestResponse res = new RestResponse();

            req.requestURI = '/LiveChatTranscriptService_V1.0/CreateChatTranscript';  //Request URL
            req.httpMethod = 'POST';//HTTP Request Type
            req.requestBody = Blob.valueof(JSONMsg);

            RestContext.request = req;
            RestContext.response= res;

            Test.StartTest();               
            CreateChatTranscriptService_I_HUM.createChatTranscriptInfo();
            RestResponse response = RestContext.response;

            CreateChatTranscriptResponse_DTO_HUM chatResponse = new CreateChatTranscriptResponse_DTO_HUM();
            chatResponse = (CreateChatTranscriptResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CreateChatTranscriptResponse_DTO_HUM.class);
            system.assertEquals(chatResponse.CreateChatTranscriptResponse.Success =='false',true); 
            
            CreateChatTranscriptRequest.ChatType = null;
            request.CreateChatTranscriptRequest = CreateChatTranscriptRequest;
            JSONMsg = System.JSON.serialize(request);
            req.requestBody = Blob.valueof(JSONMsg);
            RestContext.request = req;
            RestContext.response= res;
            CreateChatTranscriptService_I_HUM.createChatTranscriptInfo();
            response = RestContext.response;
            chatResponse = (CreateChatTranscriptResponse_DTO_HUM)System.JSON.deserialize(response.responseBody.toString(), CreateChatTranscriptResponse_DTO_HUM.class);     
            system.assertEquals(chatResponse.CreateChatTranscriptResponse.Success =='false',true); 
            
            Test.StopTest();
            

        }
    }

}