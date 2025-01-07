/*******************************************************************************************************************************
Apex Class Name : VerifyDemographicGetRequest_DTO_HUM
Version         : 1.0
Created On      : 07/29/2020
Function        : Helper class used to frame request for Verify Demographic GetRequest service
Test Class      : VerifyDemographicGetRequest_DTO_T_HUM

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                      07/29/2020                  REQ - 1065536 : Original Version
*******************************************************************************************************************************/
public class VerifyDemographicGetRequest_DTO_HUM {

    public RequestHeader RequestHeader;
    public GetverificationstatusRequest getverificationstatusRequest;

    public class RequestHeader {
        public String RequestID;
        public String SessionID;
    }

    public class GetverificationstatusRequest {
        public String PersonId;
    }
    public static VerifyDemographicGetRequest_DTO_HUM parse(String json) {
        return (VerifyDemographicGetRequest_DTO_HUM) System.JSON.deserialize(json, VerifyDemographicGetRequest_DTO_HUM.class);
    }
}