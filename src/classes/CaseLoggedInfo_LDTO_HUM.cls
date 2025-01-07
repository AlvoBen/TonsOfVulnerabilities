/*************************************************************************************************************************
Component Name   : CaseLoggedInfo_DTO_LC_HUM.component
Version          : 1.0
Created On       : 06/08/2022
Function         : To display Case Logged information table

Modification Log: 
* Developer Name           Code Review                 Date                         Description
*---------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi                                06/08/2022                   Original version
* Prasuna Pattabhi                                08/04/2022                   Added field to display the user as link
****************************************************************************************************************************/

public with sharing class CaseLoggedInfo_LDTO_HUM {

    public String Id = '';  
    public String action = '';
    public String subType = '';
    public String createdDate = '';
    public String createdBy = '';
    public String createdByQueue = ''; 
    public String sourceSystem = '';
    public String userId = '';
}