/**************************************************************************** 
Apex Class Name  : ToothHistoryServiceRequest_LH_HUM
Version          : 1.0  
Created Date     : 04/28/2023
Function         : Request helper for class for ToothHistory/GetToothHistory service for LWC
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Suraj Patil                                   05/08/2023               US 4542585 Contact Servicing: Dental Plan - Tooth History (Surge)
***************************************************************************************************/
public class ToothHistoryServiceRequest_LH_HUM
{
    @AuraEnabled
    public GetToothHistory GetToothHistory;
    public class GetToothHistory
    {
        @AuraEnabled public string MemberId = '';
        @AuraEnabled public string MemberDependentCode = '';
    }
}