/**************************************************************************** 
Apex Class Name  : ToothHistoryServiceRequest_H_HUM
Version          : 1.0  
Created Date     : 05/31/2021
Function         : Request helper for class for ToothHistory/GetToothHistory service
Test Class       : AccumsServiceRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Suraj Patil                              		05/31/2021			       USER STORY 1867803 T1PRJ0003278/PR00095917 - MF01 - Tooth History Data Fields
***************************************************************************************************/
public class ToothHistoryServiceRequest_H_HUM
{
  public GetToothHistory GetToothHistory;
  public class GetToothHistory
  {
    public string MemberId;
    public string MemberDependentCode;
  }
}