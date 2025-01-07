/************************************************************************************************************************************************
  Apex Class Name  : UnifiedSearch_Case_DTO_T_HUM
  Version          : 1.0 
  Created Date     : July 16 2021
  Function         : Test Class for UnifiedSearch_Case_DTO_HUM
  * Version                   Developer Name            Code Review                 Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
  * 1.0                       Mohammed Noor                                       07/16/2021                  Original Version  - created for US1801197
 *****************************************************************************************************************************************************************************/
@isTest
public with sharing class UnifiedSearch_Case_DTO_T_HUM 
{
    /*
    * This method creates test data for test class
    * @param None
    * @return None
    */
    @testSetup static void setUpTestData()
    {
        User testUser = HUMTestDataHelperTest.createTestUser('UnifiedCaseUser','Customer Care Specialist');
        insert testUser;
        system.assertEquals(testUser.lastname =='test UnifiedCaseUser',true);
    }

    /*
    * This method Is used to test the DTO class UnifiedSearch_Case_DTO_HUM
	* 
    * @param   None
    * @return  None
    */
    static testMethod void testWrapper() 
    {
        User testUser = [SELECT Id FROM User WHERE lastname ='test UnifiedCaseUser' limit 1];
        system.runAs(testUser)
        {
            Test.startTest();
            UnifiedSearch_Case_DTO_HUM.InteractionDetails_DTO oInt = new UnifiedSearch_Case_DTO_HUM.InteractionDetails_DTO();
            oInt.intWithType = 'intWithType';
            oInt.intWith = 'intWith';
            oInt.CaseNumber = 'CaseNumber';

            UnifiedSearch_Case_DTO_HUM.Context_DTO oContext  = new UnifiedSearch_Case_DTO_HUM.Context_DTO();            
            oContext.MajorLOB = 'MajorLOB';
            oContext.MinorLOB = 'MinorLOB';
            oContext.IntrtWithType = 'IntrtWithType';
            oContext.IntrtAbtType = 'IntrtAbtType';
            oContext.Market = 'Market';
            oContext.CRMPageName = 'CRMPageName';
            oContext.CaseNumber = 'CaseNumber';
            oContext.IntrNumber = 'IntrNumber';

            UnifiedSearch_Case_DTO_HUM oWrap = new UnifiedSearch_Case_DTO_HUM();
            oWrap.searchParams = '';
            oWrap.coveoTabName = 'coveoTabName';
            oWrap.InteractionDetails = oInt;
            oWrap.context = oContext;        

            system.assertNotEquals(oWrap, null);
            Test.stopTest();
        }
    }
}