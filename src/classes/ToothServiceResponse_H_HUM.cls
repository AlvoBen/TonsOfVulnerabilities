/*****************************************************************************************
Apex Class Name  : ToothServiceResponse_H_HUM 
Version          : 1.0  
Created Date     : January 19, 2016
Function         : response helper for ToothHistory/GetToothHistory service
Test Class       : AccumsServiceResponse_T_HUM 
*****************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj Patil                              		05/31/2021			       USER STORY 1867803 T1PRJ0003278/PR00095917 - MF01 - Tooth History Data Fields
******************************************************************************************************************************************************/
public class ToothServiceResponse_H_HUM
{  
  public GetToothHistoryResponse GetToothHistoryResponse;
  public class GetToothHistoryResponse
  {
    public GetToothHistoryResult GetToothHistoryResult;
  }
  public class GetToothHistoryResult
  {
    public list<ClaimHistoryResult> ClaimHistoryResult;
    public String ErrorMessage = '';
    public String ErrorType = '';
  }
  public class ClaimHistoryResult
  {
    public String ADACode = '';
    public String ClaimNumber= '';
    public String DateOfService = '';
    public String Description = '';
    public String Status = '';
    public String SurfaceCode = '';
    public String Tooth = '';
  }
}