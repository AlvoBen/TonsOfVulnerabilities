/*******************************************************************************************************************************
Apex Class Name : HUMSoftPhoneRoutingHelperTest 
Version         : 1.0
Created On      : 09/11/2014
Function        : Test Class for HUMSoftPhoneRoutingHelper

Modification Log: 
 * Developer Name           Code Review              Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar			17824				    09/11/2014					Original version
 *********************************************************************************************************************************************************************************************/

@isTest
private class HUMSoftPhoneRoutingHelperTest {

    
    /*
     * Method Name : wrapperTest
     * Description : Instantiate the class
     * Return type : Void
     * Paramater   : NA
     */
    static testMethod void wrapperTest() {
        
        HUMSoftPhoneRoutingHelper softPhoneRoutingHelper ;
        Test.startTest();
        
        softPhoneRoutingHelper = new HUMSoftPhoneRoutingHelper();
        
        Test.stopTest();
        //Currently Class is just a place holder and doesnt hold any logic
        System.assert(softPhoneRoutingHelper!=null);
    }
}