/****************************************************************************************************************************************************
Apex Class Name  : HUMMemberSearchWrapperHelperTest
Version          : 1.0
Created Date     : Aug 4 2015
Function         : Test class for HUMMemberSearchWrapperHelper
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani                                08/04/2015                   Original Version
*****************************************************************************************************************************************************/
@isTest
private class HUMMemberSearchWrapperHelperTest
{
    /*
    * Method Name : testMemberSearchWrapper
    * Description : Test method to instantiate member search wrapper
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testMemberSearchWrapper()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.id!=null,true);
        
        System.runAs(u)
        {
            Test.startTest();
            HUMMemberSearchWrapperHelper humMemberSearchWrapper = new HUMMemberSearchWrapperHelper();
            humMemberSearchWrapper.sMemberId = '';
            humMemberSearchWrapper.sSuffix = '';
            humMemberSearchWrapper.sFirstName = '';
            humMemberSearchWrapper.sLastName = '';
            humMemberSearchWrapper.sGroupNumber = '';
            humMemberSearchWrapper.sBirthdate = '';
            humMemberSearchWrapper.sPhone = '';
            humMemberSearchWrapper.sState = '';
            humMemberSearchWrapper.sPostalCode = '';
            humMemberSearchWrapper.sPersonEmail = '';
            humMemberSearchWrapper.sGroupName = '';
            humMemberSearchWrapper.sAgentId = '';
            humMemberSearchWrapper.sAgentType = '';
            humMemberSearchWrapper.sAgencyName = '';
            humMemberSearchWrapper.sTaxID = '';
            humMemberSearchWrapper.sNPI = '';
            humMemberSearchWrapper.sSpeciality = ''; 
            humMemberSearchWrapper.sProviderName = '';
            humMemberSearchWrapper.sFacilityName = '';
            humMemberSearchWrapper.sAddress = '';
            humMemberSearchWrapper.sOrgName = '';
            humMemberSearchWrapper.sExtID = '';
            humMemberSearchWrapper.sCity = '';
            humMemberSearchWrapper.sAddresstype = '';
            humMemberSearchWrapper.sGender = '';
            humMemberSearchWrapper.sClassification = '';
            humMemberSearchWrapper.sTaxmonycode = '';
            humMemberSearchWrapper.sDBA = '';
            humMemberSearchWrapper.bIsUserHaveReadAccess = true;
            humMemberSearchWrapper.sLocMemFound = '';
            humMemberSearchWrapper.sMemName = '';
            humMemberSearchWrapper.sPlatform = '';
            humMemberSearchWrapper.sGroupId = '';
            humMemberSearchWrapper.sProduct = '';
            humMemberSearchWrapper.sLOB = '';
            humMemberSearchWrapper.sEffectiveDate = '';
            humMemberSearchWrapper.sEndDate = '';
            humMemberSearchWrapper.sPend = '';
            humMemberSearchWrapper.sPendKey = '';
            humMemberSearchWrapper.sDateTimePend = '';
            humMemberSearchWrapper.sSSN = '';
            humMemberSearchWrapper.sStartDate = '';
            humMemberSearchWrapper.dtMinDate = Date.today();
            humMemberSearchWrapper.dtMaxDate = Date.today();
            humMemberSearchWrapper.sDegree = '';
            humMemberSearchWrapper.sEnterpriseID = '';
            humMemberSearchWrapper.sPhoneExtn = '';
            humMemberSearchWrapper.sServiceaddress = '';
            humMemberSearchWrapper.sServiceState = '';
            humMemberSearchWrapper.sServicezip = '';
            humMemberSearchWrapper.sServiceCity = '';
            Test.stopTest();
        }
    }
}