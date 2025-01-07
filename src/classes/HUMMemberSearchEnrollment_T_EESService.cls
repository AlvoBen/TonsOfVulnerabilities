/**************************************************************************************************************************************
Apex Class Name :  HUMMemberSearchEnrollment_T_EESService
Version: 1.0
Created On      : 10/10/2022
Function        : Test class for HUMMemberSearchEnrollment_H_EESService

 * Modification Log
 * Developer Name            Code Review                 Date                       Description
 *---------------------------------------------------------------------------------------------------------------------------------------
 *  Nilesh Gadkar                                        10/10/2022                 Original Version
 ****************************************************************************************************************************************/

@istest
Private class HUMMemberSearchEnrollment_T_EESService 
{
    /**
    * <p>
    * Test Setup Method 
    * @ Param     NA
    * @ Return    NA
    */
     
    @testSetup 
    static void testSetup()
    {
        
	}
    /*
     * Method Name : testValidateDemographicSearch
     * Description : This method Is used To test FN, LN, DOB and State code input validation
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateDemographicSearch()
    {
        String firstName ='abcd';
        String lastName ='xyz';
        String dob = (Datetime.now()).format('MM/dd/yyyy');
        String stateCode = 'TX';
        String sSSN = '';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,True);
        
        Test.stopTest();
    }
    
    /*
     * Method Name : testValidateSSNSearch
     * Description : This method Is used To test SSN input validation
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateSSNSearch()
    {
        String firstName ='';
        String lastName ='';
        String dob = '';
        String stateCode = '';
        String sSSN = '111585060';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,True);
        
        Test.stopTest();
    }
    /*
     * Method Name : testValidateNoInputSearch
     * Description : This method Is used To test no input is given
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateNoInputSearch()
    {
        String firstName ='';
        String lastName ='';
        String dob = '';
        String stateCode = '';
        String sSSN = '';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,False);
        
        Test.stopTest();
    }
    /*
     * Method Name : testValidateFirstNameMissingInSearch
     * Description : This method Is used To test firstname is missing 
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateFirstNameMissingInSearch()
    {
        String firstName ='';
        String lastName ='xyz';
        String dob = (Datetime.now()).format('MM/dd/yyyy');
        String stateCode = 'TX';
        String sSSN = '';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,False);
        
        Test.stopTest();
    }
    
    /*
     * Method Name : testValidateLastNameMissingInSearch
     * Description : This method Is used To test lastname is missing 
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateLastNameMissingInSearch()
    {
        String firstName ='abcd';
        String lastName ='';
        String dob = (Datetime.now()).format('MM/dd/yyyy');
        String stateCode = 'TX';
        String sSSN = '';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,False);
        
        Test.stopTest();
    }
    /*
     * Method Name : testValidateDOBMissingInSearch
     * Description : This method Is used To test DOB is missing 
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateDOBMissingInSearch()
    {
        String firstName ='abcd';
        String lastName ='xyz';
        String dob = '';
        String stateCode = 'TX';
        String sSSN = '';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,False);
        
        Test.stopTest();
    }
    /*
     * Method Name : testValidateInvalidDOBInSearch
     * Description : This method Is used To test invalid DOB not in format(MM/DD/YYYY)
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateInvalidDOBInSearch()
    {
        String firstName ='abcd';
        String lastName ='xyz';
        String dob = '13/10/2022';
        String stateCode = 'TX';
        String sSSN = '';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,False);
        
        Test.stopTest();
    }
    /*
     * Method Name : testValidateLeapDOBInSearch
     * Description : This method Is used To test validate leap DOB 
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateLeapDOBInSearch()
    {
        String firstName ='abcd';
        String lastName ='xyz';
        String dob = '10/10/2020';
        String stateCode = 'TX';
        String sSSN = '';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,True);
        
        Test.stopTest();
    }
    /*
     * Method Name : testValidateInvalidStateInSearch
     * Description : This method Is used To test invalid state
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateInvalidStateInSearch()
    {
        String firstName ='abcd';
        String lastName ='xyz';
        String dob = '02/29/2022';
        String stateCode = '';
        String sSSN = '';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,False);
        
        Test.stopTest();
    }
    /*
     * Method Name : testValidateInvalidCombinationInSearch
     * Description : This method Is used To test invalid combination for search
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testValidateInvalidCombinationInSearch()
    {
        String firstName ='abcd';
        String lastName ='xyz';
        String dob = '10/10/2022';
        String stateCode = 'TX';
        String sSSN = '111585060';
        Boolean result;
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.validateSearchEESServiceInput(firstName,lastName,dob,stateCode,sSSN);
        System.assertEquals(result,False);
        
        Test.stopTest();
    }
    /*
     * Method Name : testESSServiceRequestBody
     * Description : This method Is used To test service Request body
     * Return type : NA
     * Paramater   : NA
     */
    testMethod Static void testESSServiceRequestBody()
    {
        String firstName ='abcd';
        String lastName ='xyz';
        String dob = '10/10/2022';
        String stateCode = 'TX';
        String result='';
        String actual='{"GetPlatformMembers":{"stateCode":"TX","lastName":"xyz","indicators":{"platforms":[{"platform":"LV"},{"platform":"EM"}]},"firstName":"abcd","dob":"10/10/2022"}}';
        
        Test.startTest();
        
        result = HUMMemberSearchEnrollment_H_EESService.ESSServiceRequestBody(firstName,lastName,dob,stateCode);
        System.assertEquals(result,actual);
        
        Test.stopTest();

    }

}