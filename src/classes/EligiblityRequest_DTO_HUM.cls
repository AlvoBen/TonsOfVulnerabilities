/**
 * @File Name          : Eligiblity_DTO_HUM.cls
 * @Description        : Dto class for template data operation
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 08-27-2020
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    6/28/2020        Chetan Sindhe           Initial Version
**/
public with sharing class EligiblityRequest_DTO_HUM {
    
    public ValidateEligibilityRequest ValidateEligibilityRequest { get; set; }

    public class ValidateEligibilityRequest {
        public string timestamp { get; set; }
        public string consumer { get; set; }
        public string requestid { get; set; }
        public List<Members> members { get; set; }
    }

    public class Members {
        public list<Membercriterion> membercriterion { get; set; }
    }

    public class Membercriterion {
        public string membersourcepersonid { get; set; }
        public string subscribersourcepersonid { get; set; }
        public string source { get; set; }
        public string groupid { get; set; }
        public string platform { get; set; }
        // public string membergenkey { get; set; }
        public list<Products> products { get; set; }
    }

    public class Products {
        public string product { get; set; }
        public string producttype { get; set; }
        public string producttypecode { get; set; }
        // public string productplatform { get; set; }
        public string productidentifier { get; set; }
        public string dualdemoindicator { get; set; }
        public string asoindicator { get; set; }
        public string majorlob { get; set; }
    }

}