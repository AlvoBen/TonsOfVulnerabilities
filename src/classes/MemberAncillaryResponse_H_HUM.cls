/*****************************************************************************************
Apex class Name    : MemberAncillaryResponse_H_HUM
Version            : 1.0
Function           : DataLayer Class for the Member Ancillary response
Created Date       :  05/31/2019
Test Class         : MemberAncillaryResponse_H_T_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
*  Manikanta                      356372                05/31/2019         Original Version - REQ - 379006 Abitlity to Display Medicaid information from CI in CRM Service
*  Manikanta                                            06/07/2019         REQ - 379006 Made code changes to align with the DP changes
*********************************************************************************************************************************/

Public with sharing class MemberAncillaryResponse_H_HUM
{

    Public static MemberAncillaryResponse_H_HUM parse(String response)
    {
       return (MemberAncillaryResponse_H_HUM)System.JSON.deserialize(response, MemberAncillaryResponse_H_HUM.class);
    }
    
    Public getMemberAncillaryResponse getMemberAncillaryResponse;
    
    Public class getMemberAncillaryResponse
    {
        Public List<AncillaryEntityList>  AncillaryEntityList;
    }
    
    Public class AncillaryEntityList
    {
        Public CoverageIdentifier CoverageIdentifier;
        Public List<EntityTypeList> EntityTypeList;
        Public HeadofHousehold HeadofHousehold;
        Public LTSS LTSS;
        Public String CostShareInformation;
        Public String CommunityEngagementExemptCode;
        Public String TypeOfAssistance;
        Public Double DeductibleAmount;
    }
    
    Public class CoverageIdentifier
    {
        Public String MemberIdBase;
        Public String DependentCode;
        Public String PlatformCode;
        Public DateTime CoverageStartDate;
        Public String GroupId;
        Public String DivisionId;
        Public String ProductId;
    }
    
   
    Public class EntityTypeList
    {
        Public String Type;
        Public String Value;
        Public DateTime StartDate;
        Public DateTime EndDate;
    }
    
    Public class HeadofHousehold
    {
        Public String FirstName;
        Public String LastName;
        Public PostalAddress PostalAddress;
    }
    
    Public class PostalAddress
    {
        Public String AddressLine1;
        Public String AddressLine2;
        Public String City;
        Public String State;
        Public String Zip;
    }
    
    Public class LTSS
    {
        Public String OtherHealthIndicators;
        Public String SpecialCond;
        Public String LtcProgramRecommendation;
        Public String ProdPlanId;
        Public String ProviderCode;
        Public DateTime LevelOfCareDate;
        Public String LevelOfCareCode;
        Public String SpecialNeedsNote;
        Public String MedicaidAidCategoryCode;
        Public DateTime MedicaidBeginDate;
        Public DateTime MedicaidEndDate;
        Public String ACWM;
    }
}