/*****************************************************************************************
Apex Class Name  : ToothServiceResponse_LH_HUM 
Version          : 1.0  
Created Date     : 04/28/2023
Function         : response helper for ToothHistory/GetToothHistory service for LWC
Test Class       : 
*****************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------
* Suraj Patil                                   05/08/2023                 US 4542585 Contact Servicing: Dental Plan - Tooth History (Surge)
******************************************************************************************************************************************************/
public class ToothServiceResponse_LH_HUM
{  
    @AuraEnabled public GetToothHistoryResponse GetToothHistoryResponse;
    public class GetToothHistoryResponse
    {
        @AuraEnabled public GetToothHistoryResult GetToothHistoryResult;
    }
    public class GetToothHistoryResult
    {
        @AuraEnabled public list<ClaimHistoryResult> ClaimHistoryResult;
        @AuraEnabled public String ErrorMessage = '';
        @AuraEnabled public String ErrorType = '';
    }
    public class ClaimHistoryResult
    {
        @AuraEnabled public String ADACode = '';
        @AuraEnabled public String ClaimNumber= '';
        @AuraEnabled public String DateOfService = '';
        @AuraEnabled public String Description = '';
        @AuraEnabled public String Status = '';
        @AuraEnabled public String SurfaceCode = '';
        @AuraEnabled public String Tooth = '';
    }
}