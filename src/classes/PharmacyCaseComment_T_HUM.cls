/*******************************************************************************************************************************
Apex Class Name : PharmacyCaseComment_T_HUM.cls
Version         : 1.0
Created On      : April 25,2017
Function        : Class contains test methods PharmacyCaseComment_C_HUM class.

Modification Log: 
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma                87024                  04/25/2017                   Original Version
 * Vishal Verma 			   114200				  07/27/2017				   REQ - 323542 : Exception handling 
 * Sharan Shanmugam									  08/09/2017				   REQ - 324814 : Save Case Comment to CRM during ePost failure
 ********************************************************************************************************************************/
@isTest
private class PharmacyCaseComment_T_HUM 
{
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User usrHPS = HUMTestDataHelperTest.createTestUser('User2','Humana Pharmacy Specialist');   
        insert usrHPS;
        
        system.runAs(usrHPS)
        {
            Case objCase = new Case();
            objCase = HUMTestDataHelperTest.getCase();
            insert objCase;
            System.assertEquals(objCase.Interacting_With_Name__c, 'Test');

            CaseComment ccObj = new CaseComment();
            ccObj.CommentBody = '(TEST) Comment';
            ccObj.ParentId = objCase.Id;
            insert ccObj;
            System.assertEquals(ccObj.CommentBody, '(TEST) Comment');
            
            CaseComment ccObjMod = new CaseComment();
            ccObjMod.CommentBody = 'TEST Comment';
            ccObjMod.ParentId = objCase.Id;
            insert ccObjMod;
            System.assertEquals(ccObjMod.CommentBody, 'TEST Comment');
            
            
            HUM_Webservice_Callout__c RighSourceEditServiceDetails = new HUM_Webservice_Callout__c(Name = 'RighSourceService_EditMember',Certificate_Name__c = 'Test_Certificate' ,Active__c = true,End_Point_URL__c ='https://int-crmservicews.humana.com/RighSourceService_GetMember/' ,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c ='application/json' );
            insert RighSourceEditServiceDetails;

        }
    
    }
    
    /*
    * Method Name : testCaseCommentCreation
    * Description : This method is used To test the Case Comment Creation
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testCaseCommentCreation()
    {
        User testUser = [Select Id, Name, Current_Queue__C from User where LastName='test User2' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c, 'Agent');

            List<CaseComment> ccObjList = new List<CaseComment> ();
            ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
            System.assertEquals(ccObjList.size(), 2);
            
            ApexPages.currentpage().getParameters().put('type','New');			
            ApexPages.StandardController standCon = new ApexPages.standardController(objCase);
            PharmacyCaseComment_C_HUM cCommen = new PharmacyCaseComment_C_HUM(standCon);
            cCommen.fetchCaseComments();
            cCommen.getCaseCommentOptions();
            cCommen.populateHiddentVal();
            cCommen.sCaseCommentBody = 'Test New Comment';
            cCommen.saveCaseComments();
            Test.stopTest();
            
        }
    }
    
    /*
    * Method Name : testCaseCommentEdit
    * Description : This method Is used To test edit case comment
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testCaseCommentEdit()
    {
        User testUser = [Select Id, Name, Current_Queue__C from User where LastName='test User2' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c, 'Agent');

            List<CaseComment> ccObjList = new List<CaseComment> ();
            ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
            System.assertEquals(ccObjList.size(), 2);
            
            ApexPages.currentpage().getParameters().put('type',ccObjList[0].id);
			ApexPages.currentpage().getParameters().put('redirectFlag','true');
            ApexPages.currentpage().getParameters().put('errorCaseComment','true');
            ApexPages.StandardController standCon = new ApexPages.standardController(objCase);
            PharmacyCaseComment_C_HUM cCommen = new PharmacyCaseComment_C_HUM(standCon);
            cCommen.getCaseCommentOptions();
            cCommen.populateHiddentVal();
            cCommen.sCaseCommentBody = 'Test New Comment';
            cCommen.saveCaseComments();
            cCommen.fetchCaseComments();
            cCommen.saveHPCaseComment();
            Test.stopTest();
            
        }
    }
    
    /*
    * Method Name : testCaseCommentNegative
    * Description : This method Is used To test negative scenarios for edit case comment
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testCaseCommentNegative()
    {
        User testUser = [Select Id, Name, Current_Queue__C from User where LastName='test User2' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
            System.assertEquals(objCase.Interacting_With_Type__c, 'Agent');

            List<CaseComment> ccObjList = new List<CaseComment> ();
            ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
            System.assertEquals(ccObjList.size(), 2);
            
            ApexPages.currentpage().getParameters().put('type',ccObjList[0].id);
			ApexPages.currentpage().getParameters().put('errorCaseComment','false');
            ApexPages.StandardController standCon = new ApexPages.standardController(objCase);
            PharmacyCaseComment_C_HUM cCommen = new PharmacyCaseComment_C_HUM(standCon);
			cCommen.saveHPCaseComment();
            delete objCase;
            cCommen.getCaseCommentOptions();
            cCommen.sCaseCommentBody = null;
            cCommen.saveCaseComments();
            Test.stopTest();
            
        }
    }
    
    /*
    * Method Name : testLogNotes
    * Description : This method Is used To test log notes webservice
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testLogNotes()
    {
        User testUser = [Select Id, Name, Current_Queue__C from User where LastName='test User2' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
            List<CaseComment> ccObjList = new List<CaseComment> ();
            ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
                Continuation continueObj = (Continuation)PharmacyCaseComment_C_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' , 'sRecId' , 'sAccNum',objCase.id);
                continueObj.continuationMethod = 'parseLogNoteResponse';
                continueObj.state = 'BOTH';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                string reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                PharmacyCaseComment_C_HUM.parseLogNoteResponse(labelGetmember,continueObj);
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testLogNotesFault
    * Description : This method Is used To test log notes webservice for Fault Scenario
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testLogNotesFault()
    {
        User testUser = [Select Id, Name, Current_Queue__C from User where LastName='test User2' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
            List<CaseComment> ccObjList = new List<CaseComment> ();
            ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
                Continuation continueObj = (Continuation)PharmacyCaseComment_C_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' , 'sRecId' , 'sAccNum',objCase.id);
                continueObj.continuationMethod = 'parseLogNoteResponse';
                continueObj.state = 'BOTH';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{"Fault":{"faultstring":"Failure","faultcode":"1","detail":{"transactionInfo":{"URL_in":"http://127.0.0.1:8316/RightSourceService_V1.0/GetMember","transaction_id":"1","transaction_client":"C","processor_type":"Multiprotocol Gateway","processor_name":"JSONServicesGateway"},"errorInfo":{"error_subcode":"0x00d30003","error_protocol_response":"0","error_protocol_reason_phrase":"Test","error_message":"Member not found","error_headers":"HTTP/1.1 500 Internal Server Error","error_code":"0x00d30003"}}}}';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                string reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                PharmacyCaseComment_C_HUM.parseLogNoteResponse(labelGetmember,continueObj);
            
            Test.stopTest();
        }
    }
    
    /*
    * Method Name : testLogNotesUID
    * Description : This method Is used To test log notes webservice for UID secnario
    * Return type : None
    * Parameter   : None
    */
    Static testMethod void testLogNotesUUID()
    {
        User testUser = [Select Id, Name, Current_Queue__C from User where LastName='test User2' limit 1];
        
        System.runAs(testUser)
        {
            Test.startTest();
            Case objCase = new Case();
            objCase = [Select Id,Complaint__c,Interacting_About_Type__c,Interacting_With_Type__c from Case where Interacting_With_Type__c ='Agent' limit 1];
            List<CaseComment> ccObjList = new List<CaseComment> ();
            ccObjList = [Select Id,CommentBody from CaseComment where parentid = :objCase.id];
                Continuation continueObj = (Continuation)PharmacyCaseComment_C_HUM.sendRequestLogNotes('Test' , '1048868178' ,'sHumanaIdNum' , 'sRecId' , 'sAccNum',objCase.id);
                continueObj.continuationMethod = 'parseLogNoteResponse';
                continueObj.state = 'BOTH';
                HttpResponse httpRes = new Httpresponse ();
                String resBodyString= '{ "EditMemberResponse":{ "transactionKey":"1120000275944", "transactionTimeStamp":"Tue, Mar 21 2017 08:55:28 313 EDT", "UID":"0" } }';
                httpRes.setBody(resBodyString);
                system.assert(httpRes.getBody() == resBodyString);
                Map<String, HTTPRequest> mapDetails = continueObj.getRequests();
                string reqType = '';
                for(String s : mapDetails.keyset())
                {
                    reqType = s;
                } 
                List<string>labelGetmember = new List<string>();
                labelGetmember.add(reqType);
                Test.setContinuationResponse(reqType,httpRes);
                PharmacyCaseComment_C_HUM.parseLogNoteResponse(labelGetmember,continueObj);
            
            Test.stopTest();
        }
    }
}