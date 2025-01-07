/**
 * @File Name          : OSAServiceResponse_DTO_HUM.cls
 * @Description        : DTO class for Response from OSA Service
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 5/15/2020, 2:33:19 AM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public with sharing class OSAServiceResponse_DTO_HUM {
    
    public List<IsOSAResponse> isOSAResponse;

    public class IsOSAResponse {
        public String PlanID;
		public String OSA;
    }

    public static OSAServiceResponse_DTO_HUM parse(String json) {
        return (OSAServiceResponse_DTO_HUM) System.JSON.deserialize(json, OSAServiceResponse_DTO_HUM.class);
    }
}