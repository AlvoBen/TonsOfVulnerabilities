/**************************************************************************** 
Apex Class Name  : MemberAncillaryResponse_DTO_T_HUM 
Version          : 1.0  
Created Date     : 05/31/2019
Function         : test class for  MemberAncillaryResponse_DTO_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Manikanta                 356372             05/31/2019               Original Version - REQ - 379006 Ancillary service test methods
***************************************************************************************************/
@isTest
private Class MemberAncillaryResponse_DTO_T_HUM 
{
    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
    
    /*
    * Method Name : testAncillaryResponseDTO
    * Description : Test method for MemberAncillaryResponse_DTO_T_HUM 
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testAncillaryResponseDTO()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            Test.StartTest();
            
            MemberAncillaryResponse_DTO_HUM oRequest = new MemberAncillaryResponse_DTO_HUM ();
            oRequest.CostShareInformation='CostShareInformation';
            oRequest.CommunityEngagementExemptCode='CommunityEngagementExemptCode';
            oRequest.TypeOfAssistance='TypeOfAssistance';
            oRequest.DeductibleAmount=3.14;
            oRequest.MemberIdBase='MemberIdBase';
            oRequest.DependentCode='DependentCode';
            oRequest.PlatformCode='PlatformCode';
            oRequest.CoverageStartDate=datetime.parse('01/01/2019 10:36 AM');
            oRequest.GroupId='GroupId';
            oRequest.DivisionId='DivisionId';
            oRequest.ProductId='ProductId';
            oRequest.Type='Type';
            oRequest.premiumValue = 'premiumValue';
            oRequest.premiumStartDate = 'premiumStartDate';
            oRequest.premiumEndDate = 'premiumEndDate';
            oRequest.penaltyValue = 'penaltyValue';
            oRequest.penaltyStartDate = 'penaltyStartDate';
            oRequest.penaltyEndDate = 'penaltyEndDate';
            oRequest.suspensionValue = 'suspensionValue';
            oRequest.rewardsValue = 'rewardsValue';
            oRequest.FirstName = 'FirstName';
            oRequest.LastName='LastName';
            oRequest.AddressLine1='AddressLine1';
            oRequest.AddressLine2='AddressLine2';
            oRequest.City='City';
            oRequest.State='State';
            oRequest.Zip='Zip';
            oRequest.OtherHealthIndicators='OtherHealthIndicators';
            oRequest.SpecialCond='SpecialCond';
            oRequest.LtcProgramRecommendation='LtcProgramRecommendation';
            oRequest.ProdPlanId='ProdPlanId';
            oRequest.ProviderCode='ProviderCode';
            oRequest.LevelOfCareDate=datetime.parse('01/01/2019 10:36 AM');
            oRequest.LevelOfCareCode='LevelOfCareCode';
            oRequest.SpecialNeedsNote='SpecialNeedsNote';
            oRequest.MedicaidAidCategoryCode='MedicaidAidCategoryCode';
            oRequest.MedicaidBeginDate=datetime.parse('01/01/2019 10:36 AM');
            oRequest.MedicaidEndDate=datetime.parse('01/01/2019 10:36 AM');
            oRequest.ACWM='ACWM';
            
            system.assert(oRequest != null);
            
            Test.StopTest();
        }
    }  
}