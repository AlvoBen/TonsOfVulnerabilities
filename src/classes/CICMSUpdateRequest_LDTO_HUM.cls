/******************************************************************************************************************
Apex class Name    : CICMSUpdateRequest_LDTO_HUM
Version            : 1.0
Function           : Request DTO for CICMS Update
Created On         : July 14 2023
Test Class         : CICMS_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0              Anuradha Gajbhe                        	       07/14/2023          US: 4325820: RCC Auto create Case for State ID Cards: Ability to automatically create a case when a State ID Card Request is successfully submitted.(Lightning)
------------------------------------------------------------------------------------------------------------------*/
public with sharing class CICMSUpdateRequest_LDTO_HUM  
{
	public demographicUpdateRequestDetail demographicUpdateRequest;
    
    public class demographicUpdateRequestDetail
	{
		public CommonFieldsDetail CommonFields;
        public MemberFieldsDetail MemberFields;
	}
    
       
	public class CommonFieldsDetail 
	{
        public String MedicaidID = '';		
		public String IssueStateCode = '';	
		public String SourceCode = '';
	}	
    
    public class MemberFieldsDetail 
	{
        public String StateIdCardChgInd = '';	
		public String StateIdCardReqstInd = '';
        public String IdCardReqstReason = '';
	}
}