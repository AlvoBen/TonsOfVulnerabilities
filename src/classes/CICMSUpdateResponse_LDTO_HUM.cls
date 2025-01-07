/******************************************************************************************************************
Apex class Name    : CICMSUpdateResponse_LDTO_HUM
Version            : 1.0
Function           : Response DTO for CICMS Update
Created On         : July 14 2023
Test Class         : CICMS_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0               Anuradha Gajbhe                          	    07/14/2023          US: 4325820: RCC Auto create Case for State ID Cards: Ability to automatically create a case when a State ID Card Request is successfully submitted.(Lightning)
------------------------------------------------------------------------------------------------------------------*/
public with sharing class CICMSUpdateResponse_LDTO_HUM  
{
	@AuraEnabled public demographicUpdateResponseDetail demographicUpdateResponse;
    
    public class demographicUpdateResponseDetail
	{
		@auraEnabled public String MedicaidID = '';	
		@AuraEnabled public String SSN = '';	
		@AuraEnabled public String MedicareID = '';	
		@AuraEnabled public String UMID = '';	
		@AuraEnabled public integer PID;	
		@AuraEnabled public String IssueStateCode = '';	
		@AuraEnabled public String ProdPlanID = '';	
		@AuraEnabled public String SourceCode = '';
		@AuraEnabled public String StateIdCardChgInd = '';	
		@AuraEnabled public String StateIdCardReqstInd = '';
        @AuraEnabled public String IdCardReqstReason = '';
        @AuraEnabled public String StateIdCardRespMsg = '';
        @AuraEnabled public ResponseStatusDetail ResponseStatus;
	}
    	   
    public class ResponseStatusDetail 
	{
        @AuraEnabled public String StatusCode = '';	
		@AuraEnabled public String StatusType = '';
        @AuraEnabled public String MsgDescription = '';
	}
}