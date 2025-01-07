/*******************************************************************************************************************************
Apex Class Name : VerifyDemographicUpdateRequest_DTO_HUM
Version         : 1.0
Created On      : 07/29/2020
Function        : Helper class used to frame request for UpdateVerifyDemographics service
Test Class      : VerifyDemographicUpdateRequest_DTO_T_HUM

Modification Log: 
* Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Ajay Chakradhar                                      07/29/2020                  REQ - 1065536 : Original Version
*******************************************************************************************************************************/
public class VerifyDemographicUpdateRequest_DTO_HUM{
        
    public RequestHeader requestHeader;
	public PutverificationstatusRequest putverificationstatusRequest;

	public class PutverificationstatusRequest {
		public List<String> PersonId;
		public Demographics demographics;
	}

	public class Demographics {
		public String lastVerifiedDate;
		public String source;
		public String verifiedBy;
	}

	public class RequestHeader {
		public String RequestID;
		public String SessionID;
	}
    public static VerifyDemographicUpdateRequest_DTO_HUM parse(String json) {
        return (VerifyDemographicUpdateRequest_DTO_HUM) System.JSON.deserialize(json, VerifyDemographicUpdateRequest_DTO_HUM.class);
    }
}