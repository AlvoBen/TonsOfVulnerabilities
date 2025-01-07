/*******************************************************************************************************************************
Apex Class Name :  EHubDocumentViewer_T_HUM.cls
Version: 1.0
Created On      : Sep 30 2018
Function        : Class contains test methods EHubDocumentViewer_C_HUM Class.

Modification Log :  
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Srivinas Damera             293796                    9/30/2018                   Original Version
 ********************************************************************************************************************************/
 
@isTest
private class EHubDocumentViewer_T_HUM{

    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
         system.runAs(oUser)
        {
            HUM_Webservice_Callout__c oServiceCalloutEhub = New HUM_Webservice_Callout__c(Name = 'EhubService_GetDocument', Active__c = true);
            insert oServiceCalloutEhub ;
            system.assert(HUM_Webservice_Callout__c.getValues('EhubService_GetDocument').Name == 'EhubService_GetDocument');
         }
    }
    
    /*
     * Method Name : testEhubDocView_C_HUM
     * Description : Test method for EHubDocumentViewer_C_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testEhubDocView_C_HUM()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            EHubDocumentViewer_C_HUM oEDV = new EHubDocumentViewer_C_HUM();
            system.assert(oEDV != null);
          Test.StopTest(); 
        }
    }  
}