/*********************************************************************************************************************************************     
Apex Class Name  : PhysicianFinder_T_HUM 
Created Date     : 17 Nov 2021
Function         : 
**Modification Log
* Developer Name          Code Review          Date                       Description
******************************************************************************************************************************************************
* Suraj Patil								   11/10/2021				  REQ - 2512130 T1PRJ0193862 - MF 01 - Ability to attach PFP email automatically to CRM case (Mbr Policy Page)
*****************************************************************************************************************************************************/
@isTest
public with sharing class PhysicianFinder_T_HUM
{
    /*
    * Method Name : prepareTestData
    * Description : Used to create wrapper class data
    * Return type : void
    * Paramater   : NA
    */
    @testSetup static void prepareTestData() 
    {
        User oPSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oPSUser;
        system.assertEquals(oPSUser.LastName,'test User1');
        System.runAs(oPSUser) 
        {
            Test.StartTest();
            HUM_Webservice_Callout__c oSvcEncrypt = new HUM_Webservice_Callout__c();
            oSvcEncrypt.name='EncryptArrayService';
            oSvcEncrypt.Active__c=true;
            oSvcEncrypt.Certificate_Name__c='Humana_GuidanceCare_Pharmacy';
            oSvcEncrypt.End_Point_URL__c='https://int-crmservicews.humana.com/EncryptRestService_V2.0/EncryptStringArray';
            oSvcEncrypt.Timeout__c='12000';
            oSvcEncrypt.Content_Type__c='application/json';
            oSvcEncrypt.Request_Type__c='POST';
            insert oSvcEncrypt;
            system.assert(oSvcEncrypt.id!=null);
            system.assert(oSvcEncrypt.name == 'EncryptArrayService');
            
            Endpoint_Url_HUM__c pfURL = new Endpoint_Url_HUM__c ();
            pfURL.Name = 'PhysicianFinderURL';
            pfURL.Remote_Url__c = 'https://int-finder.humana.com/finder/search-lookup';
            insert pfURL;
            Test.stopTest(); 
        }
    }

    /*  
     * Method name : testInitateRequest
     * Description : This Method is used to test the initiate method request to Service call.
     * Return Type : Void 
     * Parameter   : NA
     */

    static testMethod void testInitateRequest()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PhysicianFinder_VF_HUM);
            ApexPages.currentPage().getParameters().put('PolicyMemberID','H7451048000'); 
            ApexPages.currentPage().getParameters().put('EffectiveDate','1/1/2020'); 
            ApexPages.currentPage().getParameters().put('EndDate','12/31/9999'); 
            ApexPages.currentPage().getParameters().put('PolicyGroupID','309842'); 
            ApexPages.currentPage().getParameters().put('HasCase','true'); 
            ApexPages.currentPage().getParameters().put('CustomerId','106'); 
            ApexPages.currentPage().getParameters().put('QueryMemgenKey','1964004638922'); 
            PhysicianFinder_C_HUM pf =new PhysicianFinder_C_HUM();
            pf.sRequest = '{"EncryptStringArray":{"Value":["H7451048000","1/1/2020","12/31/9999","309842","true","106","1964004638922","Louisville RSO Calls Support","SXP2884"],"Application":"CRM"}}';
            pf.initiateRequest(); 
            HttpResponse httpresponse = new HttpResponse();
            String res = '{"EncryptStringArrayResponse": {"EncryptStringArrayResult": {"EncryptStringArrayValue": ["MnwZJhBaUKkdXpLLLIJ8dA==","bjbfB1iIFoeEVpeoE8F/lA==","wc0RL9WCOY15IJ5GGAwM9Q==","twXxRs2itmDgQRgJGKmCNA==","f22ymozWPu0+XgZ78pMDVQ==","kG0DcrA/Ac2NPTAOefQDFg==","uQHyX5dzJ5tU7fJVTSL7+w==","Vat+Uj19yArk3tbcufASMtKGdxTPXBMzKErH86SkglY=","Ct/K8gaNuJSvDqNZi7vDVA=="]}}}';
            httpresponse.setBody(res);
            httpresponse.setStatusCode(200);
            pf.responseParse(httpresponse);
            /*Continuation objCon = New Continuation(120);
            objCon.continuationMethod='processResponse'; 
            pf.processResponse();    */       
            Test.stopTest();
        }
    }

    /*  
     * Method name : testInitateRequestException
     * Description : This Method is used to test the initiate method request to Service call for exception.
     * Return Type : Void 
     * Parameter   : NA
     */

    static testMethod void testInitateRequestException()
    {
        User testUser = [Select Id,Name from User where LastName='test User1'];
        HUM_Webservice_Callout__c wsc=[select id, name,Timeout__c from HUM_Webservice_Callout__c];
        system.assertEquals(wsc.name,'EncryptArrayService');
        wsc.name='EncryptArrayService1';
        wsc.Timeout__c='';
        update wsc;
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setCurrentPage(page.PhysicianFinder_VF_HUM);
            ApexPages.currentPage().getParameters().put('PolicyMemberID','H7451048000'); 
            ApexPages.currentPage().getParameters().put('EffectiveDate','1/1/2020'); 
            ApexPages.currentPage().getParameters().put('EndDate','12/31/9999'); 
            ApexPages.currentPage().getParameters().put('PolicyGroupID','309842'); 
            ApexPages.currentPage().getParameters().put('HasCase','true'); 
            ApexPages.currentPage().getParameters().put('CustomerId','106'); 
            ApexPages.currentPage().getParameters().put('QueryMemgenKey','1964004638922'); 
            PhysicianFinder_C_HUM pf =new PhysicianFinder_C_HUM();
            pf.sRequest = '{"EncryptStringArray":{"Value":["H7451048000","1/1/2020","12/31/9999","309842","true","106","1964004638922","Louisville RSO Calls Support","SXP2884"],"Application":"CRM"}}';
            pf.initiateRequest();
            pf.oServiceEncrypt.setupRequest(null, null, null);
            pf.oServiceEncrypt.buildRequest(null);
            Test.stopTest();
        }
    }

 }