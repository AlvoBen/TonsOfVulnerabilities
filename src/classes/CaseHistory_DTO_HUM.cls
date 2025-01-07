/*****************************************************************************************************
 Apex Class Name    :   CaseHistory_DTO_HUM 
 Version            :   1.0 
 Created Date       :   08/31/2017
 Function           :   Case History DTO
 Test Class         :   CaseHistory_DTO_HUM 
 
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Prasad Jandhyala            147508            08/31/2017                   Original Version
*******************************************************************************************************/
public with sharing class CaseHistory_DTO_HUM 
{
    public String dateCreated{get; set;}
    public String user{get; set;}
    public String userqueue{get; set;}
    public String action{get; set;}
    public String id{get;set;}
}