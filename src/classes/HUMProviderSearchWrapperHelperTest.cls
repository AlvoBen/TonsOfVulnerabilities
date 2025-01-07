/****************************************************************************************************************************************************
Apex Class Name  : HUMProviderSearchWrapperHelperTest
Version          : 1.0
Created Date     : Aug 4 2015
Function         : Test class for HUMProviderSearchWrapperHelper
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani                                08/04/2015                   Original Version
*****************************************************************************************************************************************************/
@isTest
private class HUMProviderSearchWrapperHelperTest
{
    /*
    * Method Name : testMemberSearchWrapper
    * Description : Test method to instantiate member search wrapper
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testProviderSearchWrapper()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.id!=null,true);
        
        System.runAs(u)
        {
            Test.startTest();
            HUMProviderSearchWrapperHelper humProviderSearchWrapper = new HUMProviderSearchWrapperHelper();
            humProviderSearchWrapper.sMemberId = '';
            humProviderSearchWrapper.sSuffix = '';
            humProviderSearchWrapper.sFirstName = '';
            humProviderSearchWrapper.sLastName = '';
            humProviderSearchWrapper.sGroupNumber = '';
            humProviderSearchWrapper.sBirthdate = '';
            humProviderSearchWrapper.sPhone = '';
            humProviderSearchWrapper.sState = '';
            humProviderSearchWrapper.sPostalCode = '';
            humProviderSearchWrapper.sPersonEmail = '';
            humProviderSearchWrapper.sGroupName = '';
            humProviderSearchWrapper.sAgentId = '';
            humProviderSearchWrapper.sAgentType = '';
            humProviderSearchWrapper.sAgencyName = '';
            humProviderSearchWrapper.sTaxID = '';
            humProviderSearchWrapper.sNPI = '';
            humProviderSearchWrapper.sSpeciality = ''; 
            humProviderSearchWrapper.sProviderName = '';
            humProviderSearchWrapper.sFacilityName = '';
            humProviderSearchWrapper.sAddress = '';
            humProviderSearchWrapper.sOrgName = '';
            humProviderSearchWrapper.sExtID = '';
            humProviderSearchWrapper.sCity = '';
            humProviderSearchWrapper.sAddresstype = '';
            humProviderSearchWrapper.sGender = '';
            humProviderSearchWrapper.sClassification = '';
            humProviderSearchWrapper.sTaxmonycode = '';
            humProviderSearchWrapper.sDBA = '';
            humProviderSearchWrapper.bIsUserHaveReadAccess = true;
            humProviderSearchWrapper.sLocMemFound = '';
            humProviderSearchWrapper.sMemName = '';
            humProviderSearchWrapper.sPlatform = '';
            humProviderSearchWrapper.sGroupId = '';
            humProviderSearchWrapper.sProduct = '';
            humProviderSearchWrapper.sLOB = '';
            humProviderSearchWrapper.sEffectiveDate = '';
            humProviderSearchWrapper.sEndDate = '';
            humProviderSearchWrapper.sPend = '';
            humProviderSearchWrapper.sPendKey = '';
            humProviderSearchWrapper.sDateTimePend = '';
            humProviderSearchWrapper.sSSN = '';
            humProviderSearchWrapper.sStartDate = '';
            humProviderSearchWrapper.dtMinDate = Date.today();
            humProviderSearchWrapper.dtMaxDate = Date.today();
            humProviderSearchWrapper.sDegree = '';
            humProviderSearchWrapper.sEnterpriseID = '';
            humProviderSearchWrapper.sPhoneExtn = '';
            humProviderSearchWrapper.sServiceaddress = '';
            humProviderSearchWrapper.sServiceState = '';
            humProviderSearchWrapper.sServicezip = '';
            humProviderSearchWrapper.sServiceCity = '';
            Test.stopTest();
        }
    }
}