/****************************************************************************************
Apex Class Name :  AuraErrorHandler_T_HUM
Version: 1.0 
Created Date     : 07/01/2019
Function         : Test class to AuraErrorHandler_C_HUM Apex class
Modification Log : 
*      Developer               Code Review            Date                 Description
* ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Chetan Sindhe                                    07/01/2019          REQ-397261 Original Version
****************************************************************************************************************************************************************************/
@isTest
private class AuraErrorHandler_T_HUM {
    
    /*
     * Method Name   : testCreateAuraHandledExceptionn
     * Description   : Log the records into error log table
     * Return Type   : void
     * Parameters    : NA
     */
    static testMethod void testCreateAuraHandledException() 
    {     
        Test.startTest();       
        try 
        {
            AuraErrorHandler_C_HUM.createAuraHandledException('AuraErrorHandler_T_HUM','testCreateAuraHandledException','Show Test Error Message Invalid Location');
        }
        Catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'AuraErrorHandler_C_HUM', 'testCreateAuraHandledException');
        } 
        
        Test.stopTest();        
                
        //Assert no Error Log objects in database. This Is a valid Error Message
        System.assertNotEquals(0,[Select id from Error_log__c].size());        
    }
}