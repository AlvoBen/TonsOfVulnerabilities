/**************************************************************************** 
Apex Class Name  : DocumentList_DTO_HUM
Version          : 1.0
Created Date     : 11/07/2018
Function         : DTO Class for Medhac Case comments documents services
Test Class       : DocumentList_DTO_HUM
***********************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*--------------------------------------------------------------------------------------------------
* Rajesh narasimhan                           11/07/2018                   original Version
* Swapnali Sonawane                           04/09/2021                   Req 2119511 added ExRM Fields to MHK Web Service And Additional Data for G&A Status Page in CRM 
***************************************************************************************************/
public with sharing class DocumentList_DTO_HUM
{
    public string sfileName {get; set;}
    public string sDocumentType {get; set;}
    public string sCreatedby{get; set;}
    public string sDocumentId{get; set;}
    public string internalError{get;set;}
    public string sCreateDate{get;set;}
}