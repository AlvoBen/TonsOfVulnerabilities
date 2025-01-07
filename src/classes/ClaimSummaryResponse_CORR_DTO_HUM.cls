/**
   @Author      : Rakesh Patni
   @name        : ClaimSummaryResponse_CORR_DTO_HUM
   @CreateDate  : 12/10/2018
   @Description : DTO response class for Claim Summary in Related Information
   @Version     : 1.0
   @reference   : CCPGCPDataTableExtension_C_HUM
   
   Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Rakesh Patni             319302                      12/10/2018                     Correspondence Claim Number     
********************************************************************************************************************************/
public with sharing class ClaimSummaryResponse_CORR_DTO_HUM
{  
    public List<ClaimsSummary_Wrapper_DTO_HUM> claimsResp;
    public string polMemId;
}