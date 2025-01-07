/**************************************************************************** 
Apex Class Name  : MemberAncillaryResponse_H_T_HUM 
Version          : 1.0  
Created Date     : 05/31/2019
Function         : Test class for MemberAncillaryResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Manikanta                356372              05/31/2019               Original Version - REQ - 379006 Ancillary service test methods
* Manikanta                                    06/07/2019               REQ - 379006 Made code changes to align with the DP changes
***************************************************************************************************/
@isTest
private Class MemberAncillaryResponse_H_T_HUM 
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
     * Method Name : getAncillaryinfo
     * Description : Test method for MemberAncillaryResponse_H_HUM class
     * Return type : void
     * Paramater   : NA
     */ 
    static testMethod void getAncillaryinfo() 
    {
        User u = [SELECT Id, Lastname FROM User  WHERE Lastname='test User1' LIMIT 1];
        system.assertEquals(u.Lastname, 'test User1');
        system.runAs(u)
        {
            Test.startTest();
          
            String sresponse = '{"getMemberAncillaryResponse":{"responseHeader":{"environment": "INT", "context":{"clientSessionId": "G0x7f22da06bc08-1D", "server": "LOUWEBWTL25S02", "transactionId": "0180de59-a07d-4162-9f32-1b06f4d66647"}, "metrics":{"RequestTimeStamp": "2019-06-07T09:11:52.6", "ResponseTimeStamp": "2019-06-07T09:11:52.7", "duration": 61}},"responseStatus":{"status": "SUCCESS", "message":{"code": "MASI0001", "details": "Member Ancillary details returned successfully."}},"AncillaryEntityList":[{ "CoverageIdentifier":{"MemberIdBase": "H78898283", "DependentCode": "00", "PlatformCode": "LV", "CoverageStartDate": null, "GroupId": "0Y3521", "DivisionId": "0Y3521", "ProductId": "7592490201" }, "EntityTypeList":[{ "Type": "Premium", "Value": "12.00", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "MedicallyFrail", "Value": "58", "StartDate": "2019-03-01", "EndDate": "9999-12-31" },{ "Type": "Rewards", "Value": "A", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{ "Type": "JuvenilleJustice", "Value": "Y", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "PenaltyReasonIndicator", "Value": "CP", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "SuspensionReasonIndicator", "Value": "CS", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{ "Type": "HospiceIndicator", "Value": "HP", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "FosterCareIndicator", "Value": "N", "StartDate": "2019-03-01", "EndDate": "9999-12-31"},{"Type": "GuardianshipIndicator", "Value": "Y", "StartDate": "2019-03-01", "EndDate": "9999-12-31"}],"HeadofHousehold":{"FirstName": "RTNDIN", "LastName": "RTHNSAN", "PostalAddress":{ "AddressLine1": "10855 NW 45TH ST", "AddressLine2": "", "City": "CORAL SPRINGS", "State": "12", "Zip": "33065"}}, "LTSS":{ "OtherHealthIndicators": "", "SpecialCond": "SCE", "LtcProgramRecommendation": "", "ProdPlanId": "", "ProviderCode": "", "LevelOfCareDate": null, "LevelOfCareCode": "", "SpecialNeedsNote": "", "MedicaidAidCategoryCode": "MS", "MedicaidBeginDate": null, "MedicaidEndDate": null, "ACWM": "" }, "CostShareInformation": "181Y", "CommunityEngagementExemptCode": "E", "TypeOfAssistance": "ABDM", "DeductibleAmount": 12}]}}'; 
            system.assertEquals(sresponse != null,true);
            MemberAncillaryResponse_H_HUM oGetAncillaryResponse = MemberAncillaryResponse_H_HUM.parse(sresponse );
            system.assertEquals(oGetAncillaryResponse != null,true);
            
            Test.stopTest();
        }
    }    
}