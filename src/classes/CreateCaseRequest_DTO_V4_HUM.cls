/******************************************************************************************************************
Apex class Name    : CreateCaseRequest_DTO_V4_HUM
Version            : 1.0
Function           : This class is used as request for create case Rest Service.
Created On         : April 11 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Asish Behera
*   1.1            Asish Behera                                  06/25/2019        New request paramter added to map intercation Id
*   1.2            Suraj Patil                                   10/03/2019        REQ - 409544 allow consumer to send Int With type=Internal
*   1.3            Asish Behera                                  07/08/2020        Added case identifier to create multiple cases for one inetrcating about
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CreateCaseRequest_DTO_V4_HUM{  
    
    public String sInteractingWith{get;set;}
    public String sInteractingAbout{get;set;}
    public String sInteractingAboutType{get;set;}
    public String sInteractingWithType{get;set;}
    public String sInteractionId{get;set;}
	public String sInteractingWithName {get;set;}
    public String sCaseIdentifier;
    public CaseDetailRequest_DTO_V4_HUM caseCreateRequest;
}