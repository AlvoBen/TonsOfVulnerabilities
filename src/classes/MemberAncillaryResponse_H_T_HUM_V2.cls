/**************************************************************************** 
Apex Class Name  : MemberAncillaryResponse_H_T_HUM_V2 
Version          : 1.0  
Created Date     : 03/01/2024
Function         : Test class for MemberAncillaryResponse_H_HUM_V2
****************************************************************************
Modification Log:
* Developer Name           Date                       Description
*---------------------------------------------------------------------------------------------------
* Tharun Madishetti		03/01/2024					Original Version -Ancillary service test methods	
***************************************************************************************************/
@isTest
private Class MemberAncillaryResponse_H_T_HUM_V2 
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
          
            MemberAncillaryResponse_H_HUM_V2 oResponse = new MemberAncillaryResponse_H_HUM_V2();
            MemberAncillaryResponse_H_HUM_V2.AncillaryEntityList entityList = new MemberAncillaryResponse_H_HUM_V2.AncillaryEntityList();
            MemberAncillaryResponse_H_HUM_V2.CaseHead caseHead = new MemberAncillaryResponse_H_HUM_V2.CaseHead();
            MemberAncillaryResponse_H_HUM_V2.ChishlmCase chCase = new MemberAncillaryResponse_H_HUM_V2.ChishlmCase();
            MemberAncillaryResponse_H_HUM_V2.ChoiseReasonCd choiseCd = new MemberAncillaryResponse_H_HUM_V2.ChoiseReasonCd();
            MemberAncillaryResponse_H_HUM_V2.CoordinatedSystemOfCare csoc = new MemberAncillaryResponse_H_HUM_V2.CoordinatedSystemOfCare();
            MemberAncillaryResponse_H_HUM_V2.CustodyDetails cusDet = new MemberAncillaryResponse_H_HUM_V2.CustodyDetails();
            MemberAncillaryResponse_H_HUM_V2.DepartmentOfCorrection dCorr = new MemberAncillaryResponse_H_HUM_V2.DepartmentOfCorrection();
            MemberAncillaryResponse_H_HUM_V2.HeadOfHousehold hoh = new MemberAncillaryResponse_H_HUM_V2.HeadOfHousehold();
            MemberAncillaryResponse_H_HUM_V2.LTSS ltss = new MemberAncillaryResponse_H_HUM_V2.LTSS();
            MemberAncillaryResponse_H_HUM_V2.MedicaidManagedCareDetails MMCd = new MemberAncillaryResponse_H_HUM_V2.MedicaidManagedCareDetails();
            MemberAncillaryResponse_H_HUM_V2.ParentInfoIdentifier parentII = new MemberAncillaryResponse_H_HUM_V2.ParentInfoIdentifier();
            MemberAncillaryResponse_H_HUM_V2.PenaltyReasonIndicator PRI = new MemberAncillaryResponse_H_HUM_V2.PenaltyReasonIndicator();
            MemberAncillaryResponse_H_HUM_V2.postalAddress postAddr = new MemberAncillaryResponse_H_HUM_V2.postalAddress();
            MemberAncillaryResponse_H_HUM_V2.PregnancyInfo PregInfo = new MemberAncillaryResponse_H_HUM_V2.PregnancyInfo();
            MemberAncillaryResponse_H_HUM_V2.Premium premium = new MemberAncillaryResponse_H_HUM_V2.Premium();
            MemberAncillaryResponse_H_HUM_V2.ReviewDetails revDetails = new MemberAncillaryResponse_H_HUM_V2.ReviewDetails();
            MemberAncillaryResponse_H_HUM_V2.RiseDetails riseDet = new MemberAncillaryResponse_H_HUM_V2.RiseDetails();
            system.assert(oResponse != null);
            system.assertEquals(oResponse.getMemberAncillaryResponse,null);
            
            Test.stopTest();
        }
    }    
}