/**************************************************************************** 
Apex Class Name  : MemberAncillaryResponse_DTO_T_HUM_V2 
Version          : 1.0  
Created Date     : 02/29/2024
Function         : test class for  MemberAncillaryResponse_DTO_HUM_V2 
****************************************************************************
Modification Log:
* Developer Name          Date                       Description
*---------------------------------------------------------------------------------------------------
* Tharun Madishetti       02/29/2024               Original Version - Us:5479030- Ancillary service test methods
***************************************************************************************************/
@isTest
private Class MemberAncillaryResponse_DTO_T_HUM_V2 
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
    * Description : Test method for MemberAncillaryResponse_DTO_T_HUM_V2 
    * Return type : void
    * Paramater   : NA
    */
    static testMethod void testAncillaryResponseDTO()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
            Test.StartTest();
            
            MemberAncillaryResponse_DTO_HUM_V2 oRequest = new MemberAncillaryResponse_DTO_HUM_V2 ();
            oRequest.CostShareInformation='CostShareInformation';
            oRequest.CommunityEngagementExemptCode='CommunityEngagementExemptCode';
            oRequest.TypeOfAssistance='TypeOfAssistance';
            oRequest.DeductibleAmount=3.14;
            oRequest.MemberIdBase='MemberIdBase';
            oRequest.DependentCode='DependentCode';
            oRequest.PlatformCode='PlatformCode';           
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
            oRequest.fullName='FullName'           ;
            oRequest.AddressLine1='AddressLine1';
            oRequest.AddressLine2='AddressLine2';
            oRequest.City='City';
            oRequest.State='State';
            oRequest.Zip='Zip';
            oRequest.fullAddress='FullAress';
            oRequest.OtherHealthIndicators='OtherHealthIndicators';
            oRequest.SpecialCond='SpecialCond';
            oRequest.LtcProgramRecommendation='LtcProgramRecommendation';
            oRequest.ProdPlanId='ProdPlanId';
            oRequest.ProviderCode='ProviderCode';
            oRequest.LevelOfCareDate='01/01/2019 10:36 AM';
            oRequest.LevelOfCareCode='LevelOfCareCode';
            oRequest.SpecialNeedsNote='SpecialNeedsNote';
            oRequest.MedicaidAidCategoryCode='MedicaidAidCategoryCode';
            oRequest.MedicaidBeginDate='01/01/2019 10:36 AM';
            oRequest.MedicaidEndDate='01/01/2019 10:36 AM';
            oRequest.ACWM='ACWM';
            oRequest.Preminum='Test';
    		oRequest.MedicallyFrail='Test';
            oRequest.Rewards='Reward';
            oRequest.JuvenilleJustice='Test';
            oRequest.PenaltyReasonIndicator='Y';
            oRequest.HospiceIndicator='N';
            oRequest.ForesterCareIndicator='Y';
            oRequest.GuardianshipIndicator='Y';
            oRequest.CoverageEndDate='02/29/20224';            
            oRequest.StateCode='OH';
            oRequest.StartDate='TestDate';
            oRequest.EndDate='TestEnd';
            oRequest.NewBornMothersReferenceId='Testid';
            oRequest.AdmitDate='AdmintDate';
            oRequest.dischargeDate='DisDate';    
            oRequest.Doctype='Test';
            oRequest.DocstartDate='02/12/2020';
            oRequest.DocendDate='01/11/2010';
            oRequest.DocpreReleaseDate='Testdate';
            oRequest.DoclocationIndicator='Y';
            oRequest.DocstateCode='Code';
            oRequest.Manager='manager';
            oRequest.MaintenanceReason='Test';
            oRequest.HandicapIndicator='Y';
            oRequest.HandicapIndicator='N';
            oRequest.ReviewDate='01/10/2021';
            oRequest.ChoiseValue='Test';
            oRequest.MotherMedicaidID='MotherId';
            oRequest.priorMedicaidid='priorMedicaidid';
            oRequest.priorMedicaidBeginDate='MotherBegindate';
            oRequest.CostShareInformation='Test';
            oRequest.SuspensionReasonIndicator='Y';
            oRequest.OhioRISEBeginDate='BeginDate';
            oRequest.OhioRISEEndDate='EndDate';
            oRequest.Indvalue='TestValue';
            oRequest.AIANIndicatorValue='Y';
            oRequest.PregnancyDueDate='02/1/2020';
            oRequest.ReviewDate_OK='02/10/2021';
            oRequest.CaseHeadFirstName='FName';
            oRequest.CaseHeadLastName='LName';
            oRequest.CustodyCode='code';
            oRequest.CustodyDescription='TestDes';
            oRequest.CapitationCode='Code';
            oRequest.ChoiceCode='code';
            oRequest.HighRiskPregancyCode='code';
            oRequest.BehavioralHealthCode='code';
            oRequest.ClosureCode='code';
            oRequest.RenewalDate='Date';
            oRequest.RenewalCode='code';
            oRequest.MultipleBirthIndicator='Y';
            oRequest.MedicalAssistanceCategoryCode='code';
            oRequest.CaseTypeCode='code';
            
            system.assert(oRequest != null);
            
            Test.StopTest();
        }
    }  
}