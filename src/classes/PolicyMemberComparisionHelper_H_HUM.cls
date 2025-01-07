/*******************************************************************************************************************************
Apex Class Name : Public class PolicyMemberComparisionHelper_H_HUM 
Version         : 1.0
Created On      : 12/21/2018
Function        : This class is used to sort Policy_Member__c object records based on account name.
Test Class      : HUMPolicyMembersListControllerTest
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Santhi Mandava                                      12/21/2018                 Original Version
*******************************************************************************************************************************************************************/
Public class PolicyMemberComparisionHelper_H_HUM Implements Comparable
{
    Public Policy_Member__c objPolMember{get;set;}
    Public String sName{get;set;}
    
    Public PolicyMemberComparisionHelper_H_HUM(Policy_Member__c objPolMem)
    {
        this.objPolMember = objPolMem;
        this.sName = objPolMem.Member__r.Name;
    }
    
    public Integer compareTo(Object compareTo)
    {
        PolicyMemberComparisionHelper_H_HUM objCompareTo = (PolicyMemberComparisionHelper_H_HUM)compareTo;
        return sName.compareTo(((PolicyMemberComparisionHelper_H_HUM)compareTo).sName);
    }
}