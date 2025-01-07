/**************************************************************************** 
Apex Class Name  : ToothHistory_L_DTO_HUM
Version          : 1.0  
Created Date     : 04/28/2023
Function         : DTO wrapper class for ToothHistory/GetToothHistory for LWC
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Suraj Patil                                   05/08/2023                 US 4542585 Contact Servicing: Dental Plan - Tooth History (Surge)
***************************************************************************************************/

public with sharing class ToothHistory_L_DTO_HUM{
    @AuraEnabled public string sDateOfService; 
    @AuraEnabled public date sDateOfServiceformatted;
    @AuraEnabled public string sTooth;
    @AuraEnabled public string sSurfaceCode;
    @AuraEnabled public string sADACode;
    @AuraEnabled public string sDescription;
    @AuraEnabled public string sClaimNumber;  
    @AuraEnabled public string sStatus;  
   }