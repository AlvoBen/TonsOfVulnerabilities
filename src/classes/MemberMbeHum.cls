/****************************************************************************
Apex Class Name  : MemberMbeHum
Version          : 1.0  
Created Date     : Oct 28, 2015
Function         : Helper Class to storing the JSON response parsed values
Test Class       : 
****************************************************************************
Modification Log:
*Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Apoorv Jain                               10/28/2015                    Original Version
***************************************************************************************************/



global class MemberMbeHum{

    public string sMemberGenKey {get;set;}
    public string sMyHumanaLastLogin {get;set;}
    public string sMyHumanaRegistered {get;set;}
    public string sVitalityEnrollment {get;set;}
    public string sVitalityEntityId {get;set;}
    public string sPolicyVitalityFalse {get;set;}
    public string sPolicyVitalityTrue {get;set;}
    public List<MemberMBEPolicy> lstMemberPolicies {get;set;}
}