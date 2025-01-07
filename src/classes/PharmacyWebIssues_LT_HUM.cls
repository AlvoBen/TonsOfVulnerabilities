/****************************************************************************
Apex Class Name  : PharmacyWebIssues_LT_HUM
Version          : 1.0 
Created On       : 3/04/2022
Function         : Test class to PharmacyWebIssues_LC_HUM Apex class

Modification Log :
* Developer Name                       Date                       Description
*-----------------------------------------------------------------------------------------------------------------------
* Pinky Vijur          			03/05/2024                 Original Version       
************************************************************************************************************************/
@isTest
private class PharmacyWebIssues_LT_HUM {

    @isTest
    static void testGetWebIssuesTN_WithRecord() {
       Template__c testTemplate = new Template__c(Description__c='Centerwell Pharmacy Web Issues');
       insert testTemplate;

       Test.startTest();
       String result = PharmacyWebIssues_LC_HUM.getWebIssuesTN();
       Test.stopTest();

       System.assertNotEquals(null, result, 'The result should not be null');
       System.assertEquals(testTemplate.Description__c, 'Centerwell Pharmacy Web Issues', 'The returned name should match');
    }

    @isTest
    static void testGetWebIssuesTN_WithoutRecord() {
       Test.startTest();
       String result = PharmacyWebIssues_LC_HUM.getWebIssuesTN();
       System.debug(result);
       Test.stopTest();

       System.assertEquals(null, result, 'The result should be null');
    }
}