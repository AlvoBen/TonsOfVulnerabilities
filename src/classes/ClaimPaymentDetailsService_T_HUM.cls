/***************************************************************************************************************** 
Apex Class Name  : ClaimPaymentDetailsService_T_HUM
Version          : 1.0  
Created Date     : May 04th, 2016
Function         : Test Class for ClaimPaymentDetails_S_HUM 

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Pallavi Ravishankar          26844                  05/04/2016        Original Version
 *   Pallavi Ravishankar                                 06/06/2016        Added logic for REQ 263142 code coverage
 *   Santhi Mandava                                      03/12/2019        Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c
 ******************************************************************************************************************************/
 @isTest
private Class  ClaimPaymentDetailsService_T_HUM
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
        
        HUM_Webservice_Callout__c oPaymentDetails = new HUM_Webservice_Callout__c(Request_Type__c = 'GET', Name='ClaimsService_GetClaimsPayment_V3',
        End_Point_URL__c = 'test',
        Certificate_Name__c = 'Humana_GuidanceCare',
        Timeout__c = '100',
        Content_Type__c = 'text/application');
        insert oPaymentDetails;
        System.assertEquals(oPaymentDetails.Timeout__c, '100');
        
        Endpoint_Url_HUM__c oUrl=new Endpoint_Url_HUM__c();
        oURL.name='SSO_LOGINURL';
        oUrL.Remote_Url__c='test';
        insert oUrl;
        System.assertEquals(oURL.name,'SSO_LOGINURL');
    
        Endpoint_Url_HUM__c oCheckUrl=new Endpoint_Url_HUM__c();
        oCheckURL.name='CheckStatusToolLink';
        oCheckUrL.Remote_Url__c='test2';
        insert oCheckUrl;
        System.assertEquals(oCheckURL.name,'CheckStatusToolLink');
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
          ClaimPaymentDetails_S_HUM  oPaymentService = new ClaimPaymentDetails_S_HUM ();
          HttpRequest oRequest = new HttpRequest();
          HUM_Webservice_Callout__c oSvcHeaderPayment = [Select Request_Type__c,End_Point_URL__c,Certificate_Name__c,Timeout__c,Content_Type__c From HUM_Webservice_Callout__c Limit 1];
          String sRequestBody = 'Body';
          oRequest = oPaymentService.setupRequest(oRequest, oSvcHeaderPayment, sRequestBody);
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
      ClaimPaymentDetails_S_HUM  oPaymentService = new ClaimPaymentDetails_S_HUM ();
      List<ClaimsPaymentDetails_DTO_HUM> lstPmDetails = new List<ClaimsPaymentDetails_DTO_HUM>();
      string sjson = '{"GetEhub835XmlResponse": {"GetEhub835XmlResult": {"Transaction_835":[{"CheckNumber": "1002433070","PayeeName": "ANESTHESIOLOGY CONSULTANTS", "PayeeAddress": "PO BOX 934959,ATLANTA,GA,311934959","CheckDate": "04/28/2013","PaymentType": "CHK","PaymentAmount": "630","ClaimDetails": {"ClaimDetail":    [{"ClaimNumber": "201304206682568","ClaimPaymentAmount": "157.5"},{"ClaimNumber": "201304206718250","ClaimPaymentAmount": "157.5"},{"ClaimNumber": "201304206437730","ClaimPaymentAmount": "157.5"},{"ClaimNumber": "201304206600678","ClaimPaymentAmount": "157.5"}]},"PaymentTypeID": "","PayeeID": "224673-3520913310000","PaymentIssueDate": "04/28/2013","PaymentStatus": "","PaymentStatusDate": ""}]}}}';
      System.assertNotEquals(lstPmDetails,null);
      lstPmDetails = oPaymentService.processResponse(sjson);
      sjson='{"data":[{"sPaymentTypeID":"","sPaymentType":"CHK","sPaymentStatusDate":"","sPaymentStatus":"","sPaymentIssueDate":"08/17/2014","sPaymentAmount":"69.97","sPayeeName":"MAYES COUNTY HMPN LLC","sPayeeID":"285067-0000000000000","sPayeeAddress":"PO BOX 11456,BELFAST,ME,049154005","sCheckNumber":"000034085","sCheckDate":"08/17/2014","ClaimDetails":{"ClaimDetail":[{"ClaimPaymentAmount":"46.73","ClaimNumber":"996377165"},{"ClaimPaymentAmount":"23.24","ClaimNumber":"994884560"}]}}]}}';
      lstPmDetails = oPaymentService.processResponse(sjson);
      System.assertNotEquals(lstPmDetails,null);
      Test.stopTest();
    
     }
    }  
}