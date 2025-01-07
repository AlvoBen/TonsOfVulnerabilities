/****************************************************************************************************************************************************
Apex Class Name  : HUMGroupSearchWrapperHelperTest
Version          : 1.0
Created Date     : Aug 4 2015
Function         : Test class for HUMGroupSearchWrapperHelper
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani                                08/04/2015                   Original Version
*****************************************************************************************************************************************************/
@isTest
private class HUMGroupSearchWrapperHelperTest
{
    /*
    * Method Name : testGroupSearchWrapper
    * Description : Test method to instantiate member search wrapper
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testGroupSearchWrapper()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.id!=null,true);
        
        System.runAs(u)
        {
            Test.startTest();
            HUMGroupSearchWrapperHelper humGroupSearchWrapper = new HUMGroupSearchWrapperHelper();
            humGroupSearchWrapper.sMemberId = '';
            humGroupSearchWrapper.sSuffix = '';
            humGroupSearchWrapper.sFirstName = '';
            humGroupSearchWrapper.sLastName = '';
            humGroupSearchWrapper.sGroupNumber = '';
            humGroupSearchWrapper.sBirthdate = '';
            humGroupSearchWrapper.sPhone = '';
            humGroupSearchWrapper.sState = '';
            humGroupSearchWrapper.sPostalCode = '';
            humGroupSearchWrapper.sPersonEmail = '';
            humGroupSearchWrapper.sGroupName = '';
            humGroupSearchWrapper.sAgentId = '';
            humGroupSearchWrapper.sAgentType = '';
            humGroupSearchWrapper.sAgencyName = '';
            humGroupSearchWrapper.sTaxID = '';
            humGroupSearchWrapper.sNPI = '';
            humGroupSearchWrapper.sSpeciality = ''; 
            humGroupSearchWrapper.sProviderName = '';
            humGroupSearchWrapper.sFacilityName = '';
            humGroupSearchWrapper.sAddress = '';
            humGroupSearchWrapper.sOrgName = '';
            humGroupSearchWrapper.sExtID = '';
            humGroupSearchWrapper.sCity = '';
            humGroupSearchWrapper.sAddresstype = '';
            humGroupSearchWrapper.sGender = '';
            humGroupSearchWrapper.sClassification = '';
            humGroupSearchWrapper.sTaxmonycode = '';
            humGroupSearchWrapper.sDBA = '';
            humGroupSearchWrapper.bIsUserHaveReadAccess = true;
            humGroupSearchWrapper.sLocMemFound = '';
            humGroupSearchWrapper.sMemName = '';
            humGroupSearchWrapper.sPlatform = '';
            humGroupSearchWrapper.sGroupId = '';
            humGroupSearchWrapper.sProduct = '';
            humGroupSearchWrapper.sLOB = '';
            humGroupSearchWrapper.sEffectiveDate = '';
            humGroupSearchWrapper.sEndDate = '';
            humGroupSearchWrapper.sPend = '';
            humGroupSearchWrapper.sPendKey = '';
            humGroupSearchWrapper.sDateTimePend = '';
            humGroupSearchWrapper.sSSN = '';
            humGroupSearchWrapper.sStartDate = '';
            humGroupSearchWrapper.dtMinDate = Date.today();
            humGroupSearchWrapper.dtMaxDate = Date.today();
            humGroupSearchWrapper.sDegree = '';
            humGroupSearchWrapper.sEnterpriseID = '';
            humGroupSearchWrapper.sPhoneExtn = '';
            humGroupSearchWrapper.sServiceaddress = '';
            humGroupSearchWrapper.sServiceState = '';
            humGroupSearchWrapper.sServicezip = '';
            humGroupSearchWrapper.sServiceCity = '';
            Test.stopTest();
        }
    }
}