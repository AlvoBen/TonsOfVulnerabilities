/****************************************************************************
Apex Class Name  : PharmacyInvoiceRequest_LT_HUM
Version          : 1.0 
Created On       : 3/04/2022
Function         : Test class to PharmacyInvoiceRequest_LC_HUM Apex class

Modification Log :
* Developer Name                       Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Isaac Chung            			02/26/2024                 Original Version       
************************************************************************************************************************/
@isTest
private class PharmacyInactivateRx_LT_HUM {

    @isTest
    static void testGetInactivateRxTN_WithRecord() {
       Template__c testTemplate = new Template__c(Description__c='Inactivate Rx');
       insert testTemplate;

       Test.startTest();
       String result = PharmacyInactivateRx_LC_HUM.getInactivateRxTN();
       Test.stopTest();

       System.assertNotEquals(null, result, 'The result should not be null');
       System.assertEquals(testTemplate.Description__c, 'Inactivate Rx', 'The returned name should match');
    }

    @isTest
    static void testGetInactivateRxTN_WithoutRecord() {
       Test.startTest();
       String result = PharmacyInactivateRx_LC_HUM.getInactivateRxTN();
       System.debug(result);
       Test.stopTest();

       System.assertEquals(null, result, 'The result should be null');
    }
}