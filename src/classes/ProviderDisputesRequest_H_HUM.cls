/**************************************************************************** 
Apex Class Name  : ProviderDisputesRequest_H_HUM 
Version          : 1.0  
Function         : Request helper for buildRequestProviderDispute method in ProviderDisputes_S_HUM 
Test Class       : ProviderDisputesRequest_T_HUM 

Modification Log:
* Developer Name        Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ajay Chakradhar                             10/15/2019                 Original Version 
************************************************************************************************************************************************* */
public class ProviderDisputesRequest_H_HUM {

    public GetProviderDisputesByCriteria getProviderDisputesByCriteria;

    public class GetProviderDisputesByCriteria{
        public Member member;
        public String fromDate{get;set;}
        public String thruDate{get;set;}
    }

    public class Member {
        public String externalId{get;set;}
    }

    
    public static ProviderDisputesRequest_H_HUM parse(String json) {
        return (ProviderDisputesRequest_H_HUM) System.JSON.deserialize(json, ProviderDisputesRequest_H_HUM.class);
    }
}