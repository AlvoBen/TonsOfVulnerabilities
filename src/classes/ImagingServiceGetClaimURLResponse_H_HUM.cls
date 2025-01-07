/**************************************************************************** 
Apex Class Name  : ImagingServiceGetClaimURLResponse_H_HUM 
Version          : 1.0  
Created Date     : February 16,2016
Function         : response helper for GetClaimURL method in Imaging Service
Test Class       : ImagingServiceGetClaimURLResponse_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             30118             02/16/2016                   Original Version
* Prasanthi Kandula         211792            03/30/2018                   REQ - 301521 - Updated code to incorporate changes done for Imaging Service V2
* Vijaya Koriprolu                            06/25/2018                   REQ - 366091 - changed Data Type for Repriced field
***************************************************************************************************/
public class ImagingServiceGetClaimURLResponse_H_HUM 
{
    
    public GetClaimURLsResponse GetClaimURLsResponse;
    
    public class GetClaimURLsResponse
    {
        public list<ClaimURLs> ClaimURLs;
    }
    
    public class ClaimURLs
    {
        public String DCN ='';
        public String ECN = '';
        public String ReceiptDate;
        public String Repriced;
        public Decimal TotalBilledCharges;
        public String BillingProviderLastFacilityName = '';
        public String BillingProviderId = '';
        public String RenderingProviderLastFacilityName = '';
        public String RenderingProviderId = '';
        public String ClaimFrequencyCode = '';
        public String URL = '';
    }
}