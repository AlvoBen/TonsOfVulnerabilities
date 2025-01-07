/******************************************************************************************************************
Apex class Name    : CaseServiceRestUpdateResponse_DTO_HUM
Version            : 1.0
Function           : This class is used as request for update case Rest Service UpdateCaseInfoRest_D_HUM.
Created On         : March 21 2018
Test Class         : HUMCaseServiceRestTest

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*  1.0             Moshitha Gunasekaran         215366              03/21/2018          Original Version
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CaseServiceRestUpdateResponse_DTO_HUM
{
    public UpdateCaseInfoResponseDTO UpdateCaseInfoResponse;
    public class UpdateCaseInfoResponseDTO {

        public Boolean Status {get; set; }
        public StatusCode ErrorCode {get; set;}
        public String ErrorMessage {get; set;}
        
        public UpdateCaseInfoResponseDTO (Boolean eStatus, StatusCode eCode, String eMessage)
        {
            Status = eStatus;
            ErrorCode = eCode;
            ErrorMessage = eMessage;
        }
    }
}