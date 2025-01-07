/*******************************************************************************************************************************
Apex Class Name :  AuthSummary_D_HUM.cls
Version: 1.0
Created On      : 1/13/2021
Function        : Data Class for AuthSummary_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Riya Agarwal                                         01/13/2021                 REQ - 1835131 - PR00095212- MF1 -SF -Auto-Populate Custom Benefit Grid
* Suraj Patil										   05/07/2021				  DF 3003 and DF 3001 in Qtest
* Prashant Moghe									   09/22/2021				  US - 2664945 - T1PRJ0002736 - 2021 MF6 - Security  - CR AEP Readiness - Low Security Issues- Clinical and Health Resource Page
*********************************************************************************************************************************/
public with sharing class AuthSummary_D_HUM
{
    
 @testVisible public static MentorServiceRequest_DTO_HUM RequestStructureKDMService(MentorServiceRequest_DTO_HUM KDMRequestDTO)
    {  
        string sContractNum;
        string sPBPCode;
        string sMedicaresegId; 
        string sQueryText = '';
        string sProduct; 
        string sProductType;
        string sPlatform; 
        string sBenCoverage;
        string sGrpNumber;      
        if(ApexPages.currentpage().getParameters().containsKey('Id'))
        {
            String sPolMemId = ApexPages.currentpage().getParameters().get('Id');
            if(NULL!= sPolMemId)
            {
                Policy_Member__c objPolMem = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,Policy__r.Benefit_coverage__c,
                                             Policy__r.Group_Number__c,Policy__r.Medicare_Segment_ID__c From Policy_Member__c Where Id =: ApexPages.currentpage().getParameters().get('Id')];
                if(objPolMem != null)
                {   
                    sPlatform = objPolMem.Policy_Platform__c;
                    sBenCoverage = objPolMem.Policy__r.Benefit_coverage__c;
                    sContractNum = objPolMem.Policy__r.Contract_Number__c;
                    sPBPCode = objPolMem.Policy__r.PBP_Code__c;
                    sMedicaresegId = objPolMem.Policy__r.Medicare_Segment_ID__c;  
                    sGrpNumber = objPolMem.Policy__r.Group_Number__c;
                    sProduct = objPolMem.Product__c;
                    sProductType = objPolMem.Product_Type__c;
                   
                   List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c  FROM Reference_Business_Line_Product_Grouping__c 
                                                                                              Where ProductCode__c = :sProduct And  Major_LOB__c = : sProductType And Business_Description__c = :GLOBAL_CONSTANT_HUM.VOB_MEDICARE];
                    if(sProduct  == GLOBAL_CONSTANT_HUM.MED && !lstRefBusinessLineProdGroups.isEmpty())
                    {
                        sQueryText = sContractNum+HUMConstants.sHiphan+sPBPCode;
                        if(String.isNotBlank(sMedicaresegId)) sQueryText += HUMConstants.sHiphan +  sMedicaresegId;
                    }
                    else 
                    {
                        if( sPlatform == HUMConstants.sEM_Value || sPlatform == HUMConstants.sMTV_Value )
                        {
                            sQueryText = HUMConstants.SUBGROUP_MENTORREDIRECT_HUM + sBenCoverage;
                        } 
                        else if( sPlatform == HUMConstants.sLV_Value || sPlatform == HUMConstants.sCI_Value || sPlatform == HUMConstants.sCAS_Value )
                        {
                            if(String.isNotBlank(sGrpNumber))
                            {
                                sQueryText = HUMConstants.SUBGROUP_MENTORREDIRECT_HUM + sGrpNumber;
                            }
                        } 

                    }
                 }
            }
        }
        MentorServiceRequest_DTO_HUM oRequest = new MentorServiceRequest_DTO_HUM();      
        MentorServiceRequest_DTO_HUM.Request oHasSearch = new MentorServiceRequest_DTO_HUM.Request();
        oHasSearch.BenefitGridsOnly = true;
        oHasSearch.ConsumingApp = HUMConstants.HUM_PARTD_DRUGREQUESTOR;
        oHasSearch.Query = sQueryText;
        oRequest.Request = oHasSearch;
        return oRequest;
  }  
  
    public static Boolean ParseStructureKMDocument(MentorServiceResponse_DTO_HUM KMServiceResponse)
    {
        Boolean sKMDocuStatus = false;
        if (KMServiceResponse != null && KMServiceResponse.HasSearchResultsResponse != null)
        {
                if (KMServiceResponse.HasSearchResultsResponse != null)
                {
                     sKMDocuStatus = Boolean.valueOf(KMServiceResponse.HasSearchResultsResponse.HasMatches);
                }
            
        }

        return sKMDocuStatus;
    }

}