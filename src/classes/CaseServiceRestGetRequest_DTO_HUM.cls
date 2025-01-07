/******************************************************************************************************************
Apex class Name    : CaseServiceRestGetRequest_DTO_HUM
Version            : 1.0
Function           : This class is used as request for get case Rest Service  GetCaseInfoRest_D_HUM.
Created On         : March 20 2018
Test Class         : HUMCaseServiceRestTest

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran         215366             03/20/2018          Original Version
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CaseServiceRestGetRequest_DTO_HUM
{
public GetCaseInfoDTO GetCaseInfo;
    public class GetCaseInfoDTO{
        public CaseInfoRequestDTO CaseInfoRequest;
        public boolean getCaseInfo; //true
        public EntityIdentifierDTO[] IntAboutCaseAvailabilityCheck;
    }
    public class CaseInfoRequestDTO {
        public boolean AttachmentFlag;  //true
        public boolean AttachmentLogFlag;   //true
        public boolean CaseCommentFlag; //true
        public String CaseId = '';  //
        public String EndDate = ''; //3/12/2018
        public String InqForAboutMatchRule; //
        public EntityIdentifierDTO[] InquireAboutCriteria;
        public EntityIdentifierDTO[] InquireForCriteria;
        public boolean InteractionFlag; //false
        public String InteractionId = '';   // 
        public String StartDate = '';   //01/01/2000
        public boolean TaskFlag;    //false
        public String UserId = '';  // 
    }
    public class EntityIdentifierDTO{
        public String EntityType = '';  //Member
        public String IdValue = ''; //7384003072901
    }
    
}