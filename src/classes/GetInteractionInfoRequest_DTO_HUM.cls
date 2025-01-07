/******************************************************************************************************************
Apex class Name    : GetInteractionInfoRequest_DTO_HUM
Version            : 1.0
Function           : Test class is Response DTO for GetInteractionInfo method of Case Service.
Created On         : July 01 2019
Test Class         : GetInteractionInfoRest_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Mohammed Noor                                  07/01/2019        Original Version
*   1.1            Mohammed Noor                                  07/26/2019        Remove Interacting About criteria from request DTO
* ------------------------------------------------------------------------------------------------------------------- */

public class GetInteractionInfoRequest_DTO_HUM {
    
  public GetInteractionInfoRequestDTO GetInteractionInfo;
  
    public class GetInteractionInfoRequestDTO { 
        public string SourceKey = '';
        public string SourceApp = '';
        public list<string> InteractionNumber;
        public EntityIdentifierDTO[] InteractingWithCriteria;
        public string StartDate = '';
        public string EndDate ='';
        public string StartPaging='';
        public list<string> InclusionCriteria;        
    }     
}