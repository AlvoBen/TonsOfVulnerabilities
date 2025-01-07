/******************************************************************************************************************
Apex class Name    : DesigneeHubService_Request_DTO
Version            : 1.0
Function           : Request DTO for DESIGNEEHUBSERVICE
Created On         : 10/16/2018
Test Class         : DesineeHubService_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Vandana Chaudhari            299668            10/16/2018            Original Version 
------------------------------------------------------------------------------------------------------------------*/

public with sharing class DesigneeHubService_Request_DTO {
    public GetPHIConsentRequest GetPHIConsentRequest;
    
    public class GetPHIConsentRequest{
        public String EnterpriseId='';
        public String IncludeCIPOA='';
        public List<POAInfo> POAInfo ;
        public String UserId='';
        public String RequestedDateTime='';    
    }
    
    public class POAInfo{
        public String MemberCardID='';
        public String GroupID='';
        public String BenefitSequenceNumber='';
    }
    
}