/**************************************************************************** 
Apex Class Name  : POA_DTO_HUM 
Version          : 1.0  
Created Date     : 10/30/2018
Function         : DTO for POA data to add in Power of attorney section.
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Suraj Patil                                   10/30/2018                  Original Version
***************************************************************************************************/
public with sharing class POA_DTO_HUM 
{
    public list<POAData> POAData;
    public class POAData{
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
}