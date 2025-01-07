/**************************************************************************** 
Apex Class Name  : RTIPrintSummary_DTO_HUM
Version          : 1.0
Created Date     : June 21st, 2017
Function         : DTO for GetInteractionDetails method in RTI Print View Link service
Test Class       : RTIPrintWrapper_T_HUM
***********************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Shivam Anand               106733            06/21/2017                   Original Version
* Imran                                        21/02/2020                   REQ-436960 RTI VAT changes
*  Imran                                       13/03/2020                   Req#436960 RTI VAT Changes Logo Issue
* Prasanthi Kandula                            04/16/2020                   US 1003374 - To display send link on RTI Detail page     
***************************************************************************************************/

public with sharing class RTIPrintSummary_DTO_HUM
{ 
    Public string sdocumentViewUrl {Get;Set;}  
    public string sdocumentType {get; set;}
}