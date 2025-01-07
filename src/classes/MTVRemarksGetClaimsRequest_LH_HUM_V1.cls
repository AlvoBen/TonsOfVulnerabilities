/*****************************************************************************************
Apex class Name    : MTVRemarksGetClaimsRequest_LH_HUM_V1
Version            : 1.0   
Created Date       : 06/01/2022 


Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Prashant Moghe 	   	                   		06/01/2022                 Original Version
***************************************************************************************************/
public class MTVRemarksGetClaimsRequest_LH_HUM_V1 {
    public GetClaimsRemarksRequest GetClaimsRemarksRequest;
    
    public class GetClaimsRemarksRequest
    { 
        public List<ClaimNumbersList> ClaimNumbersList;         
    }
    
    public class ClaimNumbersList
    {
        public String ClaimNumber = '';          
    }
    
}