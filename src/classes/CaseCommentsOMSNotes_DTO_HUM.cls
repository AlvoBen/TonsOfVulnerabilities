/*****************************************************************************************************
 Apex Class Name  : CaseCommentsOMSNotes_DTO_HUM
 Version   : 1.0 
 Created Date  : 09/01/2020
 Function  : DTO to fetch case comments and OMS Notes data   
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* AshokKumar Nutalapati                         09/01/2020                  Original Version
*******************************************************************************************************/
public with sharing class CaseCommentsOMSNotes_DTO_HUM 
{
    public String CaseId{get;set;}
    public String CaseNumber{get;set;}
    public String CaseURL{get;set;}
    public String LogCode{get; set;}
    public String CreatedDate {get; set;}
    public String CreatedBy{get; set;}
    public String Note{get; set;}
    public String SourceType{get; set;}
}