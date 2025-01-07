/*****************************************************************************************
Apex class Name    : MTVRemarksGetClaimsResponse_LH_HUM_V1
Version            : 1.0   
Created Date       : 06/01/2022

Modification Log:
 * Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
 * Prashant Moghe									   06/01/2022		  Original Version   
 *********************************************************************************************************************************/
 
public class MTVRemarksGetClaimsResponse_LH_HUM_V1 {
    
   public MTVRemarksResponse ClaimsRemarksResponse;
    
   public class MTVRemarksResponse {      
        public RemarksList RemarksList;
        public String ErrorMessage='';    
        public String ErrorType=''; 
    }
    
    public class RemarksList {
        public List<Remarks> Remarks;
    }
    
    public class Remarks {     
	public string EntityType='';
    public String ClaimNumber='';
    public String RemarkCode='';    
    public String RemarkTypeCode='';
    public String RemarkCategory='';    
    public String RemarkText='';
    public String PlatformCode='';  
    public String ConfidentialFlag='';    
    public String RemarkID='';    
    public String ClaimLineNumber='';   
    public String RemarkSequenceNumber='';  
	public string RemarkPlatformCreationDate = '';
    }   
}