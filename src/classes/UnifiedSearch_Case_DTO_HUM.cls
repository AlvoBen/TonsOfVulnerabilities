/************************************************************************************************************************************************
  Apex Class Name  : UnifiedSearch_Case_DTO_HUM
  Version          : 1.0 
  Created Date     : July 16 2021
  Function         : DTO class for the message JSON to be passed from Case Detail pages to Coveo Unified search screen.
  Test Class       : UnifiedSearch_Case_DTO_T_HUM
  * Version                   Developer Name            Code Review                 Date                       Description
 ------------------------------------------------------------------------------------------------------------------------------
  * 1.0                       Mohammed Noor                                       07/16/2021                  Original Version  - created for US1801197
 *****************************************************************************************************************************************************************************/
public with sharing class UnifiedSearch_Case_DTO_HUM 
{
    public UnifiedSearch_Case_DTO_HUM() {

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