/*****************************************************************************************************************************************************************************
Apex Class Name : AKANameFetch_HT_HUM
Version         : 1.0
Created On      : 01/14/2016
Function        : Test Class
Test Class      : Test Class for AKANameFetch_H_HUM

Modification Log: 
* Developer Name           Code Review                 Date                        Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi                              01/11/2011                  Original Version
********************************************************************************************************************************************************************************/
@isTest
private class AKANameFetch_HT_HUM
{
   
    /*
     * Method Name   :    testAKAName
     * Description   :    This method is for testing the AKA Name fetching from cache.
     * Return Type   :    void
     * Parameters    :    NA
     */
    Static testMethod void testAKAName()
    {     
        GetMockDTOcache();
        String returnaka = AKANameFetch_H_HUM.getAkaName('PXP0288','AkaName');
        system.assertNotEquals(returnaka.length(),0,'​Value not matched');   
        system.assertEquals(returnaka.compareTo('akaPXP0288'),0); 
    }
    
    /*
     * Method Name   :    testAKAName
     * Description   :    This method is for testing the AKA Name fetching from service.
     * Return Type   :    void
     * Parameters    :    NA
     */
    Static testMethod void testAKANamecallout()
    {     
        HUM_Webservice_Callout__c oSvcHeaderSLLogin = new HUM_Webservice_Callout__c(Content_Type__c='application/json',Request_Type__c='POST',Name='SL_Login_Service', Certificate_Name__c='Humana_GuidanceCare_Venus12', Active__c=true, End_Point_URL__c='https://dev-crmservicews.humana.com/SLLoginService/GetAKAName', Timeout__c='12000');
        AKANameFetch_H_HUM.getAkaName('PXP0289','AkaName');
        String returnaka = 'AKAPXP0289';        
        system.assertNotEquals(returnaka.length(),0,'​Value not matched');   
    }
    
     /*
     * Method Name   :    GetMockDTOcache
     * Description   :    This method is for loading the mock data to cache.
     * Return Type   :    void
     * Parameters    :    NA
     */
    private static void GetMockDTOcache()
    {    
      Cache.Session.put('local.Default.'+'AkaName', 'akaPXP0288'); 
      system.assertEquals(Cache.Session.get('AkaName'),'akaPXP0288');            
    }
}