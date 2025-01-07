/*****************************************************************************************************
 Apex Class Name  : UserInformation_DTO_HUM 
 Version   : 1.0 
 Created Date  : 06/11/2018
 Function  : DTO to fetch User Information
 
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Vandana Chaudhari                           06/11/2018                   Original Version
* Suraj patil                299668           10/22/2018                   REQ - 350627
* Suraj patil                306028           10/30/2018              		REQ - 350626 - Power of attorney display from designee service.
*******************************************************************************************************/
public with sharing class UserInformation_DTO_HUM 
{
    public String DocumentType{get; set;}
    public Integer IndexNumber{get; set;}
    public String EffectiveDate{get; set;}
    public String TerminationDate{get; set;}
    public String SecurityQuestion{get; set;}
    public String Answer{get; set;}
    public String UserValue{get; set;}    
	public String Relationship{get; set;}  
    public String ServiceStartDate{get; set;}  
    public String ServiceEndDate{get; set;}    
    public String LimitedDisclosureDetails{get; set;}  
    public String LineOfCoverage{get; set;} 
    public String SubmissionMehtod{get; set;}
    public String LevelOfConsent{get; set;}
    public String GuardianAddress{get; set;}
    public String PersonofAuthority{get; set;}
	public String PhoneNumber{get; set;}  
    public String FirstName{get; set;}  
    public String LastName{get; set;}  
    public String Type{get; set;}  
}