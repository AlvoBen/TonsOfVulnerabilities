/*******************************************************************************************************************************
Apex Class Name :  MobiusDocView_LT_HUM.cls
Version			: 1.0
Created On      : Sep 08, 2022
Function        : Class contains test methods for MobiusDocView_LC_HUM Class.

Modification Log :  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Anuradha Gajbhe                                     09/08/2022                   Original Version
********************************************************************************************************************************/
 
@isTest
private class MobiusDocView_LT_HUM{

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
            HUM_Webservice_Callout__c oServiceCalloutMobius = New HUM_Webservice_Callout__c(Name = 'MobiusService_GetMobiusDocument', Active__c = true, End_Point_URL__c ='https://dev-crmservicesvcs.humana.com:48022/MobiusGetDocumentService_V1/GetMobiusDocument');
            insert oServiceCalloutMobius;
            system.assert(HUM_Webservice_Callout__c.getValues('MobiusService_GetMobiusDocument').Name == 'MobiusService_GetMobiusDocument');
         }
    }
    
    /*
     * Method Name : testMobiusDocView_LC_HUM 
     * Description : Test method for MobiusDocView_LC_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMobiusDocView_LC_HUM()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            MobiusDocView_LC_HUM oMDV = new MobiusDocView_LC_HUM();
            object oMDVrun = MobiusDocView_LC_HUM.initiateViewRequest('fgfyf');
            system.assert(oMDVrun != null);
          Test.StopTest(); 
        }
    }  
}