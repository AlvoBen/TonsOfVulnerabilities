/*****************************************************************************************************
 Apex Class Name  : UserAssociatedInfoCoach_DTO_HUM
 Version   : 1.0 
 Created Date  : 05/22/2021
 Function  : DTO to fetch User Information
 Test Class : UserAssociatedInfoCoach_T_HUM
 
Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Prudhvi Pamarthi                                       05/22/2021                 Original version
******************************************************************************************************************************************************************************/

public with sharing class UserAssociatedInfoCoach_DTO_HUM
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