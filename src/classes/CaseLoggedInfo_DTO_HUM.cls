/**************************************************************************************************************************
Component Name   : CaseLoggedInfo_CMP_HUM.component
Version          : 1.0
Created On       : 10/10/2017
Function         : VF Component to display Case Logged information table

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*---------------------------------------------------------------------------------------------------------------------------
* Ashsih Upadhyay                                 11/10/2017                   Original version
* Pradeepkumar Dani           149184              11/17/2017                   re-factoring
****************************************************************************************************************************/
public with sharing class CaseLoggedInfo_DTO_HUM 
{
    public String Id = '';  
    public String action = '';
    public String subType = '';
    public String createdDate = '';
    public String createdBy = '';
    public String createdByQueue = '';    
}