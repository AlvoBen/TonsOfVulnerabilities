/*******************************************************************************************************************************************************
Apex Class Name  : ClaimsPaymentDetailsRequest_LH_HUM
Version          : 1.0
Created Date     : 09/10/2022
Function         : This class is request class for Claim Payment details data from service
Test Class       : 
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj Patil 				                       09/10/2022                 Original Version
************************************************************************************************************************************************/

public class ClaimsPaymentDetailsRequest_LH_HUM 
{
    @auraEnabled public GetPaymentDetails GetPaymentDetails;
    
    public class GetPaymentDetails
    {
        @auraEnabled public string SearchType = '';
        @auraEnabled public string CheckEFTNumber = '';
        @auraEnabled public string SRCNPIID = '';
        @auraEnabled public string ClaimNbr = '';
        @auraEnabled public string PlatformCd  = '';
        @auraEnabled public string ProviderID  = '';
        @auraEnabled public string PaymentMethod = '';  
    }
}