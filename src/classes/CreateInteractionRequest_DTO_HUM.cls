/******************************************************************************************************************
Apex class Name    : CreateInteractionRequest_DTO_HUM
Version            : 1.0
Function           : This class is for wrapper class for Interaction  details received from service
Created On         : March 7th 2019
Test Class         : CreateInteractionRequestWrapper_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Pallavi Ravishankar                           03/07/2019            Original Version            
*   2.0            Seema Kaulgi                                  07/04/2019            REQ - 403811 GBO Watson (Group)         
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CreateInteractionRequest_DTO_HUM
{

    public CreateInteractionRequest CreateInteractionRequest;
  
    public class CreateInteractionRequest 
    {
        public String SourceApp;
        Public String SourceIdTypeCd;
        Public Boolean RequiredCasesInResponse;
        public String InteractionOrigin;
        public String UUID;
        Public String CreatedByQueue;
        public InteractingWithAboutDTO InteractingWith;
        public List<CaseInteractingAbout> CaseInteractingAbout;
        public list<CreateCaseDetails> CreateCaseDetails{get;set;}
    }
        
    public class CaseInteractingAbout 
    {
        public String CaseIdentifier;
        public InteractingWithAboutDTO InteractingAbout;
    }   
     
    public class CreateCaseDetails 
    {
        public String CaseIdentifier;
        public CaseDetailRequest_DTO_V4_HUM CaseDetails;
    }   
     
    public class InteractingWithAboutDTO 
    {
        public String AddressLine1='';
        public String AddressLine2='';
        public String City='';
        public String DOB='';
        public String EmailID='';
        public String FirstName='';
        public String HomePhone='';
        public String ID='';
        public String IDType='';
        public String PlatformCode='';
        Public String MemberIdDependentCode='';
        public String LastName='';
        public String State='';
        public String EntityType='';
        public String ZipCode='';
        public String ZipPlus='';
        public String ProviderClassification='';
        public String AccountName='';
    } 
}