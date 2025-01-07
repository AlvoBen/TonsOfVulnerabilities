/*******************************************************************************************************************************
Apex Class Name : HUMPharamacyServiceSearchTest
Version         : 1.0
Created On      : 05/18/2015 
Function        : Test Class for "HUMPharamacyServiceSearch" apex class.              
                  
Modification Log: 
* Developer Name           Code Review                 Date                           Description
Harshith Mandya                                      05/26/2015                   Original Version
Harshith Mandya              23393                   06/02/2015                   Implementing Review Comments
Harshith Mandya                                      11/16/2015                   Implementing Search Callout coverage
Harshith Mandya                                      11/16/2015                   Update test class to handle the changes for REQ - 236993
Harshith Mandya              27805                   11/19/2015                   Implemented Review Comments
Ravikanth                                            07/28/2022                   User Story 3570705: T1PRJ0315551- SF - FR# 28  : WDI Consumer Migration - APIC and URL (From WDI 2.0 to WDI 3.0)  
*******************************************************************************************************************************/
@isTest                      
private class HUMPharamacyServiceSearchTest 
{
     /*    
    * Method Name : testSetup
    * Description : Test method to setup common test data for all test methods    
    * Return type : void    
    * Paramater   : NA    
    */
    @testSetup static void setup() 
    {
     User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
     insert usr;
     System.assertEquals(usr.Id!=NULL,true);
     HUM_Webservice_Callout__c searchWebserviceCustomSetting = new HUM_Webservice_Callout__c(End_Point_URL__c='https://int-crmservicews.humana.com/PharmacyService_V20/SearchDrug',
                                                                                             Name='PharmacyService_V20_SearchDrug',Certificate_Name__c='Humana_GuidanceCare_Mercury',
                                                                                             Active__c = true,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c = 'application/json');
     insert searchWebserviceCustomSetting ;

     
     HUM_Webservice_Callout__c searchWebserviceCustomSettingV3 = new HUM_Webservice_Callout__c(End_Point_URL__c='https://int-crmservicews.humana.com/RXDrugInfoService_V3.0/SearchDrug',
                                                                                            Name='RXDrugInfoServiceV3.0_SearchDrug',Certificate_Name__c='Humana_GuidanceCare_Mercury',
                                                                                            Active__c = true,Timeout__c = '12000',Request_Type__c = 'POST',Content_Type__c = 'application/json');
     insert searchWebserviceCustomSettingV3 ;
     System.assertEquals(searchWebserviceCustomSetting.Id!=NULL,True);
    }
    
    /*
     * Method Name   :    PharamacyServiceSearch_WebServiceTest
     * Description   :    Method for testing Web Service
     * Return Type   :    None
     * Parameters    :    None
    */
    static testmethod void PharamacyServiceSearch_WebServiceTest() 
    {
       // Setting mock class for the service to return response
        Test.setMock(HttpCalloutMock.class, new PharmacyServiceV2MockResponse_T_HUM());
        HUMPharamacyServiceSearch pharamacySearchInstance = new HUMPharamacyServiceSearch();
        Map<String,Object> inputParams = new Map<String,Object>();
     InputParams.put('drugName', 'zoc');
        Process.PluginRequest request = new Process.PluginRequest(inputParams);  
        User usr = [select Id,Name from User where name = 'test User1' limit 1];

        system.runAs(usr) 
        {         
            test.startTest();         
              pharamacySearchInstance.invoke(request);
               // Fetching current user's Id
              String currentUserId = String.valueOf(usr.Id);
              currentUserId = currentUserId.substring(0,15); 
              List<HUMDrugDetails__c> drugLst = [SELECT ID FROM HUMDrugDetails__c Where User_Id__c = :currentUserId];
              System.assertEquals(drugLst!=NULL,True);
              Process.PluginDescribeResult  result = pharamacySearchInstance.describe();
            test.stopTest();  
        }
    }
}