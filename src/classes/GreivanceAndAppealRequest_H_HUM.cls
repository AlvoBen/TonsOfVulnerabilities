/**************************************************************************** 
Apex Class Name  : GreivanceAndAppealRequest_H_HUM 
Version          : 1.0  
Function         : Response helper for buildRequestGreivanceAndAppeal method in GrievanceAndAppeal_S_HUM 
Test Class       : GreivanceAndAppealRequest_T_HUM 

Modification Log:
* Developer Name        Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Ajay Chakradhar                             10/15/2018                 Original Version 
************************************************************************************************************************************************* */
public with sharing class GreivanceAndAppealRequest_H_HUM {

    public GetGrievanceAndAppealByCriteriaRequest getGrievanceAndAppealByCriteriaRequest;

    public class GetGrievanceAndAppealByCriteriaRequest{
        public Member member;
        public String fromDate{get;set;}
        public String thruDate{get;set;}
    }

    public class Member {
        public String externalId{get;set;}
    }

    
    public static GreivanceAndAppealRequest_H_HUM parse(String json) {
        return (GreivanceAndAppealRequest_H_HUM) System.JSON.deserialize(json, GreivanceAndAppealRequest_H_HUM.class);
    }
}