/*******************************************************************************************************************************
Apex Class Name :  MobiusDocView_T_HUM.cls
Version: 1.0
Created On      : 04/20/2018
Function        : Class contains test methods MobiusDocView_C_HUM Class.

Modification Log :  
 * Developer Name            Code Review                Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
* Muralidhar Kollu                                      04/20/2018                   Original Version
 ********************************************************************************************************************************/
 
@isTest
private class MobiusDocView_T_HUM{

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
     * Method Name : testMobiusDocView_C_HUM
     * Description : Test method for MobiusDocView_C_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMobiusDocView_C_HUM()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            MobiusDocView_C_HUM oMDV = new MobiusDocView_C_HUM();
            system.assert(oMDV != null);
          Test.StopTest(); 
        }
    }  
}