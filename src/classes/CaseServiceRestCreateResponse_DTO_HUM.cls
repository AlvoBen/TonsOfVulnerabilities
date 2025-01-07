/******************************************************************************************************************
Apex class Name    : CaseServiceRestCreateResponse_DTO_HUM
Version            : 1.0
Function           : This class is used as request for create case Rest Service  CreateCaseRest_D_HUM.
Created On         : March 14 2018
Test Class         : HUMCaseServiceRestTest

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*  1.0             Moshitha Gunasekaran        215366              03/14/2018          Original Version
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CaseServiceRestCreateResponse_DTO_HUM
{
public CreateCaseInfoResponseDTO CreateCaseInfoResponse;
    public class CreateCaseInfoResponseDTO {
        public String CaseNumber = '';   //1000000221165
        public boolean Success;  //true
        public String ErrorCode = '';    //
        public String ErrorMessage = ''; //
    }
}