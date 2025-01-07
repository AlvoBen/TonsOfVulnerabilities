/*****************************************************************************************
Apex class Name    : MTVRemarksGetClaimsRequest_H_HUM_V1
Version            : 1.0   
Created Date       : 10/20/2021 


Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
    Apurva Urkude                                      10/20/2021          Initial version  REQ - 2132792
 *********************************************************************************************************************************/
 
public class MTVRemarksGetClaimsRequest_H_HUM_V1 {
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