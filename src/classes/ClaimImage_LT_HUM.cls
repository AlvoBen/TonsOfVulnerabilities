/**************************************************************************** 
Apex Class Name  : ClaimImage_LT_HUM 
Version          : 1.0  
Created Date     : dec 16, 2022
Function         : Test class for ClaimImage_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Sagar.G                                                                Original Version REQ - 3856190:Ability as a CRM Service end user to view the Claim image for a specific claim within CRM Service - Claim Detail Page
***************************************************************************************************/
@isTest
private Class ClaimImage_LT_HUM
{
/*
 * Method Name : prepareTestData
 * Description : Used to create wrapper class data
 * Return type : void
 * Paramater   : NA
 */
 @testSetup static void prepareTestData() 
 {
     User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
     insert oCCSUser;
     system.assertEquals(oCCSUser.LastName,'test User1');
     system.runAs (oCCSUser) 
     {
         HUM_Webservice_Callout__c oHUMClaimImageService = new HUM_Webservice_Callout__c();      
         oHUMClaimImageService.name='ImagingService_GetClaimURL_V2';
         oHUMClaimImageService.Active__c=true;
         oHUMClaimImageService.Certificate_Name__c='Humana_GuidanceCare_Venus';
         oHUMClaimImageService.End_Point_URL__c='https://int-crmservicews.humana.com/HumanaImagingService_V2/GetClaimURLs';
         oHUMClaimImageService.Timeout__c='120000';
         oHUMClaimImageService.Content_Type__c='application/json';
         oHUMClaimImageService.Request_Type__c='POST';
         insert oHUMClaimImageService;
         system.assert(oHUMClaimImageService.id!=null);
         system.assert(oHUMClaimImageService.name == 'ImagingService_GetClaimURL_V2');       
     }
 }
   
 /*
  * Method Name : runData
  * Description : Used to run scenerios of test data
  * Return type : void
  * Paramater   : NA
  */ 
  static testMethod void runPositiveDataPlatformLV()
  {    
      User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
      system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
          
      system.runAs (oCCSUser) 
      { 
          Test.StartTest();
           ClaimImage_LC_HUM.sStartDate='12/23/2015';
           ClaimImage_LC_HUM.sPlatform ='MTV';
           ClaimImage_LC_HUM.sClient=ClaimImage_LC_HUM.setclientValue(ClaimImage_LC_HUM.sPlatform);
           
           Continuation conti = (Continuation)ClaimImage_LC_HUM.initiateRequest('820221461365410', 'EM', '2022-26-10T00:00:00','58');
           Continuation objCon = new Continuation(120);
           objCon.continuationMethod='processResponse';
           objCon.state='test';
           string labelValue= 'Continuation-1';
           HttpResponse oResponse = new HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           string sJson = '{"GetClaimURLsResponse": {"ClaimURLs": [{ "DCN": "0000028383175040010000100001", "ECN": "0000028383175040010000100001","ReceiptDate": "2016-02-17T20:52:13Z","Repriced": false,"TotalBilledCharges": 800, "BillingProviderLastFacilityName": "CONNER WELLNESS CENTER", "BillingProviderId": "1942499918","RenderingProviderLastFacilityName": "CONNER WELLNESS CENTER","RenderingProviderId": "1942499918", "ClaimFrequencyCode": "1", "URL": "http://dev-loufeo1.humana.com/edocs/?dcn=0000028383175040010000100001&docid=1234562" }]}}';
           oResponse.setBody(sJson);
           oResponse.setStatusCode(200);
           System.debug('oResponse'+oResponse.getStatusCode());
           Test.setContinuationResponse(ClaimImage_LC_HUM.continuationLabel, oResponse);
           List<String> labels = new List<String>{ClaimImage_LC_HUM.continuationLabel};
           ClaimImage_LC_HUM.processResponse(labels, ClaimImage_LC_HUM.continuationState);
           Test.StopTest();

      }
   }
   
   static testMethod void runPositiveDataPlatformMTV()
   {    
       User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
       system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');   
       System.runAs (oCCSUser) 
       { 
           Test.StartTest();
           ClaimImage_LC_HUM.sStartDate='12/23/2015';
           ClaimImage_LC_HUM.sPlatform ='MTV';
           ClaimImage_LC_HUM.sClient=ClaimImage_LC_HUM.setclientValue(ClaimImage_LC_HUM.sPlatform);
           Continuation conti = (Continuation)ClaimImage_LC_HUM.initiateRequest('820221461365410', 'EM', '2022-26-10T00:00:00','58');
           Continuation objCon = new Continuation(120);
           objCon.continuationMethod='processResponse';
           objCon.state='test';
           string labelValue= 'Continuation-1';
           HttpResponse oResponse = new HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           string sJson = '{"GetClaimURLsResponse": {"ClaimURLs": [{ "DCN": "0000028383175040010000100001", "ECN": "0000028383175040010000100001","ReceiptDate": "2016-02-17T20:52:13Z","Repriced": false,"TotalBilledCharges": 800, "BillingProviderLastFacilityName": "CONNER WELLNESS CENTER", "BillingProviderId": "1942499918","RenderingProviderLastFacilityName": "CONNER WELLNESS CENTER","RenderingProviderId": "1942499918", "ClaimFrequencyCode": "1", "URL": "http://dev-loufeo1.humana.com/edocs/?ecn=0000028383175040010000100001&docid=1234562" },{ "DCN": "0000028383175040010000100001", "ECN": "0000028383175040010000100001","ReceiptDate": "2016-02-17T20:52:13Z","Repriced": false,"TotalBilledCharges": 800, "BillingProviderLastFacilityName": "CONNER WELLNESS CENTER", "BillingProviderId": "1942499918","RenderingProviderLastFacilityName": "CONNER WELLNESS CENTER","RenderingProviderId": "1942499918", "ClaimFrequencyCode": "1", "URL": "http://dev-loufeo1.humana.com/edocs/?ecn=0000028383175040010000100001&docid=1234562" }]}}';
           oResponse.setBody(sJson);
           oResponse.setStatusCode(200);
           System.debug('oResponse'+oResponse.getStatusCode());
           Test.setContinuationResponse(ClaimImage_LC_HUM.continuationLabel, oResponse);
           List<String> labels = new List<String>{ClaimImage_LC_HUM.continuationLabel};
           ClaimImage_LC_HUM.processResponse(labels, ClaimImage_LC_HUM.continuationState);
           Test.StopTest();
        }
    }
   
    /*
     * Method Name : claimSummaryException
     * Description : This method tests the Exception handling 
     * Return type : void
     * Parameter   : None
     */ 
     static testMethod void claimImageException() 
     {
         Test.setCurrentPage(Page.ClaimImage_VF_HUM);
         User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
         system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
         {
           Test.StartTest();
           ClaimImage_LC_HUM.sStartDate='12/23/2015';
           ClaimImage_LC_HUM.sPlatform ='MTV';
           ClaimImage_LC_HUM.sClient=ClaimImage_LC_HUM.setclientValue(ClaimImage_LC_HUM.sPlatform);
           Continuation conti = (Continuation)ClaimImage_LC_HUM.initiateRequest('820221461365410', 'EM', '2022-26-10T00:00:00','58');
           Continuation objCon = new Continuation(120);
           objCon.continuationMethod='processResponse';
           objCon.state='test';
           string labelValue= 'Continuation-1';
           HttpResponse oResponse = new HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           string sJson = '{ "ClaimURL":"Claim URL cannot be found" }';
           oResponse.setBody(sJson);
           oResponse.setStatusCode(2000);
           System.debug('oResponse'+oResponse.getStatusCode());
           Test.setContinuationResponse(ClaimImage_LC_HUM.continuationLabel, oResponse);
           List<String> labels = new List<String>{ClaimImage_LC_HUM.continuationLabel};
           ClaimImage_LC_HUM.processResponse(labels, ClaimImage_LC_HUM.continuationState);
           Test.StopTest();

       }
   }
    static testMethod void claimImageException_1() 
     {
         Test.setCurrentPage(Page.ClaimImage_VF_HUM);
         User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
         system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
         {
           Test.StartTest();
           ClaimImage_LC_HUM.sStartDate='12/23/2015';
           ClaimImage_LC_HUM.sPlatform ='MTV';
           ClaimImage_LC_HUM.sClient=ClaimImage_LC_HUM.setclientValue(ClaimImage_LC_HUM.sPlatform);
           Continuation conti = (Continuation)ClaimImage_LC_HUM.initiateRequest('820221461365410', 'EM', '2022-26-10T00:00:00','58');
           Continuation objCon = new Continuation(120);
           objCon.continuationMethod='processResponse';
           objCon.state='test';
           string labelValue= 'Continuation-1';
           HttpResponse oResponse = new HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           string sJson = '{"Fault": {"faultcode":"faultcode","message" : "error"}}';
           oResponse.setBody(sJson);
           oResponse.setStatusCode(401);
           System.debug('oResponse'+oResponse.getStatusCode());
           Test.setContinuationResponse(ClaimImage_LC_HUM.continuationLabel, oResponse);
           List<String> labels = new List<String>{ClaimImage_LC_HUM.continuationLabel};
           ClaimImage_LC_HUM.processResponse(labels, ClaimImage_LC_HUM.continuationState);
           Test.StopTest();

       }
   }
    static testMethod void claimImageException_2() 
     {
         Test.setCurrentPage(Page.ClaimImage_VF_HUM);
         User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
         system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
         {
           Test.StartTest();
           ClaimImage_LC_HUM.sStartDate='12/23/2015';
           ClaimImage_LC_HUM.sPlatform ='MTV';
           ClaimImage_LC_HUM.sClient=ClaimImage_LC_HUM.setclientValue(ClaimImage_LC_HUM.sPlatform);
           Continuation conti = (Continuation)ClaimImage_LC_HUM.initiateRequest('820221461365410', 'EM', '2022-26-10T00:00:00','58');
           Continuation objCon = new Continuation(120);
           objCon.continuationMethod='processResponse';
           objCon.state='test';
           string labelValue= 'Continuation-1';
           HttpResponse oResponse = new HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           string sJson = '{"message" :}';
           oResponse.setBody(sJson);
           oResponse.setStatusCode(200);
           System.debug('oResponse'+oResponse.getStatusCode());
           Test.setContinuationResponse(ClaimImage_LC_HUM.continuationLabel, oResponse);
           List<String> labels = new List<String>{ClaimImage_LC_HUM.continuationLabel};
           ClaimImage_LC_HUM.processResponse(labels, ClaimImage_LC_HUM.continuationState);
           Test.StopTest();

       }
   }
}