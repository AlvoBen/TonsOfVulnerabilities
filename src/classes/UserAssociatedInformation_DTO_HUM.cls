/*****************************************************************************************************
 Apex Class Name  : UserAssociatedInformation_DTO_HUM 
 Version   : 1.0 
 Created Date  : 11/25/2020
 Function  : DTO to fetch User Information
 Test Class : userAssociatedInformation_LT_HUM
 
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Rahul Krishan                              11/25/2020                   Original Version
* Ashish Kumar							 	 02/08/2021				   	  Added - isDeceased
*******************************************************************************************************/

public with sharing class UserAssociatedInformation_DTO_HUM 
{
    @auraEnabled public String DocumentType{get; set;}
    @auraEnabled public Integer IndexNumber{get; set;}
    @auraEnabled public String EffectiveDate{get; set;}
    @auraEnabled public String TerminationDate{get; set;}
    @auraEnabled public String SecurityQuestion{get; set;}
    @auraEnabled public String Answer{get; set;}
    @auraEnabled public String UserValue{get; set;}    
	@auraEnabled public String Relationship{get; set;}  
    @auraEnabled public String ServiceStartDate{get; set;}  
    @auraEnabled public String ServiceEndDate{get; set;}    
    @auraEnabled public String LimitedDisclosureDetails{get; set;}  
    @auraEnabled public String LineOfCoverage{get; set;} 
    @auraEnabled public String SubmissionMehtod{get; set;}
    @auraEnabled public String LevelOfConsent{get; set;}
    @auraEnabled public String GuardianAddress{get; set;}
    @auraEnabled public String PersonofAuthority{get; set;}
	@auraEnabled public String PhoneNumber{get; set;}  
    @auraEnabled public String FirstName{get; set;}  
    @auraEnabled public String LastName{get; set;}  
    @auraEnabled public String Type{get; set;} 
    @auraEnabled public Boolean isDeceased{get; set;}
  
}