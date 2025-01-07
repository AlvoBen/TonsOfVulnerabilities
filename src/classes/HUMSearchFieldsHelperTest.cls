/*******************************************************************************************************************************
Apex Class Name : HUMSearchFieldsHelperTest
Version         : 1.0
Created On      : 08/06/2014
Function        : Test class for HUMSearchFieldsHelper
Modification Log: 
* Developer Name           Code Review               Date                        Description
*------------------------------------------------------------------------------------------------------------------------------
*Kritika P Popat            17122                    08/06/2014                  Original Version
*Tanushree Hazari           17122                    08/08/2014                  Code Re factoring
*Kritika P Popat            17521                    08/27/2014                  Defect fix for alignment
*Kritika P Popat            17521                    08/28/2014                  Adding assert and breaking of test Methods 
                                                                                 to less then 100 lines
*Kritika P Popat            17521                    08/28/2014                  Modified according to code review : 17521
*Manish Kumar               17722                    09/03/2014                  Added consumer id encryption test methods.
*Manish Kumar               18362                    10/09/2014                  Defect fix for review#18362
*Ranjeeth Nagishetty        18655                    10/28/2014                  test class updated
*Tanushree Hazari           19690                    11/26/2014                  Updated test class method for Last Name Validation 
                                                                                 as per REQ-70935
*Santhi Mandava             23667                    06/05/2015                  Updated test class to fix coverage issue.
*Santhi Mandava             23918                    06/15/2015                  Fixed test class failure issue in QA.
*Santhi Mandava             348723                   04/02/2019                  Cleanup cipher cloud references
*Ajay Chakradhar                                     08/12/2020                  Fixed test class failure issue
*******************************************************************************************************************************/

