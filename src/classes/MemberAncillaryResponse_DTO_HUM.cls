/**************************************************************************** 
Apex Class Name  : MemberAncillaryResponse_DTO_HUM 
Version          : 1.0 
Function         : This class Is wrapper class to store data from service
Created Date     :  05/31/2019
Test Class       : MemberAncillaryResponse_DTO_T_HUM

****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Manikanta                 356372            05/31/2019         Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
*  Manikanta                                   06/26/2019         REQ - 379006 Defect Fix 424421.0001
***************************************************************************************************/
public with sharing class MemberAncillaryResponse_DTO_HUM 
{
  
    Public String CostShareInformation{get;set;}
    Public String CommunityEngagementExemptCode{get;set;}
    Public String TypeOfAssistance{get;set;}
    Public Double DeductibleAmount{get;set;}
    Public String MemberIdBase{get;set;}
    Public String DependentCode{get;set;}
    Public String PlatformCode{get;set;}
    Public DateTime CoverageStartDate{get;set;}
    Public String GroupId{get;set;}
    Public String DivisionId{get;set;}
    Public String ProductId{get;set;}
    Public String Type{get;set;}
    Public String premiumValue{get;set;}
    Public String premiumStartDate{get;set;}
    Public String premiumEndDate{get;set;}
    Public String penaltyValue{get;set;}
    Public String penaltyStartDate{get;set;}
    Public String penaltyEndDate{get;set;}
    Public String suspensionValue{get;set;}
    Public String rewardsValue{get;set;}
    Public String FirstName{get;set;}
    Public String LastName{get;set;}
    Public String fullName{get;set;}
    Public String AddressLine1{get;set;}
    Public String AddressLine2{get;set;}
    Public String City{get;set;}
    Public String State{get;set;}
    Public String Zip{get;set;}
    Public String fullAddress{get;set;}
    Public String OtherHealthIndicators{get;set;}
    Public String SpecialCond{get;set;}
    Public String LtcProgramRecommendation{get;set;}
    Public String ProdPlanId{get;set;}
    Public String ProviderCode{get;set;}
    Public DateTime LevelOfCareDate{get;set;}
    Public String LevelOfCareCode{get;set;}
    Public String SpecialNeedsNote{get;set;}
    Public String MedicaidAidCategoryCode{get;set;}
    Public DateTime MedicaidBeginDate{get;set;}
    Public DateTime MedicaidEndDate{get;set;}
    Public String ACWM{get;set;}
}