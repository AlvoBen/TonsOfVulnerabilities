/**************************************************************************** 
Apex Class Name  : MemberAncillaryRequest_H_T_HUM
Version          : 1.0  
Created Date     : 05/31/2019
Function         : Test class for  MemberAncillaryRequest_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Manikanta                356372             05/31/2019         Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
*  Manikanta                                   06/07/2019         REQ - 379006 Made code changes to align with the DP changes
***************************************************************************************************/
@isTest
private Class MemberAncillaryRequest_H_T_HUM
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
     * Method Name : testMedicaidAncillaryRequest
     * Description : Test method for MemberAncillaryRequest_H_HUM
     * Return type : void
     * Paramater   : NA
     */
    static testMethod void testMedicaidAncillaryRequest()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
           Test.StartTest();
         
           List<String> IEntityTypeList = new List<String>();
           IEntityTypeList.add('Premium');
           MemberAncillaryRequest_H_HUM oAncillaryReq= new MemberAncillaryRequest_H_HUM();
           MemberAncillaryRequest_H_HUM.getMemberAncillary ogetmember = new MemberAncillaryRequest_H_HUM.getMemberAncillary();                             
           MemberAncillaryRequest_H_HUM.includes oIncludes = new MemberAncillaryRequest_H_HUM.includes();
           MemberAncillaryRequest_H_HUM.IAncillaryEntity oIAncillaryEntity   = new  MemberAncillaryRequest_H_HUM.IAncillaryEntity();           
           
           MemberAncillaryRequest_H_HUM.ancillaryRequest ancillaryRequest = new MemberAncillaryRequest_H_HUM.AncillaryRequest();
           List<MemberAncillaryRequest_H_HUM.AncillaryRequest> AncillaryReqList = new  List<MemberAncillaryRequest_H_HUM.AncillaryRequest>();
           MemberAncillaryRequest_H_HUM.CoverageIdentifier CoverageIdentifier = new MemberAncillaryRequest_H_HUM.CoverageIdentifier();               
           CoverageIdentifier.MemberIdBase = 'H66692336';
           CoverageIdentifier.DependentCode = '00';
           CoverageIdentifier.PlatformCode = 'LV';
           CoverageIdentifier.CoverageStartDate = '12/01/2015';
           CoverageIdentifier.GroupId = 'N7474';
           CoverageIdentifier.DivisionId =  'N7474001';
           CoverageIdentifier.ProductId = 'COSHW613';
           ancillaryRequest.SequenceId = 1;
           ancillaryRequest.CoverageIdentifier = CoverageIdentifier; 
           AncillaryReqList.add(ancillaryRequest); 
           
           oIAncillaryEntity.ICoverageIdentifier = true;
           oIAncillaryEntity.ICostShareInformation = true;
           oIAncillaryEntity.ICommunityEngagementExemptCode = true;
           oIAncillaryEntity.ITypeOfAssistance = true;
           oIAncillaryEntity.IDeductibleAmount = true;
           oIAncillaryEntity.IEntityTypeList = IEntityTypeList;
           oIAncillaryEntity.IHeadofHousehold = true;
           oIAncillaryEntity.ILTSS = true;
           oIncludes.IAncillaryEntity = oIAncillaryEntity;
                     
           ogetmember.ancillaryRequest = AncillaryReqList;
           ogetmember.includes = oIncludes;
           oAncillaryReq.getMemberAncillary = ogetmember;
           system.assert(oAncillaryReq!= null);
           
           Test.StopTest();
        }
    }  
}