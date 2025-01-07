/**************************************************************************** 
Apex Class Name  : ClaimImage_T_HUM 
Version          : 1.0  
Created Date     : April 18, 2016
Function         : Test class for ClaimImage_C_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Harika Devi Kalidindi        25398            04/18/2016             Original Version REQ - 244383:Ability as a CRM Service end user to view the Claim image for a specific claim within CRM Service - Claim Detail Page
* Prasanthi Kandula            211792           03/30/2018             REQ - 301521 Updated logic to handle mutliple claims images recevied from service
* Suraj Patil								 	04/09/2021			   REQ - 1173638  - PR00094226-Decommissioned - SF - Humana Imaging Service
***************************************************************************************************/
 @isTest
 private Class ClaimImage_T_HUM
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
           Test.setCurrentPage(Page.ClaimImage_VF_HUM);
           ApexPages.currentPage().getParameters().put('ClaimNumber','620204893');
           ApexPages.currentPage().getParameters().put('Platform','LV');
           ApexPages.currentPage().getParameters().put('StartDate','12/23/2015');
           ApexPages.currentPage().getParameters().put('SrcLvCASPrefix','57');
           
          
           ClaimImage_C_HUM oClaimImage = new ClaimImage_C_HUM();
           oClaimImage.sStartDate='12/23/2015';
           oClaimImage.sPlatform ='LV';
           oClaimImage.sClient=oClaimImage.setclientValue(oClaimImage.sPlatform);
           System.assertEquals(oClaimImage.sClient, '57');
                       
           Continuation objCon =  (Continuation)oClaimImage.initiateRequest();
           Map<String, HttpRequest> requests = objCon.getRequests();
           system.assert(requests.size() == 1);
           
           HttpResponse oResponse = new HttpResponse();
           oResponse.setHeader('Content-Type', 'application/json');
           oResponse.setHeader('SOAPAction', '');    
           oResponse.setHeader('Connection','keep-alive');
           string sJson = '{"GetClaimURLsResponse": {"ClaimURLs": [{ "DCN": "0000028383175040010000100001", "ECN": "0000028383175040010000100001","ReceiptDate": "2016-02-17T20:52:13Z","Repriced": false,"TotalBilledCharges": 800, "BillingProviderLastFacilityName": "CONNER WELLNESS CENTER", "BillingProviderId": "1942499918","RenderingProviderLastFacilityName": "CONNER WELLNESS CENTER","RenderingProviderId": "1942499918", "ClaimFrequencyCode": "1", "URL": "http://dev-loufeo1.humana.com/edocs/?ecn=0000028383175040010000100001&docid=1234562"   }, {  "DCN": "0000028383175040010000100001", "ECN": "0000028383175040010000100001", "ReceiptDate": "2016-02-17T20:52:13Z", "Repriced": false,"TotalBilledCharges": 800, "BillingProviderLastFacilityName": "CONNER WELLNESS CENTER", "BillingProviderId": "1942499918", "RenderingProviderLastFacilityName": "CONNER WELLNESS CENTER", "RenderingProviderId": "1942499918", "ClaimFrequencyCode": "1", "URL": "http://dev-loufeo1.humana.com/edocs/?ecn=0000028383175040010000100001&docid=1234562" },{ "DCN": "0000028383175040010000100001","ECN": "0000028383175040010000100001", "ReceiptDate": "2016-02-17T20:52:13Z","Repriced": false, "TotalBilledCharges": 800, "BillingProviderLastFacilityName": "CONNER WELLNESS CENTER", "BillingProviderId": "1942499918","RenderingProviderLastFacilityName": "CONNER WELLNESS CENTER", "RenderingProviderId": "1942499918", "ClaimFrequencyCode": "1", "URL": "http://dev-loufeo1.humana.com/edocs/?ecn=0000028383175040010000100001&docid=1234562" }]}}';
           oResponse.setBody(sJson);
           oResponse.setStatusCode(200); 
           List<ImagingService_DTO_HUM> lstImagingDTO = new List<ImagingService_DTO_HUM>();
           lstImagingDTO = oClaimImage.parseResponse(oResponse);
           Object result = Test.invokeContinuationMethod(oClaimImage, objCon);
           system.assert(lstImagingDTO.size()>0);
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
            Test.setCurrentPage(Page.ClaimImage_VF_HUM);
            ApexPages.currentPage().getParameters().put('ClaimNumber','201512231384992'); 
            ApexPages.currentPage().getParameters().put('Platform','MTV');
            ApexPages.currentPage().getParameters().put('StartDate','12/23/2015');
            ApexPages.currentPage().getParameters().put('SrcLvCASPrefix','42');
  
            ClaimImage_C_HUM oClaimImage = new ClaimImage_C_HUM();   
            oClaimImage.sStartDate='12/23/2015';
            oClaimImage.sPlatform ='MTV';
            oClaimImage.sClient=oClaimImage.setclientValue(oClaimImage.sPlatform);
            System.assertEquals(oClaimImage.sClient, '42');
            oClaimImage .initiateRequest();
            Continuation objCon = new Continuation(120);
            objCon.continuationMethod='processResponse';
            oClaimImage.processResponse();
            HttpResponse oResponse = new HttpResponse();
            oResponse.setHeader('Content-Type', 'application/json');
            oResponse.setHeader('SOAPAction', '');    
            oResponse.setHeader('Connection','keep-alive');
            string sJson = '{"GetClaimURLsResponse": {"ClaimURLs": [{ "DCN": "0000028383175040010000100001", "ECN": "0000028383175040010000100001","ReceiptDate": "2016-02-17T20:52:13Z","Repriced": false,"TotalBilledCharges": 800, "BillingProviderLastFacilityName": "CONNER WELLNESS CENTER", "BillingProviderId": "1942499918","RenderingProviderLastFacilityName": "CONNER WELLNESS CENTER","RenderingProviderId": "1942499918", "ClaimFrequencyCode": "1", "URL": "http://dev-loufeo1.humana.com/edocs/?ecn=0000028383175040010000100001&docid=1234562" }]}}';
            oResponse.setBody(sJson);
            oResponse.setStatusCode(200);
            oClaimImage.parseResponse(oResponse);
            system.assertEquals(oClaimImage.stargetURL,'http://dev-loufeo1.humana.com/edocs/?ecn=0000028383175040010000100001&docid=1234562');
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
              Test.startTest();            
              Test.setCurrentPage(Page.ClaimImage_VF_HUM);
              ApexPages.currentPage().getParameters().put('ClaimNumber','201512231384992');
              ApexPages.currentPage().getParameters().put('Platform','LV');
              ApexPages.currentPage().getParameters().put('StartDate','12/23/2015');
              ApexPages.currentPage().getParameters().put('SrcLvCASPrefix','57');
             
              ClaimImage_C_HUM oClaimImage = new ClaimImage_C_HUM();
              oClaimImage.sStartDate='12/23/2015';
              oClaimImage.sPlatform ='LV';
              oClaimImage.sClient=oClaimImage.setclientValue(oClaimImage.sPlatform);
              System.assertEquals(oClaimImage.sClient, '57');
              oClaimImage.initiateRequest();
              Continuation objCon = new Continuation(120);
              objCon.continuationMethod='processResponse';
              oClaimImage.processResponse();
              HttpResponse oResponse = new HttpResponse();
              oResponse.setHeader('Content-Type', 'application/json');
              string sjson  = '{ "ClaimURL":"Claim URL cannot be found" }';
              oResponse.setBody(sjson);
              oResponse.setStatusCode(2000);
              Test.stopTest();    
        }
    }
 }