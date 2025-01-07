/**************************************************************************** 
Apex Class Name  : GroupBenefits_ResponseDTO_HUM
Version          : 1.0  
Created Date     : April 30, 2018
Function         : Response helper for Group Benefits
Test Class       : GroupBenefits_ResponseDTO_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rakesh Patni                               04/30/2016                    Original Version
***************************************************************************************************/

public with sharing class GroupBenefits_ResponseDTO_HUM
{
    public PBEResponseV2_DTO_HUM vPBEResponse{get;set;}
    public GBEResponse_DTO_HUM vGBEResponse{get;set;}
    public boolean bIsGBERes{get;set;}
    public boolean bIsPBERes{get;set;}
    public string covPolicyPlan {get;set;}
    public PBEServiceTypeV2DTO_HUM serviceDTOStr {get;set;}
}