/*******************************************************************************************************************************
Apex Class Name : HUMPharamacyDosageServiceTest
Version         : 1.0
Created On      : 05/18/2015 
Function        : Test Class for "HUMPharamacyDosageService" apex class.              
                  
Modification Log: 
* Developer Name           Code Review                 Date                        Description
Harshith Mandya                                      05/26/2015                   Original Version
Shreya Choodamani                                    06/19/2015                   Updated test class to handle the changes to code
Harshith Mandya                                      11/16/2015                   Update test class to handle the changes for REQ - 236993
Harshith Mandya              27805                   11/19/2015                   Implemented Review Comments
Ravikanth                                            07/28/2022                   User Story 3570705: T1PRJ0315551- SF - FR# 28  : WDI Consumer Migration - APIC and URL (From WDI 2.0 to WDI 3.0)  
*******************************************************************************************************************************/
@isTest                      
private class HUMPharamacyDosageServiceTest 
{
     /*    
    * Method Name : testSetup
    * Description : Test method to setup common test data for all test methods    
    * Return type : void    
    * Paramater   : NA    
    */
    @testSetup static void setup() {
         User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
         insert usr;
         System.assertEquals(usr.Id!=NULL,true);
         HUM_Webservice_Callout__c dosageWebserviceCustomSetting = new HUM_Webservice_Callout__c(End_Point_URL__c='https://int-crmservicews.humana.com/PharmacyService_V20/GetDrugDosage',                                                                                                  
                                                                                                      Name='PharmacyService_V20_GetDrugDosage',                                                                                             
                                                                                                      Certificate_Name__c='Humana_GuidanceCare_Mercury',                                                                                             
                                                                                                      Active__c = true,                                                                                             
                                                                                                      Timeout__c = '12000',                                                                                             
                                                                                                      Request_Type__c = 'POST',                                                                                             
                                                                                                      Content_Type__c = 'application/json',
                                                                                                      SOAP_Action__c = 'GetDrugDosage'                                                                                             
                                                                                                      );     
         
         HUM_Webservice_Callout__c searchWebserviceCustomSetting = new HUM_Webservice_Callout__c(End_Point_URL__c='https://int-crmservicews.humana.com/PharmacyService_V20/SearchDrug',
                                                                                                 Name='PharmacyService_V20_SearchDrug',
                                                                                                 Certificate_Name__c='Humana_GuidanceCare_Mercury',
                                                                                                 Active__c = true,
                                                                                                 Timeout__c = '12000',
                                                                                                 Request_Type__c = 'POST',
                                                                                                 Content_Type__c = 'application/json'
                                                                                                 );
         HUM_Webservice_Callout__c dosageWebserviceCustomSettingV3 = new HUM_Webservice_Callout__c(End_Point_URL__c='https://int-crmservicews.humana.com/RXDrugInfoService_V3.0/GetDrugDosage',                                                                                                  
                                                                                                      Name='RXDrugInfoServiceV3.0_GetDrugDosage',                                                                                             
                                                                                                      Certificate_Name__c='Humana_GuidanceCare_Mercury',                                                                                             
                                                                                                      Active__c = true,                                                                                             
                                                                                                      Timeout__c = '12000',                                                                                             
                                                                                                      Request_Type__c = 'POST',                                                                                             
                                                                                                      Content_Type__c = 'application/json',
                                                                                                      SOAP_Action__c = 'GetDrugDosage'                                                                                             
                                                                                                      );     
         
         HUM_Webservice_Callout__c searchWebserviceCustomSettingV3 = new HUM_Webservice_Callout__c(End_Point_URL__c='https://int-crmservicews.humana.com/RXDrugInfoService_V3.0/SearchDrug',
                                                                                                 Name='RXDrugInfoServiceV3.0_SearchDrug',
                                                                                                 Certificate_Name__c='Humana_GuidanceCare_Mercury',
                                                                                                 Active__c = true,
                                                                                                 Timeout__c = '12000',
                                                                                                 Request_Type__c = 'POST',
                                                                                                 Content_Type__c = 'application/json'
                                                                                                 );
        
        
         List<HUM_Webservice_Callout__c> lstCallouts = new List<HUM_Webservice_Callout__c>();
         lstCallouts.add(dosageWebserviceCustomSetting);
         lstCallouts.add(searchWebserviceCustomSetting);
         lstCallouts.add(dosageWebserviceCustomSettingV3);
         lstCallouts.add(searchWebserviceCustomSettingV3);
         insert lstCallouts;

         System.assertEquals(lstCallouts.size(),4);
         System.assertEquals(lstCallouts.get(0).Id!=NULL,true);
    }
    
    /*
     * Method Name   :    HUMPharamacyDosageService_WebServiceTest
     * Description   :    Method for testing PharmacyService - Drug Usage
     * Return Type   :    None
     * Parameters    :    None
    */
    static testmethod void HUMPharamacyDosageService_WebServiceTest() 
    {
        
        // Setting mock class for the service to return response
        Test.setMock(HttpCalloutMock.class, new PharmacyServiceV2MockResponse_T_HUM());
        HUMPharamacyDosageService dosageSearchInstance = new HUMPharamacyDosageService();
        Map<String,Object> inputParams = new Map<String,Object>();
        InputParams.put('MedIdValue', '286606');
        Process.PluginRequest request = new Process.PluginRequest(inputParams);  
        User usr = [select Id,Name from User where name = 'test User1' limit 1];

        system.runAs(usr) 
        {         
            test.startTest();         
                dosageSearchInstance.invoke(request);
              Process.PluginDescribeResult  result = dosageSearchInstance.describe();
            test.stopTest();            
        }
        
        // Fetching current user's Id
        String currentUserId = String.valueOf(usr.Id);
        currentUserId = currentUserId.substring(0,15); 
        List<HUMDosageDetails__c> dosageLst = [SELECT ID FROM HUMDosageDetails__c Where User_Id__c = :currentUserId] ;
        System.assertEquals(dosageLst!=NULL,true);
        System.assertEquals(dosageLst.size(),5);
    } 

}