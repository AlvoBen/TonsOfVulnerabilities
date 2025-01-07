/******************************************************************************************************************
Apex class Name    : DesigneeHubService_Response_DTO
Version            : 1.0
Function           : Response DTO for DESIGNEEHUBSERVICE
Created On         : 10/16/2018
Test Class         : DesigneeHubService_T_HUM

Modification Log:
*   Version             Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0                Vandana Chaudhari           299668             10/16/2018            Original Version
------------------------------------------------------------------------------------------------------------------*/

public with sharing class DesigneeHubService_Response_DTO {
    public GetPHIConsentResponse GetPHIConsentResponse;
    
    public class GetPHIConsentResponse{
        public ResponseStatus ResponseStatus;
        public List<DesigneeList> DesigneeList;
    }
    
    public class ResponseStatus{        
        public String Success  ='';
        public String Error  = '';
    }
    
    public class DesigneeList{
        public String DesigneeId  = '';
        public RelationshipDetails RelationshipDetails;
        public Demographics Demographics;
    }
    
    public class RelationshipDetails{
        public String DocumentType  = '';
        public String Relationship  = '';
        public String LevelOfConsent  = '';
        public String LineOfCoverage  = '';
        public String LimitedDisclosureDetails  = '';
        public String EffectiveDate  = '';
        public String TerminationDate  = '';
        public String ServiceStartDate  = '';
        public String ServiceEndDate  = '';
        public String SubmissionMehtod  = '';
        public POA POA;
    }    
     
    public class POA{
        public String Type  = '';
        public String Description  = '';
    }
    
    public class Demographics{
        public PersonofAuthority PersonofAuthority;
        public GuardianAddress GuardianAddress;     
        public Phone Phone;
    }
    
    public class GuardianAddress{
        public String AddressType  = '';
        public String AddressLineOne  = '';
        public String AddressLineTwo  = '';
        public String City  = '';
        public String State  = '';
        public String Zip  = '';
        public String Validated  = '';
    }   
    
    public class Phone{
        public String PhoneType  = '';
        public String PhoneNumber  = '';
        public String Extension  = '';
    }
	
    public class PersonofAuthority{
        public String FirstName = '';
        public String MiddleName = '';
        public String LastName = '';
    }
}