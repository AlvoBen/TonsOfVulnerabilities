/*******************************************************************************************************************************
Apex Class Name : VerifyDemographicUpdateResponse_DTO_HUM
Version         : 1.0
Created On      : 07/29/2020
Function        : Helper class used to parse response for UpdateVerifyDemographics service
Test Class      : VerifyDemographicUpdateResponse_DTO_T_HUM

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                      07/29/2020                  REQ - 1065536 Original Version
*******************************************************************************************************************************/
public class VerifyDemographicUpdateResponse_DTO_HUM{
	public ResponseHeader responseHeader;
	public PutVerificationStatusResponse putverificationstatusResponse;
	public class ResponseHeader {
		public String RequestId;	
		public String SessionId;	
		public String ResponseDuration;	
		public String CoreApiHttpStatusCode;	
	}
	public class PutVerificationStatusResponse {
		public String Status;	
	}
    public static VerifyDemographicUpdateResponse_DTO_HUM parse(String json) {
        return (VerifyDemographicUpdateResponse_DTO_HUM) System.JSON.deserialize(json, VerifyDemographicUpdateResponse_DTO_HUM.class);
    }
}