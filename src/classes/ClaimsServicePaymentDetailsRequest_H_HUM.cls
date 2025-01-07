/**************************************************************************** 
Apex Class Name  : ClaimsServicePaymentDetailsRequest_H_HUM 
Version          : 1.0  
Created Date     : February 26, 2016
Function         : request helper for GetPaymentDetails method in claims service
Test Class       : ClaimsServicePaymentDetailsRequest_T_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             30282             02/26/2016                   Original Version
* Adil Mohammed             30282             06/08/2016                   Adding additional input parameters
***************************************************************************************************/
public class ClaimsServicePaymentDetailsRequest_H_HUM 
{
    public GetPaymentDetails GetPaymentDetails;
    
    public class GetPaymentDetails
    {
        public string SearchType = '';
        public string CheckEFTNumber = '';
        public string SRCNPIID = '';
        public string ClaimNbr = '';
        public string PlatformCd  = '';
        public string ProviderID  = '';
        public string PaymentMethod = '';  
    }
}