@isTest
private class HUMSearchFieldsHelperTest
{
    /*
     * Method Name : testDataMemberSearch
     * Description : Test Method to cover validations of Member Search
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void testDataMemberSearch()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            List<HUMSearchFieldsHelper> lstHumSearchFieldsHelper = new List<HumSearchFieldsHelper>();
            System.assertNotEquals(lstHumSearchFieldsHelper, null, 'Value not matched');
            for(integer i = 0; i < 15; i++)
            {
                HUMSearchFieldsHelper oHumSearchFieldsHelper = new HUMSearchFieldsHelper();
                lstHumSearchFieldsHelper.add(oHUMSearchFieldsHelper);
            }
            
            lstHumSearchFieldsHelper[0].oSearchWrapper.sGroupNumber = '987654321';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sGroupNumber, '987654321',  'Value not matched');
            lstHumSearchFieldsHelper[1].oSearchWrapper.sGroupNumber = '@987654321';
            System.assertEquals(lstHumSearchFieldsHelper[1].oSearchWrapper.sGroupNumber, '@987654321', 'Value not matched');
            
            lstHumSearchFieldsHelper[2].oSearchWrapper.sMemberId = '12345';
            System.assertEquals(lstHumSearchFieldsHelper[2].oSearchWrapper.sMemberId, '12345', 'Value not matched');
            lstHumSearchFieldsHelper[3].oSearchWrapper.sMemberId = '12@345';
            System.assertEquals(lstHumSearchFieldsHelper[3].oSearchWrapper.sMemberId, '12@345', 'Value not matched');
            lstHumSearchFieldsHelper[4].oSearchWrapper.sMemberId = '1';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sMemberId, '1', 'Value not matched');
            
            lstHumSearchFieldsHelper[5].oSearchWrapper.sFirstName = 'huey';
            System.assertEquals(lstHumSearchFieldsHelper[5].oSearchWrapper.sFirstName, 'huey', 'Value not matched');
            lstHumSearchFieldsHelper[6].oSearchWrapper.sFirstName = '@huey';
            System.assertEquals(lstHumSearchFieldsHelper[6].oSearchWrapper.sFirstName, '@huey', 'Value not matched');
            
            lstHumSearchFieldsHelper[7].oSearchWrapper.sLastName = 'duck';
            System.assertEquals(lstHumSearchFieldsHelper[7].oSearchWrapper.sLastName, 'duck', 'Value not matched');
            lstHumSearchFieldsHelper[8].oSearchWrapper.sLastName = 'd';
            System.assertEquals(lstHumSearchFieldsHelper[8].oSearchWrapper.sLastName, 'd', 'Value not matched');
            
            lstHumSearchFieldsHelper[9].oSearchWrapper.sPostalCode = '40202';
            System.assertEquals(lstHumSearchFieldsHelper[9].oSearchWrapper.sPostalCode, '40202', 'Value not matched');
            lstHumSearchFieldsHelper[10].oSearchWrapper.sPostalCode = '402';
            System.assertEquals(lstHumSearchFieldsHelper[10].oSearchWrapper.sPostalCode, '402', 'Value not matched');
            lstHumSearchFieldsHelper[11].oSearchWrapper.sPostalCode = 'abc@d';
            System.assertEquals(lstHumSearchFieldsHelper[11].oSearchWrapper.sPostalCode, 'abc@d', 'Value not matched');
         
            lstHumSearchFieldsHelper[12].oSearchWrapper.sPhone = '(123) 456-7890';
            System.assertEquals(lstHumSearchFieldsHelper[12].oSearchWrapper.sPhone, '(123) 456-7890', 'Value not matched'); 
            lstHumSearchFieldsHelper[13].oSearchWrapper.sPhone = 'abc';
            System.assertEquals(lstHumSearchFieldsHelper[13].oSearchWrapper.sPhone, 'abc', 'Value not matched'); 
            lstHumSearchFieldsHelper[14].oSearchWrapper.sPhone = '(123) 56-789';
            System.assertEquals(lstHumSearchFieldsHelper[14].oSearchWrapper.sPhone, '(123) 56-789', 'Value not matched'); 
            lstHumSearchFieldsHelper[14].oSearchWrapper.sSuffix = 'a';
            System.assertEquals(lstHumSearchFieldsHelper[14].oSearchWrapper.sSuffix , 'a', 'Value not matched'); 
            lstHumSearchFieldsHelper[14].oSearchWrapper.sAgencyName= 'arrr&*3#';
            System.assertEquals(lstHumSearchFieldsHelper[14].oSearchWrapper.sAgencyName, 'arrr&*3#', 'Value not matched'); 
            lstHumSearchFieldsHelper[14].oSearchWrapper.sState= 'None';
            System.assertEquals(lstHumSearchFieldsHelper[14].oSearchWrapper.sState, 'None', 'Value not matched');
            test.startTest();
            lstHumSearchFieldsHelper[0].validateGroupNumber();
            System.assertEquals(lstHumSearchFieldsHelper[0].validateGroupNumber(), true, 'Value not matched');
            lstHumSearchFieldsHelper[1].validateGroupNumber();
            System.assertEquals(lstHumSearchFieldsHelper[1].validateGroupNumber(), false, 'Value not matched');
            lstHumSearchFieldsHelper[2].validateMemberId();
            System.assertEquals(lstHumSearchFieldsHelper[2].validateMemberId(), true, 'Value not matched');
            lstHumSearchFieldsHelper[3].validateMemberId();
            System.assertEquals(lstHumSearchFieldsHelper[3].validateMemberId(), false, 'Value not matched');
            lstHumSearchFieldsHelper[2].memberIdLength();
            System.assertEquals(lstHumSearchFieldsHelper[2].memberIdLength(), true, 'Value not matched');
            lstHumSearchFieldsHelper[4].memberIdLength();
            System.assertEquals(lstHumSearchFieldsHelper[4].memberIdLength(), false, 'Value not matched');
            lstHumSearchFieldsHelper[5].validateFirstName();
            System.assertEquals(lstHumSearchFieldsHelper[5].validateFirstName(), true, 'Value not matched');
            lstHumSearchFieldsHelper[6].validateFirstName();
            System.assertEquals(lstHumSearchFieldsHelper[6].validateFirstName(), false, 'Value not matched');
            lstHumSearchFieldsHelper[7].lastNameLength();
            System.assertEquals(lstHumSearchFieldsHelper[7].lastNameLength(), true, 'Value not matched');
            lstHumSearchFieldsHelper[8].lastNameLength();
            System.assertEquals(lstHumSearchFieldsHelper[8].lastNameLength(), false, 'Value not matched');    
            lstHumSearchFieldsHelper[9].validatePostalCode();
            System.assertEquals(lstHumSearchFieldsHelper[9].validatePostalCode(), true, 'Value not matched');    
            lstHumSearchFieldsHelper[10].validatePostalCode();
            System.assertEquals(lstHumSearchFieldsHelper[10].validatePostalCode(), false, 'Value not matched');    
            lstHumSearchFieldsHelper[11].validateUnknownPostalCode();
            System.assertEquals(lstHumSearchFieldsHelper[11].validateUnknownPostalCode(), false, 'Value not matched');    
            lstHumSearchFieldsHelper[12].validatePhone();
            System.assertEquals(lstHumSearchFieldsHelper[12].validatePhone(), true, 'Value not matched');    
            lstHumSearchFieldsHelper[13].validatePhone();
            System.assertEquals(lstHumSearchFieldsHelper[13].validatePhone(), false, 'Value not matched');    
            lstHumSearchFieldsHelper[14].validatePhone();
            System.assertEquals(lstHumSearchFieldsHelper[14].validatePhone(), false, 'Value not matched');  
            lstHumSearchFieldsHelper[14].validateSuffix();
            System.assertEquals(lstHumSearchFieldsHelper[14].validateSuffix(), false, 'Value not matched');    
            lstHumSearchFieldsHelper[14].validateSuffixLength();
            System.assertEquals(lstHumSearchFieldsHelper[14].validateSuffixLength(), false, 'Value not matched');  
            lstHumSearchFieldsHelper[14].validateAgencyName();
            System.assertEquals(lstHumSearchFieldsHelper[14].validateAgencyName(), false, 'Value not matched'); 
            lstHumSearchFieldsHelper[14].validateCBISFirstName();
            System.assertEquals(lstHumSearchFieldsHelper[14].validateCBISFirstName(), false, 'Value not matched');   
            lstHumSearchFieldsHelper[14].validateCBISLastName();
            System.assertEquals(lstHumSearchFieldsHelper[14].validateCBISLastName(), false, 'Value not matched'); 
            lstHumSearchFieldsHelper[14].validateCBISState();
            System.assertEquals(lstHumSearchFieldsHelper[14].validateCBISState(), false, 'Value not matched'); 
            lstHumSearchFieldsHelper[14].validateCBISBirthDay();
            System.assertEquals(lstHumSearchFieldsHelper[14].validateCBISBirthDay(), false, 'Value not matched'); 
            
            test.stopTest();
        }
    }
    
    /*
     * Method Name : testCustomHelper
     * Description : Test Method to cover properties in HUMCustomSearchHelper class.
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void testCustomHelper()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');

        System.runAs(oCCSUser)
        {
            HUMCustomSearchHelper objCustomHelper = new HUMCustomSearchHelper();
            objCustomHelper.sMemberId = '12345';
            objCustomHelper.sFirstName = 'Donald';
            objCustomHelper.sLastName = 'Teri';
            objCustomHelper.sBirthdate = '30/02/1995';
            objCustomHelper.sState = 'Arizona';
            objCustomHelper.sPostalCode = '45678';
            objCustomHelper.sPhone = '1234567890';
            objCustomHelper.sGroupNumber = '4654646';
            objCustomHelper.sPersonEmail = '123@test.com';
            objCustomHelper.sGroupName = 'Testing';
            objCustomHelper.sAgentId = '456456';
            objCustomHelper.sAgentType = 'Contract';
            objCustomHelper.sAgencyName  = 'Testing';
            objCustomHelper.sTaxID = '1234567891';
            objCustomHelper.sNPI  = '1234567891';
            objCustomHelper.sSpeciality = 'Medical';
            objCustomHelper.sProviderName = 'Donald';
            objCustomHelper.sFacilityName = 'QA';
            objCustomHelper.sAddress = 'Arizona';
            objCustomHelper.sOrgName = 'Oil & Gas';
            objCustomHelper.sExtID  = '567';
            objCustomHelper.sCity  = 'Arizona';
            objCustomHelper.sAddresstype  = 'Email';
            objCustomHelper.sGender  = 'M';
            objCustomHelper.sClassification  = 'Billing';
            objCustomHelper.sTaxmonycode  = '00';
            objCustomHelper.sDBA  = '67';
            objCustomHelper.bIsUserHaveReadAccess = true;
            objCustomHelper.sLocMemFound  = 'Yes';
            objCustomHelper.sMemName  = 'Donald';
            objCustomHelper.sPlatform  = 'EM';
            objCustomHelper.sGroupId  = '12344';
            objCustomHelper.sProduct = 'ESM';
            objCustomHelper.sLOB = 'Medical';
            objCustomHelper.sEffectiveDate = '30/02/1995';
            objCustomHelper.sEndDate  = '30/02/1995';
            objCustomHelper.sPend  = '345';
            objCustomHelper.sPendKey = '4355445';
            objCustomHelper.sDateTimePend  = '30/02/1995';
            objCustomHelper.sSSN = '1234567891';
            objCustomHelper.sStartDate  = '30/02/1995';
            objCustomHelper.dtMinDate = Date.today();
            objCustomHelper.dtMaxDate = Date.today();
            objCustomHelper.sDegree  = '76867876';
            objCustomHelper.sEnterpriseID = '67890';
            objCustomHelper.sPhoneExtn  = '1234';
            objCustomHelper.sServiceaddress = 'Arizona';
            objCustomHelper.sServiceState  = 'Arizona';
            objCustomHelper.sServicezip  = '12345';
            objCustomHelper.sServiceCity  = 'Arizona';
            objCustomHelper.sSuffix  = '00';
            System.assertNotEquals(objCustomHelper, null, 'Value not matched');
        }
     }
    
    /*
     * Method Name : testDataLastNameValidation
     * Description : Test Method to cover validations of Last Name in Member search
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void testDataLastNameValidation()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            List<HUMSearchFieldsHelper> lstHumSearchFieldsHelper = new List<HumSearchFieldsHelper>();
            System.assertNotEquals(lstHumSearchFieldsHelper, null, 'Value not matched');
            for(integer i = 0; i < 2; i++)
            {
                HUMSearchFieldsHelper oHumSearchFieldsHelper = new HUMSearchFieldsHelper();
                lstHumSearchFieldsHelper.add(oHUMSearchFieldsHelper);
            }
            lstHumSearchFieldsHelper[0].oSearchWrapper.sLastName = 'duck.duck';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sLastName, 'duck.duck', 'Value not matched');
            lstHumSearchFieldsHelper[1].oSearchWrapper.sLastName = 'duck@';
            System.assertEquals(lstHumSearchFieldsHelper[1].oSearchWrapper.sLastName, 'duck@', 'Value not matched');
            
            test.startTest();
            lstHumSearchFieldsHelper[0].validateLastName();
            System.assertEquals(lstHumSearchFieldsHelper[0].validateLastName(), true, 'Value not matched');
            lstHumSearchFieldsHelper[1].validateGroupNumber();
            System.assertEquals(lstHumSearchFieldsHelper[1].validateLastName(), false, 'Value not matched');
            test.stopTest();
        } 
    }   
    
    /*
     * Method Name : testDataMemberSearchBirthDate
     * Description : Test Method to cover validations of birth date in Member search
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void testDataMemberSearchBirthDate()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            List<HUMSearchFieldsHelper> lstHumSearchFieldsHelper = new List<HumSearchFieldsHelper>();
            System.assertNotEquals(lstHumSearchFieldsHelper, null, 'Value not matched');
            for(integer i = 0; i < 5; i++)
            {
                HUMSearchFieldsHelper oHumSearchFieldsHelper = new HUMSearchFieldsHelper();
                lstHumSearchFieldsHelper.add(oHUMSearchFieldsHelper);
            }
            
            lstHumSearchFieldsHelper[0].oSearchWrapper.sBirthdate = '08/05/1995';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sBirthdate, '08/05/1995', 'Value not matched');
            lstHumSearchFieldsHelper[1].oSearchWrapper.sBirthdate = '08/05/2020';
            System.assertEquals(lstHumSearchFieldsHelper[1].oSearchWrapper.sBirthdate, '08/05/2020', 'Value not matched');
            lstHumSearchFieldsHelper[2].oSearchWrapper.sBirthdate = 'abc';
            System.assertEquals(lstHumSearchFieldsHelper[2].oSearchWrapper.sBirthdate, 'abc', 'Value not matched');
            lstHumSearchFieldsHelper[3].oSearchWrapper.sBirthdate = '30/02/1995';
            System.assertEquals(lstHumSearchFieldsHelper[3].oSearchWrapper.sBirthdate, '30/02/1995', 'Value not matched');
            lstHumSearchFieldsHelper[4].oSearchWrapper.sBirthdate = '30/02/2012';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sBirthdate, '30/02/2012', 'Value not matched');
        
            test.startTest();
            lstHumSearchFieldsHelper[0].validateBirthdate();  
            System.assertEquals(lstHumSearchFieldsHelper[0].validateBirthdate(), true, 'Value not matched');
            lstHumSearchFieldsHelper[1].validateBirthdate();  
            System.assertEquals(lstHumSearchFieldsHelper[1].validateBirthdate(), true, 'Value not matched');
            lstHumSearchFieldsHelper[2].validateBirthdate();  
            System.assertEquals(lstHumSearchFieldsHelper[2].validateBirthdate(), false, 'Value not matched');
            lstHumSearchFieldsHelper[3].validateBirthdate();  
            System.assertEquals(lstHumSearchFieldsHelper[3].validateBirthdate(), false, 'Value not matched');
            lstHumSearchFieldsHelper[4].validateBirthdate();  
            System.assertEquals(lstHumSearchFieldsHelper[4].validateBirthdate(), false, 'Value not matched');   
            test.stopTest();
        }
    }
    
    /*
     * Method Name : testDataGroupAgencySearch
     * Description : Test Method to cover validations of Group and Agency Search
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void testDataGroupAgencySearch()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            List<HUMSearchFieldsHelper> lstHumSearchFieldsHelper = new List<HumSearchFieldsHelper>();
            System.assertNotEquals(lstHumSearchFieldsHelper, null, 'Value not matched');
            List<String> lstEncryptFieldValues = new List<String>();
            for(integer i = 0; i < 6; i++)
            {
                HUMSearchFieldsHelper oHumSearchFieldsHelper = new HUMSearchFieldsHelper();
                lstHumSearchFieldsHelper.add(oHUMSearchFieldsHelper);
            }
            
            lstHumSearchFieldsHelper[0].oSearchWrapper.sAgentId = '1234567890';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sAgentId, '1234567890', 'Value not matched');
            lstHumSearchFieldsHelper[1].oSearchWrapper.sAgentId = 'abc@';
            System.assertEquals(lstHumSearchFieldsHelper[1].oSearchWrapper.sAgentId, 'abc@', 'Value not matched');
             
            lstHumSearchFieldsHelper[2].oSearchWrapper.sState = 'None';
            System.assertEquals(lstHumSearchFieldsHelper[2].oSearchWrapper.sState, 'None', 'Value not matched');
            lstHumSearchFieldsHelper[2].oSearchWrapper.sAgentType = 'abc';
            System.assertEquals(lstHumSearchFieldsHelper[2].oSearchWrapper.sAgentType, 'abc', 'Value not matched');
    
            lstHumSearchFieldsHelper[3].oSearchWrapper.sLastName = 'abc';
            System.assertEquals(lstHumSearchFieldsHelper[3].oSearchWrapper.sLastName, 'abc', 'Value not matched');
            lstHumSearchFieldsHelper[3].oSearchWrapper.sState = 'None';
            System.assertEquals(lstHumSearchFieldsHelper[3].oSearchWrapper.sState, 'None', 'Value not matched'); 
            System.assertNotEquals(lstHumSearchFieldsHelper, null, 'Value not matched');
            
            lstHumSearchFieldsHelper[4].oSearchWrapper.sPostalCode = '12345';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sPostalCode, '12345', 'Value not matched');
            lstHumSearchFieldsHelper[4].oSearchWrapper.sFacilityName = 'abcd';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sFacilityName, 'abcd', 'Value not matched');
            lstHumSearchFieldsHelper[4].oSearchWrapper.sFirstName = 'huey';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sFirstName, 'huey', 'Value not matched');
            lstHumSearchFieldsHelper[4].oSearchWrapper.sLastname = 'duck';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sLastname, 'duck', 'Value not matched');
            lstHumSearchFieldsHelper[4].oSearchWrapper.sTaxID = '1234567891';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sTaxID, '1234567891', 'Value not matched');
            lstHumSearchFieldsHelper[4].oSearchWrapper.sGroupName = 'tcs';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sGroupName, 'tcs', 'Value not matched'); 
            lstHumSearchFieldsHelper[4].oSearchWrapper.sAgencyName = '1234567890';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sAgencyName, '1234567890', 'Value not matched'); 
            lstHumSearchFieldsHelper[4].oSearchWrapper.sBirthdate = '05/18/1990';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sBirthdate, '05/18/1990', 'Value not matched'); 
            lstHumSearchFieldsHelper[4].oSearchWrapper.sPhone = '1234567890';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sPhone, '1234567890', 'Value not matched');
            
            lstHumSearchFieldsHelper[5].oSearchWrapper.sState = 'Arizona';
            System.assertEquals(lstHumSearchFieldsHelper[5].oSearchWrapper.sState, 'Arizona', 'Value not matched');
            lstHumSearchFieldsHelper[5].oSearchWrapper.sAgentType = 'abc';
            System.assertEquals(lstHumSearchFieldsHelper[5].oSearchWrapper.sAgentType, 'abc', 'Value not matched');
            
            lstEncryptFieldValues.add('Account');
            lstEncryptFieldValues.add('member_id__c');
            System.assertNotEquals(lstEncryptFieldValues, null, 'Value not matched');
            
            test.startTest();
            lstHumSearchFieldsHelper[0].validateAgentId();
            System.assertEquals(lstHumSearchFieldsHelper[0].validateAgentId(), true, 'Value not matched');
            lstHumSearchFieldsHelper[1].validateAgentId();
            System.assertEquals(lstHumSearchFieldsHelper[1].validateAgentId(), false, 'Value not matched');
            lstHumSearchFieldsHelper[2].validateAgencySearch();
            System.assertEquals(lstHumSearchFieldsHelper[2].validateAgencySearch(), false, 'Value not matched');
            lstHumSearchFieldsHelper[2].validateGroupSearch();
            System.assertEquals(lstHumSearchFieldsHelper[2].validateGroupSearch(), false, 'Value not matched');
            lstHumSearchFieldsHelper[3].validateAgencySearch();
            System.assertEquals(lstHumSearchFieldsHelper[3].validateAgencySearch(), false, 'Value not matched');
            lstHumSearchFieldsHelper[5].validateGroupSearch();
            System.assertEquals(lstHumSearchFieldsHelper[5].validateGroupSearch(), false, 'Value not matched');
            test.stopTest();
        }
    }
    
    /*
     * Method Name : testDataProviderSearch
     * Description : Test Method to cover validations of Provider Search
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void testDataProviderSearch()
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertNotEquals(oCCSUser, NULL, 'Value not matched');
        
        System.runAs(oCCSUser)
        {
            List<HUMSearchFieldsHelper> lstHumSearchFieldsHelper = new List<HumSearchFieldsHelper>();
            System.assertNotEquals(lstHumSearchFieldsHelper, null, 'Value not matched');
            for(integer i = 0; i < 10; i++)
            {
                HUMSearchFieldsHelper oHumSearchFieldsHelper = new HUMSearchFieldsHelper();
                lstHumSearchFieldsHelper.add(oHUMSearchFieldsHelper);
            }
            
            lstHumSearchFieldsHelper[0].oSearchWrapper.sPostalCode = '12345';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sPostalCode, '12345', 'Value not matched');
            lstHumSearchFieldsHelper[0].oSearchWrapper.sFacilityName = 'abcd';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sFacilityName, 'abcd', 'Value not matched');
            lstHumSearchFieldsHelper[0].oSearchWrapper.sFirstName = 'huey';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sFirstName, 'huey', 'Value not matched');
            lstHumSearchFieldsHelper[0].oSearchWrapper.sLastname = 'duck';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sLastname, 'duck', 'Value not matched');
            lstHumSearchFieldsHelper[0].oSearchWrapper.sTaxID = '1234567891';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sTaxID, '1234567891', 'Value not matched');
            lstHumSearchFieldsHelper[0].oSearchWrapper.sNPI = '1234567891';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sNPI, '1234567891', 'Value not matched');
            lstHumSearchFieldsHelper[0].oSearchWrapper.sSpeciality = 'None';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sSpeciality, 'None', 'Value not matched');
            lstHumSearchFieldsHelper[0].oSearchWrapper.sState = 'Arizona';
            System.assertEquals(lstHumSearchFieldsHelper[0].oSearchWrapper.sState, 'Arizona', 'Value not matched');
            
            lstHumSearchFieldsHelper[1].oSearchWrapper.sState = 'None';
            System.assertEquals(lstHumSearchFieldsHelper[1].oSearchWrapper.sState, 'None', 'Value not matched');
            lstHumSearchFieldsHelper[1].oSearchWrapper.sSpeciality = 'None';
            System.assertEquals(lstHumSearchFieldsHelper[1].oSearchWrapper.sSpeciality, 'None', 'Value not matched');
            
            lstHumSearchFieldsHelper[2].oSearchWrapper.sTaxID = '123456789';
            System.assertEquals(lstHumSearchFieldsHelper[2].oSearchWrapper.sTaxID, '123456789', 'Value not matched');
            lstHumSearchFieldsHelper[3].oSearchWrapper.sTaxID = '12345';
            System.assertEquals(lstHumSearchFieldsHelper[3].oSearchWrapper.sTaxID, '12345', 'Value not matched');
            lstHumSearchFieldsHelper[4].oSearchWrapper.sTaxID = '12@#345';
            System.assertEquals(lstHumSearchFieldsHelper[4].oSearchWrapper.sTaxID, '12@#345', 'Value not matched');
            
            lstHumSearchFieldsHelper[5].oSearchWrapper.sNPI = '123';
            System.assertEquals(lstHumSearchFieldsHelper[5].oSearchWrapper.sNPI, '123', 'Value not matched');
            lstHumSearchFieldsHelper[6].oSearchWrapper.sNPI = '1234567891';
            System.assertEquals(lstHumSearchFieldsHelper[6].oSearchWrapper.sNPI, '1234567891', 'Value not matched');
            lstHumSearchFieldsHelper[7].oSearchWrapper.sNPI = '123@4567891';
            System.assertEquals(lstHumSearchFieldsHelper[7].oSearchWrapper.sNPI, '123@4567891', 'Value not matched');
            
            lstHumSearchFieldsHelper[8].oSearchWrapper.sFacilityName = '@%^SJJ';
            System.assertEquals(lstHumSearchFieldsHelper[8].oSearchWrapper.sFacilityName, '@%^SJJ', 'Value not matched');
            lstHumSearchFieldsHelper[9].oSearchWrapper.sFacilityName = 'abcd';
            System.assertEquals(lstHumSearchFieldsHelper[9].oSearchWrapper.sFacilityName, 'abcd', 'Value not matched');
            
            test.startTest();
            lstHumSearchFieldsHelper[0].validateProviderSearch();
            System.assertEquals(lstHumSearchFieldsHelper[0].validateProviderSearch(), true, 'Value not matched');
            lstHumSearchFieldsHelper[1].validateProviderSearch();
            System.assertEquals(lstHumSearchFieldsHelper[1].validateProviderSearch(), false, 'Value not matched');
            lstHumSearchFieldsHelper[2].validateTaxIDLength();
            System.assertEquals(lstHumSearchFieldsHelper[2].validateTaxIDLength(), true, 'Value not matched');
            lstHumSearchFieldsHelper[3].validateTaxIDLength();
            System.assertEquals(lstHumSearchFieldsHelper[3].validateTaxIDLength(), false, 'Value not matched');
            lstHumSearchFieldsHelper[2].validateTaxID();
            System.assertEquals(lstHumSearchFieldsHelper[2].validateTaxID(), true, 'Value not matched');
            lstHumSearchFieldsHelper[4].validateTaxID();
            System.assertEquals(lstHumSearchFieldsHelper[4].validateTaxID(), false, 'Value not matched');
            lstHumSearchFieldsHelper[5].validateNPILength();
            System.assertEquals(lstHumSearchFieldsHelper[5].validateNPILength(), false, 'Value not matched');
            lstHumSearchFieldsHelper[6].validateNPILength();
            System.assertEquals(lstHumSearchFieldsHelper[6].validateNPILength(), true, 'Value not matched');
            lstHumSearchFieldsHelper[6].validateNPI();
            System.assertEquals(lstHumSearchFieldsHelper[6].validateNPI(), true, 'Value not matched');
            lstHumSearchFieldsHelper[7].validateNPI();
            System.assertEquals(lstHumSearchFieldsHelper[7].validateNPI(), false, 'Value not matched');
            lstHumSearchFieldsHelper[8].validateFacilityName();
            System.assertEquals(lstHumSearchFieldsHelper[8].validateFacilityName(), false, 'Value not matched');
            lstHumSearchFieldsHelper[9].validateFacilityName();
            System.assertEquals(lstHumSearchFieldsHelper[9].validateFacilityName(), true, 'Value not matched');
            test.stopTest();
        }
    }
 }