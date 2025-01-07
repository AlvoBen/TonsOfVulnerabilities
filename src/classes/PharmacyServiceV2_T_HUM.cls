/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2_T_HUM
Version          : 1.0  
Created Date     : October 29 2015  
Description      : This class is used as test class for PharmacyServiceV2_S_HUM
****************************************************************************
Modification Log:

* Developer Name          Code Review     Date            Description
*------------------------------------------------------------------------------------------------------------
* Gargey Sharma             27449       10/29/2015      Original Version 
* Ravikanth                             07/28/2022                 User Story 3570705: T1PRJ0315551- SF - FR# 28  : WDI Consumer Migration - APIC and URL (From WDI 2.0 to WDI 3.0)  
***************************************************************************************************/
@isTest
private Class PharmacyServiceV2_T_HUM
{
    
    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {
        CRMFunctionality_ONOFF_Switch__c wdiV3Switch = new CRMFunctionality_ONOFF_Switch__c( name = '3570705', IsON__C = true); 
        insert wdiV3Switch;

        List<HUM_Webservice_Callout__c> lstEndpoints = new List<HUM_Webservice_Callout__c>();
        lstEndpoints.add(new HUM_Webservice_Callout__c(Name='PharmacyService_V20_GetDrugDosage', Certificate_Name__c='Humana_GuidanceCare_Mars', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/PharmacyService_V20/GetDrugDosage', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json',Methods_Available__c='A;B;C',Enable_Service_Tester__c=true));                    
        lstEndpoints.add(new HUM_Webservice_Callout__c(Name='PharmacyService_V20_SearchDrug', Certificate_Name__c='Humana_GuidanceCare_Mars', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/PharmacyService_V20/SearchDrug', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json',Methods_Available__c='A;B;C',Enable_Service_Tester__c=true));
        lstEndpoints.add(new HUM_Webservice_Callout__c(Name='RXDrugInfoServiceV3.0_GetDrugDosage', Certificate_Name__c='Humana_GuidanceCare_Mars', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/RXDrugInfoService_V3.0/GetDrugDosage', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json',Methods_Available__c='A;B;C',Enable_Service_Tester__c=true));                    
        lstEndpoints.add(new HUM_Webservice_Callout__c(Name='RXDrugInfoServiceV3.0_SearchDrug', Certificate_Name__c='Humana_GuidanceCare_Mars', SampleParameter__c='sample',  Active__c=true, End_Point_URL__c='https://int-crmservicews.humana.com/RXDrugInfoService_V3.0/SearchDrug', Timeout__c='12000', Request_Type__c='POST', Content_Type__c='application/json',Methods_Available__c='A;B;C',Enable_Service_Tester__c=true));
        insert lstEndpoints;
        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
        HUMTestDataHelperTest.getHUMConstantsData();
    }
    
    /*
    * Method Name : testPharmacyServiceSearchDrug
    * Description : Test method for Search Drug method in Pharmacy Service
    * Return type : void     
    * Paramater   : NA  
    */       
    static testMethod void testPharmacyServiceSearchDrug()
    {                 
        Test.setMock(HttpCalloutMock.class, new PharmacyServiceV2MockResponse_T_HUM());       
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser) 
        {
            test.startTest();
                PharmacyServiceV2SearchRequest_H_HUM oRequest = new PharmacyServiceV2SearchRequest_H_HUM();
                PharmacyServiceV2SearchRequest_H_HUM.PharmacyV2DrugSearchRequest_H_HUM oSearchRequest = new PharmacyServiceV2SearchRequest_H_HUM.PharmacyV2DrugSearchRequest_H_HUM ();
                oSearchRequest.Requestor = 'CRM';  
                oSearchRequest.SearchKey = 'Name';  
                oSearchRequest.SearchValue = 'zoc';
                oRequest.DrugSearchRequest = oSearchRequest;
                PharmacyServiceV2SearchResponse_H_HUM oResponse = new PharmacyServiceV2SearchResponse_H_HUM();
                oResponse = PharmacyServiceV2_s_HUM.SearchDrug(oRequest);              
                system.assert(oResponse.SearchDrugResponse != null);
            test.stopTest();
        }
    }
    
    /*
    * Method Name : testPharmacyServiceGetDrugDosage
    * Description : Test method for Get Drug Dosage method in Pharmacy Service
    * Return type : void     
    * Paramater   : NA  
    */       
    static testMethod void testPharmacyServiceGetDrugDosage()
    {                 
        Test.setMock(HttpCalloutMock.class, new PharmacyServiceV2MockResponse_T_HUM());       
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser) 
        {
            test.startTest();
                PharmacyServiceV2DosageRequest_H_HUM oRequest = new PharmacyServiceV2DosageRequest_H_HUM();
                PharmacyServiceV2DosageRequest_H_HUM.PharmacyV2DrugDosageRequest_H_HUM oDrugDos = new PharmacyServiceV2DosageRequest_H_HUM.PharmacyV2DrugDosageRequest_H_HUM(); 
                oDrugDos.Requestor = 'CDT';  
                PharmacyServiceV2DosageRequest_H_HUM.PharmacyServiceV2DrugId_H_HUM oDrugReq = new PharmacyServiceV2DosageRequest_H_HUM.PharmacyServiceV2DrugId_H_HUM();
                oDrugReq.Key = 'MedId';  
                oDrugReq.Value = '286606';
                oDrugDos.DrugId = oDrugReq;
                oRequest.DrugDosageRequest = oDrugDos;
                PharmacyServiceV2DosageResponse_H_HUM oDrugDosageResponse = new PharmacyServiceV2DosageResponse_H_HUM();
                oDrugDosageResponse = PharmacyServiceV2_S_HUM.GetDrugDosage(oRequest);
                system.assert(oDrugDosageResponse.DrugDosageResponse != null);
            test.stopTest();
        }
    }
    
     /*
    * Method Name : testNegative
    * Description : negative test method
    * Return type : void     
    * Paramater   : NA  
    */       
    static testMethod void testNegative()
    {                 
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser) 
        {
            test.startTest();
                PharmacyServiceV2DosageResponse_H_HUM oDrugDosageResponse = new PharmacyServiceV2DosageResponse_H_HUM();
                oDrugDosageResponse = PharmacyServiceV2_S_HUM.GetDrugDosage(null);
                system.assert(oDrugDosageResponse.DrugDosageResponse == null);

                PharmacyServiceV2SearchResponse_H_HUM oResponse = new PharmacyServiceV2SearchResponse_H_HUM();
                oResponse = PharmacyServiceV2_s_HUM.SearchDrug(null);              
                system.assert(oResponse.SearchDrugResponse == null);
           test.stopTest();
        }
    }
}