/*****************************************************************************************************
 Apex Class Name  : CaseComment_DTO_HUM
 Version   : 1.0 
 Created Date  : 07/23/2017
 Function  : DTO to fetch case comments related to cases
 Test Class  :   CaseCommentsDataTable_T_HUM
 
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Prasad Jandhyala          149239            08/31/2017                   Original Version
*******************************************************************************************************/
public with sharing class CaseComment_DTO_HUM 
{
    public String CreatedDate {get; set;}
    public String CreatedBy{get; set;}
    public String CreatedByQueue {get; set;}
    public String LastModified {get; set;}
    public String LastModifiedDate {get; set;}
    public String LastModifiedByQueue {get; set;}
    public String LogCode{get; set;}
    public String Comment{get; set;}
    public String CommentFull{get; set;}
    public String Id{get;set;}
    public String recId{get;set;}
    public String editDel{get;set;}
}