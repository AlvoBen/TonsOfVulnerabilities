/******************************************************************************************************************
Apex class Name    : CreateCaseRequest_V4_HUM
Version            : 1.0
Function           : This class is used as request for create case Rest Service.
Created On         : July 11 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Asish Behera

------------------------------------------------------------------------------------------------------------------- */
public class CreateCaseRequest_V4_HUM {
    
    
    public CreateCaseRequestDTO CreateCaseRequest;
    public class CreateCaseRequestDTO{
        public CaseDetailRequest_DTO_V4_HUM CaseDetails;
    	public InteractingAccount_DTO_HUM InteractingWith;
    	public InteractingAccount_DTO_HUM InteractingAbout;
        public String SourceKey;
    	Public String SourceApp;
    }
    
    
    
}