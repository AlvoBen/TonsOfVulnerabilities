/*******************************************************************************************************************************
Apex Class Name : HUMStringHelperTest
Version         : 1.0
Created On      : 09/24/2014
Function        : Test class for HUMStringHelper

 Modification Log:
 * Developer             		Code Review                    Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Manish Kumar                 18081                          09/24/2014                  Original Version
 *******************************************************************************************************************************/
@isTest
private class HUMStringHelperTest
{
    /*
     * Method Name   :    overAllTestCoverage
     * Description   :    This method is for overall test coverage.
     * Return Type   :    void
     * Parameters    :    NA
     */
    static testmethod void overAllTestCoverage()
    {
        String sRetValue = '';
        
        Test.startTest();

        sRetValue  = HUMStringHelper.parseSearchStringWithORs('FirstName', 'LIKE', 'Clear Text', 'OR', 'Cipher Text', true);
        System.assertNotEquals(sRetValue, NULL);
        
        sRetValue  = HUMStringHelper.parseSearchStringWithORs('FirstName', 'LIKE', 'Clear Text', 'OR', 'Cipher Text', false);
        System.assertNotEquals(sRetValue, NULL);
        
        sRetValue  = HUMStringHelper.parseSearchStringWithORs('FirstName', NULL, 'Clear Text', NULL, 'Cipher Text', false);
        System.assertEquals(sRetValue, NULL);
        
        Test.stopTest();
    }
}