/******************************************************************************************************************
Apex class Name    : TRRServiceRestRequest_DTO_HUM
Version            : 1.0
Function           : This class is used as request for get TRR Detail using Rest Service
Created On         : Feb 28 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Seema Kaulgi                                                  	REQ - 379048  SF - Ability for all CRM users to SEARCH for member specific Transaction Reply Reports (TRR) in CRM
------------------------------------------------------------------------------------------------------------------- *
**/

public class TRRServiceRestRequest_DTO_HUM { 
  public class TRRRequestClass{
        public string MedicareId ='';
        public string FirstName ='';
        public string LastName ='';
        public string DateOfBirth ='';
        public string HumanaReceivedDateFrom ='';
        public string HumanaReceivedDateTo ='';
    } 
  public  TRRRequestClass TRRRequest;
    
}