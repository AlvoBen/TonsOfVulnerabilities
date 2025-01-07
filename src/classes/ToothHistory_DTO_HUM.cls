/**************************************************************************** 
Apex Class Name  : ToothHistory_DTO_HUM
Version          : 1.0  
Created Date     : April 28, 20121
Function         : DTO wrapper class for ToothHistory/GetToothHistory service
Test Class       : AccumsServiceRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Suraj Patil                              		05/31/2021			       USER STORY 1867803 T1PRJ0003278/PR00095917 - MF01 - Tooth History Data Fields
***************************************************************************************************/

public with sharing class ToothHistory_DTO_HUM{
 public string sDateOfService; 
 public string sTooth;
 public string sSurfaceCode;
 public string sADACode;
 public string sDescription;
 public string sClaimNumber;  
 public string sStatus;  
}