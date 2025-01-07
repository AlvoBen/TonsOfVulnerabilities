/**
 * @File Name          : UpdateMemberDemographicsResponse_DTO_HUM.cls
 * @Description        : Dto class for template data operation
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 7/3/2020, 1:09:14 AM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    6/28/2020        Chetan Sindhe           Initial Version
**/
public with sharing class UpdateMemberDemographicsResponse_DTO_HUM {
    
    public UpdateMemberResponse UpdateMemberResponse { get; set; }

    public class UpdateMemberResponse {
        public string timestamp { get; set; }
        public string result { get; set; }
        public string responseCode { get; set; }
        public string requestid { get; set; }
        public List<string> errorsource { get; set; }
        public string errormessage { get; set; }
        public List<Members> members { get; set; }
    }

    public class Members {
        public List<string> systemsupdated { get; set; }
        public string cdmmsgack { get; set; }
        public List<MemberCriterion> membercriterion { get; set; }
    }

    public class MemberCriterion {
        public string membersourcepersonid { get; set; }
        public string subscribersourcepersonid { get; set; }
        public string platform { get; set; }
        public List<UpdateStatus> updatestatus { get; set; }
    }

    public class UpdateStatus {
        public string requesttype { get; set; }
        public string status { get; set; }
        public string errorcode { get; set; }
        public string errormessage { get; set; }
    }
}