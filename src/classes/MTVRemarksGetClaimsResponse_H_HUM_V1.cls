/*****************************************************************************************
Apex class Name    : MTVRemarksGetClaimsResponse_H_HUM_V1
Version            : 1.0   
Created Date       : 10/20/2021 


Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------   
    Apurva Urkude                                      10/22/2021          Initial version(#2039867)
 *********************************************************************************************************************************/
 
public class MTVRemarksGetClaimsResponse_H_HUM_V1 {
    
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