/****************************************************************************
Apex Class Name  : MemberMbe_H_HUM
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Helper Class for storing  the JSON response parsed values
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               10/28/2015                    Original Version
* Prudhvi Pamarthi                          05/30/2019                    REQ-396330 Fix for CA Ticket - 8172941 -Go365 link does Not show the first time the policy tab Is opened
* Ritik Agarwal                            05/25/2021                     Add AuraEnabeld in front of varibales for lightning
***************************************************************************************************/



public with sharing class MemberMbe_H_HUM{
    @AuraEnabled 
    public string sVitalityFlagValue ='';
    @AuraEnabled
    public string VitalityEntityID = '';
    @AuraEnabled
    public string PolicyVitalityTrue = '';
    @AuraEnabled
    public string PolicyVitalityFalse = '';
    @AuraEnabled
    public string MemberGenKey = ''; 
    @AuraEnabled
    public string LastLoginDateTime= '';
    @AuraEnabled
    public string IsWebRegistered= '';
    @AuraEnabled
    public string ErrorMemberWebActivity='';
    @AuraEnabled
    public string sError='';
    @AuraEnabled
    public List<MemberMBEPolicyList_H_HUM> ListPoliciesWithVitalityProg;
    @AuraEnabled
    public Boolean sVitalityUpdated = false;  
    @AuraEnabled
    public String humanaLink='';
    @AuraEnabled
    public String webEmulateLink='';
}