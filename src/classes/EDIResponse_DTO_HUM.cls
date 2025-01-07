/*******************************************************************************************************************************************************
Apex Class Name  : EDIResponse_DTO_HUM
Version          : 1.0
Created Date     : 01/24/2019
Function         : This class is used as response  for EDI from PolicyMemberDetail_CE_HUM.
Test Class       : 

Modification Log :
* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Shailesh Mali                                  01/24/2019                 Original Version
************************************************************************************************************************************************/
public class EDIResponse_DTO_HUM 
{
    @AuraEnabled public GetEDIGroupInfoResponse GetEDIGroupInfoResponse;
    
    public class GetEDIGroupInfoResponse 
    {
        @AuraEnabled public String Timestamp ='';
        @AuraEnabled public String ResponseType ='';
        @AuraEnabled public String ErrorCode ='';
        @AuraEnabled public String ErrorDescription ='';
        @AuraEnabled public String RequestGuid ='';
        @AuraEnabled public List<EdiGroupDetails> EdiGroupDetails;
    }
    
    public class EdiGroupDetails
    {
		@AuraEnabled public String ErrorCode='';
		@AuraEnabled public String ErrorDescription ='';
		@AuraEnabled public String GroupId ='';
		@AuraEnabled public String EDIGroup ='';
		@AuraEnabled public String Platform ='';
		@AuraEnabled public String PCP_PCDFlag ='';
		@AuraEnabled public String Frequency ='';
		@AuraEnabled public String Day1 ='';
		@AuraEnabled public String Day2 ='';
		@AuraEnabled public String Day3 ='';
		@AuraEnabled public String Day4 ='';
	}    
}