/*************************************************************************** 
Apex Class Name  : ClaimImageService_LT_HUM
Version          : 1.0  
Function         : Test class for ClaimImageService_LS_HUM 
Created Date     :  27 dec 2022
Test Class       : ClaimImageService_LT_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 * Sagar.G                                                                Original Version REQ - 3856190:Ability as a CRM Service end user to view the Claim image for a specific claim within CRM Service - Claim Detail Page
 *****************************************************************************************************************************/

 @isTest
 private Class ClaimImageService_LT_HUM
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
              system.assertEquals(oHUMClaimImageService.Name,'ImagingService_GetClaimURL_V2');
              system.assertEquals(oHUMClaimImageService.Certificate_Name__c,'Humana_GuidanceCare_Venus');
              system.assertEquals(oHUMClaimImageService.Timeout__c,'120000');

              ImagingServiceGetClaimURLRequest_H_HUM oClaimImageReq = new ImagingServiceGetClaimURLRequest_H_HUM();
              oClaimImageReq.ClaimNumber = '620204893';
              oClaimImageReq.Client = '57';
              oClaimImageReq.ServiceStartDate = '2015-12-23T00:00:00';
              oClaimImageReq.ImageLoadDaysBuffer = '10';    
          }
     }
   
     /*
      * Method Name : runData
      * Description : Used to run scenerios of test data
      * Return type : void
      * Paramater   : NA
      */ 
      static testMethod void runPositiveData()
      {    
          User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
          system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
          System.runAs (oCCSUser) 
          { 
              Test.StartTest();
              HUM_Webservice_Callout__c oHUMClaimImageService= HUM_Webservice_Callout__c.getAll().values();
              system.assertequals(oHUMClaimImageService.End_Point_URL__c,'https://int-crmservicews.humana.com/HumanaImagingService_V2/GetClaimURLs');
              system.assertequals(oHUMClaimImageService.Certificate_Name__c,'Humana_GuidanceCare_Venus');
              HttpRequest orequest = new HttpRequest();
              string sbody='{"ServiceStartDate":"2015-12-23T00:00:00","ImageLoadDaysBuffer":"10","Client":"57","ClaimNumber":"620204893"}';
              ClaimImageService_LS_HUM.assignValuesToHelper('620204893', '2015-12-23T00:00:00', '57' );
              HttpRequest vhttpRequest=ClaimImageService_LS_HUM.setupRequest(orequest,oHUMClaimImageService,sbody);
              system.assertNotequals(vhttpRequest,null);

              Test.stopTest();  
          }
      }

    /*
    * Method Name : runNoData
    * Description : Used to test no data scenerios.
    * Return type : void
    * Paramater   : NA
    */    
    static testMethod void runNoData()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            HUM_Webservice_Callout__c oHUMClaimImageService= HUM_Webservice_Callout__c.getAll().values();
            system.assertequals(oHUMClaimImageService.End_Point_URL__c,'https://int-crmservicews.humana.com/HumanaImagingService_V2/GetClaimURLs');
            system.assertequals(oHUMClaimImageService.Certificate_Name__c,'Humana_GuidanceCare_Venus');
            HttpRequest orequest = new HttpRequest();
            string sbody ='{"ServiceStartDate":"","ImageLoadDaysBuffer":"10","Client":"","ClaimNumber":""}';
            ClaimImageService_LS_HUM.assignValuesToHelper('', '', '' );
            HttpRequest vhttpRequest=ClaimImageService_LS_HUM.setupRequest(orequest,oHUMClaimImageService,sbody);
            system.assertNotequals(vhttpRequest,null);
            Test.stopTest();  
        }
    }
	static testMethod void runException()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            HUM_Webservice_Callout__c oHUMClaimImageService= HUM_Webservice_Callout__c.getAll().values();
            system.assertequals(oHUMClaimImageService.End_Point_URL__c,'https://int-crmservicews.humana.com/HumanaImagingService_V2/GetClaimURLs');
            system.assertequals(oHUMClaimImageService.Certificate_Name__c,'Humana_GuidanceCare_Venus');
            HttpRequest orequest = new HttpRequest();
            string sbody ='{"ServiceStartDate":"","ImageLoadDaysBuffer":"10","Client":"","ClaimNumber"}';
            ClaimImageService_LS_HUM.assignValuesToHelper(Null, Null, Null );
            HttpRequest vhttpRequest=ClaimImageService_LS_HUM.setupRequest(orequest,oHUMClaimImageService,sbody);
            system.assertNotequals(vhttpRequest,null);
            Test.stopTest();  
        }
    }
}