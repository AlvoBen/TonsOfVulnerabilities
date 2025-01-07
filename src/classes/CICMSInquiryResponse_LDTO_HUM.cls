/******************************************************************************************************************
Apex class Name    : CICMSInquiryResponse_LDTO_HUM
Version            : 1.0
Function           : Response DTO for CICMS Inquiry
Created On         : July 05 2023
Test Class         : CICMS_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0               Vishal Shinde                                   07/14/2023         User Story 3891752: CRM to populate Previous State ID Card Requests (Lightning)                           	
------------------------------------------------------------------------------------------------------------------*/
public with sharing class CICMSInquiryResponse_LDTO_HUM  
{
	public demographicInquiryResponseDetail demographicInquiryResponse;
    
    public class demographicInquiryResponseDetail
	{
		public String MedicaidID = '';	
		public String SSN = '';	
		public String MedicareID = '';	
		public String UMID = '';	
		public integer PID;	
		public String IssueStateCode = '';	
		public String ProdPlanID = '';	
		public String SourceCode = '';
		public String ReferenceDate = '';	
		public String StateIdCardReqstInd = '';
        public String StateIdCardSourceSysReqstDate = '';
        public String StateIdCardStateRptDate = '';
        public String StateIdCardRespMsg = '';
        public ResponseStatusDetail ResponseStatus;
	}
    	   
    public class ResponseStatusDetail 
	{
        public String StatusCode = '';	
		public String StatusType = '';
        public String MsgDescription = '';
	}
}