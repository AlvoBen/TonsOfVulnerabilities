/******************************************************************************************************************
Apex class Name    : UpdateInteractionRequest_DTO_HUM
Version            : 1.0
Function           : This class is for wrapper class for Update Interaction  details received from service
Created On         : May 2nd, 2019
Test Class         : UpdateInteractionRequest_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Pallavi Ravishankar          354971             05/02/2019            Original Version            
------------------------------------------------------------------------------------------------------------------- */
public with sharing class UpdateInteractionRequest_DTO_HUM
{

    public UpdateInteractionRequest UpdateInteractionRequest;
  
    public class UpdateInteractionRequest 
    {
        public String SourceApp;
        public String SourceIdTypeCd;
        Public String InteractionNumber;
        public Boolean RequiredCasesInResponse;
        public String InteractionOrigin;
        Public String CreatedByQueue;
        public List<CaseInteractingAbout> CaseInteractingAbout;
        public list<CreateCaseDetails> CreateCaseDetails{get;set;}
        public list<CaseInteraction> CaseInteractions{get;set;}
    }
        
    public class CaseInteractingAbout 
    {
        public String CaseIdentifier;
        public CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO InteractingAbout;
    }   
     
    public class CreateCaseDetails 
    {
        public String CaseIdentifier;
        public CaseDetailRequest_DTO_V4_HUM CaseDetails;
    }   
     
    public class CaseInteraction
    {
      public String CaseNumber;
    }
    
    }