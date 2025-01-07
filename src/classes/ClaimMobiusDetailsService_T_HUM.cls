/***************************************************************************************************************** 
Apex Class Name  : ClaimMobiusDetailsService_T_HUM 
Version          : 1.0  
Created Date     : May 18th, 2016
Function         : Test Class for ClaimMobiusDetails_S_HUM 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Pallavi Ravishankar           27974                05/18/2016        Original Version
 *   Prasanthi Kandula                                  11/25/2019        REQ - 416044 Removed references for 'MobiusService_GetDocument' webservice callout
 *   Anjani Vaddadi                                     02/25/2020        REQ-764250
 ******************************************************************************************************************************/
 @isTest
Private Class ClaimMobiusDetailsService_T_HUM 
{
    /*
     * Method Name : prepareTestData
     * Description : Used to create wrapper class data
     * Return type : void
     * Paramater   : NA
     */
    @testSetup static void dataSetup()
    {
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        System.assertEquals(oUser.LastName, 'test User1');
        
        HUM_Webservice_Callout__c oDocumentList = new HUM_Webservice_Callout__c(Request_Type__c = 'POST', Name='MobiusService_GetDocumentList',
        End_Point_URL__c = 'test',
        Certificate_Name__c = 'Humana_GuidanceCare',
        Timeout__c = '12000',
        Content_Type__c = 'text/application');
        insert oDocumentList;
        System.assertEquals(oDocumentList.Timeout__c, '12000');
       
    }
    
   /*
    * Method Name : TestSetupRequest
    * Description : Used to test SetupRequest method
    * Return type : void
    * Paramater   : NA
    */  
   testMethod static void TestSetupRequest()
   {
     User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
     System.assertEquals('test User1',oUser.LastName);
     system.runAs(oUser)
     {  
          ClaimMobiusDetails_S_HUM   oMobiusService = new ClaimMobiusDetails_S_HUM ();
          HttpRequest oRequest = new HttpRequest();
          HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c Limit 1];
          string sRequestBody = 'Body';
          oRequest = oMobiusService.setupRequest(orequest,oSvcHeaderPayment, sRequestBody);
          system.assert(oRequest!=null);
         
     }
    }   
    /*
    * Method Name : TestProcessResponse
    * Description : Used to test ProcessResponse method
    * Return type : void
    * Paramater   : NA
    */ 
    
   testMethod static void TestProcessResponse()
   {
     User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
     System.assertEquals('test User1',oUser.LastName);
     system.runAs(oUser)
     {
      Test.startTest();
      ClaimMobiusDetails_S_HUM   oMobiusService = new ClaimMobiusDetails_S_HUM ();
      List<ClaimsDocumentsList_DTO_HUM> lstDocDetails = New  List<ClaimsDocumentsList_DTO_HUM>();
      Account objAcc = HUMTestDataHelperTest.getAccount();
         objAcc.Name = 'testacc';
         objAcc.Enterprise_ID__c = '213654';
         objAcc.Mbr_Gen_Key__c = '0099414358170';
         insert objAcc;
         
         Policy__c policy = HUMTestDataHelperTest.getPolicy();
         policy.Group_Name__c = objAcc.id;
         insert policy;
         
         Policy_Member__c oPolicymem = HUMTestDataHelperTest.getPolicyMember();
         oPolicymem.Effective_Date__c = Date.today().addDays(-1).format();
         oPolicymem.End_Date__c = Date.today().addDays(1).format();
         oPolicymem.Name='testpolmem';
         oPolicymem.Has_Vitality__c ='Y';       
         oPolicymem.Policy__c = policy.id;
         oPolicymem.Member__c = objAcc.id;        
         insert oPolicymem ;
         
         Account objAcc1 = HUMTestDataHelperTest.getAccount();
         objAcc1.Name = 'testacc1';
         objAcc1.Enterprise_ID__c = '513654';
         objAcc1.Mbr_Gen_Key__c = '4131413039909';
         insert objAcc1;
         
         Policy__c policy1 = HUMTestDataHelperTest.getPolicy();
         policy1.Group_Name__c = objAcc1.id;
         insert policy1;
         
         Policy_Member__c oPolicymem1 = HUMTestDataHelperTest.getPolicyMember();
         oPolicymem1.Effective_Date__c = Date.today().addDays(-1).format();
         oPolicymem1.End_Date__c = Date.today().addDays(1).format();
         oPolicymem1.Name='testpolmem1';
         oPolicymem1.Has_Vitality__c ='Y';       
         oPolicymem1.Policy__c = policy1.id;
         oPolicymem1.Member__c = objAcc1.id;
         oPolicymem1.Subscriber__c = oPolicymem.id;
         insert oPolicymem1 ;  
         string sStmtType ='Member';
         string sjson = '{"getDocumentListResponse": {"getDocumentListReturn": {"DocumentSearch": [{"TopicResultList": {"TopicResult": [{"TopicKeyList":{"TopicKey":[{"topicName": "PersonId","PersonId": "1001981051","MemberGenKey": "4131413039909","statementBeginDate": "2019-10-13","statementEndDate": "2019-10-14","statementType": "XBSF",       "statementDescription": "Family Roll-up � SmartEOB"}]},"DocumentKey": "AFP VIEWDIRECTSS|AFPTEST   |20191014062823|41314130399092019101320191014X|10","PrintedDate": "2019-10-14T06:28:23"}]}}]}}}';//'{"getDocumentListResponse": {"getDocumentListReturn":{"DocumentSearch": [{"TopicResultList":{"TopicResult":[{"TopicKeyList": {"TopicKey": [{"topicName":"EOBMEMBER","PersonId":"","statementBeginDate":"","statementEndDate": "","statementType": ""}]},"DocumentKey": "AFP WI EOB|EOBSTMT   |20151231045205|LVH53778960021102861          |4","PrintedDate": "2014-11-14T06:32:58"}]}},{"TopicResultList":{"TopicResult":[{"TopicKeyList": {"TopicKey": [{"topicName":"PersonId","PersonId":"1038533017","statementBeginDate": "","statementEndDate": "","statementType":"XBSI"}]},"DocumentKey":"AFP VIEWDIRECTSS|AFPTEST|20141114063258|61510030760012014101120141109X|8","PrintedDate": "2014-11-14T06:32:58"}]}}]}}}';
      lstDocDetails = oMobiusService.processResponse(sjson, sStmtType, oPolicymem1.Id);
      System.assertNotEquals(lstDocDetails,null);
      sjson='xyz';
      lstDocDetails = oMobiusService.processResponse(sjson, sStmtType, oPolicymem1.Id);
      Test.stopTest();
    
     }
    }  
    
  /*
    * Method Name : TestAssignValuesToDocumentHelper
    * Description : Used to test AssignValuesToDocumentHelper method
    * Return type : void
    * Paramater   : NA
    */  
   testMethod static void TestAssignValuesToDocumentHelper()
   {
     User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
     System.assertEquals('test User1',oUser.LastName);
     system.runAs(oUser)
     {  
         ClaimMobiusDetails_S_HUM   oMobiusService = new ClaimMobiusDetails_S_HUM  ();
           MobiusServiceGetDocumentRequest_H_HUM oRequest=new  MobiusServiceGetDocumentRequest_H_HUM();
          string sDocumentKey='AFP VIEWDIRECTSS|AFPTEST   |20150915015451|61510030760012015080120150831P|10';
          oRequest = oMobiusService.assignValuesToDocumentHelper(sDocumentKey);
          system.assert(oRequest!=null);
         
     }
    }  
    
       
     /*
    * Method Name : TestParseDocumentResponse
    * Description : Used to test parseDocumentResponse method
    * Return type : void
    * Paramater   : NA
    */ 
    
   testMethod static void TestParseDocumentResponse()
   {
     User oUser = [Select Id,lastname, Current_Queue__c from User where LastName = 'test User1'];
     System.assertEquals('test User1',oUser.LastName);
     system.runAs(oUser)
     {
      Test.startTest();
       ClaimMobiusDetails_S_HUM  oMobiusService = new ClaimMobiusDetails_S_HUM ();
      ClaimStatementsDocument_DTO_HUM ostmtDoc = new  ClaimStatementsDocument_DTO_HUM();
      HttpResponse oresponse=new HttpResponse();
      oResponse.setHeader('Content-Type', 'application/json');
      oResponse.setHeader('SOAPAction', '');    
      oResponse.setHeader('Connection','keep-alive');
      string sJson = '{"getDocumentResponse":{"getDocumentReturn":"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"}}';
      oResponse.setBody(sJson);
      oResponse.setStatusCode(200); 
      ostmtDoc=oMobiusService.parseDocumentResponse(oresponse);
      System.assertNotEquals(ostmtDoc,null);
      Test.stopTest();
    
     }
    }    
}