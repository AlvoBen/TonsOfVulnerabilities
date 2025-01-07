/*******************************************************************************************************************************
Apex Class Name : VerifyDemographicGetResponse_DTO_HUM 
Version         : 1.0
Created On      : 07/29/2020
Function        : Helper class used to frame response for Verify Demographic GetRequest service
Test Class      : VerifyDemographicGetResponse_DTO_T_HUM 

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                      07/29/2020                  REQ - 1065536 : Original Version
*******************************************************************************************************************************/
public class VerifyDemographicGetResponse_DTO_HUM {
    public ResponseHeader ResponseHeader;
    
    public class ResponseHeader {
        public String RequestId;
        public String SessionId;
        public String ResponseDuration;
        public String CoreApiHttpStatusCode;
    }
    
    public GetverificationstatusResponse getverificationstatusResponse;

    public class GetverificationstatusResponse {
        public Demographics demographics;
    }
    
    public class Demographics {
        public String lastVerifiedDate;
        public String source;
        public String verifiedBy;
    }
    public static VerifyDemographicGetResponse_DTO_HUM parse(String json) {
        return (VerifyDemographicGetResponse_DTO_HUM) System.JSON.deserialize(json, VerifyDemographicGetResponse_DTO_HUM.class);
    }
}