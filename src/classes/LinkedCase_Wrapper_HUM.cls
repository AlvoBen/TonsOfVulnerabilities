/******************************************************************************************************************************
Apex Class Name  : LinkedCase_Wrapper_HUM
Version          : 
Created Date     : 09/10/2022
Function         : Wrapper Class to handle display linked case section on Case detail page.                   
Test Class       : 

Modification Log :
 *    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 * Dinesh Subramaniyan					11/04/2022				Initial Version
  *********************************************************************************************************************************/
public with sharing class LinkedCase_Wrapper_HUM 
{
    public String sCaseId {get; set;} 
    public String sCaseNumber {get; set;}
    public string sCaseCreatedByQueue{get; set;}
    public String sCreatedByName {get; set;}  
    public String sCaseCreatedByName {get; set;}
    public String sCaseIntent {get; set;}
    public String dCreatedDate {get; set;}
    public String sWorkQueueName{get; set;}
    public String sCaseStatus {get; set;}  
    public String sCaseClassification {get; set;}
    public String sCaseMasterId {get; set;}  
    public Boolean sAction {get; set;}
    public  String sCaseMasterCaseNumber{get;set;} 
        public LinkedCase_Wrapper_HUM()
    {
    }

    

}