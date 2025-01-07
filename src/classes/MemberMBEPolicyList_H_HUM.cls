/****************************************************************************
Apex Class Name  : MemberMBEPolicyList_H_HUM
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Helper Class for storing the JSON response parsed policy list values
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               10/28/2015                    Original Version
***************************************************************************************************/

public with sharing  class MemberMBEPolicyList_H_HUM{
    @AuraEnabled
    public string EffectiveDate = '';
    @AuraEnabled
    public string EndDate = '';
    @AuraEnabled
    public string MLOB = '';
    @AuraEnabled
    public string productLineCd = '';

}