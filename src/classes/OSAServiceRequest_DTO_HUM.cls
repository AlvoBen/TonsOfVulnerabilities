/**
 * @File Name          : OSAServiceRequest_DTO_HUM.cls
 * @Description        : DTO class for Request for OSA Service
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/15/2020, 2:32:50 AM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public with sharing class OSAServiceRequest_DTO_HUM {
    
    public IsOSARequest isOSARequest;

    public class IsOSARequest {
        public List<String> PlanID;
        public String FipsCode;
        public String ZipCode;
    }

    public static OSAServiceRequest_DTO_HUM parse(String json) {
        return (OSAServiceRequest_DTO_HUM) System.JSON.deserialize(json, OSAServiceRequest_DTO_HUM.class);
    }
}