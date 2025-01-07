/****************************************************************************************************************************************************
Apex Class Name  : HUMAgencySearchWrapperHelperTest
Version          : 1.0
Created Date     : Aug 4 2015
Function         : Test class for HUMAgencySearchWrapper
******************************************************************************************************************************************************

Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Shreya Choodamani                                08/04/2015                   Original Version
*****************************************************************************************************************************************************/
@isTest
private class HUMAgencySearchWrapperHelperTest
{
    /*
    * Method Name : testGroupSearchWrapper
    * Description : Test method to instantiate member search wrapper
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testAgencySearchWrapper()
    {
        User u = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert u;
        system.assertEquals(u.id!=null,true);
        
        System.runAs(u)
        {
            Test.startTest();
            HUMAgencySearchWrapperHelper humAgencySearchWrapper = new HUMAgencySearchWrapperHelper();
            humAgencySearchWrapper.sMemberId = '';
            humAgencySearchWrapper.sSuffix = '';
            humAgencySearchWrapper.sFirstName = '';
            humAgencySearchWrapper.sLastName = '';
            humAgencySearchWrapper.sGroupNumber = '';
            humAgencySearchWrapper.sBirthdate = '';
            humAgencySearchWrapper.sPhone = '';
            humAgencySearchWrapper.sState = '';
            humAgencySearchWrapper.sPostalCode = '';
            humAgencySearchWrapper.sPersonEmail = '';
            humAgencySearchWrapper.sGroupName = '';
            humAgencySearchWrapper.sAgentId = '';
            humAgencySearchWrapper.sAgentType = '';
            humAgencySearchWrapper.sAgencyName = '';
            humAgencySearchWrapper.sTaxID = '';
            humAgencySearchWrapper.sNPI = '';
            humAgencySearchWrapper.sSpeciality = ''; 
            humAgencySearchWrapper.sProviderName = '';
            humAgencySearchWrapper.sFacilityName = '';
            humAgencySearchWrapper.sAddress = '';
            humAgencySearchWrapper.sOrgName = '';
            humAgencySearchWrapper.sExtID = '';
            humAgencySearchWrapper.sCity = '';
            humAgencySearchWrapper.sAddresstype = '';
            humAgencySearchWrapper.sGender = '';
            humAgencySearchWrapper.sClassification = '';
            humAgencySearchWrapper.sTaxmonycode = '';
            humAgencySearchWrapper.sDBA = '';
            humAgencySearchWrapper.bIsUserHaveReadAccess = true;
            humAgencySearchWrapper.sLocMemFound = '';
            humAgencySearchWrapper.sMemName = '';
            humAgencySearchWrapper.sPlatform = '';
            humAgencySearchWrapper.sGroupId = '';
            humAgencySearchWrapper.sProduct = '';
            humAgencySearchWrapper.sLOB = '';
            humAgencySearchWrapper.sEffectiveDate = '';
            humAgencySearchWrapper.sEndDate = '';
            humAgencySearchWrapper.sPend = '';
            humAgencySearchWrapper.sPendKey = '';
            humAgencySearchWrapper.sDateTimePend = '';
            humAgencySearchWrapper.sSSN = '';
            humAgencySearchWrapper.sStartDate = '';
            humAgencySearchWrapper.dtMinDate = Date.today();
            humAgencySearchWrapper.dtMaxDate = Date.today();
            humAgencySearchWrapper.sDegree = '';
            humAgencySearchWrapper.sEnterpriseID = '';
            humAgencySearchWrapper.sPhoneExtn = '';
            humAgencySearchWrapper.sServiceaddress = '';
            humAgencySearchWrapper.sServiceState = '';
            humAgencySearchWrapper.sServicezip = '';
            humAgencySearchWrapper.sServiceCity = '';
            Test.stopTest();
        }
    }
}