/**************************************************************************** 
Apex Class Name  : ClaimsGetCodeEditDetailsDTO_T_HUM
Version          : 1.0  
Created Date     : May 20, 2016
Function         : Test class for ClaimsGetCodeEditDetails_DTO_HUM
******************************************************************************
Modification Log:
* Developer Name          Code Review         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prasanthi Kandula         27974            05/20/2016                  Original Version
* Prasanthi Kandula         28667            05/30/2016                 Increased Code coverage
***************************************************************************************************/
@isTest
private Class ClaimsGetCodeEditDetailsDTO_T_HUM
{
    /*
     * Method Name : prepareTestData
     * Description : Create common test Data
     * Return type : void
     * Paramater   : NA
     */
     @testSetup static void prepareTestData() 
    {
        User oCCSUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oCCSUser;
        system.assertEquals(oCCSUser.LastName,'test User1');
    }
    
    /*
     * Method Name : TestGetCodeEditDetails
     * Description : Used to run scenerios of test data
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void TestGetCodeEditDetails()
    {    
        User oCCSUser = [Select id,UserName from User where LastName = 'test User1' LIMIT 1 ] ;
        system.assertEquals(oCCSUser.UserName,'user1user1@humcrmstest.com');
        System.runAs (oCCSUser) 
        { 
            Test.StartTest();
            ClaimsGetCodeEditDetails_DTO_HUM oClaimCodeEdit= new ClaimsGetCodeEditDetails_DTO_HUM();
            oClaimCodeEdit.sCodeEditInd = 'iHealth';
            oClaimCodeEdit.sClaimNbr = '123456';
            oClaimCodeEdit.sExCode = '123';
            oClaimCodeEdit.sRationaleDesc = 'Rationale Description';
            oClaimCodeEdit.sCurrentQueue = 'Cincinati Calls';
            oClaimCodeEdit.sUrl = 'test';
            oClaimCodeEdit.sContinuationState = 'Cincinati Calls';
            System.assertEquals(oClaimCodeEdit.sCodeEditInd, 'iHealth');
            System.assertEquals(oClaimCodeEdit.sClaimNbr , '123456');
            Test.StopTest();
        }
        
    }    
}