/**
 * @File Name          : EligiblityResponse_DTO_HUM.cls
 * @Description        : Dto class for template data operation
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 6/30/2020, 3:43:30 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    6/28/2020        Chetan Sindhe           Initial Version
**/
public with sharing class EligiblityResponse_DTO_HUM {
    
    public ValidateEligibilityResponse ValidateEligibilityResponse { get; set; }

    public class ValidateEligibilityResponse {
        public string timestamp { get; set; }
        public string result { get; set; }
        public string responseCode { get; set; }
        public string requestid { get; set; }
        public List<string> errorsource { get; set; }
        public string errormessage { get; set; }
        public List<Members> members { get; set; }
    }

    public class Members {
        public List<Eligibilities> eligibilities { get; set; }
    }

    public class Eligibilities {
        public GroupRecord groupRecord { get; set; }
        public string heirarchy { get; set; }
        public string template { get; set; }
        public string ruleName { get; set; }
        public EditableDemographicType editableDemographicType { get; set; }
        public string contactHierarchyMessage { get; set; }
        public string criticalHierarchyMessage { get; set; }
        public string routingMessage { get; set; }
        public string message { get; set; }
        public string iseligible { get; set; }
        public string error { get; set; }
    }

    public class GroupRecord {
        public string groupId { get; set; }
        public string dualDemoIndicator { get; set; }
        public string platformCode { get; set; }
        public string ediGroupIndicator { get; set; }
        public string exchangeIndicator { get; set; }
        public string exchangeType { get; set; }
        public string product { get; set; }
        public string productType { get; set; }
        public string productTypeCode { get; set; }
        public string asoIndicator { get; set; }
        public string majorLOB { get; set; }
    }

    public class EditableDemographicType {
        public string mailingAddress { get; set; }
        public string residentialAddress { get; set; }
        public string workEmail { get; set; }
        public string homeEmail { get; set; }
        public string homePhone { get; set; }
        public string workPhone { get; set; }
        public string name { get; set; }
        public string gender { get; set; }
        public string dob { get; set; }
        public string ssn { get; set; }
    }
}