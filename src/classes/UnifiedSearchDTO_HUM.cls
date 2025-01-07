/******************************************************************************************************************
Apex class Name    : UnifiedSearchDTO_HUM
Version            : 1.0
Function           : DTO for  Unified Search DTO
Created On         : 01/15/2021

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Yogesh Gupta                                    01/13/2021          Original Version - V_1.0
------------------------------------------------------------------------------------------------------------------*/
public with sharing class UnifiedSearchDTO_HUM{
    public UnifiedSearchDTO_HUM(){
        
    }
    public string searchParams {get; set;}
    public string coveoTabName {get; set;}
    public InteractionDetails_DTO InteractionDetails {get; set;}
    public Context_DTO context {get; set;}

    public class InteractionDetails_DTO
    {
        public string intWithType {get; set;}
        public string intWith {get; set;}	
        public string CaseNumber {get; set;}	
    }
    
    public class Context_DTO
    {
        public string MajorLOB {get; set;}  //productType
        public string MinorLOB {get; set;}  //Product
        public string IntrtWithType {get; set;} //Interacting With Type
        public string IntrtAbtType {get; set;}  //Interacting About Type
        public string Market {get; set;} //Market
        public string CRMPageName {get; set;}
        public string CaseNumber {get; set;}
        public string IntrNumber {get; set;}                
    } 
}