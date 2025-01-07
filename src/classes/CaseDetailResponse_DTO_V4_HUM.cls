/******************************************************************************************************************
Apex class Name    : CaseDetailResponse_DTO_V4_HUM
Version            : 1.0
Function           : This class is used as request for create case Rest Service.
Created On         : March 01 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*  1.0             Sagar                      04/14/2019        Original Version
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CaseDetailResponse_DTO_V4_HUM
{   
        public String CaseNumber{get;set;}
        public String CaseId{get;set;} 
        public boolean Success{get;set;}
        public String ErrorCode{get;set;}
        public String ErrorMessage{get;set;}
 
}